package science.icebreaker.media;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String mimeType;

    private String originalName;

    public Media() {
    }

    public Media(
        String mimeType,
        String originalName
    ) {
        this(null, mimeType, originalName);
    }

    public Media(
        Integer id,
        String mimeType,
        String originalName
    ) {
        this.id = id;
        this.mimeType = mimeType;
        this.originalName = originalName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

}
