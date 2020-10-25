package science.icebreaker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class EntryNotFoundException extends BaseException {
    public EntryNotFoundException() { }
}
