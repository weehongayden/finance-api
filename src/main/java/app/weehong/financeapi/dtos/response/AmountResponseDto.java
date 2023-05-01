package app.weehong.financeapi.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AmountResponseDto {

  private Long id;

  private String name;

  @JsonProperty("initial_amount")
  private BigDecimal initialAmount;

  @JsonProperty("leftover_amount")
  private BigDecimal leftoverAmount;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("updated_at")
  private LocalDateTime updatedAt;
}
