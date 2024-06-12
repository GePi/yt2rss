package farm.giggle.yt2rss.atom.services;

import farm.giggle.yt2rss.atom.structure.*;
import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.serv.ChannelService;
import farm.giggle.yt2rss.serv.UserService;
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
    public String getAtomFeedForUser(UUID userUUID, AtomFeedsTimeIntervalEnum timeInterval) throws JaxbMarshallerException {
        User user = userService.getUserByUUID(userUUID);
        AtomFeed atomFeed = createAtomFeed(user, (timeInterval == null) ? AtomFeedsTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(atomFeed);
    }

    @Transactional
    public String getAtomFeedForChannel(UUID channelUUID, AtomFeedsTimeIntervalEnum timeInterval) throws JaxbMarshallerException, ResourceNotFoundException {
        Channel channel = channelService.getChannel(channelUUID);
        if (channel == null) {
            throw new ResourceNotFoundException("Канал с таким номером не найден");
        }
        AtomFeed atomFeed = createAtomFeed(channel, (timeInterval == null) ? AtomFeedsTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(atomFeed);
    }

    public String getErrorAtom(Exception e) {
        ResponseStatus annotation = AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class);
        AtomFeed atomFeed = AtomFeed.builder()
                .setTitle(e.getMessage())
                .setId("http://y2rss.ru/error")
                .setSubtitle(annotation != null ?
                        annotation.code().value() + " " + annotation.code().getReasonPhrase() :
                        HttpStatus.INTERNAL_SERVER_ERROR.value() + " " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .build();
        return atomFeed.toString();
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

    private AtomFeed createAtomFeed(User user, AtomFeedsTimeIntervalEnum timeInterval) {
        List<File> files = new ArrayList<>();
        List<Channel> channels = channelService.getChannelList(user.getId());
        for (var channel : channels) {
            files.addAll(channelService.getFileListDownloadedAfter(channel, timeInterval.getDateFrom()));
        }

        AtomFeed atomFeed = AtomFeed.builder()
                .setTitle("Y2RSS: user feed")
                .setId("https://y2rss.ru/user/" + user.getUuid())
                .setLink(new AtomLinkWithNS(AtomLink.REL_SELF, AtomLink.TYPE_RSS, "https://y2rss.ru/user/" + user.getUuid()))
                .setUpdated(
                        files.stream()
                                .map(file -> OffsetDateTime.of(file.getUpdatedAt(), ZoneOffset.UTC))
                                .max(OffsetDateTime::compareTo)
                                .orElse(OffsetDateTime.now(Clock.systemUTC())))
                .setEntries(createAtomEntries(files))
                .build();

        return atomFeed;
    }

    private AtomFeed createAtomFeed(Channel channel, AtomFeedsTimeIntervalEnum timeInterval) {
        List<File> files = new ArrayList<>();
        files.addAll(channelService.getFileListDownloadedAfter(channel, timeInterval.getDateFrom()));

        AtomFeed atomFeed = AtomFeed.builder()
                .setTitle(channel.getTitle())
                .setId("https://y2rss.ru/channel/" + channel.getUuid())
                .setLink(new AtomLinkWithNS(AtomLink.REL_SELF, AtomLink.TYPE_RSS, "https://y2rss.ru/channel/" + channel.getUuid()))
                .setUpdated(
                        files.stream()
                                .map(file -> OffsetDateTime.of(file.getUpdatedAt(), ZoneOffset.UTC))
                                .max(OffsetDateTime::compareTo)
                                .orElse(OffsetDateTime.now(Clock.systemUTC())))
                .setEntries(createAtomEntries(files))
                .build();

        return atomFeed;
    }

    private List<AtomEntry> createAtomEntries(List<File> files) {
        return files.stream()
                .map(this::createAtomEntry)
                .collect(Collectors.toList());
    }

    private AtomFeed addFilesIntoAtomFeed(List<File> files, AtomFeed atomFeed) {
        atomFeed.addAtomEntries(
                files.stream()
                        .map(this::createAtomEntry)
                        .collect(Collectors.toList()));
        return atomFeed;
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
