package app.weehong.financeapi.repositories;

import app.weehong.financeapi.entities.Amount;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AmountRepository extends CrudRepository<Amount, Long> {

    @Query("""
            SELECT a
            FROM Amount a
            WHERE a.user.id = :id
            """)
    Iterable<Amount> findAllByUserId(String id);

    @Modifying
    @Query("""
            UPDATE Amount a
            SET a.leftoverAmount = :leftoverAmount
            WHERE a.id = :id
            """)
    void updateLeftoverAmountById(Long id, BigDecimal leftoverAmount);
}
