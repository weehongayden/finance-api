package app.weehong.financeapi.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BankRequestDto {

  @NotNull(message = "User ID is required")
  @JsonProperty("user_id")
  private String userId;

  @NotBlank(message = "Name is required")
  private String name;
}
