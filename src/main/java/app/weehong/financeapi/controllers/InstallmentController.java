package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.InstallmentRequestDto;
import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.services.InstallmentService;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/installments")
public class InstallmentController {

  private final InstallmentService<InstallmentResponseDto, InstallmentRequestDto> installmentService;


  public InstallmentController(
      InstallmentService<InstallmentResponseDto, InstallmentRequestDto> installmentService) {
    this.installmentService = installmentService;
  }

  @GetMapping
  public ResponseEntity getInstallments(Authentication authentication) {
    log.info("getInstallments() function called");
    List<InstallmentResponseDto> amounts = installmentService.all(authentication.getName());
    return ResponseUtil.ResponseMapping(amounts, "Record has fetched successfully", HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity getInstallment(Authentication authentication, @PathVariable Long id) {
    log.info("getInstallment(" + id + ") function called");
    InstallmentResponseDto amount = installmentService.getById(id, authentication.getName());
    return ResponseUtil.ResponseMapping(amount, "Record has fetched successfully", HttpStatus.OK);
  }

  @GetMapping("/total-price-per-month")
  public ResponseEntity getTotalPricePerMonth(Authentication authentication) {
    log.info("getTotalPricePerMonth() function called");
    List<InstallmentResponseDto> amounts = installmentService.totalPricePerMonth(authentication.getName());
    return ResponseUtil.ResponseMapping(amounts, "Record has fetched successfully", HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity createInstallment(Authentication authentication,
      @Valid @RequestBody InstallmentRequestDto amountRequestDto) {
    log.info("createInstallment() function called");
    InstallmentResponseDto amount = installmentService.create(authentication.getName(), amountRequestDto);
    return ResponseUtil.ResponseMapping(amount, "Successfully created amount", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateInstallment(Authentication authentication, @PathVariable Long id,
      @Valid @RequestBody InstallmentRequestDto amountRequestDto) {
    log.info("updateInstallment(" + id + ") function called");
    InstallmentResponseDto amount = installmentService.update(id, authentication.getName(), amountRequestDto);
    return ResponseUtil.ResponseMapping(amount, "Successfully updated amount", HttpStatus.OK);
  }

  @DeleteMapping("{id}")
  public ResponseEntity deleteCard(Authentication authentication, @PathVariable Long id) {
    log.info("deleteCard(" + id + ") function called");
    boolean result = installmentService.delete(id, authentication.getName());
    return ResponseUtil.ResponseMapping(result, "Successfully deleted amount", HttpStatus.OK);
  }
}
