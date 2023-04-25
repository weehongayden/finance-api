package app.weehong.financeapi.mappers;

import app.weehong.financeapi.dtos.response.InstallmentResponseDto;
import app.weehong.financeapi.entities.Installment;

public class InstallmentMapper {

    public static InstallmentResponseDto mapInstallmentToInstallmentResponseDto(Installment installment) {
        InstallmentResponseDto response = new InstallmentResponseDto();

        response.setId(installment.getId());
        response.setName(installment.getName());
        response.setTotalAmount(installment.getTotalAmount());
        response.setTenure(installment.getTenure());
        response.setLeftoverTenure(installment.getLeftoverTenure());
        response.setPricePerMonth(installment.getPricePerMonth());
        response.setStartDate(installment.getStartDate());
        response.setEndDate(installment.getEndDate());
        response.setIsActive(installment.isActive());
        response.setCreatedAt(installment.getCreatedAt());
        response.setUpdatedAt(installment.getUpdatedAt());

        return response;
    }

}
