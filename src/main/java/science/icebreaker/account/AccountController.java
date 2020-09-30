package science.icebreaker.account;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


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

    @PostMapping("/validate-email")
    public ResponseEntity<Object> confirmAccount(@RequestParam(name = "key") String confirmationToken) {
        service.confirmAccount(confirmationToken);

        Map<String, String> body = new HashMap<>();
        body.put("message", "Account verified");

        return ResponseEntity.ok(body);
    }

    @PostMapping("/resend-confirmation-email")
    public ResponseEntity<Object> resendConfirmationEmail(@RequestParam String email) {
        service.resendConfirmationEmail(email);

        Map<String, String> body = new HashMap<>();
        body.put("message", "Account confirmation email sent");

        return ResponseEntity.ok(body);
    }

}
