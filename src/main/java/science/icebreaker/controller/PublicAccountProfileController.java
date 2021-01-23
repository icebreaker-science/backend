package science.icebreaker.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import science.icebreaker.dao.entity.AccountProfile;
import science.icebreaker.dao.repository.AccountProfileRepository;
import science.icebreaker.data.response.GetPublicAccountProfileResponse;


@RestController
@RequestMapping("/profile")
public class PublicAccountProfileController {

    private final AccountProfileRepository accountProfileRepository;


    public PublicAccountProfileController(
            AccountProfileRepository accountProfileRepository
    ) {

        this.accountProfileRepository = accountProfileRepository;
    }


    @GetMapping("/{id}")
    @ApiOperation("Get a profile by ID")
    public GetPublicAccountProfileResponse getWikiPage(@PathVariable Integer id) {
        AccountProfile profile = this.accountProfileRepository.getOne(id);
        return new GetPublicAccountProfileResponse()
                .setFullName(profile.getFullName());
    }

}
