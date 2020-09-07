package science.icebreaker.validation;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import science.icebreaker.validation.exception.IllegalRequestParameterException;

import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.*;

/*
 * This class reformats the api response for invalid input annotated with @Valid
 * */
@ControllerAdvice
public class GlobalValidationExceptionHandler extends ResponseEntityExceptionHandler {

    // error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {

        Map<String, List<FieldError>> errorsByField = ex.getBindingResult().getFieldErrors().stream()
                .collect(groupingBy(FieldError::getField, toList()));

        Map<String, List<String>> errors = errorsByField.entrySet().stream().collect(toMap(
                Map.Entry::getKey,
                entry -> {
                    List<String> errorMessages = new ArrayList<>();
                    for (FieldError fieldError : entry.getValue()) {
                        // return only NotNull error if field contains NotNull error
                        if (Objects.equals(fieldError.getCode(), "NotNull")) {
                            return singletonList(fieldError.getDefaultMessage());
                        } else {
                            errorMessages.add(fieldError.getDefaultMessage());
                        }
                    }
                    return errorMessages;
                }
        ));

        return formatErrorResponse(headers, status, errors);
    }

    // error handle for IllegalRequestParameterException
    @ExceptionHandler({IllegalRequestParameterException.class})
    public Object handleConstraintViolation(IllegalRequestParameterException ex) {
        return formatErrorResponse(new HttpHeaders(), HttpStatus.BAD_REQUEST,
                singletonMap(ex.getField(), singletonList(ex.getMessage())));
    }

    private ResponseEntity<Object> formatErrorResponse(HttpHeaders headers, HttpStatus status,
                                                       Map<String, List<String>> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", new Date());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("errors", errors);
        return new ResponseEntity<>(body, headers, status);
    }
}
