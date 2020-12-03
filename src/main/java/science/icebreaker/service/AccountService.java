package science.icebreaker.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import science.icebreaker.data.request.RegistrationRequest;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountConfirmation;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.dao.entity.AccountRole;
import science.icebreaker.dao.entity.ResetPasswordToken;
import science.icebreaker.dao.repository.AccountConfirmationRepository;
import science.icebreaker.dao.repository.AccountProfileRepository;
import science.icebreaker.dao.repository.AccountRepository;
import science.icebreaker.dao.repository.AccountRoleRepository;
import science.icebreaker.dao.repository.ResetPasswordTokenRepository;
import science.icebreaker.exception.AccountConfirmationException;
import science.icebreaker.exception.AccountCreationException;
import science.icebreaker.exception.AccountNotFoundException;
import science.icebreaker.exception.CaptchaInvalidException;
import science.icebreaker.exception.EntryNotFoundException;
import science.icebreaker.exception.ErrorCodeEnum;
import science.icebreaker.exception.IllegalRequestParameterException;

/**
 * Please refer to {@link SecurityConfig} for an overview of the security concept.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final AccountRoleRepository accountRoleRepository;

    private final AccountProfileRepository accountProfileRepository;

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final String hostUrl;

    private final String jwtSecret;

    private final long jwtTokenValidityMs;

    private final AccountConfirmationRepository accountConfirmationRepository;

    private MailService mailService;

    private CaptchaService captchaService;
    private CryptoService cryptoService;

    private TimeService clock;

    private long resetPasswordTokenTimeout;

    public AccountService(
            AccountRepository accountRepository,
            AccountRoleRepository accountRoleRepository,
            AccountProfileRepository accountProfileRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            @Value("${icebreaker.host}") String hostUrl,
            @Value("${icebreaker.jwt-secret}") String jwtSecret,
            @Value("${icebreaker.jwt-token-validity-ms}") long jwtTokenValidityMs,
            @Value("${icebreaker.account.resetPasswordDuration}") long resetPasswordTokenTimeout,
            AccountConfirmationRepository accountConfirmationRepository,
            CryptoService cryptoService,
            ResetPasswordTokenRepository resetPasswordTokenRepository,
            TimeService clock,
            CaptchaService captchaService
    ) {
        this.accountRepository = accountRepository;
        this.accountRoleRepository = accountRoleRepository;
        this.accountProfileRepository = accountProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.hostUrl = hostUrl;
        this.jwtSecret = jwtSecret;
        this.jwtTokenValidityMs = jwtTokenValidityMs;
        this.accountConfirmationRepository = accountConfirmationRepository;
        this.captchaService = captchaService;
        this.cryptoService = cryptoService;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.resetPasswordTokenTimeout = resetPasswordTokenTimeout;
        this.clock = clock;
    }

    @Autowired
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     *
     * @param accountId The ID of an account
     * @return The profile of an account
     * @throws AccountNotFoundException If there is no account with the given ID
     */
    public AccountProfile getAccountProfile(int accountId) throws AccountNotFoundException {
        Optional<AccountProfile> profile = accountProfileRepository.findById(accountId);
        if (profile.isEmpty()) {
            throw new AccountNotFoundException()
                    .withErrorCode(ErrorCodeEnum.ERR_ACC_004)
                    .withArgs(accountId);
        }
        return profile.get();
    }


    /**
     * This function checks if an Account contains the required information to be created.
     * The following fields are
     * expected to be not empty: forename, surname, email, password, institution, city.
     * The email has to have the correct format.
     * @param registration The registeration request
     * @return true if the account is valid; false otherwise
     */
    public boolean validateRegistration(RegistrationRequest registration) {
        Account account = registration.getAccount();
        AccountProfile profile = registration.getProfile();
        return !profile.getForename().isBlank()
                && !profile.getSurname().isBlank()
                && !profile.getInstitution().isBlank()
                && !account.getEmail().isBlank()
                && !account.getPassword().isBlank()
                && EmailValidator.getInstance().isValid(account.getEmail());
    }


    /**
     *
     * @param registration The registration data; the ID fields must be null.
     * @return The ID of the new account
     * @throws AccountCreationException If data is missing or if the ID fields are not null.
     */
    @Transactional
    public int createAccount(RegistrationRequest registration)
            throws AccountCreationException, CaptchaInvalidException {
        Account account = registration.getAccount();
        AccountProfile profile = registration.getProfile();
        if (account.getId() != null || profile.getAccountId() != null) {
            throw new AccountCreationException().withErrorCode(ErrorCodeEnum.ERR_ACC_002);
        }
        if (!validateRegistration(registration)) {
            throw new AccountCreationException().withErrorCode(ErrorCodeEnum.ERR_ACC_003);
        }
        captchaService.verifyCaptcha(registration.getCaptcha());

        account.setEmail(account.getEmail().strip().toLowerCase());
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setEnabled(Boolean.FALSE);

        // TODO Save the data in a single transaction
        try {
            Account savedAccount = accountRepository.save(account);
            int accountId = account.getId();
            accountRoleRepository.save(new AccountRole(account.getEmail(), "USER"));
            profile.setAccountId(accountId);
            accountProfileRepository.save(profile);
            saveAccountConfirmationTokenAndSendEmail(savedAccount);
        } catch (DataIntegrityViolationException e) {
            throw new AccountCreationException().withErrorCode(ErrorCodeEnum.ERR_ACC_001);
        }

        return account.getId();
    }


    /**
     * An exception will be thrown if the login data are wrong. Please look up the documentation of
     * {@link AuthenticationManager#authenticate} to see the possible RuntimeExceptions.
     *
     * @param account The login data; the ID field must be null.
     * @return A new JWT token
     */
    public String login(Account account) {
        if (account.getId() != null) {
            throw new IllegalRequestParameterException().withErrorCode(ErrorCodeEnum.ERR_ACC_002);
        }
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(account.getEmail(), account.getPassword()));
        return generateJwtToken(account);
    }


    private String generateJwtToken(Account account) {
        Map<String, Object> claims = new HashMap<>();
        int accountId = accountRepository.findAccountByEmail(account.getEmail()).getId();
        claims.put("account_id", accountId);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(account.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenValidityMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public void confirmAccount(String confirmationToken) {
        AccountConfirmation accountConfirmation = accountConfirmationRepository
                .findAccountConfirmationByConfirmationToken(confirmationToken);

        final int oneHour = 3600000; //one hour in milliseconds
        Date oneHourAgo = new Date(System.currentTimeMillis() - oneHour);

        if (accountConfirmation == null || accountConfirmation.getCreatedDate().before(oneHourAgo)) {
            throw new AccountConfirmationException()
                    .withErrorCode(ErrorCodeEnum.ERR_ACC_006);
        }

        Account account = accountConfirmation.getAccount();
        account.setEnabled(Boolean.TRUE);
        accountRepository.save(account);

        accountConfirmationRepository.delete(accountConfirmation);
    }

    public void resendConfirmationEmail(String email) {
        Account account = accountRepository.findAccountByEmail(email);

        if (account.getEnabled()) {
            throw new AccountConfirmationException()
                    .withErrorCode(ErrorCodeEnum.ERR_ACC_005);
        }

        saveAccountConfirmationTokenAndSendEmail(account);
    }

    public void saveAccountConfirmationTokenAndSendEmail(Account account) {
        final int tokenLength = 32;
        String confirmationToken = this.cryptoService.getSecureSecret(tokenLength);

        AccountConfirmation accountConfirmation = new AccountConfirmation();

        accountConfirmation.setCreatedDate(new Date());
        accountConfirmation.setConfirmationToken(confirmationToken);
        accountConfirmation.setAccount(account);

        accountConfirmationRepository.save(accountConfirmation);
        sendAccountConfirmationEmail(account, confirmationToken);
    }

    public void sendAccountConfirmationEmail(Account account, String confirmationToken) {
        AccountProfile accountProfile = getAccountProfile(account.getId());

        Map<String, String> templateValues = new HashMap<>();
        templateValues.put("hostUrl", hostUrl);
        templateValues.put("confirmationToken", confirmationToken);
        templateValues.put("title", accountProfile.getTitle());
        templateValues.put("surname", accountProfile.getSurname());

        String subject = "Complete Registration!";

        mailService.sendTemplateMail(account.getEmail(), "accountConfirmation", subject, templateValues);
    }

    /**
     * Finds an account by email and sends an email to that account prompting
     * the user to change his password
     * @param email The email of the account
     * @throws AccountNotFoundException if the email is not associated with any account
     */
    @Transactional
    public void sendPasswordResetRequest(String email) throws AccountNotFoundException {
        Account accountToReset = this.accountRepository.findAccountByEmail(email);

        // defer handling non registered emails
        if (accountToReset == null) {
            throw new AccountNotFoundException()
                .withErrorCode(ErrorCodeEnum.ERR_ACC_007);
        }

        // delete previous account entry
        this.resetPasswordTokenRepository.deleteByAccount(accountToReset);

        final int tokenLength = 32;
        String token = this.cryptoService.getSecureSecret(tokenLength);
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(token, accountToReset);
        this.resetPasswordTokenRepository.save(resetPasswordToken);

        this.mailService.sendResetPasswordRequest(token, accountToReset);
    }

    /**
     * Resets the password of an account given a reset password token
     * @param resetPasswordToken The reset password token
     * @param newPassword The new password
     * @throws EntryNotFoundException if the given token is not registered or has expired
     * @return The updated account
     */
    @Transactional
    public Account resetPassword(String resetPasswordToken, String newPassword) throws EntryNotFoundException {
        ResetPasswordToken token = this.resetPasswordTokenRepository.findById(resetPasswordToken)
            .flatMap(curToken -> {
                final LocalDateTime deadline = curToken.getCreatedAt()
                    .plusSeconds(this.resetPasswordTokenTimeout);

                if (this.clock.now().isAfter(deadline)) {
                    return Optional.empty();
                } else {
                    return Optional.of(curToken);
                }
            })
            .orElseThrow(() ->
                new EntryNotFoundException()
                    .withErrorCode(ErrorCodeEnum.ERR_RST_PASS_001)
            );

        Account account = token.getAccount();
        account.setPassword(passwordEncoder.encode(newPassword));
        this.accountRepository.save(account);
        this.resetPasswordTokenRepository.deleteById(resetPasswordToken);

        return account;
    }
}
