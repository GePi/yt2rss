package farm.giggle.yt2rss.api;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
public class ExchangeFileFormat {
    @NonNull
    final private Long Id;
    @NonNull
    final private String videoUrl;
    final private String downloadedUrl;
    final private LocalDateTime downloadedAt;

    public ExchangeFileFormat(@NonNull Long id, @NonNull String videoUrl, String downloadedUrl, LocalDateTime downloadedAt) {
        this.Id = id;
        this.videoUrl = videoUrl;
        this.downloadedUrl = downloadedUrl;
        this.downloadedAt = downloadedAt;
    }
}
