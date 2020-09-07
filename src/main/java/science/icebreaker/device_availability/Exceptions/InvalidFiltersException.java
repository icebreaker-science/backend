package science.icebreaker.device_availability.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidFiltersException extends RuntimeException {
	public InvalidFiltersException(String msg) {
        super(msg);
    }
}
