package farm.giggle.yt2rss.exceptions;

import jakarta.xml.bind.JAXBException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class JaxbMarshallerException extends Throwable {
    public JaxbMarshallerException(JAXBException e) {
        super(e);
    }
}
