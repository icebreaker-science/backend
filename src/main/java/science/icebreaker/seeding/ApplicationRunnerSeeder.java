package science.icebreaker.seeding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.dao.entity.DeviceAvailability;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.entity.WikiPage.PageType;
import science.icebreaker.dao.repository.AccountProfileRepository;
import science.icebreaker.dao.repository.AccountRepository;
import science.icebreaker.dao.repository.DeviceAvailabilityRepository;
import science.icebreaker.dao.repository.WikiPageRepository;

@Component
public class ApplicationRunnerSeeder implements ApplicationRunner {

    private final String populateArg = "populatedb";

    private final AccountRepository accountRepository;
    private final AccountProfileRepository accountProfileRepositoy;
    private final WikiPageRepository wikiPageRepository;
    private final DeviceAvailabilityRepository deviceAvailabilityRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationRunnerSeeder(
        AccountRepository accountRepository,
        AccountProfileRepository accountProfileRepositoy,
        WikiPageRepository wikiPageRepository,
        DeviceAvailabilityRepository deviceAvailabilityRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.accountProfileRepositoy = accountProfileRepositoy;
        this.wikiPageRepository = wikiPageRepository;
        this.deviceAvailabilityRepository = deviceAvailabilityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void run(ApplicationArguments args) {
        if (args.containsOption(populateArg)) {
            // Can be used to define multiple ways for population
            if (args.getOptionValues(populateArg).contains("true")) {
                loadRecords();
            }
        }
    }

    private void loadRecords() {
       predefinedRecords();
    }

    private void predefinedRecords() {
        Account account1 = this.createAccount("User", "One",
            "test1@test.com", "password", "Mr.", "TU KL", "Informatik", "KL");

        Account account2 = this.createAccount("User", "Two",
            "test2@test.com", "password", "Mrs.", "TU KL", "Chemistry", "KL");

        Account account3 = this.createAccount("User", "Three",
            "test3@test.com", "password", "Mrs.", "TUM", "Informatik", "Munich");

        Account account4 = this.createAccount("User", "Four",
            "test4@test.com", "password", "Mr.", "TUM", "Chemistry", "Munich");

        WikiPage device1 = this.createDevice("Device One",
            "The description of device one", "The references of device one");

        WikiPage device2 = this.createDevice("Device Two",
            "The description of device two", "The references of device two");

        WikiPage device3 = this.createDevice("Device Three",
            "The description of device three", "The references of device three");

        WikiPage device4 = this.createDevice("Device Four",
            "The description of device four", "The references of device four");

        DeviceAvailability availabilityDevice1Account1 = this.addDeviceAvailability(
            device1,
            account1,
            "Comment from user1 on device1",
            "67655",
            "TU KL",
            "Informatik"
        );

        DeviceAvailability availabilityDevice2Account1 = this.addDeviceAvailability(
            device2,
            account1,
            "Comment from user1 on device2",
            "67655",
            "TU KL",
            "Informatik"
        );

        DeviceAvailability availabilityDevice1Account3 = this.addDeviceAvailability(
            device3,
            account1,
            "Comment from user3 on device1",
            "67655",
            "TUM",
            "Informatik"
        );

        DeviceAvailability availabilityDevice3Account4 = this.addDeviceAvailability(
            device3,
            account4,
            "Comment from user4 on device3",
            "67655",
            "TUM",
            "Chemistry"
        );

    }

    private Account createAccount(
        String firstName,
        String lastName,
        String email,
        String password,
        String title,
        String institution,
        String researchArea,
        String city
    ) {
        Account account = new Account(null, email, passwordEncoder.encode(password));
        account.setEnabled(true);
        AccountProfile profile = new AccountProfile();
        profile.setCity(city);
        profile.setForename(firstName);
        profile.setInstitution(institution);
        profile.setResearchArea(researchArea);
        profile.setSurname(lastName);
        profile.setTitle(title);

        Account savedAccount = this.accountRepository.save(account);
        profile.setAccountId(savedAccount.getId());
        this.accountProfileRepositoy.save(profile);

        return savedAccount;
    }

    private WikiPage createDevice(
        String title,
        String description,
        String references
    ) {
        WikiPage device = new WikiPage(PageType.DEVICE, title, description, references);
        return this.wikiPageRepository.save(device);
    }

    private DeviceAvailability addDeviceAvailability(
        WikiPage device,
        Account owner,
        String comment,
        String germanPostalCode,
        String institution,
        String researchGroup

    ) {
        DeviceAvailability availability = new DeviceAvailability(
            device,
            comment,
            germanPostalCode,
            institution,
            researchGroup,
            owner
        );
        return this.deviceAvailabilityRepository.save(availability);
    }
}
