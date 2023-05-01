package app.weehong.financeapi.repositories;

import app.weehong.financeapi.entities.Bank;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends CrudRepository<Bank, Long> {

  @Query("""
      SELECT b
      FROM Bank b
      WHERE b.user.id = :userId
      """)
  Iterable<Bank> findAllByUserId(String userId);

  @Query("""
      SELECT b
      FROM Bank b
      WHERE b.id = :id
      AND b.user.id = :userId
      """)
  Optional<Bank> findByUserId(Long id, String userId);
}
