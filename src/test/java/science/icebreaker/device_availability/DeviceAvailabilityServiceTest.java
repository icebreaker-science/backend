package science.icebreaker.device_availability;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import science.icebreaker.controller.DeviceAvailabilityController;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.DeviceAvailability;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.repository.DeviceAvailabilityRepository;
import science.icebreaker.data.request.ContactRequest;
import science.icebreaker.exception.DeviceAvailabilityCreationException;
import science.icebreaker.exception.DeviceAvailabilityNotFoundException;
import science.icebreaker.exception.EntryNotFoundException;
import science.icebreaker.exception.MailException;
import science.icebreaker.service.DeviceAvailabilityService;
import science.icebreaker.util.TestHelper;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
@SuppressWarnings("ConstantConditions")
public class DeviceAvailabilityServiceTest {

    private final DeviceAvailabilityRepository deviceAvailabilityRepository;
    private final DeviceAvailabilityService deviceAvailabilityService;
    private final DeviceAvailabilityController deviceAvailabilityController;
    private final TestHelper testHelper;

    @Autowired
    public DeviceAvailabilityServiceTest(DeviceAvailabilityService deviceAvailabilityService,
                                         DeviceAvailabilityController deviceAvailabilityController,
                                         DeviceAvailabilityRepository deviceAvailabilityRepository, TestHelper testHelper) {
        this.deviceAvailabilityService = deviceAvailabilityService;
        this.deviceAvailabilityRepository = deviceAvailabilityRepository;
        this.deviceAvailabilityController = deviceAvailabilityController;
        this.testHelper = testHelper;
    }


    @Test
    public void saveDeviceAvailability_success() throws DeviceAvailabilityCreationException {
        Account account = testHelper.createAccount();
        WikiPage device = testHelper.createWikiPage();
        this.deviceAvailabilityService.addDeviceAvailability(device.getId(), "Some Comment", "67660",
                "Some institution", "Some research group", account);
    }

    @Test
    public void saveDeviceAvailability_failure() {
        Account account = testHelper.createAccount();
        assertThatThrownBy(() -> this.deviceAvailabilityService.addDeviceAvailability(1, "Some Comment", "67660",
                "Some institution", "Some research group", account))
                        .isInstanceOf(DeviceAvailabilityCreationException.class);
    }

    @Test
    public void saveDeviceAvailability_test_optionals_success() throws DeviceAvailabilityCreationException {
        Account account = testHelper.createAccount();
        WikiPage device = testHelper.createWikiPage();
        this.deviceAvailabilityService.addDeviceAvailability(device.getId(), null, null, "Some institution", null,
                account);
    }

    @Test
    public void getDeviceAvailability_exists_success() {
        DeviceAvailability deviceAvailability = testHelper.createDeviceAvailability();
        this.deviceAvailabilityRepository
                .save(new DeviceAvailability(2, deviceAvailability.getDevice(), "comment", "67660", "TUM", "Informatik People", deviceAvailability.getAccount()));

        List<DeviceAvailability> deviceAvailabilityList = this.deviceAvailabilityService
                .getDeviceAvailability(deviceAvailability.getDevice().getId(), null, false);

        assertThat(deviceAvailabilityList.size()).isEqualTo(2);
    }

    @Test
    public void getDeviceAvailability_empty_success() {
        Integer deviceId = 1;

        // should count 0 regardless of disabled status
        List<DeviceAvailability> deviceAvailabilityList =
            this.deviceAvailabilityService.getDeviceAvailability(deviceId, null, true);

        assertThat(deviceAvailabilityList.size()).isEqualTo(0);
    }

    @Test
    public void getDeviceAvailabilityOfUser_empty_success() {
        Account account = testHelper.createAccount();
        List<DeviceAvailability> deviceAvailabilityList =
            this.deviceAvailabilityService.getDeviceAvailability(null, account.getId(), true);
        assertThat(deviceAvailabilityList.size()).isEqualTo(0);
    }

