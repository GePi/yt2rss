package farm.giggle.yt2rss.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class Auth2ProviderNotSupportedException extends RuntimeException {
    public Auth2ProviderNotSupportedException(String providerName) {
        super(String.format("The OAuth2 provider %s is not supported", providerName));
    }
}



