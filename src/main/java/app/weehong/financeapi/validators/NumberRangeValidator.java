package app.weehong.financeapi.validators;

import app.weehong.financeapi.annotations.NumberRange;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumberRangeValidator implements ConstraintValidator<NumberRange, Integer> {

  private int min;
  private int max;

  public void initialize(NumberRange numberRange) {
    this.min = numberRange.min();
    this.max = numberRange.max();
  }

  public boolean isValid(Integer value, ConstraintValidatorContext context) {
    return value >= min && value <= max;
  }
}