    /**
     * Creates two accounts and associates 2 devices with each, then checks the
     * method response when fetching device for one user
     */
    @Test
    public void getDeviceAvailabilityOfUser_exists_success() {
        Account account = testHelper.createAccount();
        Account otherAccount = testHelper.createAccount2();

        WikiPage device = testHelper.createWikiPage();

        this.deviceAvailabilityRepository.save(
                new DeviceAvailability(1, device, "comment", "67660", "TU KL", "Informatik People", account));
        this.deviceAvailabilityRepository
                .save(new DeviceAvailability(2, device, "comment", "67660", "TUM", "Informatik People", account));

        this.deviceAvailabilityRepository.save(
                new DeviceAvailability(1, device, "comment", "67660", "TU KL", "Informatik People", otherAccount));
        this.deviceAvailabilityRepository
                .save(new DeviceAvailability(2, device, "comment", "67660", "TUM", "Informatik People", otherAccount));

        List<DeviceAvailability> deviceAvailabilityList = this.deviceAvailabilityService.getDeviceAvailability(null,
                account.getId(), true);

        assertThat(deviceAvailabilityList.size()).isEqualTo(2);
    }

    @Test
    public void sendContactRequest_prepare_mail_success() throws MailException {
        DeviceAvailability deviceAvailability = testHelper.createDeviceAvailability();
        ContactRequest contactRequest = new ContactRequest("A", "email@icebreaker.science", "message");
        this.deviceAvailabilityController.sendContactRequest(deviceAvailability.getId(), contactRequest);
    }

    @Test
    public void sendContactRequest_device_not_exist() {
        testHelper.mockCaptcha(true);
        assertThatThrownBy(() -> {
            ContactRequest contactRequest = new ContactRequest("A", "email@icebreaker.science", "message");
            this.deviceAvailabilityController.sendContactRequest(-1, contactRequest);
        }).isInstanceOf(DeviceAvailabilityNotFoundException.class);
    }

