package science.icebreaker.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {

    private HttpStatus status;
    private ErrorCodeEnum errorCode;
    private Object[] args;

    public BaseException() {

    }

    public BaseException(ErrorCodeEnum errorCode) {
        super(errorCode.value);
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCodeEnum errorCode, HttpStatus status) {
        super(errorCode.value);
        this.status = status;
        this.errorCode = errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public ErrorCodeEnum getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object... args) {
        this.args = args;
    }

    public BaseException withErrorCode(ErrorCodeEnum errorCodeToSet) {
        this.errorCode = errorCodeToSet;
        return this;
    }

    public BaseException withStatus(HttpStatus statusToSet) {
        this.status = statusToSet;
        return this;
    }

    public BaseException withArgs(Object... argsToSet) {
        this.args = argsToSet;
        return this;
    }
}
