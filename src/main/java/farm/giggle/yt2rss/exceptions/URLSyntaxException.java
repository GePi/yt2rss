package farm.giggle.yt2rss.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class URLSyntaxException extends RuntimeException{
    public URLSyntaxException(String message) {
        super(message);
    }
}
