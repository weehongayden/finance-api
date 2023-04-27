package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.AmountRequestDto;
import app.weehong.financeapi.dtos.response.AmountResponseDto;
import app.weehong.financeapi.services.AmountService;
import app.weehong.financeapi.utils.JwtUtil;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/amounts")
public class AmountController {

    private final AmountService<AmountResponseDto, AmountRequestDto> amountService;

    @Autowired
    public AmountController(AmountService<AmountResponseDto, AmountRequestDto> amountService) {
        this.amountService = amountService;
    }

    @GetMapping
    public ResponseEntity getAmounts(@RequestHeader(name = "Authorization") String authorizationHeader) {
        log.info("getAmounts() function called");

        String userId = JwtUtil.extractUserId(authorizationHeader);

        if (userId == null) {
            return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
        }

        List<AmountResponseDto> amounts = amountService.all(userId);
        return ResponseUtil.ResponseMapping(amounts, "Record has fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getAmount(@RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable Long id) {
        log.info("getAmount(" + id + ") function called");

        String userId = JwtUtil.extractUserId(authorizationHeader);

        if (userId == null) {
            return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
        }

        AmountResponseDto amount = amountService.getById(id);
        return ResponseUtil.ResponseMapping(amount, "Record has fetched successfully", HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createAmount(@Valid @RequestBody AmountRequestDto amountRequestDto) throws ParseException {
        log.info("createAmount() function called");

        AmountResponseDto amount = amountService.create(amountRequestDto);
        return ResponseUtil.ResponseMapping(amount, "Successfully created amount", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateAmount(@PathVariable Long id, @Valid @RequestBody AmountRequestDto amountRequestDto) throws ParseException {
        log.info("updateAmount(" + id + ") function called");

        AmountResponseDto amount = amountService.update(id, amountRequestDto);
        return ResponseUtil.ResponseMapping(amount, "Successfully updated amount", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteCard(@PathVariable Long id) {
        log.info("deleteCard(" + id + ") function called");

        boolean result = amountService.delete(id);
        return ResponseUtil.ResponseMapping(result, "Successfully deleted amount", HttpStatus.OK);
    }
}
