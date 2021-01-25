package science.icebreaker.dao.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
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

    @NotNull
    @ElementCollection
    @CollectionTable(name = "wiki_page_network_keyword", joinColumns = @JoinColumn(name = "wiki_page_id"))
    @Column(name = "network_keyword")
    private List<String> networkKeywords;

    @OneToOne
    private Media media;

    @JoinColumn(name = "last_altered_by")
    @OneToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    private Account lastAlteredBy;

    // If an account is deleted, keep the name of the editor
    @Column(name = "last_altered_by_name")
    private String lastAlteredByName;

    public WikiPage() {
    }

    public WikiPage(
        @NotNull PageType type,
        @NotNull @NotBlank String title,
        @NotNull @NotBlank String description,
        @Nullable String references
    ) {
        this(type, title, description, references, new ArrayList<>(), null);
    }

    public WikiPage(
        @NotNull PageType type,
        @NotNull @NotBlank String title,
        @NotNull @NotBlank String description,
        @Nullable String references,
        @NotNull List<String> networkKeywords,
        @Nullable Media media
    ) {
        this.type = type;
        this.title = title;
        this.description = description;
        this.references = references;
        this.networkKeywords = networkKeywords;
        this.media = media;
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

    public List<String> getNetworkKeywords() {
        return networkKeywords;
    }

    public void setNetworkKeywords(List<String> networkKeywords) {
        this.networkKeywords = networkKeywords;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public Account getLastAlteredBy() {
        return lastAlteredBy;
    }

    public void setLastAlteredBy(Account lastAlteredBy) {
        this.lastAlteredBy = lastAlteredBy;
    }

    public String getLastAlteredByName() {
        return lastAlteredByName;
    }

    public void setLastAlteredByName(String lastAlteredByName) {
        this.lastAlteredByName = lastAlteredByName;
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
                && type == wikiPage.type
                && title.equals(wikiPage.title)
                && description.equals(wikiPage.description)
                && Objects.equals(references, wikiPage.references)
                && Objects.equals(networkKeywords, wikiPage.networkKeywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, title, description, references, networkKeywords);
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
