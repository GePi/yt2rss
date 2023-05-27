package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.exceptions.UserNotFoundException;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl {
    private UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public User getUserById(Long userId) throws UserNotFoundException {
        return userRepo.findById(userId).orElseThrow( ()->  new UserNotFoundException("User " + userId + "not found") );
    }
}

