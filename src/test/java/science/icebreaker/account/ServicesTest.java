package science.icebreaker.account;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import science.icebreaker.data.request.RegistrationRequest;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.exception.AccountCreationException;
import science.icebreaker.exception.AccountNotFoundException;
import science.icebreaker.exception.BaseException;
import science.icebreaker.service.AccountService;
import science.icebreaker.service.JwtTokenValidationService;
import science.icebreaker.util.mock.RegistrationRequestMock;
import science.icebreaker.util.TestHelper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
    private final JwtTokenValidationService jwtTokenValidationService;
    private final TestHelper testHelper;


    @Autowired
    public ServicesTest(AccountService accountService, JwtTokenValidationService jwtTokenValidationService, TestHelper testHelper) {
        this.accountService = accountService;
        this.jwtTokenValidationService = jwtTokenValidationService;
        this.testHelper = testHelper;
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
}
