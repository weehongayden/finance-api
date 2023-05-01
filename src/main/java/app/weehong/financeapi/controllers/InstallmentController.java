package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.InstallmentRequestDto;
import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.services.InstallmentService;
import app.weehong.financeapi.utils.JwtUtil;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/installments")
public class InstallmentController {

  private final InstallmentService<InstallmentResponseDto, InstallmentRequestDto> installmentService;

  private final JwtUtil jwtUtil;

  public InstallmentController(
      InstallmentService<InstallmentResponseDto, InstallmentRequestDto> installmentService,
      JwtUtil jwtUtil) {
    this.installmentService = installmentService;
    this.jwtUtil = jwtUtil;
  }

  @GetMapping
  public ResponseEntity getInstallments(
      @RequestHeader(name = "Authorization") String authorizationHeader) {
    log.info("getInstallments() function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    List<InstallmentResponseDto> amounts = installmentService.all(userId);
    return ResponseUtil.ResponseMapping(amounts, "Record has fetched successfully", HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity getInstallment(
      @RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable Long id) {
    log.info("getInstallment(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    InstallmentResponseDto amount = installmentService.getById(id, userId);
    return ResponseUtil.ResponseMapping(amount, "Record has fetched successfully", HttpStatus.OK);
  }

  @GetMapping("/total-price-per-month")
  public ResponseEntity getTotalPricePerMonth(
      @RequestHeader(name = "Authorization") String authorizationHeader) {
    log.info("getTotalPricePerMonth() function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    List<InstallmentResponseDto> amounts = installmentService.totalPricePerMonth(userId);
    return ResponseUtil.ResponseMapping(amounts, "Record has fetched successfully", HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity createInstallment(
      @RequestHeader(name = "Authorization") String authorizationHeader,
      @Valid @RequestBody InstallmentRequestDto amountRequestDto) {
    log.info("createInstallment() function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    InstallmentResponseDto amount = installmentService.create(userId, amountRequestDto);
    return ResponseUtil.ResponseMapping(amount, "Successfully created amount", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateInstallment(
      @RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable Long id,
      @Valid @RequestBody InstallmentRequestDto amountRequestDto) {
    log.info("updateInstallment(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    InstallmentResponseDto amount = installmentService.update(id, userId, amountRequestDto);
    return ResponseUtil.ResponseMapping(amount, "Successfully updated amount", HttpStatus.OK);
  }

  @DeleteMapping("{id}")
  public ResponseEntity deleteCard(
      @RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable Long id) {
    log.info("deleteCard(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    boolean result = installmentService.delete(id, userId);
    return ResponseUtil.ResponseMapping(result, "Successfully deleted amount", HttpStatus.OK);
  }
}
