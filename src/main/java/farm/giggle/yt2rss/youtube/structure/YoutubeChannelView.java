package farm.giggle.yt2rss.youtube.structure;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class YoutubeChannelView {
    private String videoId;
    private String videoUrl;
    private String title;
    private OffsetDateTime publishedAt;
    private OffsetDateTime updatedAt;
}
