package science.icebreaker.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.repository.WikiPageRepository;
import science.icebreaker.data.request.EditWikiPageRequest;
import science.icebreaker.exception.EntryNotFoundException;

@Service
public class WikiPageService {

    private WikiPageRepository wikiPageRepository;
    private AccountService accountService;

    public WikiPageService(
        WikiPageRepository wikiPageRepository,
        AccountService accountService
    ) {
        this.wikiPageRepository = wikiPageRepository;
        this.accountService = accountService;
    }

    /**
     * Edits the wiki page
     * @param wikiPageId The id of the wiki page to edit
     * @param editWikiPageRequest The data to edit the wiki page with
     * @param editor The user who edits the wiki page
     * @throws EntryNotFoundException no wiki page exists with the given id
     */
    @Transactional
    public void editWikiPage(
        Integer wikiPageId,
        EditWikiPageRequest editWikiPageRequest,
        Account editor
    ) {
        WikiPage wikiPage = this.wikiPageRepository.findById(wikiPageId).orElseThrow(EntryNotFoundException::new);

        AccountProfile editorProfile = accountService.getAccountProfile(editor.getId());
        wikiPage.setLastAlteredByName(editorProfile.getFullName());
        wikiPage.setLastAlteredBy(editor);

        wikiPage.setTitle(editWikiPageRequest.getTitle());
        wikiPage.setDescription(editWikiPageRequest.getDescription());
        wikiPage.setReferences(editWikiPageRequest.getReferences());
        wikiPageRepository.save(wikiPage);
    }
}
