package science.icebreaker.device_availability;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;

import science.icebreaker.account.Account;
import science.icebreaker.wiki.WikiPage;

import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "device_availability")
public class DeviceAvailability {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private WikiPage device;

    private String comment;

    private String germanPostalCode;

    @Column(nullable = false)
    private String institution;

    private String researchGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    public DeviceAvailability() { }
    public DeviceAvailability(
        Integer id,
        WikiPage device,
        String comment,
        String germanPostalCode,
        String institution,
        String researchGroup,
        Account account
    ) {
        this.id = id;
        this.device = device;
        this.comment = comment;
        this.germanPostalCode = germanPostalCode;
        this.institution = institution;
        this.researchGroup = researchGroup;
        this.account = account;
    }
    public DeviceAvailability(
        WikiPage device,
        String comment,
        String germanPostalCode,
        String institution,
        String researchGroup,
        Account account
    ) {
        this(null, device, comment, germanPostalCode, institution, researchGroup, account);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public WikiPage getDevice() {
        return device;
    }

    public void setDevice(WikiPage device) {
        this.device = device;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGermanPostalCode() {
        return germanPostalCode;
    }

    public void setGermanPostalCode(String germanPostalCode) {
        this.germanPostalCode = germanPostalCode;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getResearchGroup() {
        return researchGroup;
    }

    public void setResearchGroup(String researchGroup) {
        this.researchGroup = researchGroup;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
