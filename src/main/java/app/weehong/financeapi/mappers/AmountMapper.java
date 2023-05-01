package app.weehong.financeapi.mappers;

import app.weehong.financeapi.dtos.response.AmountResponseDto;
import app.weehong.financeapi.entities.Amount;

public class AmountMapper {

  public static AmountResponseDto mapAmountToAmountResponseDto(Amount amount) {
    AmountResponseDto response = new AmountResponseDto();

    response.setId(amount.getId());
    response.setName(amount.getName());
    response.setInitialAmount(amount.getInitialAmount());
    response.setLeftoverAmount(amount.getLeftoverAmount());
    response.setCreatedAt(amount.getCreatedAt());
    response.setUpdatedAt(amount.getUpdatedAt());

    return response;
  }
}
