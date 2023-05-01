package app.weehong.financeapi.projections.installments;

import java.math.BigDecimal;

public interface SumInstallmentByBank {

  BigDecimal getTotalAmount();

  String getName();
}
