package farm.giggle.yt2rss.serv;

import farm.giggle.yt2rss.api.RssTimeIntervalEnum;
import farm.giggle.yt2rss.atom.RssEntry;
import farm.giggle.yt2rss.atom.RssFeed;
import farm.giggle.yt2rss.atom.RssLink;
import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.exceptions.UserNotFoundException;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service()
public class RssProducerServ {
    private final ChannelServiceImpl channelService;
    private final UserServiceImpl userService;

    public RssProducerServ(ChannelServiceImpl channelService, UserServiceImpl userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    public String getUserRss(Long userId, RssTimeIntervalEnum timeInterval) throws UserNotFoundException, JaxbMarshallerException {
        User user = userService.getUserById(userId);
        RssFeed rssFeed = createRssFeed(user, (timeInterval == null) ? RssTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(rssFeed);
    }

    public String getChannelRss(Long channelId, RssTimeIntervalEnum timeInterval) throws JaxbMarshallerException, ResourceNotFoundException {
        Channel channel = channelService.getChannel(channelId);
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
        rssFeed.setTitle("User feed");
        List<File> files = new ArrayList<>();
        List<Channel> channels = channelService.getChannelList(user.getId());
        for (var channel : channels) {
            files.addAll(channel.getFileList());
        }
        return addFilesIntoRssFeed(files, timeInterval, rssFeed);
    }

    private RssFeed createRssFeed(Channel channel, RssTimeIntervalEnum timeInterval) {
        RssFeed rssFeed = new RssFeed();
        rssFeed.setTitle(channel.getTitle());
        return addFilesIntoRssFeed(channel.getFileList(), timeInterval, rssFeed);
    }

    private RssFeed addFilesIntoRssFeed(List<File> files, RssTimeIntervalEnum timeInterval, RssFeed rssFeed) {
        OffsetDateTime dateWith = timeInterval.getDateFrom();
        rssFeed.setRssEntries(
                files.stream()
                        .filter(file -> Objects.nonNull(file.getDowloadedAt()))
                        .filter(file -> dateWith.isBefore(file.getDowloadedAt()))
                        .map(this::mapFileToRssEntry)
                        .collect(Collectors.toList()));
        return rssFeed;
    }

    private RssEntry mapFileToRssEntry(File file) {
        RssEntry rssEntry = new RssEntry();
        rssEntry.setTitle(file.getTitle());
        rssEntry.setPublished(file.getPublishedAt());
        rssEntry.setUpdated(file.getDowloadedAt());
        RssLink rssLink = new RssLink();
        rssLink.setHref(file.getDownloadedFileUrl1());
        rssEntry.setLink(rssLink);
        return rssEntry;
    }
}
