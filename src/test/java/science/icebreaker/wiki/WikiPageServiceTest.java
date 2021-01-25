package science.icebreaker.wiki;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.repository.WikiPageRepository;
import science.icebreaker.data.request.EditWikiPageRequest;
import science.icebreaker.service.AccountService;
import science.icebreaker.service.WikiPageService;
import science.icebreaker.util.TestHelper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@ActiveProfiles("test")
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
@SuppressWarnings("ConstantConditions")
public class WikiPageServiceTest {
    private final WikiPageService wikiPageService;
    private final WikiPageRepository wikiPageRepository;
    private final AccountService accountService;
    private final TestHelper testHelper;

    @Autowired
    public WikiPageServiceTest(
        WikiPageService wikiPageService,
        WikiPageRepository wikiPageRepository,
        AccountService accountService,
        TestHelper testHelper
    ) {
        this.wikiPageService = wikiPageService;
        this.wikiPageRepository = wikiPageRepository;
        this.accountService = accountService;
        this.testHelper = testHelper;
    }

    @Test
    public void saveDeviceAvailability_success() throws Exception {
        Account account = testHelper.createAccount();
        WikiPage wikiPage = testHelper.createWikiPage();
        EditWikiPageRequest request = new EditWikiPageRequest("NEW title", "NEW Description", "NEW References");

        this.wikiPageService.editWikiPage(wikiPage.getId(), request, account);

        Optional<WikiPage> updatedWikiPageOpt = wikiPageRepository.findById(wikiPage.getId());
        assertThat(updatedWikiPageOpt).isPresent();

        WikiPage updatedWikiPage = updatedWikiPageOpt.get();

        assertThat(updatedWikiPage.getTitle()).isEqualTo(request.getTitle());
        assertThat(updatedWikiPage.getDescription()).isEqualTo(request.getDescription());
        assertThat(updatedWikiPage.getReferences()).isEqualTo(request.getReferences());

        assertThat(updatedWikiPage.getLastAlteredBy()).isEqualTo(account);

        AccountProfile profile = this.accountService.getAccountProfile(account.getId());
        assertThat(updatedWikiPage.getLastAlteredByName()).isEqualTo(profile.getFullName());
    }
}
