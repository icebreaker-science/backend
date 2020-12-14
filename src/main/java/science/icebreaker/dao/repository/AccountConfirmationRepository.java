package science.icebreaker.dao.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import science.icebreaker.dao.entity.AccountConfirmation;
import science.icebreaker.dao.entity.Account;

public interface AccountConfirmationRepository extends JpaRepository<AccountConfirmation, Integer> {
    AccountConfirmation findAccountConfirmationByConfirmationToken(String token);
    Optional<AccountConfirmation> findAccountConfirmationByAccount(Account account);
    void deleteAccountConfirmationByConfirmationToken(String token);
}
