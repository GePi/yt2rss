package farm.giggle.yt2rss.youtube;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class RssFile {
    private String videoId;
    private String videoUrl;
    private String title;
    private OffsetDateTime publishedAt;
    private OffsetDateTime updatedAt;

    public OffsetDateTime getTimeOfLastPublication() {
        return (updatedAt == null || publishedAt.isBefore(updatedAt)) ? publishedAt : updatedAt;
    }
}
