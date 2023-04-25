package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.InstallmentRequestDto;
import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.services.InstallmentService;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/installments")
public class InstallmentController {

    private final InstallmentService<InstallmentResponseDto, InstallmentRequestDto> installmentService;

    public InstallmentController(InstallmentService<InstallmentResponseDto, InstallmentRequestDto> installmentService) {
        this.installmentService = installmentService;
    }

    @GetMapping
    public ResponseEntity getInstallments() {
        log.info("getInstallments() function called");

        List<InstallmentResponseDto> amounts = installmentService.all();
        return ResponseUtil.ResponseMapping(amounts, "Record has fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getInstallment(@PathVariable Long id) {
        log.info("getInstallment(" + id + ") function called");

        InstallmentResponseDto amount = installmentService.getById(id);
        return ResponseUtil.ResponseMapping(amount, "Record has fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/total-price-per-month")
    public ResponseEntity getTotalPricePerMonth() {
        log.info("getTotalPricePerMonth() function called");

        List<InstallmentResponseDto> amounts = installmentService.totalPricePerMonth();
        return ResponseUtil.ResponseMapping(amounts, "Record has fetched successfully", HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createInstallment(@Valid @RequestBody InstallmentRequestDto amountRequestDto) throws ParseException {
        log.info("createInstallment() function called");

        InstallmentResponseDto amount = installmentService.create(amountRequestDto);
        return ResponseUtil.ResponseMapping(amount, "Successfully created amount", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateInstallment(@PathVariable Long id, @Valid @RequestBody InstallmentRequestDto amountRequestDto) throws ParseException {
        log.info("updateInstallment(" + id + ") function called");

        InstallmentResponseDto amount = installmentService.update(id, amountRequestDto);
        return ResponseUtil.ResponseMapping(amount, "Successfully updated amount", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteCard(@PathVariable Long id) {
        log.info("deleteCard(" + id + ") function called");

        boolean result = installmentService.delete(id);
        return ResponseUtil.ResponseMapping(result, "Successfully deleted amount", HttpStatus.OK);
    }
}
