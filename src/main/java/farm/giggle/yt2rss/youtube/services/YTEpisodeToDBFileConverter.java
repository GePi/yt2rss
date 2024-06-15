package farm.giggle.yt2rss.youtube.services;

import farm.giggle.yt2rss.model.Channel;
import farm.giggle.yt2rss.model.File;
import farm.giggle.yt2rss.youtube.dto.YoutubeEpisodeDTO;
import org.springframework.lang.NonNull;

import java.time.ZoneOffset;

public class YTEpisodeToDBFileConverter {
    static public File createFileByYTEpisode(YoutubeEpisodeDTO youtubeEpisodeDTO) {
        return createFileByYTEpisode(youtubeEpisodeDTO, null);
    }

    static public File createFileByYTEpisode(YoutubeEpisodeDTO youtubeEpisodeDTO, Channel channel) {
        return new File(
                youtubeEpisodeDTO.getVideoId(),
                youtubeEpisodeDTO.getTitle(),
                youtubeEpisodeDTO.getVideoUrl(),
                youtubeEpisodeDTO.getPublishedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime(),
                youtubeEpisodeDTO.getUpdatedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime(),
                channel);
    }

    public static void setFIleDateTime(@NonNull YoutubeEpisodeDTO youtubeEpisodeDTO, @NonNull File file) {
        file.setPublishedAt(youtubeEpisodeDTO.getPublishedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
        file.setUpdatedAt(youtubeEpisodeDTO.getUpdatedAt().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime());
    }
}
