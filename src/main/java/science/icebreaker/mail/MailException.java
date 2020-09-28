package science.icebreaker.mail;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MailException extends Exception {

    public MailException() {
        super("Error while sending mail.");
    }

    public MailException(String message) {
        super(message);
    }
}
