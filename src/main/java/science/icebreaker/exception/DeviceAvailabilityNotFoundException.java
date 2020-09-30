package science.icebreaker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception will be thrown if an device availability entry does not exist.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeviceAvailabilityNotFoundException extends BaseException {

    public DeviceAvailabilityNotFoundException() {
    }
}
