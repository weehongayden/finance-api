package app.weehong.financeapi.services;

import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import java.util.List;

public interface InstallmentService<T, K> extends GenericService<T, K> {

  List<InstallmentResponseDto> totalPricePerMonth(String userId);
}
