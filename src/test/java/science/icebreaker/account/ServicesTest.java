package science.icebreaker.account;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;


/**
 * This class tests the {@link AccountService} and the {@link JwtTokenValidationService}.
 */
@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS) //reset context and db before test
public class ServicesTest {

    private final AccountService accountService;

    private final JwtTokenValidationService jwtTokenValidationService;

    private String jwtToken;


    @Autowired
    public ServicesTest(AccountService accountService, JwtTokenValidationService jwtTokenValidationService) {
        this.accountService = accountService;
        this.jwtTokenValidationService = jwtTokenValidationService;
    }


    @Test
    @Order(1)
    public void createAccount_validInput_success() throws AccountCreationException {
        RegistrationRequest request = RegistrationRequestMock.createRegistrationRequest();
        accountService.createAccount(request);
    }


    @Test
    @Order(2)
    public void createAccount_emailAlreadyExist_failure() {
        RegistrationRequest request = RegistrationRequestMock.createRegistrationRequest();
        assertThatThrownBy(() -> accountService.createAccount(request))
                .isInstanceOf(AccountCreationException.class);
    }


    @Test
    @Order(2)
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
    @Order(2)
    public void login_correctData_success() {
        Account account = RegistrationRequestMock.createRegistrationRequest().getAccount();
        jwtToken = accountService.login(account);
        assertThat(jwtToken).isNotBlank();
    }


    @Test
    @Order(2)
    public void login_wrongData_failure() {
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
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @Order(2)
    public void getAccountProfile_idExists_success() throws AccountNotFoundException {
        AccountProfile correctProfile = RegistrationRequestMock.createRegistrationRequest().getProfile();
        AccountProfile accountProfile = accountService.getAccountProfile(1);
        assertThat(accountProfile).isEqualTo(correctProfile);
    }


    @Test
    @Order(2)
    public void getAccountProfile_idDoesNotExist_failure() {
        assertThatThrownBy(() -> accountService.getAccountProfile(111))
                .isInstanceOf(AccountNotFoundException.class);
    }


    @Test
    @Order(3)
    public void validateJwtToken_correctToken_success() {
        login_correctData_success();
        Account account = jwtTokenValidationService.validateJwtToken(jwtToken);
        assertThat(account.getEmail())
                .isEqualTo(RegistrationRequestMock.createRegistrationRequest().getAccount().getEmail());
    }


    /**
     * This is a superficial test to check if the JWT token is validated in any way. It should not be considered as
     * an actual security test!
     */
    @Test
    @Order(3)
    public void validateJwtToken_simpleWrongToken_failure() {
        assertThatThrownBy(() -> jwtTokenValidationService.validateJwtToken(jwtToken + "x"));
    }
}
