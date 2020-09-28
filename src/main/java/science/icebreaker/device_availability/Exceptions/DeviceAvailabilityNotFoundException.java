package science.icebreaker.device_availability.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception will be thrown if an device availability entry does not exist.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DeviceAvailabilityNotFoundException extends Exception {

    private final int id;

    public DeviceAvailabilityNotFoundException(int id) {
        super("There is no device availability with the ID " + id + ".");
        this.id = id;
    }

    public int getDeviceId() {
        return id;
    }
}
