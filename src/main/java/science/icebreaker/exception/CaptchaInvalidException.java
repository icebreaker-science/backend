package science.icebreaker.exception;

import org.springframework.http.HttpStatus;

public class CaptchaInvalidException extends BaseException {

    public CaptchaInvalidException(ErrorCodeEnum errorCode, HttpStatus status) {
        super(errorCode, status);
    }
}
