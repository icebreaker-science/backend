package science.icebreaker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/")
public class WelcomeController {

    @GetMapping("")
    public String index() {
        return "{\"message\": \"Icebreaker up and running!\"}";
    }

}
