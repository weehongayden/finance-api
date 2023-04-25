package app.weehong.financeapi.controllers;

import app.weehong.financeapi.dtos.request.BankRequestDto;
import app.weehong.financeapi.dtos.response.BankResponseDto;
import app.weehong.financeapi.services.BankService;
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
@RequestMapping("/api/v1/banks")
public class BankController {

    private final BankService<BankResponseDto, BankRequestDto> bankService;

    public BankController(BankService<BankResponseDto, BankRequestDto> bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public ResponseEntity getBanks() {
        log.info("getBanks() function called");

        List<BankResponseDto> banks = bankService.all();
        return ResponseUtil.ResponseMapping(banks, "Record has fetched successfully", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getBank(@PathVariable Long id) {
        log.info("getBank(" + id + ") function called");

        BankResponseDto bank = bankService.getById(id);
        return ResponseUtil.ResponseMapping(bank, "Record has fetched successfully", HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity createBank(@Valid @RequestBody BankRequestDto bankRequestDto) throws ParseException {
        log.info("createBank() function called");

        BankResponseDto bank = bankService.create(bankRequestDto);
        return ResponseUtil.ResponseMapping(bank, "Successfully created bank", HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBank(@PathVariable Long id, @Valid @RequestBody BankRequestDto bankRequestDto) throws ParseException {
        log.info("updateBank(" + id + ") function called");

        BankResponseDto bank = bankService.update(id, bankRequestDto);
        return ResponseUtil.ResponseMapping(bank, "Successfully updated bank", HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteCard(@PathVariable Long id) {
        log.info("deleteCard(" + id + ") function called");

        boolean result = bankService.delete(id);
        return ResponseUtil.ResponseMapping(result, "Successfully deleted bank", HttpStatus.OK);
    }
}
