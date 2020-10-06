package science.icebreaker.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class DeviceAvailabilityCreationException extends BaseException {
    public DeviceAvailabilityCreationException() {

    }
}
