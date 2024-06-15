package farm.giggle.yt2rss.services;

import farm.giggle.yt2rss.config.security.Auth2ProviderEnum;
import farm.giggle.yt2rss.exceptions.UserCannotBeRegisteredException;
import farm.giggle.yt2rss.exceptions.UserNotFoundException;
import farm.giggle.yt2rss.model.Role;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.model.UserRole;
import farm.giggle.yt2rss.model.repo.UserRepo;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserService {
    final private UserRepo userRepo;
    private final HttpSession httpSession;
    private final InviteService inviteService;

    public UserService(UserRepo userRepo, HttpSession httpSession, InviteService inviteService) {
        this.userRepo = userRepo;
        this.httpSession = httpSession;
        this.inviteService = inviteService;
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new RuntimeException(new UserNotFoundException(userId)));
    }

    public User findUserByOAuth2Id(Auth2ProviderEnum auth2ProviderEnum, String auth2Id) {
        return userRepo.findUserByAuth2ProviderAndAuth2Id(auth2ProviderEnum, auth2Id);
    }

    public Page<User> getUserPage(Integer pageNum, Integer entriesOnPage) {
        return userRepo.findAll(PageRequest.of(pageNum, entriesOnPage));
    }

    public void becomeAdmin(Long userId) throws UserNotFoundException {
        User user = userRepo.getUserById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (!hasAdminRole(user)) {
            user.addRole(userRepo.getRoleByName(Role.RoleEnum.USER_MANAGEMENT));
        }
    }

    public User getUserByUUID(UUID userUUID) {
        return userRepo.findByUuid(userUUID);
    }

    public boolean hasAdminRole(User user) {
        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRole().getRoleName().equals(Role.RoleEnum.USER_MANAGEMENT)) {
                return true;
            }
        }
        return false;
    }

    public User createOrdinaryUser(String name, Auth2ProviderEnum auth2Provider, String auth2Id, UUID uuid) {
        String invite_code = (String) httpSession.getAttribute("invite_code");
        if (invite_code == null || !inviteService.captureCode(invite_code)) {
            throw new UserCannotBeRegisteredException(
                    "Для регистрации необходимо приглашение. Код приглашения можно ввести на главной странице.");
        }

        Role ordinaryUser = userRepo.getRoleByName(Role.RoleEnum.ORDINARY_USER);
        User user = new User(name, auth2Provider, auth2Id, uuid);
        user.addRole(ordinaryUser);
        return userRepo.save(user);
    }

    public void deleteUser(Long userId) {
        if (userId == null || userId == 0) {
            return;
        }
        userRepo.deleteById(userId);
    }
}

