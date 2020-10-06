package science.icebreaker.account;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.lang.Nullable;

import java.security.Principal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Account implements Principal {

    @Id
    @Nullable
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;

    private Boolean isEnabled;


    public Account() {
    }



    public Account(@Nullable Integer id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }


    @Nullable
    public Integer getId() {
        return id;
    }


    public void setId(@Nullable Integer id) {
        this.id = id;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account account = (Account) o;
        return Objects.equals(id, account.id)
                && email.equals(account.email)
                && password.equals(account.password);
    }


    @Override
    public int hashCode() {
        return Objects.hash(id, email, password);
    }


    @Override
    @ApiModelProperty(hidden = true)
    public String getName() {
        return email;
    }
}
