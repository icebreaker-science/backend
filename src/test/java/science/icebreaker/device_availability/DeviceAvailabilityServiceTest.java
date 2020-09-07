package science.icebreaker.device_availability;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import org.springframework.test.context.ActiveProfiles;
import science.icebreaker.account.Account;
import science.icebreaker.account.AccountCreationException;
import science.icebreaker.account.AccountProfile;
import science.icebreaker.account.AccountRepository;
import science.icebreaker.account.AccountService;
import science.icebreaker.account.RegistrationRequest;
import science.icebreaker.account.RegistrationRequestMock;
import science.icebreaker.device_availability.Exceptions.DeviceAvailabilityCreationException;
import science.icebreaker.wiki.WikiPage;
import science.icebreaker.wiki.WikiPageRepository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class DeviceAvailabilityServiceTest {

    private DeviceAvailabilityRepository deviceAvailabilityRepository;
    private DeviceAvailabilityService deviceAvailabilityService;
    private WikiPageRepository wikiPageRepository;
    private AccountService accountService;
    private AccountRepository accountRepository;
    private Account account;

    @Autowired
    public DeviceAvailabilityServiceTest(DeviceAvailabilityService deviceAvailabilityService,
            AccountService accountService, DeviceAvailabilityRepository deviceAvailabilityRepository,
            WikiPageRepository wikiPageRepository, AccountRepository accountRepository) {
        this.deviceAvailabilityService = deviceAvailabilityService;
        this.deviceAvailabilityRepository = deviceAvailabilityRepository;
        this.wikiPageRepository = wikiPageRepository;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
    }

    @BeforeAll
    public void setupUser() throws AccountCreationException {
        this.accountRepository.deleteAll();
        RegistrationRequest accountInfo = RegistrationRequestMock.createRegistrationRequest(); 
        Integer id = this.accountService.createAccount(accountInfo);
        Account account = new Account();
        account.setId(id);
        this.account = account;
    }

    @AfterEach
    public void clearContext() {
        this.deviceAvailabilityRepository.deleteAll();
        this.wikiPageRepository.deleteAll();
    }

    @AfterAll
    public void clearAll() {
        this.accountRepository.deleteAll();
    }

    @Test
    public void saveDeviceAvailability_success() throws DeviceAvailabilityCreationException {
        WikiPage device = new WikiPage(WikiPage.PageType.DEVICE, "device title", "device description", "device references");
        device = this.wikiPageRepository.save(device);
        this.deviceAvailabilityService.addDeviceAvailability(
            device.getId(),
            "Some Comment",
            "67660",
            "Some institution",
            "Some research group",
            account
        );
    }

    @Test
    public void saveDeviceAvailability_failure() {
        assertThatThrownBy(() -> this.deviceAvailabilityService.addDeviceAvailability(
            1,
            "Some Comment",
            "67660",
            "Some institution",
            "Some research group",
            account
        )).isInstanceOf(DeviceAvailabilityCreationException.class);
    }

    @Test
    public void saveDeviceAvailability_test_optionals_success() throws DeviceAvailabilityCreationException {
        WikiPage device = new WikiPage(WikiPage.PageType.DEVICE, "device title", "device description", "device references");
        device = this.wikiPageRepository.save(device);
        this.deviceAvailabilityService.addDeviceAvailability(
            device.getId(),
            null,
            null,
            "Some institution",
            null,
            account
        );
    }

    @Test
    public void getDeviceAvailability_exists_success() {
        WikiPage device = new WikiPage(WikiPage.PageType.DEVICE, "device title", "device description", "device references");
        device = this.wikiPageRepository.save(device);
        this.deviceAvailabilityRepository.save(
            new DeviceAvailability(1, device, "comment", "67660", "TU KL", "Informatik People", this.account));
        this.deviceAvailabilityRepository.save(
            new DeviceAvailability(2, device, "comment", "67660", "TUM", "Informatik People", this.account));

        List<DeviceAvailability> deviceAvailabilityList = 
            this.deviceAvailabilityService.getDeviceAvailability(device.getId(), null);
            
        assertThat(deviceAvailabilityList.size()).isEqualTo(2);
    }

    @Test
    public void getDeviceAvailability_empty_success() {
        Integer deviceId = 1;

        List<DeviceAvailability> deviceAvailabilityList = 
            this.deviceAvailabilityService.getDeviceAvailability(deviceId, null);

        assertThat(deviceAvailabilityList.size()).isEqualTo(0);
    }

    @Test
    public void getDeviceAvailabilityOfUser_empty_success() {
        List<DeviceAvailability> deviceAvailabilityList = 
            this.deviceAvailabilityService.getDeviceAvailability(null, this.account.getId());

        assertThat(deviceAvailabilityList.size()).isEqualTo(0);
    }

    /**
     * Creates two accounts and associates 2 devices with each, then checks the
     * method response when fetching device for one user
     * @throws AccountCreationException
     */
    @Test 
    public void getDeviceAvailabilityOfUser_exists_success() throws AccountCreationException {
        RegistrationRequest request = new RegistrationRequest();
        Account account = new Account(null, "test2@test.com", "secure");
        AccountProfile accountProfile = new AccountProfile();
        accountProfile.setForename("F");
        accountProfile.setSurname("S");
        accountProfile.setInstitution("TU KL");
        accountProfile.setCity("KL");
        accountProfile.setResearchArea("Chemistry Stuff");
        request.setAccount(account);
        request.setProfile(accountProfile);

        Integer id = this.accountService.createAccount(request);
        Account otherAccount = new Account();
        otherAccount.setId(id);

        WikiPage device = new WikiPage(WikiPage.PageType.DEVICE, "device title", "device description", "device references");
        device = this.wikiPageRepository.save(device);

        this.deviceAvailabilityRepository.save(
            new DeviceAvailability(1, device, "comment", "67660", "TU KL", "Informatik People", this.account));
        this.deviceAvailabilityRepository.save(
            new DeviceAvailability(2, device, "comment", "67660", "TUM", "Informatik People", this.account));

        this.deviceAvailabilityRepository.save(
            new DeviceAvailability(1, device, "comment", "67660", "TU KL", "Informatik People", otherAccount));
        this.deviceAvailabilityRepository.save(
            new DeviceAvailability(2, device, "comment", "67660", "TUM", "Informatik People", otherAccount));

        List<DeviceAvailability> deviceAvailabilityList =
            this.deviceAvailabilityService.getDeviceAvailability(null, this.account.getId());

        assertThat(deviceAvailabilityList.size()).isEqualTo(2);
    } 
}
