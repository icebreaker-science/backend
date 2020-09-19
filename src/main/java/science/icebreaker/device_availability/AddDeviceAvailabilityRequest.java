package science.icebreaker.device_availability;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class AddDeviceAvailabilityRequest {

    @NotNull(message = "A device ID must be provided")
    private Integer deviceId;
    private String comment;
    @Pattern(regexp = "[\\d]{5}", message = "Invalid postal code")
    private String germanPostalCode;
    @NotBlank(message = "An institution name must be provided")
    private String institution;
    private String researchGroup;

    public AddDeviceAvailabilityRequest(Integer deviceId, String comment,
    String germanPostalCode, String institution, String researchGroup) {
        this.deviceId = deviceId;
        this.comment = comment;
        this.germanPostalCode = germanPostalCode;
        this.institution = institution;
        this.researchGroup = researchGroup;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
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

}
