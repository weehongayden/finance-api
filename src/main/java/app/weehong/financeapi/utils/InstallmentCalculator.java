package app.weehong.financeapi.utils;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class InstallmentCalculator {

  public BigDecimal calculateLeftoverAmount(BigDecimal totalAmount, BigDecimal initialAmount) {
    if (initialAmount == null) {
      return initialAmount;
    }
    return initialAmount.subtract(totalAmount);
  }
}
