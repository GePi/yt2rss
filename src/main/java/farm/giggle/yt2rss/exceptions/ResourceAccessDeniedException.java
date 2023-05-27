package farm.giggle.yt2rss.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ResourceAccessDeniedException extends Exception {
    public ResourceAccessDeniedException(String message) {
        super(message);
    }
}
