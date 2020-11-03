package science.icebreaker.wiki;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import science.icebreaker.exception.ErrorCodeEnum;
import science.icebreaker.exception.IllegalRequestParameterException;

import science.icebreaker.exception.EntryNotFoundException;
import science.icebreaker.media.Media;
import science.icebreaker.media.MediaService;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
public class WikiController {

    private final WikiPageRepository wikiPageRepository;
    private final MediaService mediaService;

    public WikiController(WikiPageRepository wikiPageRepository, MediaService mediaService) {
        this.wikiPageRepository = wikiPageRepository;
        this.mediaService = mediaService;
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
}



