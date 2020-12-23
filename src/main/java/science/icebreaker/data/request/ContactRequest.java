package science.icebreaker.data.request;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ContactRequest {

    @NotNull
    @NotBlank
    private String name;

    @Email
    @NotNull
    private String email;

    @NotNull
    @NotBlank
    private String message;

    @Nullable
    @ApiModelProperty(value = "HCaptcha token. Required, if user is not authenticated.")
    private String captcha;

    public ContactRequest() { }

    public ContactRequest(
            @NotNull @NotBlank String name,
            @Email @NotNull String email,
            @NotNull @NotBlank String message,
            @Nullable @NotBlank String captcha) {
        this.name = name;
        this.email = email;
        this.message = message;
        this.captcha = captcha;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public String getCaptcha() {
        return captcha;
    }
}
