package farm.giggle.yt2rss.controllers;

import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.exportfeeds.FeedProducer;
import farm.giggle.yt2rss.exportfeeds.FeedsTimeIntervalEnum;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/rss")
public class RssProducerController {
    FeedProducer feedProducer;

    public RssProducerController(@Qualifier("RssFeedProducer") FeedProducer feedProducer) {
        this.feedProducer = feedProducer;
    }

    @GetMapping(value = {"/channel/{channelUUID}", "/channel/{timeInterval}/{channelUUID}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getChannelFeed(@PathVariable("channelUUID") UUID channelUUID,
                                                 @PathVariable(value = "timeInterval", required = false) FeedsTimeIntervalEnum timeInterval,
                                                 HttpServletRequest request) throws JaxbMarshallerException, ResourceNotFoundException {
        return new ResponseEntity<>(
                feedProducer.getChannelFeed(channelUUID, timeInterval, request.getRequestURL().toString()),
                HttpStatus.OK);
    }

    @GetMapping(value = {"/user/{userUUID}", "/user/{timeInterval}/{userUUID}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getUserFeed(@PathVariable("userUUID") UUID userUUID,
                                              @PathVariable(value = "timeInterval", required = false) FeedsTimeIntervalEnum timeInterval,
                                              HttpServletRequest request) throws JaxbMarshallerException, ResourceNotFoundException {

        return new ResponseEntity<>(
                feedProducer.getUserFeed(userUUID, timeInterval, request.getRequestURL().toString()),
                HttpStatus.OK);
    }

    @ExceptionHandler
    @ResponseBody
    private ResponseEntity<String> handleException(HttpServletRequest request, Exception e) throws JaxbMarshallerException {
        log.error("RssProducerError {}", request.getRequestURL().toString(), e);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<>(
                feedProducer.getErrorFeed(e),
                headers,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
