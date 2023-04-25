package app.weehong.financeapi.projections.installments;

import app.weehong.financeapi.entities.Card;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface AllValidInstallmentsProjection {

    Long getId();

    LocalDate getEndDate();

    Integer getLeftoverTenure();

    String getName();

    BigDecimal getPricePerMonth();

    Card getCard();
}
