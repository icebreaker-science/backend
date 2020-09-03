package science.icebreaker.wiki;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
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
    @Order(1)
    void addWikiPage_success() {
        WikiPage wikiPage = new WikiPage(WikiPage.PageType.DEVICE, "title", "description", null);
        int id = wikiController.addWikiPage(wikiPage);
        wikiPage.setId(id);
        WikiPage savedPage = wikiController.getWikiPages(WikiPage.PageType.DEVICE).get(0);
        assertThat(wikiPage).isEqualTo(savedPage);
    }

    @Test
    @Order(2)
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
