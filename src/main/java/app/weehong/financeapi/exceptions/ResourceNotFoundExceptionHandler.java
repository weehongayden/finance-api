package app.weehong.financeapi.exceptions;

import app.weehong.financeapi.dtos.response.GenericResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestControllerAdvice
public class ResourceNotFoundExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<Object> handleNotFoundException(ResponseStatusException ex) {
    log.error("ResourceNotFoundException: " + ex.getMessage());
    GenericResponseDto<Object> genericResponseDto = GenericResponseDto.builder()
        .isSuccess(false)
        .message(ex.getReason())
        .build();
    return new ResponseEntity<>(genericResponseDto, null, 404);
  }
}
