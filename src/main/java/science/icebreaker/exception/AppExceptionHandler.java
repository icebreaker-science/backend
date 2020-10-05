package science.icebreaker.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class AppExceptionHandler {

    private final MessageSource messageSource;

    public AppExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({
            AccountCreationException.class,
            AccountNotFoundException.class,
            InvalidFiltersException.class,
            DeviceAvailabilityCreationException.class,
            IllegalRequestParameterException.class
    })
    public ResponseEntity<Object> handleExceptions(
        HttpServletRequest request,
        BaseException exception,
        Locale locale
    ) {

        Object[] args = exception.getArgs();
        String errorCode = exception.getErrorCode().value;

        String message = messageSource.getMessage(errorCode, args, locale);
        HttpStatus status = exception.getStatus() == null
            ? HttpStatus.BAD_REQUEST : exception.getStatus();

        return buildResponse(new HttpHeaders(), status, Collections.singletonList(message));
    }

    private ResponseEntity<Object> buildResponse(
        HttpHeaders headers,
        HttpStatus status,
        List<String> errors
    ) {
        AppErrorResponse errorResponse = new AppErrorResponse();

        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(status.value());
        errorResponse.setError(status.getReasonPhrase());
        errorResponse.setErrors(errors);

        return new ResponseEntity<>(errorResponse, headers, status);
    }
}
