package science.icebreaker.account;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import science.icebreaker.data.request.RegistrationRequest;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.dao.repository.AccountRepository;
import science.icebreaker.dao.repository.ResetPasswordTokenRepository;
import science.icebreaker.exception.AccountCreationException;
import science.icebreaker.exception.AccountNotFoundException;
import science.icebreaker.exception.BaseException;
import science.icebreaker.exception.CaptchaInvalidException;
import science.icebreaker.exception.EntryNotFoundException;
import science.icebreaker.service.AccountService;
import science.icebreaker.service.CryptoService;
import science.icebreaker.service.JwtTokenValidationService;
import science.icebreaker.service.MailService;
import science.icebreaker.util.mock.RegistrationRequestMock;
import science.icebreaker.util.TestHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;


/**
 * This class tests the {@link AccountService} and the {@link JwtTokenValidationService}.
 */
@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS) //reset context and db before test
@Transactional
@SuppressWarnings("ConstantConditions")
public class ServicesTest {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final JwtTokenValidationService jwtTokenValidationService;
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;
    private final CryptoService cryptoService;
    private final TestHelper testHelper;
    private final Long resetPasswordTokenTimeout;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public ServicesTest(
        AccountService accountService,
        AccountRepository accountRepository,
        JwtTokenValidationService jwtTokenValidationService,
        TestHelper testHelper,
        ResetPasswordTokenRepository resetPasswordTokenRepository,
        CryptoService cryptoService,
        PasswordEncoder passwordEncoder,
        @Value("${icebreaker.account.resetPasswordDuration}") long resetPasswordTokenTimeout
    ) {
        this.accountService = accountService;
        this.jwtTokenValidationService = jwtTokenValidationService;
        this.testHelper = testHelper;
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
        this.cryptoService = cryptoService;
        this.resetPasswordTokenTimeout = resetPasswordTokenTimeout;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Test
    public void createAccount_validInput_success() throws AccountCreationException {
        testHelper.createAccount();
    }


    @Test
    public void createAccount_emailAlreadyExist_failure() {
        testHelper.createAccount();
        assertThatThrownBy(testHelper::createAccount)
                .isInstanceOf(AccountCreationException.class); // create account twice
    }


    @Test
    public void createAccount_invalidInput_failure() {
        // ID is not null.
        RegistrationRequest request = RegistrationRequestMock.createRegistrationRequest();
        request.getAccount().setId(5);
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(AccountCreationException.class);

        // Password is missing.
        RegistrationRequest request2 = RegistrationRequestMock.createRegistrationRequest();
        request2.getAccount().setPassword("");
        assertThatThrownBy(() -> accountService.createAccount(request2))
                .isInstanceOf(AccountCreationException.class);

        // Email address has an invalid format.
        RegistrationRequest request3 = RegistrationRequestMock.createRegistrationRequest();
        request3.getAccount().setEmail("thisis@wrong");
        assertThatThrownBy(() -> accountService.createAccount(request3))
                .isInstanceOf(AccountCreationException.class);

        // Profile data is missing.
        RegistrationRequest request4 = RegistrationRequestMock.createRegistrationRequest();
        request4.getProfile().setForename("");
        assertThatThrownBy(() -> accountService.createAccount(request4))
                .isInstanceOf(AccountCreationException.class);
    }


    @Test
    public void createAccount_invalidCaptcha_failure() {
        RegistrationRequest request = RegistrationRequestMock.createRegistrationRequest();
        testHelper.mockCaptcha(false);
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(CaptchaInvalidException.class);
    }


    @Test
    public void login_correctData_success() {
        String jwtToken = testHelper.createAndLoginAccount();
        assertThat(jwtToken).isNotBlank();
    }


    @Test
    public void login_wrongData_failure() {
        testHelper.createAccount();

        // Non-existing email
        Account account = RegistrationRequestMock.createRegistrationRequest().getAccount();
        account.setEmail("wrong@email.de");
        assertThatThrownBy(() -> accountService.login(account))
                .isInstanceOf(BadCredentialsException.class);

        // Wrong password
        Account account2 = RegistrationRequestMock.createRegistrationRequest().getAccount();
        account2.setPassword("wrong");
        assertThatThrownBy(() -> accountService.login(account2))
                .isInstanceOf(BadCredentialsException.class);

        // Invalid account object
        Account account3 = RegistrationRequestMock.createRegistrationRequest().getAccount();
        account3.setId(1);
        assertThatThrownBy(() -> accountService.login(account3))
                .isInstanceOf(BaseException.class);
    }


    @Test
    public void getAccountProfile_idExists_success() throws AccountNotFoundException {
        Account account = testHelper.createAccount();
        AccountProfile correctProfile = RegistrationRequestMock.createRegistrationRequest().getProfile();
        AccountProfile accountProfile = accountService.getAccountProfile(account.getId());
        assertThat(accountProfile).isEqualTo(correctProfile);
    }


    @Test
    public void getAccountProfile_idDoesNotExist_failure() {
        assertThatThrownBy(() -> accountService.getAccountProfile(111))
                .isInstanceOf(AccountNotFoundException.class);
    }


    @Test
    public void validateJwtToken_correctToken_success() {
        Account loginData = RegistrationRequestMock.createRegistrationRequest().getAccount();
        String jwtToken = testHelper.createAndLoginAccount();
        Account account = jwtTokenValidationService.validateJwtToken(jwtToken);
        assertThat(account.getEmail())
                .isEqualTo(loginData.getEmail());
    }


    /**
     * This is a superficial test to check if the JWT token is validated in any way. It should not be considered as
     * an actual security test!
     */
    @Test
    public void validateJwtToken_simpleWrongToken_failure() {
        String jwtToken = testHelper.createAndLoginAccount();
        assertThatThrownBy(() -> jwtTokenValidationService.validateJwtToken(jwtToken + "x"));
    }

    @Test
    public void requestResetPassword_nonExistentAccount_failure() {
        final String unregisteredEmail = "someone@somewhere.com";
        assertThatThrownBy(() -> accountService.sendPasswordResetRequest(unregisteredEmail))
            .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    public void requestResetPassword_accountExists_success() {
        final MailService mailService = testHelper.getMailService();
        final Account account = testHelper.createAccount();

        accountService.sendPasswordResetRequest(account.getEmail());

        final ArgumentCaptor<String> tokenArg = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendResetPasswordRequest(tokenArg.capture(), eq(account));

        final String generatedToken = tokenArg.getValue();

        assertThat(this.resetPasswordTokenRepository.findById(generatedToken)).isNotEmpty();

    }

    @Test
    public void requestResetPassword_multipleAccounts_success() {
        final MailService mailService = testHelper.getMailService();
        final Account account = testHelper.createAccount();
        
        final ArgumentCaptor<String> tokenArgs = ArgumentCaptor.forClass(String.class);
        accountService.sendPasswordResetRequest(account.getEmail());
        accountService.sendPasswordResetRequest(account.getEmail());

        verify(mailService, times(2)).sendResetPasswordRequest(tokenArgs.capture(), eq(account));

        final List<String> tokensGenerated = tokenArgs.getAllValues();
        final String firstGeneratedToken = tokensGenerated.get(0);
        final String secondGeneratedToken = tokensGenerated.get(1);

        assertThat(this.resetPasswordTokenRepository.findById(firstGeneratedToken)).isEmpty();
        assertThat(this.resetPasswordTokenRepository.findById(secondGeneratedToken)).isNotEmpty();
    }

    @Test
    public void resetPassword_tokenDoesNotExist_failure() {
        final String randomToken = this.cryptoService.getSecureSecret(32);
        final String newPassword = "secure";
        
        assertThatThrownBy(() -> this.accountService.resetPassword(randomToken, newPassword))
            .isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void resetPassword_tokenExpired_failure() {
        final MailService mailService = testHelper.getMailService();
        final Account account = testHelper.createAccount();
        final String newPassword = "securepassword";

        accountService.sendPasswordResetRequest(account.getEmail());

        final ArgumentCaptor<String> tokenArg = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendResetPasswordRequest(tokenArg.capture(), eq(account));

        final String generatedToken = tokenArg.getValue();

        final long deadlineDuration = this.resetPasswordTokenTimeout;
        testHelper.advanceTimeBySeconds(deadlineDuration);

        assertThatThrownBy(() -> this.accountService.resetPassword(generatedToken, newPassword))
            .isInstanceOf(EntryNotFoundException.class);
    }


    @Test
    public void resetPassword_tokenExists_success() {
        testHelper.resetTime();
        final MailService mailService = testHelper.getMailService();
        final Account account = testHelper.createAccount();
        final String newPassword = "securepassword";

        accountService.sendPasswordResetRequest(account.getEmail());

        final ArgumentCaptor<String> tokenArg = ArgumentCaptor.forClass(String.class);
        verify(mailService).sendResetPasswordRequest(tokenArg.capture(), eq(account));

        final String generatedToken = tokenArg.getValue();

        this.accountService.resetPassword(generatedToken, newPassword);
        assertThat(
            this.accountRepository.findById(account.getId())
                .flatMap(modifiedAccount -> 
                    Optional.of(this.passwordEncoder.matches(newPassword, modifiedAccount.getPassword()))
                )
        ).isEqualTo(Optional.of(true));

        assertThat(this.resetPasswordTokenRepository.findById(generatedToken)).isEmpty();
    }
}
