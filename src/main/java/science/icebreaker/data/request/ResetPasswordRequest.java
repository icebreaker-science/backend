package science.icebreaker.data.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank
    private final String token;

    @NotBlank
    @Size(min = 8, max = 64)
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
