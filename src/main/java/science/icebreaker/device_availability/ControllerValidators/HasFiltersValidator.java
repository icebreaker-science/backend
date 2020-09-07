package science.icebreaker.device_availability.ControllerValidators;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;

import science.icebreaker.device_availability.Exceptions.InvalidFiltersException;

@SupportedValidationTarget(ValidationTarget.PARAMETERS)
public class HasFiltersValidator implements ConstraintValidator<HasFiltersConstraint, Object[]> {

    /**
     * Given a list of params passed to a controller, checks if 
     * at least one is not null
     * 
     * @param values The params passed
     * @param conext
     * @return true if the params satisfy the validation condition.
     * @throws InvalidFiltersException if all params are null.
     */
    @Override
    public boolean isValid(Object[] values, ConstraintValidatorContext context) {

        Boolean hasFilters = Arrays.asList(values).stream().anyMatch((obj) -> obj != null);

        if(hasFilters) return true;
        else throw new InvalidFiltersException("Must provide at least one filter param");
    }
}
