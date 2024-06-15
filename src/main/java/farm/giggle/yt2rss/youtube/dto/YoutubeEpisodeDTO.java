package farm.giggle.yt2rss.youtube.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class YoutubeEpisodeDTO {
    private String videoId;
    private String videoUrl;
    private String title;
    private String content;
    private OffsetDateTime publishedAt;
    private OffsetDateTime updatedAt;
}
