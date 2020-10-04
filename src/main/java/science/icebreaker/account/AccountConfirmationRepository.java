package science.icebreaker.account;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountConfirmationRepository extends JpaRepository<AccountConfirmation, Integer> {
    AccountConfirmation findAccountConfirmationByConfirmationToken(String token);
}
