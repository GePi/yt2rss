package farm.giggle.yt2rss.controllers;

import farm.giggle.yt2rss.config.security.userservice.MixUserManagement;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class WelcomeController {

    @GetMapping
    public String welcomePage(@AuthenticationPrincipal MixUserManagement principal,
                              @RequestParam(value = "invite", required = false) String inviteCode,
                              Model model,
                              HttpSession httpSession) {
        if ((principal == null) || (principal.getUser() == null)) {
            model.addAttribute("user_uuid", "");
        } else {
            model.addAttribute("user_uuid", principal.getUser().getUuid());
        }
        if (inviteCode != null) {
            httpSession.setAttribute("invite_code", inviteCode);
        }
        model.addAttribute("invite_code",
                httpSession.getAttribute("invite_code") == null ? "" : httpSession.getAttribute("invite_code"));
        return "welcome";
    }

    @PostMapping(path = "/setinvitationcode")
    public String fillInvitePostHandler(@RequestParam("invite") String inviteCode,
                                        @AuthenticationPrincipal MixUserManagement principal,
                                        HttpSession httpSession,
                                        Model model) {
        httpSession.setAttribute("invite_code", inviteCode);
        return welcomePage(principal, "", model, httpSession);
    }
}