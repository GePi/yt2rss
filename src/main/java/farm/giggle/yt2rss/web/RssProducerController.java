package farm.giggle.yt2rss.web;

import farm.giggle.yt2rss.api.RssTimeIntervalEnum;
import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.exceptions.UserNotFoundException;
import farm.giggle.yt2rss.serv.RssProducerServ;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/feeds")
public class RssProducerController {
    RssProducerServ rssProducerServ;

    public RssProducerController(RssProducerServ rssProducerServ) {
        this.rssProducerServ = rssProducerServ;
    }

    @GetMapping(value = {"/channel/{channelUUID}", "/channel/{channelUUID}/{timeInterval}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getChannelRss(@PathVariable("channelUUID") UUID channelUUID,
                                                @PathVariable(value = "timeInterval", required = false) RssTimeIntervalEnum timeInterval) throws JaxbMarshallerException, ResourceNotFoundException {
        String xml = rssProducerServ.getChannelRss(channelUUID, timeInterval);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<>(xml, headers, HttpStatus.OK);
    }

    @GetMapping(value = {"/user/{userUUID}", "/user/{userUUID}/{timeInterval}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getUserAllFilesRss(@PathVariable("userUUID") UUID userUUID,
                                                     @PathVariable(value = "timeInterval", required = false) RssTimeIntervalEnum timeInterval) throws JaxbMarshallerException, UserNotFoundException {
        String xml = rssProducerServ.getUserRss(userUUID, timeInterval);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<>(xml, headers, HttpStatus.OK);
    }

    @ExceptionHandler
    @ResponseBody
    private ResponseEntity<String> handleException(HttpServletRequest req, Exception e, HttpServletResponse res) {
        log.error("RssProducerError", e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<>(rssProducerServ.getErrorRss(e), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
