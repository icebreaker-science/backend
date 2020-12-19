package science.icebreaker.wiki;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import science.icebreaker.controller.WikiController;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.exception.EntryNotFoundException;
import science.icebreaker.exception.IllegalRequestParameterException;

import javax.validation.ConstraintViolationException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@SuppressWarnings("ConstantConditions")
class WikiControllerTest {

    private final WikiController wikiController;

    @Autowired
    public WikiControllerTest(WikiController wikiController) {
        this.wikiController = wikiController;
    }

    @Test
    void addWikiPage_success() throws EntryNotFoundException {
        WikiPage wikiPage = new WikiPage(WikiPage.PageType.DEVICE, "title", "description", null);
        int id = wikiController.addWikiPage(wikiPage, null);
        wikiPage.setId(id);
        WikiPage savedPage = wikiController.getWikiPage(id);
        assertThat(wikiPage).isEqualTo(savedPage);
    }

    @Test
    void addWikiPage_invalidInput_failure() {
        //empty title
        assertThatThrownBy(() -> {
            WikiPage fail1 = new WikiPage(WikiPage.PageType.DEVICE, "", "description", null);
            wikiController.addWikiPage(fail1, null);
        }).isInstanceOf(ConstraintViolationException.class);

        //empty description
        assertThatThrownBy(() -> {
            WikiPage fail1 = new WikiPage(WikiPage.PageType.DEVICE, "title", "", null);
            wikiController.addWikiPage(fail1, null);
        }).isInstanceOf(ConstraintViolationException.class);

        // non-existing network keyword
        assertThatThrownBy(() -> {
            WikiPage fail1 = new WikiPage(WikiPage.PageType.DEVICE, "title", "", null,
                    Arrays.asList("thisisnotarealkeyword"), null);
            wikiController.addWikiPage(fail1, null);
        }).isInstanceOf(IllegalRequestParameterException.class);
    }
}
