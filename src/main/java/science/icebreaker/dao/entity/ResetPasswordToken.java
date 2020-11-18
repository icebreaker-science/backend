package science.icebreaker.dao.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class ResetPasswordToken {
    @Id
    private String token;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private LocalDateTime createdAt;

    public ResetPasswordToken() { }

    public ResetPasswordToken(String token, Account account) {
        this(token, account, LocalDateTime.now());
    }

    public ResetPasswordToken(String token, Account account, LocalDateTime createdAt) {
        this.token = token;
        this.account = account;
        this.createdAt = createdAt;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
