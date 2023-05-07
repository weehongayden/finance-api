package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.AmountRequestDto;
import app.weehong.financeapi.dtos.response.AmountResponseDto;
import app.weehong.financeapi.services.AmountService;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity getAmounts(Authentication authentication) {
        log.info("getAmounts() function called");
        List<AmountResponseDto> amounts = amountService.all(authentication.getName());
        return ResponseUtil.ResponseMapping(amounts, "Record has fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getAmount(Authentication authentication, @PathVariable Long id) {
        log.info("getAmount(" + id + ") function called");
        AmountResponseDto amount = amountService.getById(id, authentication.getName());
        return ResponseUtil.ResponseMapping(amount, "Record has fetched successfully", HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createAmount(
            Authentication authentication,
            @Valid @RequestBody AmountRequestDto amountRequestDto) {
        log.info("createAmount() function called");
        AmountResponseDto amount = amountService.create(authentication.getName(), amountRequestDto);
        return ResponseUtil.ResponseMapping(amount, "Successfully created amount", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateAmount(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody AmountRequestDto amountRequestDto) {
        log.info("updateAmount(" + id + ") function called");
        AmountResponseDto amount = amountService.update(id, authentication.getName(), amountRequestDto);
        return ResponseUtil.ResponseMapping(amount, "Successfully updated amount", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteCard(Authentication authentication, @PathVariable Long id) {
        log.info("deleteCard(" + id + ") function called");
        boolean result = amountService.delete(id, authentication.getName());
        return ResponseUtil.ResponseMapping(result, "Successfully deleted amount", HttpStatus.OK);
    }
}
