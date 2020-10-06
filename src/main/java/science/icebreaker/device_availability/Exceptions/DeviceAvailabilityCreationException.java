package science.icebreaker.device_availability.Exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class DeviceAvailabilityCreationException extends Exception {
    public DeviceAvailabilityCreationException(String msg) {
        super(msg);
    }
}
