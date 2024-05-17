package farm.giggle.yt2rss.web;

import farm.giggle.yt2rss.config.ApplicationConfig;
import farm.giggle.yt2rss.config.HasRightToUsers;
import farm.giggle.yt2rss.config.security.MixUserManagement;
import farm.giggle.yt2rss.exceptions.UserNotFoundException;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.serv.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
@RequestMapping("/users")
public class UserController {
    final UserService userService;
    final ApplicationConfig applicationConfig;

    public UserController(UserService userService, ApplicationConfig applicationConfig) {
        this.userService = userService;
        this.applicationConfig = applicationConfig;
    }

    @GetMapping("/imadmin")
    public String becomeAdmin(@RequestParam("admin_key") String adminKey,
                              @AuthenticationPrincipal OAuth2User principal) throws UserNotFoundException {
        if (principal == null) {
            return "redirect:/";
        }
        // /users/imadmin/FFF-HELN-2875-HlLNH-0983L-NLHTY
        String systemVariableAdminKey = System.getenv("ADMIN_KEY");
        //systemVariableAdminKey = "FFF-HELN-2875-HlLNH-0983L-NLHTY"; //TODO System.getenv("ADMIN_KEY");
        if (adminKey.equals(systemVariableAdminKey)) {
            Long id = ((MixUserManagement) principal).getUser().getId();
            userService.becomeAdmin(id);
        }
        return "redirect:/";
    }

    @GetMapping
    @HasRightToUsers
    public String usersPage(@RequestParam(value = "page", required = false) Integer page,
                            Model model) {
        Integer pageNum = Objects.requireNonNullElse(page, 1) - 1;
        Page<User> userPage = userService.getUserPage(pageNum, applicationConfig.getChannelPage().getNumberEntriesOnPage());

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", userPage.getTotalPages());
        return "users";
    }


}
