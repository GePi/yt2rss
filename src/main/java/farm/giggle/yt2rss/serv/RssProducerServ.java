package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.api.RssTimeIntervalEnum;
import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.producedRssStructure.RssEntry;
import farm.giggle.yt2rss.producedRssStructure.RssFeed;
import farm.giggle.yt2rss.producedRssStructure.RssLink;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RssProducerServ {
    private final ChannelService channelService;
    private final UserService userService;

    public RssProducerServ(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    @Transactional
    public String getUserRss(UUID userUUID, RssTimeIntervalEnum timeInterval) throws JaxbMarshallerException {
        User user = userService.getUserByUUID(userUUID);
        RssFeed rssFeed = createRssFeed(user, (timeInterval == null) ? RssTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(rssFeed);
    }

    @Transactional
    public String getChannelRss(UUID channelUUID, RssTimeIntervalEnum timeInterval) throws JaxbMarshallerException, ResourceNotFoundException {
        Channel channel = channelService.getChannel(channelUUID);
        if (channel == null) {
            throw new ResourceNotFoundException("Канал с таким номером не найден");
        }
        RssFeed rssFeed = createRssFeed(channel, (timeInterval == null) ? RssTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(rssFeed);
    }

    public String getErrorRss(Exception e) {
        RssFeed rssFeed = new RssFeed();
        rssFeed.setTitle(e.getMessage());
        ResponseStatus annotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        rssFeed.setSubtitle(annotation != null ?
                annotation.code().value() + " " + annotation.code().getReasonPhrase() :
                HttpStatus.INTERNAL_SERVER_ERROR.value() + " " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        return rssFeed.toString();
    }

    private String marshal(RssFeed rssFeed) throws JaxbMarshallerException {
        String result;
        try {
            JAXBContext context = JAXBContext.newInstance(RssFeed.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            mar.marshal(rssFeed, sw);
            result = sw.toString();
        } catch (JAXBException e) {
            throw new JaxbMarshallerException(e);
        }
        return result;
    }

    private RssFeed createRssFeed(User user, RssTimeIntervalEnum timeInterval) {
        RssFeed rssFeed = new RssFeed();
        rssFeed.setTitle("Y2RSS: user feed");
        List<File> files = new ArrayList<>();
        List<Channel> channels = channelService.getChannelList(user.getId());
        for (var channel : channels) {
            files.addAll(channelService.getFileListDownloadedAfter(channel, timeInterval.getDateFrom()));
        }
        return addFilesIntoRssFeed(files, rssFeed);
    }

    private RssFeed createRssFeed(Channel channel, RssTimeIntervalEnum timeInterval) {
        RssFeed rssFeed = new RssFeed();
        rssFeed.setTitle(channel.getTitle());
        return addFilesIntoRssFeed(
                channelService.getFileListDownloadedAfter(channel, timeInterval.getDateFrom()), rssFeed);
    }

    private RssFeed addFilesIntoRssFeed(List<File> files, RssFeed rssFeed) {
        rssFeed.setRssEntries(
                files.stream()
                        .map(this::mapFileToRssEntry)
                        .collect(Collectors.toList()));
        return rssFeed;
    }

    private RssEntry mapFileToRssEntry(File file) {
        return new RssEntry(
                file.getTitle(),
                file.getVideoId(),
                new RssLink("", "", file.getDownloadedFileUrl()),
                OffsetDateTime.of(file.getPublishedAt(), ZoneOffset.UTC),
                OffsetDateTime.of(file.getDowloadedAt(), ZoneOffset.UTC));
    }
}
