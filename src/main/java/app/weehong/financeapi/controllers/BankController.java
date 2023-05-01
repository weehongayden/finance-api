package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.BankRequestDto;
import app.weehong.financeapi.dtos.response.BankResponseDto;
import app.weehong.financeapi.services.BankService;
import app.weehong.financeapi.utils.JwtUtil;
import app.weehong.financeapi.utils.ResponseUtil;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v1/banks")
public class BankController {

  private final BankService<BankResponseDto, BankRequestDto> bankService;

  private final JwtUtil jwtUtil;

  @Autowired
  public BankController(BankService<BankResponseDto, BankRequestDto> bankService, JwtUtil jwtUtil) {
    this.bankService = bankService;
    this.jwtUtil = jwtUtil;
  }

  @GetMapping
  public ResponseEntity getBanks(
      @RequestHeader(name = "Authorization") String authorizationHeader) {
    log.info("getBanks() function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    List<BankResponseDto> banks = bankService.all(userId);
    return ResponseUtil.ResponseMapping(banks, "Record has fetched successfully", HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity getBank(@RequestHeader(name = "Authorization") String authorizationHeader,
      @PathVariable Long id) {
    log.info("getBank(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    BankResponseDto bank = bankService.getById(id, userId);
    return ResponseUtil.ResponseMapping(bank, "Record has fetched successfully", HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity createBank(
      @RequestHeader(name = "Authorization") String authorizationHeader,
      @Valid @RequestBody BankRequestDto bankRequestDto) {
    log.info("createBank() function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    BankResponseDto bank = bankService.create(userId, bankRequestDto);
    return ResponseUtil.ResponseMapping(bank, "Successfully created bank", HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity updateBank(
      @RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable Long id,
      @Valid @RequestBody BankRequestDto bankRequestDto) {
    log.info("updateBank(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    BankResponseDto bank = bankService.update(id, userId, bankRequestDto);
    return ResponseUtil.ResponseMapping(bank, "Successfully updated bank", HttpStatus.OK);
  }

  @DeleteMapping("{id}")
  public ResponseEntity deleteCard(
      @RequestHeader(name = "Authorization") String authorizationHeader, @PathVariable Long id) {
    log.info("deleteCard(" + id + ") function called");

    String userId = jwtUtil.extractUserId(authorizationHeader);

    if (userId == null) {
      return ResponseUtil.ResponseMapping(null, "Invalid token", HttpStatus.UNAUTHORIZED);
    }

    boolean result = bankService.delete(id, userId);
    return ResponseUtil.ResponseMapping(result, "Successfully deleted bank", HttpStatus.OK);
  }
}
