package app.weehong.financeapi.utils;

import app.weehong.financeapi.dtos.response.GenericResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity ResponseMapping(T data, String message, HttpStatus status) {
        GenericResponseDto result;

        if (data == null) {
            result = GenericResponseDto.builder()
                    .isSuccess(false)
                    .message("Record doesn't exists")
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        result = GenericResponseDto.builder()
                .isSuccess(true)
                .message(message)
                .data(data)
                .build();

        return ResponseEntity.status(status).body(result);
    }
}
