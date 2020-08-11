package science.icebreaker.wiki;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class WikiController {

    private final WikiPageRepository wikiPageRepository;

    public WikiController(WikiPageRepository wikiPageRepository) {
        this.wikiPageRepository = wikiPageRepository;
    }

    @PostMapping("/wiki")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "ID of the new device/wiki page"),
            @ApiResponse(code = 400, message = "Parameters not valid")})
    public int addWikiPage(@RequestBody @Valid WikiPage wikiPage) {
        WikiPage res = wikiPageRepository.save(wikiPage);
        return res.getId();
    }
}



