package app.weehong.financeapi.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardResponseDto {

  private Long id;

  @JsonProperty("bank_id")
  private Long bankId;

  private String name;

  @JsonProperty("statement_date")
  private int statementDate;

  @JsonProperty("initial_amount")
  private BigDecimal initialAmount;

  @JsonProperty("leftover_amount")
  private BigDecimal leftoverAmount;

  private Set<InstallmentResponseDto> installments;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
}
