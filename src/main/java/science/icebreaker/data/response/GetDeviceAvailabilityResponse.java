package science.icebreaker.data.response;

import science.icebreaker.dao.entity.DeviceAvailability;

public class GetDeviceAvailabilityResponse {
    private Integer id;
    private Integer deviceId;
    private Integer accountId;
    private String comment;
    private String germanPostalCode;
    private String institution;
    private String researchGroup;
    private boolean disabled;

    public GetDeviceAvailabilityResponse(
        Integer id,
        Integer deviceId,
        Integer accountId,
        String comment,
        String germanPostalCode,
        String institution,
        String researchGroup,
        boolean disabled
    ) {
        this.id = id;
        this.deviceId = deviceId;
        this.accountId = accountId;
        this.comment = comment;
        this.germanPostalCode = germanPostalCode;
        this.institution = institution;
        this.researchGroup = researchGroup;
        this.disabled = disabled;
    }

    public Integer getId() {
        return id;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public static GetDeviceAvailabilityResponse fromEntity(DeviceAvailability deviceAvailability) {
        return new GetDeviceAvailabilityResponse(
            deviceAvailability.getId(),
            deviceAvailability.getDevice().getId(),
            deviceAvailability.getAccount().getId(),
            deviceAvailability.getComment(),
            deviceAvailability.getGermanPostalCode(),
            deviceAvailability.getInstitution(),
            deviceAvailability.getResearchGroup(),
            deviceAvailability.isDisabled()
        );
    }
}
