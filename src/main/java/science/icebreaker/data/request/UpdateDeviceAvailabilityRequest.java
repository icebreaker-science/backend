package science.icebreaker.data.request;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class UpdateDeviceAvailabilityRequest {

    private String comment;
    @Pattern(regexp = "[\\d]{5}", message = "Invalid postal code")
    private String germanPostalCode;
    @Size(min = 1, message = "An institution name must be provided")
    private String institution;
    private String researchGroup;
    private Boolean disabled;

    public UpdateDeviceAvailabilityRequest() { }
    public UpdateDeviceAvailabilityRequest(
        String comment,
        String germanPostalCode,
        String institution,
        String researchGroup,
        Boolean disabled
    ) {
        this.comment = comment;
        this.germanPostalCode = germanPostalCode;
        this.institution = institution;
        this.researchGroup = researchGroup;
        this.disabled = disabled;
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

    public Boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
