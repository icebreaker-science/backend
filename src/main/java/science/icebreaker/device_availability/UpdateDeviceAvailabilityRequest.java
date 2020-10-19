package science.icebreaker.device_availability;

import javax.validation.constraints.NotNull;

public class UpdateDeviceAvailabilityRequest {
    @NotNull(message = "A comment must be provided")
    private String comment;

    public UpdateDeviceAvailabilityRequest() { }
    public UpdateDeviceAvailabilityRequest(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
