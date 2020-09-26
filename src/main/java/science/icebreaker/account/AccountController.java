package science.icebreaker.account;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import science.icebreaker.exception.AccountCreationException;
import science.icebreaker.exception.AccountNotFoundException;

import java.security.Principal;


@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService service;


    public AccountController(AccountService service) {
        this.service = service;
    }


    @PostMapping("/register")
    public int register(@RequestBody RegistrationRequest registrationRequest) throws AccountCreationException {
        return service.createAccount(registrationRequest);
    }


    @PostMapping("/login")
    public String login(@RequestBody Account account) {
        return service.login(account);
    }


    @GetMapping("/my-profile")
    public AccountProfile getMyProfile(Principal principal) throws AccountNotFoundException {
        Account account = (Account) ((Authentication) principal).getPrincipal();
        return service.getAccountProfile(account.getId());
    }

}
