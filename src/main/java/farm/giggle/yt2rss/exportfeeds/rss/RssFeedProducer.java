package farm.giggle.yt2rss.exportfeeds.rss;

import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import farm.giggle.yt2rss.exportfeeds.FeedProducerAbstract;
import farm.giggle.yt2rss.exportfeeds.FeedsTimeIntervalEnum;
import farm.giggle.yt2rss.exportfeeds.rss.structure.RssChannel;
import farm.giggle.yt2rss.exportfeeds.rss.structure.RssFeed;
import farm.giggle.yt2rss.exportfeeds.rss.structure.RssItem;
import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.model.User;
import farm.giggle.yt2rss.services.ChannelService;
import farm.giggle.yt2rss.services.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("RssFeedProducer")
public class RssFeedProducer extends FeedProducerAbstract {
    public RssFeedProducer(ChannelService channelService, UserService userService) {
        super(channelService, userService);
    }

    @Override
    public String getUserFeed(UUID userUUID, FeedsTimeIntervalEnum timeInterval, String requestURL) throws JaxbMarshallerException, ResourceNotFoundException {
        User user = userService.getUserByUUID(userUUID);
        if (user == null) {
            throw new ResourceNotFoundException("Пользователь не найден");
        }
        RssFeed rssFeed = createFeed(user, requestURL, (timeInterval == null) ? FeedsTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(rssFeed);
    }

    @Override
    public String getChannelFeed(UUID channelUUID, FeedsTimeIntervalEnum timeInterval, String requestURL) throws JaxbMarshallerException, ResourceNotFoundException {
        Channel channel = channelService.getChannel(channelUUID);
        if (channel == null) {
            throw new ResourceNotFoundException("Канал не найден");
        }
        RssFeed rssFeed = createFeed(channel, (timeInterval == null) ? FeedsTimeIntervalEnum.FAR_FAR_AWAY : timeInterval);
        return marshal(rssFeed);
    }

    @Override
    public String getErrorFeed(Exception e) throws JaxbMarshallerException {
        RssFeed rssFeed = new RssFeed();
        RssChannel rssChannel = new RssChannel("Произошла ошибка");
        rssFeed.setChannel(rssChannel);
        return marshal(rssFeed);
    }

    private RssFeed createFeed(User user, String requestURL, FeedsTimeIntervalEnum timeInterval) {
        List<File> files = new ArrayList<>();
        List<Channel> channels = channelService.getChannelList(user.getId());
        for (var channel : channels) {
            files.addAll(channelService.getFileListDownloadedAfter(channel, timeInterval.getDateFrom()));
        }

        RssFeed rssFeed = new RssFeed();
        RssChannel rssChannel = new RssChannel("Все файлы пользователя");
        rssFeed.setChannel(rssChannel);
        files.stream().map(RssItem::new).forEach(rssChannel::addItem);

        return rssFeed;
    }

    protected RssFeed createFeed(Channel channel, FeedsTimeIntervalEnum timeInterval) {
        List<File> files = new ArrayList<>(channelService.getFileListDownloadedAfter(channel, timeInterval.getDateFrom()));

        RssFeed rssFeed = new RssFeed();
        RssChannel rssChannel = new RssChannel(channel.getTitle());
        rssFeed.setChannel(rssChannel);
        files.stream().map(RssItem::new).forEach(rssChannel::addItem);

        return rssFeed;
    }
}
