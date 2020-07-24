package science.icebreaker.account;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {

    private final AccountRepository repository;

    AccountController(AccountRepository repository){
        this.repository = repository;
    }

    @GetMapping("/")
    public String index() {
        return "Icebreaker up and running!";
    }

    @GetMapping("/register")
    List<Account> all() {
        return repository.findAll();
    }

    @PostMapping("/register")
    //todo Account validation if it already exists
    //todo groß und kleinschreibung
    Account newAccount(@RequestBody Account newAccount) {
        return repository.save(newAccount);
    }

}

