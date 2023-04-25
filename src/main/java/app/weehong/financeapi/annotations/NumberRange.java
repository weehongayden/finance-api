package app.weehong.financeapi.annotations;

import app.weehong.financeapi.validators.NumberRangeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Documented
@Constraint(validatedBy = NumberRangeValidator.class)
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberRange {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 1;

    int max() default 31;

    String message() default "Invalid number range";
}
