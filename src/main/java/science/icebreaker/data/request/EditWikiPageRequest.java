package science.icebreaker.data.request;

import javax.validation.constraints.NotNull;

public class EditWikiPageRequest {

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String references;


    public EditWikiPageRequest() { }
    public EditWikiPageRequest(
        String title,
        String description,
        String references
    ) {
        this.title = title;
        this.description = description;
        this.references = references;
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
    public String getReferences() {
        return references;
    }
    public void setReferences(String references) {
        this.references = references;
    }

}
