package science.icebreaker.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import science.icebreaker.dao.entity.Account;


public interface AccountRepository extends JpaRepository<Account, Integer> {

    Account findAccountByEmail(String email);

}
