package farm.giggle.yt2rss.api;

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

@Slf4j
@Controller
@RequestMapping("/api/v1/rss")
public class RssProducerController {
    RssProducerServ rssProducerServ;

    public RssProducerController(RssProducerServ rssProducerServ) {
        this.rssProducerServ = rssProducerServ;
    }

    @GetMapping(value = {"/channel/{channelId}", "/channel/{channelId}/{timeInterval}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getChannelRss(@PathVariable("channelId") Long channelId,
                                                @PathVariable(value = "timeInterval", required = false) RssTimeIntervalEnum timeInterval) throws JaxbMarshallerException, ResourceNotFoundException {
        String xml = rssProducerServ.getChannelRss(channelId, timeInterval);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<>(xml, headers, HttpStatus.OK);
    }

    @GetMapping(value = {"/user/{userId}", "/channel/{userId}/{timeInterval}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getUserAllFilesRss(@PathVariable("userId") Long userId,
                                                     @PathVariable(value = "timeInterval", required = false) RssTimeIntervalEnum timeInterval) throws JaxbMarshallerException, UserNotFoundException {
        String xml = rssProducerServ.getUserRss(userId, timeInterval);
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
