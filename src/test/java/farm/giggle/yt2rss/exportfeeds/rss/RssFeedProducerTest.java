package farm.giggle.yt2rss.exportfeeds.rss;

import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.exportfeeds.FeedsTimeIntervalEnum;
import farm.giggle.yt2rss.exportfeeds.rss.structure.RssChannel;
import farm.giggle.yt2rss.exportfeeds.rss.structure.RssFeed;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.services.ChannelService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RssFeedProducerTest {

    @Mock
    ChannelService channelService;

    @InjectMocks
    RssFeedProducer producer;

    private AutoCloseable closeable;

    @BeforeEach
    void serUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void after() throws Exception {
        closeable.close();
    }

    @Test
    void createFeedChannel_addedEnclosure() {
        FeedsTimeIntervalEnum timeInterval = FeedsTimeIntervalEnum.FAR_FAR_AWAY;
        LocalDateTime downloadedAfter = timeInterval.getDateFrom();

        Channel channel = new Channel("https://ffaway.ru?channelId=111111", "Test channel", new User());
        List<File> fileList = getFiles(channel, LocalDateTime.now(Clock.systemUTC()));

        when(channelService.getFileListDownloadedAfter(channel, downloadedAfter)).thenReturn(fileList);

        RssFeed feed = producer.createFeed(channel, timeInterval);

        assertNotEquals(null, feed.getChannel());
        RssChannel rssChannel = feed.getChannel();
        assertEquals(2, rssChannel.getItems().size());
        assertEquals(100500, rssChannel.getItems().get(0).getEnclosure().getLength());
        assertEquals(100400, rssChannel.getItems().get(1).getEnclosure().getLength());
    }

    @Test
    void createFeedChannel_marshalled() throws JaxbMarshallerException, ResourceNotFoundException {
        UUID channelUUID = UUID.randomUUID();

        FeedsTimeIntervalEnum timeInterval = FeedsTimeIntervalEnum.FAR_FAR_AWAY;
        LocalDateTime downloadedAfter = timeInterval.getDateFrom();

        Channel channel = new Channel("https://ffaway.ru?channelId=111111", "Test channel", new User());
        List<File> fileList = getFiles(channel, LocalDateTime.now(Clock.systemUTC()));

        when(channelService.getFileListDownloadedAfter(channel, downloadedAfter)).thenReturn(fileList);
        when(channelService.getChannel(channelUUID)).thenReturn(channel);
        String channelFeedXMLString = producer.getChannelFeed(channelUUID, FeedsTimeIntervalEnum.FAR_FAR_AWAY, "https://y2rss.ru/rss?channelId=" + channelUUID);
        assertNotNull(channelFeedXMLString);
    }

    @NotNull
    private static List<File> getFiles(Channel channel, LocalDateTime downloadedAt) {
        File file0 = new File("VID0", "Заголовок файла 1", "https://original.ffaway.ru?fileId=VID0", LocalDateTime.of(2024, 5, 10, 9, 0), LocalDateTime.of(2024, 5, 10, 9, 0), channel);
        File file1 = new File("VID1", "Заголовок файла 2", "https://original.ffaway.ru?fileId=VID2", LocalDateTime.of(2024, 5, 12, 10, 0), LocalDateTime.of(2024, 5, 12, 10, 0), channel);
        file0.setDowloadedAt(downloadedAt);
        file0.setDownloadedFileUrl("https://storage.ffaway.ru/file0");
        file0.setDownloadedSize(100500);
        file1.setDowloadedAt(downloadedAt);
        file1.setDownloadedFileUrl("https://storage.ffaway.ru/file1");
        file1.setDownloadedSize(100400);

        return new ArrayList<>(Arrays.asList(file0, file1));
    }
}