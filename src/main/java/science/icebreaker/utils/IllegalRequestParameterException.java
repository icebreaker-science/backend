package science.icebreaker.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IllegalRequestParameterException extends RuntimeException {

    public IllegalRequestParameterException(String message) {
        super(message);
    }
}
