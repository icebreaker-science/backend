package science.icebreaker.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Paper {

    /**
     * This field is called icebreakerId to distinguish
     * from the original core id (ID in the core dataset).
     */
    @Id
    private int icebreakerId;

    private String doi;

    private String title;

    private int year;


    public int getIcebreakerId() {
        return icebreakerId;
    }


    public Paper setIcebreakerId(int icebreakerId) {
        this.icebreakerId = icebreakerId;
        return this;
    }


    public String getDoi() {
        return doi;
    }


    public Paper setDoi(String doi) {
        this.doi = doi;
        return this;
    }


    public String getTitle() {
        return title;
    }


    public Paper setTitle(String title) {
        this.title = title;
        return this;
    }


    public int getYear() {
        return year;
    }


    public Paper setYear(int year) {
        this.year = year;
        return this;
    }
}
