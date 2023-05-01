package app.weehong.financeapi.dtos.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmountRequestDto {

  @NotBlank(message = "Name is required")
  private String name;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.00", message = "Initial amount must be greater than 0.00")
  @Digits(integer = 10, fraction = 2, message = "Initial amount must be less than 10 digits and 2 decimal places")
  private BigDecimal amount;
}
