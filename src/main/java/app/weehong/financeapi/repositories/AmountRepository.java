package app.weehong.financeapi.repositories;

import app.weehong.financeapi.entities.Amount;
import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmountRepository extends CrudRepository<Amount, Long> {

  @Query("""
      SELECT a
      FROM Amount a
      WHERE a.user.id = :userId
      """)
  Iterable<Amount> findAllByUserId(String userId);

  @Query("""
      SELECT a
      FROM Amount a
      WHERE a.id = :id
      AND a.user.id = :userId
      """)
  Optional<Amount> findByUserId(Long id, String userId);

  @Modifying
  @Query("""
      UPDATE Amount a
      SET a.leftoverAmount = :leftoverAmount
      WHERE a.id = :id
      """)
  void updateLeftoverAmountById(Long id, BigDecimal leftoverAmount);
}
