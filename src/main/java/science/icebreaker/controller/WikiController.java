package science.icebreaker.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import science.icebreaker.dao.entity.Account;
import science.icebreaker.dao.entity.Media;
import science.icebreaker.dao.entity.WikiPage;
import science.icebreaker.dao.repository.WikiPageRepository;
import science.icebreaker.data.network.Node;
import science.icebreaker.data.request.EditWikiPageRequest;
import science.icebreaker.exception.EntryNotFoundException;
import science.icebreaker.exception.ErrorCodeEnum;
import science.icebreaker.exception.IllegalRequestParameterException;
import science.icebreaker.service.MediaService;
import science.icebreaker.service.NetworkService;
import science.icebreaker.service.WikiPageService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class WikiController {

    private final WikiPageRepository wikiPageRepository;
    private final MediaService mediaService;
    private final NetworkService networkService;
    private final WikiPageService wikiPageService;

    public WikiController(
            WikiPageRepository wikiPageRepository,
            MediaService mediaService,
            NetworkService networkService,
            WikiPageService wikiPageService
    ) {
        this.wikiPageRepository = wikiPageRepository;
        this.mediaService = mediaService;
        this.networkService = networkService;
        this.wikiPageService = wikiPageService;
    }

    @PostMapping("/wiki")
    @ApiOperation("Add a new wiki page.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ID of the new device/wiki page"),
            @ApiResponse(code = 400, message = "Parameters not valid")})
    public int addWikiPage(
        @ModelAttribute @Valid WikiPage wikiPage,
        @RequestParam(required = false) MultipartFile image
    )
    throws IllegalRequestParameterException {
        // prevent modification of existing wiki pages
        if (wikiPage.getId() != 0) {
            throw new IllegalRequestParameterException()
                    .withErrorCode(ErrorCodeEnum.ERR_WIKI_001);
        }

        // Check if the referenced keywords exist
        Set<String> existingKeywords
                = networkService.getAllKeywordNodes().stream().map(Node::getName).collect(Collectors.toSet());
        if (wikiPage.getNetworkKeywords() != null) {
            List<String> nonExistingKeywords = new ArrayList<>();
            for (String networkKeyword : wikiPage.getNetworkKeywords()) {
                if (!existingKeywords.contains(networkKeyword)) {
                    nonExistingKeywords.add(networkKeyword);
                }
            }
            if (!nonExistingKeywords.isEmpty()) {
                throw new IllegalRequestParameterException()
                        .withErrorCode(ErrorCodeEnum.ERR_WIKI_003)
                        .withArgs(String.join(",", nonExistingKeywords));
            }
        }

        if (image != null) {
            Media imageMedia = mediaService.addMedia(image);
            wikiPage.setMedia(imageMedia);
        }

        WikiPage res = wikiPageRepository.save(wikiPage);
        return res.getId();
    }

    @GetMapping("/wiki")
    @ApiOperation("Get all wiki pages by type.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "a list of devices/wiki pages"),
            @ApiResponse(code = 400, message = "Parameter not valid")})
    public List<WikiPage> getWikiPages(@RequestParam WikiPage.PageType type) {
        return wikiPageRepository.findAllByType(type);
    }

    @GetMapping("/wiki/{id}")
    @ApiOperation("Get a wiki page by ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "The wiki page entry"),
        @ApiResponse(code = 404, message = "Wiki page entry not found")
    })
    public WikiPage getWikiPage(@PathVariable Integer id) throws EntryNotFoundException {
        Optional<WikiPage> wikiPage = wikiPageRepository.findById(id);
        if (wikiPage.isPresent()) {
            return wikiPage.get();
        } else {
            throw new EntryNotFoundException()
                .withErrorCode(ErrorCodeEnum.ERR_WIKI_002)
                .withArgs(id);
        }
    }

    @PutMapping("/wiki/{id}")
    @ApiOperation("Edit wiki page")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Entry edited"),
        @ApiResponse(code = 404, message = "Wiki page entry not found")
    })
    public void editWikiPage(
        @PathVariable Integer id,
        @RequestBody @Valid EditWikiPageRequest editWikiPageRequest,
        Principal principal
    ) {
        Account account = (Account) ((Authentication) principal).getPrincipal();
        this.wikiPageService.editWikiPage(id, editWikiPageRequest, account);
    }
}
