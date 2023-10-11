package farm.giggle.yt2rss.youtube;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import org.springframework.lang.NonNull;

import java.time.ZoneOffset;

public class RssToDBConverter {
    static public File rssFile2DBFile(RssFile rssFile) {
        return rssFile2DBFile(rssFile, null);
    }

    static public File rssFile2DBFile(RssFile rssFile, Channel channel) {
        return new File(
                rssFile.getVideoId(),
                rssFile.getTitle(),
                rssFile.getVideoUrl(),
                rssFile.getPublishedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime(),
                rssFile.getUpdatedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime(),
                channel);
    }

    public static void rssFile2DBFileUpdatedTime(@NonNull RssFile rssFile, @NonNull File file) {
        file.setPublishedAt(rssFile.getPublishedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
        file.setUpdatedAt(rssFile.getUpdatedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }
}
