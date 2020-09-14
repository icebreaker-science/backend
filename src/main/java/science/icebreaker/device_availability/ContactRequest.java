package science.icebreaker.device_availability;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ContactRequest {

    @NotNull
    @NotBlank
    private final String name;

    @Email
    @NotNull
    private final String email;

    @NotNull
    @NotBlank
    private final String message;

    public ContactRequest(@NotNull @NotBlank String name, @Email @NotNull String email, @NotNull @NotBlank String message) {
        this.name = name;
        this.email = email;
        this.message = message;
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
}
