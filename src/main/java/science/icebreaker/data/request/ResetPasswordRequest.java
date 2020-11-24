package science.icebreaker.data.request;

import javax.validation.constraints.NotBlank;

public class ResetPasswordRequest {

    @NotBlank
    private final String token;

    @NotBlank
    private final String password;

    public ResetPasswordRequest(
        String token,
        String password
    ) {
        this.token = token;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }

}
