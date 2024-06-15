package farm.giggle.yt2rss.controllers;

import farm.giggle.yt2rss.config.ApplicationConfig;
import farm.giggle.yt2rss.config.HasRightToUser;
import farm.giggle.yt2rss.config.HasRightToUsers;
import farm.giggle.yt2rss.config.security.userservice.MixUserManagement;
import farm.giggle.yt2rss.exceptions.UserNotFoundException;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.services.InviteService;
import farm.giggle.yt2rss.services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
@RequestMapping("/users")
public class UsersController {
    final UserService userService;
    final ApplicationConfig applicationConfig;
    private final InviteService inviteService;

    @Value("${ADDING_ADMINISTRATOR_RIGHTS_KEY}")
    String systemVariableAdminKey;

    public UsersController(UserService userService, ApplicationConfig applicationConfig, InviteService inviteService) {
        this.userService = userService;
        this.applicationConfig = applicationConfig;
        this.inviteService = inviteService;
    }

    @GetMapping("/imadmin")
    public String becomeAdmin(@RequestParam("admin_key") String adminKey,
                              @AuthenticationPrincipal OAuth2User principal) throws UserNotFoundException {
        if (principal == null || systemVariableAdminKey == null || systemVariableAdminKey.isBlank()) {
            return "redirect:/";
        }
        if (adminKey.equals(systemVariableAdminKey)) {
            Long id = ((MixUserManagement) principal).getUser().getId();
            userService.becomeAdmin(id);
        }
        return "redirect:/";
    }

    @PostMapping("/delete")
    @HasRightToUser
    public String delete(@RequestParam("userId") Long userId) {
        userService.deleteUser(userId);
        return "redirect:/users";
    }

    @GetMapping
    @HasRightToUsers
    public String usersPage(@RequestParam(value = "page", required = false) Integer page,
                            Model model) {
        Integer pageNum = Objects.requireNonNullElse(page, 1) - 1;
        Page<User> userPage = userService.getUserPage(pageNum, applicationConfig.getChannelPage().getNumberEntriesOnPage());

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("totalPages", Math.max(userPage.getTotalPages(), 1));
        return "users";
    }

    @GetMapping(path = "add_invite")
    @HasRightToUsers
    public String addInvite() {
        return "invites/addinviteform";
    }

    @PostMapping(path = "add_invite")
    @HasRightToUsers
    public String addInvitePostHandler(@RequestParam("invite") String inviteCode,
                                       Model model) {
        String message;
        if (inviteCode.isBlank()) {
            message = "Пустой код приглашения добавить нельзя!";
        } else {
            inviteService.addInvite(inviteCode);
            message = "Приглашение добавлено, вы можете скопировать ссылку с приглашением отправив её пользователю! "+
            "https://y2rss.ru?invite=" + inviteCode;
        }
        model.addAttribute("inviteMessage",message);
        return "invites/invitemessage";
    }
}
