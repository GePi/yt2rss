package farm.giggle.yt2rss.exceptions;

public class UserCannotBeRegisteredException extends RuntimeException {
    public UserCannotBeRegisteredException(String message) {
        super(message);
    }
}
