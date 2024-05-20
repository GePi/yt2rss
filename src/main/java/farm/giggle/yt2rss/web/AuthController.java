package farm.giggle.yt2rss.web;

import farm.giggle.yt2rss.config.security.MixUserManagement;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {
    @GetMapping("/oauth2-login")
    public String authPage(@AuthenticationPrincipal MixUserManagement principal,
                           Model model) {
        if ((principal == null) || (principal.getUser() == null)) {
            return "oauth2-login";
        } else {
            return "redirect:/";
        }
    }
}