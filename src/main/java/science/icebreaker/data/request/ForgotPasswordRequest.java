package science.icebreaker.data.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String captcha;

    public ForgotPasswordRequest(@NotBlank @Email String email, @NotBlank String captcha) {
        this.email = email;
        this.captcha = captcha;
    }

    public String getEmail() {
        return email;
    }

    public String getCaptcha() {
        return captcha;
    }

}
