package science.icebreaker.util;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.stereotype.Service;
import science.icebreaker.data.captcha.HCaptchaResponse;
import science.icebreaker.data.request.RegistrationRequest;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.DeviceAvailability;
import science.icebreaker.dao.repository.AccountRepository;
import science.icebreaker.dao.repository.DeviceAvailabilityRepository;
import science.icebreaker.service.CaptchaService;
import science.icebreaker.service.MailService;
import science.icebreaker.service.TimeService;
import science.icebreaker.service.AccountService;
import science.icebreaker.util.mock.RegistrationRequestMock;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.repository.WikiPageRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

/**
 * This class collect methods used in different tests
 */
@Service
public class TestHelper {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final WikiPageRepository wikiPageRepository;
    private final DeviceAvailabilityRepository deviceAvailabilityRepository;

    @SpyBean
    private MailService mailService;

    @SpyBean
    private CaptchaService captchaService;

    @MockBean
    private TimeService timeService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public TestHelper(AccountService accountService,
                      AccountRepository accountRepository,
                      WikiPageRepository wikiPageRepository,
                      DeviceAvailabilityRepository deviceAvailabilityRepository) {
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.wikiPageRepository = wikiPageRepository;
        this.deviceAvailabilityRepository = deviceAvailabilityRepository;
    }

    public MailService getMailService() {
        return this.mailService;
    }

    private Account createAccountInternal(RegistrationRequest request) {
        doReturn(null).when(mailService).sendMail(anyString(), anyString(), anyString());
        mockCaptcha(true);

        int accountId = accountService.createAccount(request);
        Account account = request.getAccount();
        account.setId(accountId);
        account.setEnabled(true);
        accountRepository.save(account);

        entityManager.flush(); // flush to db to make login possible

        return account;
    }

    /**
     * Create an activated account.
     * @return activated account
     */
    public Account createAccount() {
        RegistrationRequest request = RegistrationRequestMock.createRegistrationRequest();
        return createAccountInternal(request);
    }

    /**
     * Create an activated account that is different from the one created by {@link #createAccount() createAccount}.
     * @return activated account
     */
    public Account createAccount2() {
        RegistrationRequest request = RegistrationRequestMock.createRegistrationRequest2();
        return createAccountInternal(request);
    }


    /**
     * Create a new account and login that account.
     * @return jwtToken
     */
    public String createAndLoginAccount() {
        createAccount();
        Account loginData = RegistrationRequestMock.createRegistrationRequest().getAccount();
        return accountService.login(loginData);
    }

    /**
     * Create a wiki page and store it in the database.
     * @return WikiPage
     */
    public WikiPage createWikiPage() {
        WikiPage device = new WikiPage(WikiPage.PageType.DEVICE, "device title", "device description",
                "device references");
        return this.wikiPageRepository.save(device);
    }

    /**
     * Create a new device availability and store it in the database.
     * @return DeviceAvailability
     */
    public DeviceAvailability createDeviceAvailability() {
        Account account = createAccount();
        WikiPage device = createWikiPage();
        return this.deviceAvailabilityRepository.save(
                new DeviceAvailability(device, "comment", "67660", "TU KL", "Informatik People", account));
    }

    public void mockCaptcha(boolean success) {
        if (success) {
            HCaptchaResponse hCaptchaResponse = new HCaptchaResponse();
            hCaptchaResponse.setSuccess(true);
            doReturn(hCaptchaResponse).when(captchaService).getValidationResponse(anyString());
        } else {
            doReturn(new HCaptchaResponse()).when(captchaService).getValidationResponse(anyString());
        }
    }

    public void advanceTimeBySeconds(long seconds) {
        when(this.timeService.now()).thenReturn(LocalDateTime.now().plusSeconds(seconds));
    }

    public void resetTime() {
        when(this.timeService.now()).thenCallRealMethod();
    }
}
