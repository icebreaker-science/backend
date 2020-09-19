package science.icebreaker.wiki;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class WikiPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // readOnly used to get the ui right
    @ApiModelProperty(accessMode = ApiModelProperty.AccessMode.READ_ONLY, readOnly = true)
    @Nullable
    private int id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private PageType type;

    @NotNull
    @NotBlank
    private String title;

    @NotNull
    @NotBlank
    private String description;

    @Nullable
    @Column(name = "reference") // references is a reserved key word
    private String references;

    public WikiPage() {
    }

    public WikiPage(
        @NotNull PageType type,
        @NotNull @NotBlank String title,
        @NotNull @NotBlank String description,
        @Nullable String references
    ) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.references = references;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public PageType getType() {
        return type;
    }

    public void setType(PageType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Nullable
    public String getReferences() {
        return references;
    }

    public void setReferences(@Nullable String references) {
        this.references = references;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WikiPage wikiPage = (WikiPage) o;
        return id == wikiPage.id
                &&
                type == wikiPage.type
                &&
                title.equals(wikiPage.title)
                &&
                description.equals(wikiPage.description)
                &&
                Objects.equals(references, wikiPage.references);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, title, description, references);
    }

    public enum PageType {
        @JsonProperty("device")
        DEVICE
    }

    // Allow request parameters to be lower-case
    @Component
    static class PageTypeConverter implements Converter<String, PageType> {

        @Override
        public PageType convert(String source) {
            return PageType.valueOf(source.toUpperCase());
        }
    }
}
