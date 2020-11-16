package science.icebreaker.dao.entity;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class AccountRole implements GrantedAuthority {

    @Id
    private String email;

    private String role;


    public AccountRole() {
    }


    public AccountRole(String email, String role) {
        this.email = email;
        this.role = role;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    @Override
    public String getAuthority() {
        return role;
    }
}
