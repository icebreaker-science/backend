package science.icebreaker.device_availability;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import science.icebreaker.account.Account;
import science.icebreaker.device_availability.Exceptions.DeviceAvailabilityCreationException;
import science.icebreaker.device_availability.Exceptions.DeviceAvailabilityNotFoundException;
import science.icebreaker.wiki.WikiPage;
import science.icebreaker.wiki.WikiPageRepository;

@Service
public class DeviceAvailabilityService {
    
    //Ideally, the wiki page service could provide an interface method for fetching wikipage/device
    private WikiPageRepository wikiPageRepository;
    private DeviceAvailabilityRepository deviceAvailabilityRepository;

    public DeviceAvailabilityService(DeviceAvailabilityRepository deviceAvailabilityRepository, WikiPageRepository wikiPageRepository) {
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

    public void setDeviceAvailabilityRepository(DeviceAvailabilityRepository deviceAvailabilityRepository) {
        this.deviceAvailabilityRepository = deviceAvailabilityRepository;
    }

    /**
     * Adds the device availability entry
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
        if(device.isEmpty()) throw new DeviceAvailabilityCreationException("Device does not exist");
        if(device.get().getType() != WikiPage.PageType.DEVICE) throw new DeviceAvailabilityCreationException("Wiki Page is not a device");
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
     * @return A list of the device availabilities
     */
    public List<DeviceAvailability> getDeviceAvailability(Integer deviceId, Integer ownerId) {
        WikiPage device = null;
        if(deviceId != null) {
            device = new WikiPage();
            device.setId(deviceId);
        }
        Account account = null;
        if(ownerId != null) {
            account = new Account();
            account.setId(ownerId);
        }
        DeviceAvailability availabilityEntry = new DeviceAvailability();
        availabilityEntry.setAccount(account);
        availabilityEntry.setDevice(device);
        return this.deviceAvailabilityRepository.findAll(Example.of(availabilityEntry));
    }

    /**
     * Get device availability entry by {@code id}
     * @param id the id of the device availability entry
     * @return one device availability entry
     * @throws DeviceAvailabilityNotFoundException if no entry with {@code id} exist
     */
    public DeviceAvailability getDeviceAvailability(Integer id) throws DeviceAvailabilityNotFoundException {
        return this.deviceAvailabilityRepository.findById(id).orElseThrow(() -> new DeviceAvailabilityNotFoundException(id));
    }
}
