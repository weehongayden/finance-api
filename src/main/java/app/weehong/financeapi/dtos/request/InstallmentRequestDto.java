package app.weehong.financeapi.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InstallmentRequestDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.00", message = "Total amount must be greater than 0.00")
    @Digits(integer = 10, fraction = 2, message = "Initial amount must be less than 10 digits and 2 decimal places")
    @JsonProperty("amount")
    private BigDecimal totalAmount;

    @NotNull(message = "Tenure is required")
    private Integer tenure;

    @NotNull(message = "Start date is required")
    @JsonProperty("start_date")
    private LocalDate startDate;

    @NotNull(message = "Card ID is required")
    @JsonProperty("card_id")
    private Long cardId;
}
