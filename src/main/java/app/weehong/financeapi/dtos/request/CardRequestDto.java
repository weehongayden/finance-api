package app.weehong.financeapi.dtos.request;

import app.weehong.financeapi.annotations.NumberRange;
import app.weehong.financeapi.utils.validations.DefaultGroup;
import app.weehong.financeapi.utils.validations.NotNullGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@GroupSequence({NotNullGroup.class, DefaultGroup.class, CardRequestDto.class})
public class CardRequestDto {

    @NotBlank(message = "Name is required", groups = NotNullGroup.class)
    private String name;

    @NotNull(message = "Amount ID is required", groups = NotNullGroup.class)
    @JsonProperty("amount_id")
    private Long amountId;

    @NotNull(message = "Bank ID is required", groups = NotNullGroup.class)
    @JsonProperty("bank_id")
    private Long bankId;

    @NotNull(message = "Statement date is required", groups = NotNullGroup.class)
    @NumberRange(message = "Statement date must be between 1 and 31", groups = DefaultGroup.class)
    @JsonProperty("statement_date")
    private Integer statementDate;
}

