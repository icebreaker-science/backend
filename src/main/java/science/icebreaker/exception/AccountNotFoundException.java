package science.icebreaker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * This exception will be thrown if an account does not exist.
 */
@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class AccountNotFoundException extends BaseException {

}
