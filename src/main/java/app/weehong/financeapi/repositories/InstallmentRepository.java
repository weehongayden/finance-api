package app.weehong.financeapi.repositories;

import app.weehong.financeapi.entities.Installment;
import app.weehong.financeapi.projections.installments.AllValidInstallmentsProjection;
import app.weehong.financeapi.projections.installments.SumInstallmentByBank;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface InstallmentRepository extends CrudRepository<Installment, Long> {

    @Query("""
            SELECT i
            FROM Installment i
            WHERE i.isActive = true
            ORDER BY i.leftoverTenure ASC
            """)
    Iterable<Installment> findAll();

    @Query("""
            SELECT COALESCE(SUM(i.pricePerMonth * i.leftoverTenure), 0)
            FROM Installment i
            LEFT JOIN FETCH Card c ON i.card = c
            LEFT JOIN FETCH Amount a ON c.amount = a
            WHERE a.id = :id
            """)
    BigDecimal SumInstallmentByAmountId(Long id);

    @Query("""
            SELECT i.endDate as endDate, i.id as id, i.leftoverTenure as leftoverTenure, i.name as name, i.pricePerMonth as pricePerMonth, i.card as card
            FROM Installment i
            LEFT JOIN FETCH Card c ON i.card = c
            LEFT JOIN FETCH Amount a ON c.amount = a
            WHERE i.isActive = true
            """)
    List<AllValidInstallmentsProjection> findAllValidInstallments();

    @Query("""
            SELECT SUM(i.pricePerMonth) as totalAmount, b.name as name
            FROM Installment i
            LEFT JOIN FETCH Card c ON i.card = c
            LEFT JOIN FETCH Bank b ON c.bank = b
            WHERE i.isActive = true
            GROUP BY b.name
            """)
    List<SumInstallmentByBank> SumInstallmentGroupByBank();
}
