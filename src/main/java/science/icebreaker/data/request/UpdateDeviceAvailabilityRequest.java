package science.icebreaker.data.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UpdateDeviceAvailabilityRequest {

    private String comment;
    @Pattern(regexp = "[\\d]{5}", message = "Invalid postal code")
    private String germanPostalCode;
    @NotBlank(message = "An institution name must be provided")
    private String institution;
    private String researchGroup;

    public UpdateDeviceAvailabilityRequest() { }
    public UpdateDeviceAvailabilityRequest(
        String comment,
        String germanPostalCode,
        String institution,
        String researchGroup
    ) {
        this.comment = comment;
        this.germanPostalCode = germanPostalCode;
        this.institution = institution;
        this.researchGroup = researchGroup;
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
