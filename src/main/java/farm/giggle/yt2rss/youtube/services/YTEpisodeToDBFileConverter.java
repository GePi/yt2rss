package farm.giggle.yt2rss.youtube.services;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.youtube.structure.YoutubeChannelView;
import org.springframework.lang.NonNull;

import java.time.ZoneOffset;

public class YTEpisodeToDBFileConverter {
    static public File rssFile2DBFile(YoutubeChannelView youtubeChannelView) {
        return rssFile2DBFile(youtubeChannelView, null);
    }

    static public File rssFile2DBFile(YoutubeChannelView youtubeChannelView, Channel channel) {
        return new File(
                youtubeChannelView.getVideoId(),
                youtubeChannelView.getTitle(),
                youtubeChannelView.getVideoUrl(),
                youtubeChannelView.getPublishedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime(),
                youtubeChannelView.getUpdatedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime(),
                channel);
    }

    public static void rssFile2DBFileUpdatedTime(@NonNull YoutubeChannelView youtubeChannelView, @NonNull File file) {
        file.setPublishedAt(youtubeChannelView.getPublishedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
        file.setUpdatedAt(youtubeChannelView.getUpdatedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }
}
