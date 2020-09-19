package science.icebreaker.account;


import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;


@Entity
public class AccountProfile {

    @Id
    @Nullable
    private Integer accountId;

    private String title = "";

    private String forename = "";

    private String surname = "";

    private String institution = "";

    private String city = "";

    private String researchArea = "";


    @Nullable
    public Integer getAccountId() {
        return accountId;
    }


    public void setAccountId(@Nullable Integer accountId) {
        this.accountId = accountId;
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


    public String getInstitution() {
        return institution;
    }


    public void setInstitution(String institution) {
        this.institution = institution;
    }


    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }


    public String getResearchArea() {
        return researchArea;
    }


    public void setResearchArea(String researchArea) {
        this.researchArea = researchArea;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AccountProfile that = (AccountProfile) o;
        return title.equals(that.title)
                &&
                forename.equals(that.forename)
                &&
                surname.equals(that.surname)
                &&
                institution.equals(that.institution)
                &&
                city.equals(that.city)
                &&
                researchArea.equals(that.researchArea);
    }


    @Override
    public int hashCode() {
        return Objects.hash(title, forename, surname, institution, city, researchArea);
    }
}
