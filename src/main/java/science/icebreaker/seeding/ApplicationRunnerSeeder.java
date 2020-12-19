package science.icebreaker.seeding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.dao.entity.DeviceAvailability;
import science.icebreaker.dao.entity.Media;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.entity.WikiPage.PageType;
import science.icebreaker.dao.repository.AccountProfileRepository;
import science.icebreaker.dao.repository.AccountRepository;
import science.icebreaker.dao.repository.DeviceAvailabilityRepository;
import science.icebreaker.dao.repository.MediaRepository;
import science.icebreaker.dao.repository.WikiPageRepository;

@Component
@SuppressWarnings({"checkstyle:NoWhitespaceBefore", "checkstyle:LineLength"})
public class ApplicationRunnerSeeder implements ApplicationRunner {

    private final String populateArg = "populatedb";

    private final AccountRepository accountRepository;
    private final AccountProfileRepository accountProfileRepositoy;
    private final WikiPageRepository wikiPageRepository;
    private final DeviceAvailabilityRepository deviceAvailabilityRepository;
    private final MediaRepository mediaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationRunnerSeeder(
        AccountRepository accountRepository,
        AccountProfileRepository accountProfileRepositoy,
        WikiPageRepository wikiPageRepository,
        DeviceAvailabilityRepository deviceAvailabilityRepository,
        MediaRepository mediaRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.accountProfileRepositoy = accountProfileRepositoy;
        this.wikiPageRepository = wikiPageRepository;
        this.deviceAvailabilityRepository = deviceAvailabilityRepository;
        this.mediaRepository = mediaRepository;
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
        String newLine = System.getProperty("line.separator");

        Account account1 = this.createAccount("Guy", "Person",
            "testguyperson@icebreaker.science", "securepassword", "Dr.", "TUM", "Chemistry", "Munich");

        Media device1Media = addMedia("image", "image/jpeg");
        Media device2Media = addMedia("image", "image/png");
        Media device3Media = addMedia("image", "image/jpeg");

        WikiPage device1 = this.createDevice("MiniFlex Rigaku",
            String.join(newLine,
                "Benchtop powder x-ray diffraction (XRD) instrument\n"
                , ""
                , "Qualitative and quantitative phase analysis of poly-crystalline materials\n"
                , ""
                , "- Phase identification"
                , "- Phase quantification (phase ID)"
                , "- Percent (%) crystallinity"
                , "- Crystallite size and strain"
                , "- Lattice parameter refinement"
                , "- Rietveld refinement"
            )
            , "https://www.rigaku.com/products/xrd/miniflex"
            , device1Media
        );

        WikiPage device2 = this.createDevice("Orbitrap ID-X Tribrid Mass Spectrometer",
            String.join(newLine,
                "**Thermo Scientific Orbitrap ID-X Tribrid Mass Spectrometer System**"
                , ""
                , "* Quadrupole mass filter up to 0.4 amu"
                , "* Ultra-high-field orbitrap mass analyzer (resolution up to 500 000 FWHM)"
                , "* Ion-routing multipole"
                , "* Dual-pressure linear ion trap"
            )
            , "https://assets.thermofisher.com/TFS-Assets/CMD/brochures/br-65171-ms-orbitrap-id-x-tribrid-ms-br65171-en.pdf"
            , device2Media
        );

        WikiPage device3 = this.createDevice("XPS",
            "Quantitative, qualtitative and chemical environment analysis in the UHV. Suitable for surface-measurements.",
            String.join(newLine,
                "Van der Heide, P. X-ray Photoelectron Spectroscopy: An introduction to Principles and Practices, ISBN 978-1-118-16292-7, Wiley"
                , ""
                , "Shabanova, I N; Kodolov, V I.Polymers Research Journal; Hauppauge Bd. 5, Ausg. 2,  (2011): 237-243."
            ),
            device3Media
        );

        WikiPage device4 = this.createDevice("Dual Beam UV/VIS Spectrometer",
            "Dual beam UV/VIS spectrometer for measuring adsorption spectra with an integrated reference. Ideal for measuring nucleic acids, proteins and complexes with an adsorption maximum in the UV/VIS range."
            , "Perkampus, H.-H. UV-VIS Spectroscopy and Its Applications, ISBN-13: 978-3-642-77479-9, Springer Laboratory."
            , null
        );

        DeviceAvailability availabilityDevice1Account1 = this.addDeviceAvailability(
            device1,
            account1,
            "I am looking for new cooperation partners. If you are interested in my research topic, you are welcome to contact me by email.",
            "85748",
            "Technical University Munich",
            ""
        );

        DeviceAvailability availabilityDevice2Account1 = this.addDeviceAvailability(
            device2,
            account1,
            "",
            "85748",
            "Technical University of Munich",
            ""
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
        String references,
        Media media
    ) {
        WikiPage device = new WikiPage(PageType.DEVICE, title, description, references);
        if (media != null) {
            device.setMedia(media);
        }
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

    private Media addMedia(String name, String mimeType) {
        Media media = new Media(mimeType, name);
        return this.mediaRepository.save(media);
    }
}
