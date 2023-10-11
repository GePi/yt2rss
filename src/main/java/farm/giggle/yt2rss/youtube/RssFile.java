package farm.giggle.yt2rss.youtube;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class RssFile {
    private String videoId;
    private String videoIdOld;
    private String videoUrl;
    private String title;
    private OffsetDateTime publishedAt;
    private OffsetDateTime updatedAt;
}
