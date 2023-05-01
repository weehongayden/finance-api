package app.weehong.financeapi.repositories;

import app.weehong.financeapi.entities.Installment;
import app.weehong.financeapi.projections.installments.AllValidInstallmentsProjection;
import app.weehong.financeapi.projections.installments.SumInstallmentByBank;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstallmentRepository extends CrudRepository<Installment, Long> {

  @Query("""
      SELECT i
      FROM Installment i
      LEFT JOIN FETCH Card c ON i.card = c
      LEFT JOIN FETCH Bank b ON c.bank = b
      WHERE i.isActive = true
      AND b.user.id = :userId
      ORDER BY i.leftoverTenure ASC
      """)
  Iterable<Installment> findAllByUserId(String userId);

  @Query("""
      SELECT i
      FROM Installment i
      LEFT JOIN FETCH Card c ON i.card = c
      LEFT JOIN FETCH Bank b ON c.bank = b
      WHERE i.isActive = true
      AND i.id = :id
      AND b.user.id = :userId
      """)
  Optional<Installment> findByUserId(Long id, String userId);

  @Query("""
      SELECT COALESCE(SUM(i.pricePerMonth * i.leftoverTenure), 0)
      FROM Installment i
      LEFT JOIN FETCH Card c ON i.card = c
      LEFT JOIN FETCH Bank b ON c.bank = b
      LEFT JOIN FETCH Amount a ON c.amount = a
      WHERE a.id = :id
      AND b.user.id = :userId
      """)
  BigDecimal SumInstallmentByAmountId(Long id, String userId);

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
      AND b.user.id = :userId
      GROUP BY b.name
      """)
  List<SumInstallmentByBank> SumInstallmentGroupByBank(String userId);
}
