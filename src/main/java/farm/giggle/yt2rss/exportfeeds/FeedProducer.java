package farm.giggle.yt2rss.exportfeeds;

import farm.giggle.yt2rss.exceptions.JaxbMarshallerException;
import farm.giggle.yt2rss.exceptions.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface FeedProducer {
    @Transactional
    String getUserFeed(UUID userUUID, FeedsTimeIntervalEnum timeInterval, String requestURL) throws JaxbMarshallerException, ResourceNotFoundException;

    @Transactional
    String getChannelFeed(UUID channelUUID, FeedsTimeIntervalEnum timeInterval, String requestURL) throws JaxbMarshallerException, ResourceNotFoundException;

    String getErrorFeed(Exception e) throws JaxbMarshallerException;
}
