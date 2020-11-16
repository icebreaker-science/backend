package science.icebreaker.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import science.icebreaker.dao.entity.AccountConfirmation;

public interface AccountConfirmationRepository extends JpaRepository<AccountConfirmation, Integer> {
    AccountConfirmation findAccountConfirmationByConfirmationToken(String token);
}
