package farm.giggle.yt2rss.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends Throwable {
    public UserNotFoundException(Long userId) {
        super("User " + userId + "not found");
    }
}
