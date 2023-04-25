package app.weehong.financeapi.dtos.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"status", "message", "data", "error"})
public class GenericResponseDto<T> {

    @JsonProperty("status")
    private boolean isSuccess;

    private String message;

    private T data;

    private Map<String, String> errors;
}
