package science.icebreaker.data.captcha;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HCaptchaResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("challenge_ts")
    private String challengeTs;

    @JsonProperty("hostname")
    private String hostname;

    @JsonProperty("error-codes")
    private ErrorCode[] errorCodes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getChallengeTs() {
        return challengeTs;
    }

    public String getHostname() {
        return hostname;
    }

    public ErrorCode[] getErrorCodes() {
        return errorCodes;
    }

    @JsonIgnore
    public boolean hasServerError() {
        ErrorCode[] errors = getErrorCodes();
        if (errors == null) {
            return false;
        }
        for (ErrorCode error : errors) {
            switch (error) {
                case MissingSecret:
                case InvalidSecret:
                case BadRequest:
                    return true;
                default:
            }
        }
        return false;
    }

    enum ErrorCode {
        MissingSecret, InvalidSecret,
        MissingResponse, InvalidResponse,
        BadRequest, InvalidOrAlreadySeenResponse,
        SitekeySecretMismatch;

        private static final Map<String, ErrorCode> ERRORS_MAP = new HashMap<>(7);

        static {
            ERRORS_MAP.put("missing-input-secret", MissingSecret);
            ERRORS_MAP.put("invalid-input-secret", InvalidSecret);
            ERRORS_MAP.put("missing-input-response", MissingResponse);
            ERRORS_MAP.put("invalid-input-response", InvalidResponse);
            ERRORS_MAP.put("bad-request", BadRequest);
            ERRORS_MAP.put("invalid-or-already-seen-response", InvalidOrAlreadySeenResponse);
            ERRORS_MAP.put("sitekey-secret-mismatch", SitekeySecretMismatch);
        }

        @JsonCreator
        public static ErrorCode forValue(String value) {
            return ERRORS_MAP.get(value.toLowerCase());
        }
    }
}