    @Test
    public void sendContactRequest_request_not_valid() {
        assertThatThrownBy(() -> {
            ContactRequest contactRequest = new ContactRequest("A", "email.icebreaker.science", "message");
            this.deviceAvailabilityController.sendContactRequest(1, contactRequest);
        }).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void deleteDeviceAvailability_notExists_fail() {
        final Integer entryID = 420;
        Account account = testHelper.createAccount();
        assertThatThrownBy(() -> deviceAvailabilityService.deleteDeviceAvailability(entryID, account))
                .isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void deleteDeviceAvailability_notOwnEntry_fail() {
        Account owner = testHelper.createAccount();
        Account otherUser = testHelper.createAccount2();
        WikiPage device = testHelper.createWikiPage();
        DeviceAvailability deviceAvailability = new DeviceAvailability(device, "comment", "99999", "Somewhere",
                "SomeStuff", owner);
        final int deviceAvailabilityId = deviceAvailabilityRepository.save(deviceAvailability).getId();

        assertThatThrownBy(() -> deviceAvailabilityService.deleteDeviceAvailability(deviceAvailabilityId, otherUser))
                .isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void deleteDeviceAvailability_ownEntry_success() {
        DeviceAvailability deviceAvailability = testHelper.createDeviceAvailability();
        deviceAvailabilityService.deleteDeviceAvailability(deviceAvailability.getId(), deviceAvailability.getAccount());
        Optional<DeviceAvailability> availabilityData =
            deviceAvailabilityRepository.findById(deviceAvailability.getId());
        assertThat(availabilityData).isEmpty();
    }

    @Test
    public void updateDeviceAvailability_notExists_fail() {
        final Integer entryID = 420;
        final String newComment = "new comment";
        Account account = testHelper.createAccount();
        assertThatThrownBy(() -> deviceAvailabilityService.updateDeviceAvailability(
                entryID,
                account,
                newComment,
                null,
                null,
                null,
                false
        )).isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void updateDeviceAvailability_notOwnEntry_fail() {
        Account otherUser = testHelper.createAccount2();
        final String newComment = "new comment";
        DeviceAvailability deviceAvailability = testHelper.createDeviceAvailability();

        assertThatThrownBy(
                () -> deviceAvailabilityService.updateDeviceAvailability(
                        deviceAvailability.getId(),
                        otherUser,
                        newComment,
                        null,
                        null,
                        null,
                        false
                )
        ).isInstanceOf(EntryNotFoundException.class);
    }

    @Test
    public void updateDeviceAvailability_ownEntry_success() {
        final String newComment = "new comment";
        final String oldResearchGroup = "group";
        final String newInstitution = "new inst";
        final String newPostalCode = "88888";
        DeviceAvailability deviceAvailability = testHelper.createDeviceAvailability();

        deviceAvailabilityService.updateDeviceAvailability(
                deviceAvailability.getId(),
                deviceAvailability.getAccount(),
                newComment,
                newPostalCode,
                newInstitution,
                oldResearchGroup,
                false
        );
        deviceAvailability = deviceAvailabilityRepository.findById(deviceAvailability.getId()).get();
        assertThat(deviceAvailability.getComment()).isEqualTo(newComment);
        assertThat(deviceAvailability.getGermanPostalCode()).isEqualTo(newPostalCode);
        assertThat(deviceAvailability.getInstitution()).isEqualTo(newInstitution);
        assertThat(deviceAvailability.getResearchGroup()).isEqualTo(oldResearchGroup);
    }

    @Test
    public void updateDeviceAvailability_disable_and_find_success() {
        DeviceAvailability deviceAvailability = testHelper.createDeviceAvailability();

        deviceAvailabilityService.updateDeviceAvailability(
                deviceAvailability.getId(),
                deviceAvailability.getAccount(),
                null,
                null,
                null,
                null,
                true
        );

        assertThatThrownBy(() ->
            deviceAvailabilityService.getDeviceAvailability(deviceAvailability.getId())
        ).isInstanceOf(DeviceAvailabilityNotFoundException.class);

        deviceAvailabilityService.updateDeviceAvailability(
                deviceAvailability.getId(),
                deviceAvailability.getAccount(),
                null,
                null,
                null,
                null,
                false
        );

        DeviceAvailability foundAvailability =
            deviceAvailabilityService.getDeviceAvailability(deviceAvailability.getId());

        assertThat(foundAvailability).isNotNull();
    }

    @Test
    public void updateDeviceAvailability_disable_and_find_list_success() {
        DeviceAvailability deviceAvailability = testHelper.createDeviceAvailability();

        deviceAvailabilityService.updateDeviceAvailability(
                deviceAvailability.getId(),
                deviceAvailability.getAccount(),
                null,
                null,
                null,
                null,
                true
        );

        List<DeviceAvailability> foundListings = deviceAvailabilityService.getDeviceAvailability(
                                deviceAvailability.getDevice().getId(), deviceAvailability.getAccount().getId(), false);

        assertThat(foundListings).doesNotContain(deviceAvailability);

        deviceAvailabilityService.updateDeviceAvailability(
                deviceAvailability.getId(),
                deviceAvailability.getAccount(),
                null,
                null,
                null,
                null,
                false
        );

        List<DeviceAvailability> secondFoundListings = deviceAvailabilityService.getDeviceAvailability(
                                deviceAvailability.getDevice().getId(), deviceAvailability.getAccount().getId(), false);

        assertThat(secondFoundListings).contains(deviceAvailability);
    }

    @Test
    public void updateDeviceAvailability_set_disabled_and_get_own_entries_success() {
        DeviceAvailability deviceAvailability = testHelper.createDeviceAvailability();

        deviceAvailabilityService.updateDeviceAvailability(
                deviceAvailability.getId(),
                deviceAvailability.getAccount(),
                null,
                null,
                null,
                null,
                true
        );

        List<DeviceAvailability> foundListings = deviceAvailabilityService.getDeviceAvailability(
                                deviceAvailability.getDevice().getId(), deviceAvailability.getAccount().getId(), true);

        List<DeviceAvailability> secondFoundListings = deviceAvailabilityService.getDeviceAvailability(
                                deviceAvailability.getDevice().getId(), null, false);

        assertThat(foundListings).contains(deviceAvailability);
        assertThat(secondFoundListings).doesNotContain(deviceAvailability);
    }
}
