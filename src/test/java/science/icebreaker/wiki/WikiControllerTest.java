package science.icebreaker.wiki;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class WikiControllerTest {

    private final WikiController wikiController;
    private final WikiPageRepository wikiPageRepository;

    @Autowired
    public WikiControllerTest(WikiController wikiController, WikiPageRepository wikiPageRepository) {
        this.wikiController = wikiController;
        this.wikiPageRepository = wikiPageRepository;
    }

    @Test
    void addWikiPage_success() {
        WikiPage wikiPage = new WikiPage(WikiPage.PageType.DEVICE, "title", "description", null);
        int id = wikiController.addWikiPage(wikiPage);
        wikiPage.setId(id);
        WikiPage savedPage = wikiPageRepository.findById(id).get();
        assertThat(wikiPage).isEqualTo(savedPage);
    }

    @Test
    void addWikiPage_invalidInput_failure() {
        //empty title
        assertThatThrownBy(() -> {
            WikiPage fail1 = new WikiPage(WikiPage.PageType.DEVICE, "", "description", null);
            wikiController.addWikiPage(fail1);
        }).isInstanceOf(ConstraintViolationException.class);

        //empty description
        assertThatThrownBy(() -> {
            WikiPage fail1 = new WikiPage(WikiPage.PageType.DEVICE, "title", "", null);
            wikiController.addWikiPage(fail1);
        }).isInstanceOf(ConstraintViolationException.class);
    }
}