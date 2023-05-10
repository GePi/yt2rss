package farm.giggle.yt2rss.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WelcomeController {
    @GetMapping
    public String welcomePage() {
        return "welcome";
    }
}
