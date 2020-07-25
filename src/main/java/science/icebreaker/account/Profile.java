package science.icebreaker.account;


import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Profile {

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
}
