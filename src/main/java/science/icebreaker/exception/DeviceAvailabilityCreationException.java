package science.icebreaker.device_availability.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import science.icebreaker.exception.BaseException;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class DeviceAvailabilityCreationException extends BaseException {
	public DeviceAvailabilityCreationException() {

    }
}
