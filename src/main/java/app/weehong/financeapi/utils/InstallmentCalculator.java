package app.weehong.financeapi.utils;

import app.weehong.financeapi.repositories.InstallmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InstallmentCalculator {

    private static InstallmentRepository installmentRepository;

    @Autowired
    public InstallmentCalculator(InstallmentRepository installmentRepository) {
        this.installmentRepository = installmentRepository;
    }

    public static BigDecimal calculateLeftoverAmount(Long id, BigDecimal amount) {
        BigDecimal totalAmount = installmentRepository.SumInstallmentByAmountId(id);
        if (totalAmount == null) {
            return amount;
        }
        return amount.subtract(totalAmount);
    }
}
