package science.icebreaker.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.ResetPasswordToken;

public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, String> {
    void deleteByAccount(Account account);
}
