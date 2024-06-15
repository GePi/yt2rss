package farm.giggle.yt2rss.atom.services;

import farm.giggle.yt2rss.atom.structure.AtomEntry;
import farm.giggle.yt2rss.atom.structure.AtomFeed;
import farm.giggle.yt2rss.atom.structure.AtomFeedsTimeIntervalEnum;
import farm.giggle.yt2rss.atom.structure.AtomLink;
import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.services.ChannelService;
import farm.giggle.yt2rss.services.UserService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.StringWriter;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AtomFeedProducer {
    private final ChannelService channelService;
    private final UserService userService;

    public AtomFeedProducer(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    @Transactional
    public String getUserAtomXML(UUID userUUID, AtomFeedsTimeIntervalEnum timeInterval, String requestURL) throws JaxbMarshallerException, ResourceNotFoundException {
        User user = userService.getUserByUUID(userUUID);
        if (user == null) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        AtomFeed atomFeed = createAtomFeed(user, requestURL, (timeInterval == null) ? AtomFeedsTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(atomFeed);
    }

    @Transactional
    public String getChannelAtomXML(UUID channelUUID, AtomFeedsTimeIntervalEnum timeInterval, String requestURL) throws JaxbMarshallerException, ResourceNotFoundException {
        Channel channel = channelService.getChannel(channelUUID);
        if (channel == null) {
            throw new ResourceNotFoundException("Канал не найден");
        }
        AtomFeed atomFeed = createAtomFeed(channel, requestURL, (timeInterval == null) ? AtomFeedsTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(atomFeed);
    }

    public String getErrorAtomXML(Exception e) throws JaxbMarshallerException {
        return marshal(createAtomFeed(e));
    }

    private AtomFeed createAtomFeed(Exception e) {
        ResponseStatus annotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        return AtomFeed.builder()
                .setTitle("Произошла ошибка")
                .setId("https://y2rss.ru/error")
                .setSubtitle(annotation != null ?
                        annotation.code().value() + " " + annotation.code().getReasonPhrase() :
                        HttpStatus.INTERNAL_SERVER_ERROR.value() + " " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .setLink(new AtomLink(AtomLink.REL_SELF, AtomLink.TYPE_RSS, "https://y2rss.ru/error" ))
                .setUpdated(OffsetDateTime.now(Clock.systemUTC()))
                .build();
    }

    private String marshal(AtomFeed atomFeed) throws JaxbMarshallerException {
        String result;
        try {
            JAXBContext context = JAXBContext.newInstance(AtomFeed.class);
            Marshaller mar = context.createMarshaller();
            mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter sw = new StringWriter();
            mar.marshal(atomFeed, sw);
            result = sw.toString();
        } catch (JAXBException e) {
            throw new JaxbMarshallerException(e);
        }
        return result;
    }

    private AtomFeed createAtomFeed(User user, String requestURL, AtomFeedsTimeIntervalEnum timeInterval) {
        List<File> files = new ArrayList<>();
        List<Channel> channels = channelService.getChannelList(user.getId());
        for (var channel : channels) {
            files.addAll(channelService.getFileListDownloadedAfter(channel, timeInterval.getDateFrom()));
        }

        return AtomFeed.builder()
                .setTitle("Все файлы пользователя")
                .setId(requestURL)
                .setLink(new AtomLink(AtomLink.REL_SELF, AtomLink.TYPE_RSS, requestURL))
                .setUpdated(
                        files.stream()
                                .map(file -> OffsetDateTime.of(file.getUpdatedAt(), ZoneOffset.UTC))
                                .max(OffsetDateTime::compareTo)
                                .orElse(OffsetDateTime.now(Clock.systemUTC())))
                .setEntries(createAtomEntries(files))
                .build();
    }

    private AtomFeed createAtomFeed(Channel channel, String requestURL, AtomFeedsTimeIntervalEnum timeInterval) {
        List<File> files = new ArrayList<>(channelService.getFileListDownloadedAfter(channel, timeInterval.getDateFrom()));

        return AtomFeed.builder()
                .setTitle(channel.getTitle())
                .setId(requestURL)
                .setLink(new AtomLink(AtomLink.REL_SELF, AtomLink.TYPE_RSS, requestURL))
                .setUpdated(
                        files.stream()
                                .map(file -> OffsetDateTime.of(file.getUpdatedAt(), ZoneOffset.UTC))
                                .max(OffsetDateTime::compareTo)
                                .orElse(OffsetDateTime.now(Clock.systemUTC())))
                .setEntries(createAtomEntries(files))
                .build();
    }

    private List<AtomEntry> createAtomEntries(List<File> files) {
        return files.stream()
                .map(this::createAtomEntry)
                .collect(Collectors.toList());
    }

    private AtomEntry createAtomEntry(File file) {
        return new AtomEntry(
                file.getTitle(),
                file.getOriginalUrl(),
                new AtomLink(AtomLink.REL_RELATED, AtomLink.TYPE_AUDIO, file.getDownloadedFileUrl()),
                OffsetDateTime.of(file.getPublishedAt(), ZoneOffset.UTC),
                OffsetDateTime.of(file.getUpdatedAt(), ZoneOffset.UTC));
    }
}
