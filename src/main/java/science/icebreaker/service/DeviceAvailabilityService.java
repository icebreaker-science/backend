package science.icebreaker.service;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.DeviceAvailability;
import science.icebreaker.dao.repository.DeviceAvailabilityRepository;
import science.icebreaker.exception.DeviceAvailabilityNotFoundException;
import science.icebreaker.exception.EntryNotFoundException;
import science.icebreaker.exception.DeviceAvailabilityCreationException;
import science.icebreaker.exception.ErrorCodeEnum;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.repository.WikiPageRepository;

@Service
public class DeviceAvailabilityService {

    //Ideally, the wiki page service could provide an interface method for fetching wikipage/device
    private WikiPageRepository wikiPageRepository;
    private DeviceAvailabilityRepository deviceAvailabilityRepository;

    public DeviceAvailabilityService(
        DeviceAvailabilityRepository deviceAvailabilityRepository,
        WikiPageRepository wikiPageRepository) {
        this.deviceAvailabilityRepository = deviceAvailabilityRepository;
        this.wikiPageRepository = wikiPageRepository;
    }

    public WikiPageRepository getWikiPageRepository() {
        return wikiPageRepository;
    }

    public void setWikiPageRepository(WikiPageRepository wikiPageRepository) {
        this.wikiPageRepository = wikiPageRepository;
    }

    public DeviceAvailabilityRepository getDeviceAvailabilityRepository() {
        return deviceAvailabilityRepository;
    }

    public void setDeviceAvailabilityRepository(
        DeviceAvailabilityRepository deviceAvailabilityRepository) {
        this.deviceAvailabilityRepository = deviceAvailabilityRepository;
    }

    /**
     * Adds the device availability entry.
     *
     * @param deviceId The wiki device id
     * @param comment The entry comment
     * @param germanPostalCode the 5 digit postalcode
     * @param institution The institution providing the device
     * @param researchGroup The research group responsible of the device
     * @param account The account of the person resposible for this entry
     * @throws DeviceAvailabilityCreationException If the device is not recorded in the database
     * or the wiki page type is not {@link WikiPage.PageType#DEVICE}
     */
    public void addDeviceAvailability(
            Integer deviceId,
            String comment,
            String germanPostalCode,
            String institution,
            String researchGroup,
            Account account
    ) throws DeviceAvailabilityCreationException {
        Optional<WikiPage> device = this.wikiPageRepository.findById(deviceId);
        if (device.isEmpty()) {
            throw new DeviceAvailabilityCreationException()
                    .withErrorCode(ErrorCodeEnum.ERR_DEVICE_001)
                    .withStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (device.get().getType() != WikiPage.PageType.DEVICE) {
            throw new DeviceAvailabilityCreationException()
                    .withErrorCode(ErrorCodeEnum.ERR_DEVICE_002)
                    .withStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        DeviceAvailability deviceAvailability = new DeviceAvailability(
            device.get(),
            comment,
            germanPostalCode,
            institution,
            researchGroup,
            account
        );
        this.deviceAvailabilityRepository.save(deviceAvailability);
    }

    /**
     * Gets all availability records based on some criteria
     *
     * @param deviceId The device id to search availabilities for
     * @param ownerId The id of the owner of the device entry
     * @param ignoreDisabled returns all entries regardless of disabled status
     * @return A list of the device availabilities
     */
    public List<DeviceAvailability> getDeviceAvailability(Integer deviceId, Integer ownerId, boolean ignoreDisabled) {
        DeviceAvailability availabilityEntry = new DeviceAvailability();
        if (deviceId != null) {
            WikiPage device = new WikiPage();
            device.setId(deviceId);
            availabilityEntry.setDevice(device);
        }
        if (ownerId != null) {
            Account account = new Account();
            account.setId(ownerId);
            availabilityEntry.setAccount(account);
        }

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        if (ignoreDisabled) {
            matcher = matcher.withIgnorePaths("disabled");
        }

        return this.deviceAvailabilityRepository.findAll(Example.of(availabilityEntry, matcher));
    }

    /**
     * Get device availability entry by {@code id}
     * @param id the id of the device availability entry
     * @return one device availability entry
     * @throws DeviceAvailabilityNotFoundException if no entry with {@code id} exist
     */
    public DeviceAvailability getDeviceAvailability(Integer id)
        throws DeviceAvailabilityNotFoundException {
        return this.deviceAvailabilityRepository.findByIdAndDisabledFalse(id)
                .orElseThrow(
                        () -> new DeviceAvailabilityNotFoundException()
                                .withErrorCode(ErrorCodeEnum.ERR_DEVICE_003)
                                .withArgs(id)
                );
    }

    /**
     * Deletes the device availability entry of with a specific id
     * belonging to a user
     *
     * @param id id of the entry
     * @param owner the owner of the entry
     */
    @Transactional
    public void deleteDeviceAvailability(Integer id, Account owner) {
        Long countDeleted = this.deviceAvailabilityRepository.deleteByIdAndAccount(id, owner);
        if (countDeleted == 0) {
            // Details of whether this entry doesn't exist
            // or exists but does not belong to the user should not be transparent
            throw new EntryNotFoundException()
                .withErrorCode(ErrorCodeEnum.ERR_DEVICE_AVAIL_001);
        }
    }

    /**
     * Updates a device availability entry
     *
     * @param id id of the entry
     * @param account owner
     * @param comment the comment in the entry
     * @param germanPostalCode the postal code
     * @param institution the intitution
     * @param researchGroup the research group
     * @param disabled listing disabled status
     */
    public void updateDeviceAvailability(
        Integer id,
        Account account,
        String comment,
        String germanPostalCode,
        String institution,
        String researchGroup,
        Boolean disabled
    ) {
        DeviceAvailability entry = this.deviceAvailabilityRepository
            .findById(id)
            .orElseThrow(() -> new EntryNotFoundException()
                .withErrorCode(ErrorCodeEnum.ERR_DEVICE_AVAIL_001)
            );

        if (entry.getAccount().getId() != account.getId()) {
            throw new EntryNotFoundException()
                .withErrorCode(ErrorCodeEnum.ERR_DEVICE_AVAIL_001);
        }
        if (comment != null) {
            entry.setComment(comment);
        }
        if (germanPostalCode != null) {
            entry.setGermanPostalCode(germanPostalCode);
        }
        if (institution != null) {
            entry.setInstitution(institution);
        }
        if (researchGroup != null) {
            entry.setResearchGroup(researchGroup);
        }
        if (disabled != null) {
            entry.setDisabled(disabled);
        }

        this.deviceAvailabilityRepository.save(entry);
    }
}
