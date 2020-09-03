package science.icebreaker.device_availability.ControllerValidators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Defines a constraint for checking if at least one of a method
 * params is not null.
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HasFiltersValidator.class)
@Documented
public @interface HasFiltersConstraint {
    String message() default "Must provide at least one filter";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
