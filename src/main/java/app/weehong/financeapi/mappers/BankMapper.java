package app.weehong.financeapi.mappers;

import app.weehong.financeapi.dtos.response.BankResponseDto;
import app.weehong.financeapi.entities.Bank;

public class BankMapper {

  public static BankResponseDto mapBankToBankResponseDto(Bank bank) {
    BankResponseDto response = new BankResponseDto();

    response.setId(bank.getId());
    response.setName(bank.getName());
    response.setCreatedAt(bank.getCreatedAt());
    response.setUpdatedAt(bank.getUpdatedAt());

    return response;
  }
}
