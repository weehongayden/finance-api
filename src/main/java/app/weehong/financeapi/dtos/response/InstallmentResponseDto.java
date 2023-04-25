package app.weehong.financeapi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstallmentResponseDto {

    private Long id;

    private String name;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    private Integer tenure;

    @JsonProperty("leftover_tenure")
    private Integer leftoverTenure;

    @JsonProperty("price_per_month")
    private BigDecimal pricePerMonth;

    @JsonProperty("start_date")
    private LocalDate startDate;

    @JsonProperty("end_date")
    private LocalDate endDate;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
