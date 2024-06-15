package farm.giggle.yt2rss.services.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class LatestChannelUpdateDatesDTO {
    @Getter
    private final long id;
    @Getter
    private final String url;
    @Getter
    private final String title;
    @Getter
    private final OffsetDateTime publishedAt;
    @Getter
    private final OffsetDateTime updatedAt;

    public LatestChannelUpdateDatesDTO(long id, String url, String title, LocalDateTime publishedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.publishedAt = OffsetDateTime.of(publishedAt, ZoneOffset.UTC);
        this.updatedAt = OffsetDateTime.of(updatedAt, ZoneOffset.UTC);
    }
}
