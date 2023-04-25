package app.weehong.financeapi.repositories;

import app.weehong.financeapi.entities.Bank;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankRepository extends CrudRepository<Bank, Long> {

}
