package farm.giggle.yt2rss.controllers;

import farm.giggle.yt2rss.config.security.MixUserManagement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class WelcomeController {
    @GetMapping
    public String welcomePage(@AuthenticationPrincipal MixUserManagement principal,
                              Model model) {
        if ((principal == null) || (principal.getUser() == null)) {
            model.addAttribute("user_uuid", "");
        } else {
            model.addAttribute("user_uuid", principal.getUser().getUuid());
        }
        return "welcome";
    }
}