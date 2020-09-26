package science.icebreaker.device_availability.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import science.icebreaker.exception.BaseException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidFiltersException extends BaseException {
	public InvalidFiltersException() {

    }
}
