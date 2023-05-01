package app.weehong.financeapi.repositories;

import app.weehong.financeapi.entities.Card;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends CrudRepository<Card, Long> {

  @Query("""
      SELECT c
      FROM Card c
      LEFT JOIN FETCH Bank b ON c.bank = b
      WHERE b.user.id = :userId
      """)
  Iterable<Card> findAllByUserId(String userId);

  @Query("""
      SELECT c
      FROM Card c
      LEFT JOIN FETCH Bank b ON c.bank = b
      WHERE c.id = :id
      AND b.user.id = :userId
      """)
  Optional<Card> findByUserId(Long id, String userId);
}
