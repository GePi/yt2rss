package farm.giggle.yt2rss.controllers;

import farm.giggle.yt2rss.atom.structure.AtomFeedsTimeIntervalEnum;
import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.exceptions.UserNotFoundException;
import farm.giggle.yt2rss.atom.services.AtomFeedProducer;
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
@RequestMapping("/atom")
public class AtomProducerController {
    AtomFeedProducer atomFeedProducer;

    public AtomProducerController(AtomFeedProducer atomFeedProducer) {
        this.atomFeedProducer = atomFeedProducer;
    }

    @GetMapping(value = {"/channel/{channelUUID}", "/channel/{channelUUID}/{timeInterval}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getChannelFeed(@PathVariable("channelUUID") UUID channelUUID,
                                                 @PathVariable(value = "timeInterval", required = false) AtomFeedsTimeIntervalEnum timeInterval) throws JaxbMarshallerException, ResourceNotFoundException {
        String xml = atomFeedProducer.getAtomFeedForChannel(channelUUID, timeInterval);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        return new ResponseEntity<>(xml, headers, HttpStatus.OK);
    }

    @GetMapping(value = {"/user/{userUUID}", "/user/{userUUID}/{timeInterval}"}, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public ResponseEntity<String> getUserFeed(@PathVariable("userUUID") UUID userUUID,
                                              @PathVariable(value = "timeInterval", required = false) AtomFeedsTimeIntervalEnum timeInterval) throws JaxbMarshallerException, UserNotFoundException {
        String xml = atomFeedProducer.getAtomFeedForUser(userUUID, timeInterval);
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
        return new ResponseEntity<>(atomFeedProducer.getErrorAtom(e), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
