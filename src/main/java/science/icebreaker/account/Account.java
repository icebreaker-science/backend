package science.icebreaker.account;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;


@Entity
class Account {

    private @Id @GeneratedValue Long id;

    private String title;
    private String forename;
    private String surname;
    private String email;
    private String password;
    private String institution;
    private String city;
    private String researchArea;

    public Account() {}

    public Account(String title, String forename, String surname, String email, String password, String institution, String city, String researchArea) {
        this.title = title;
        this.forename = forename;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.institution = institution;
        this.researchArea = researchArea;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getResearchArea() {
        return researchArea;
    }

    public void setResearchArea(String researchArea) {
        this.researchArea = researchArea;
    }

    public String getCity(){return city;}

    public void setCity(String city) {this.city = city;}

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Account))
            return false;
        Account account = (Account) o;
        return Objects.equals(this.id, account.id) && Objects.equals(this.email, account.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.title, this.forename, this.surname, this.email, this.password, this.institution, this.city, this.researchArea);
    }

    @Override
    public String toString() {
        return "User{" + "id=" + this.id + ", name='" + this.forename + " " + this.surname + '\'' + '}';
    }
}
