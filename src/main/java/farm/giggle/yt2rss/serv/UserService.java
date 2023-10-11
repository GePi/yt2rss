package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.exceptions.UserNotFoundException;
import farm.giggle.yt2rss.model.Role;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.model.UserRole;
import farm.giggle.yt2rss.model.repo.UserRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class UserService {
    final private UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getUserById(Long userId) {
        return userRepo.findById(userId).orElseThrow(() -> new RuntimeException(new UserNotFoundException(userId)));
    }

    public Page<User> getUserPage(Integer pageNum, Integer entriesOnPage) {
        return userRepo.findAll(PageRequest.of(pageNum, entriesOnPage));
    }

    public void becomeAdmin(Long userId) throws UserNotFoundException {
        User user = userRepo.getUserById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if (!hasAdminRole(user)) {
            user.addRole(userRepo.getRoleByName(Role.RoleEnum.ADMIN));
        }
    }

    public User getUserByUUID(UUID userUUID) {
        return userRepo.findByUuid(userUUID);
    }

    public boolean hasAdminRole(User user) {
        for (UserRole userRole : user.getUserRoles()) {
            if (userRole.getRole().getRoleName().equals(Role.RoleEnum.ADMIN)) {
                return true;
            }
        }
        return false;
    }
}

