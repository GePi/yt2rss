package farm.giggle.yt2rss.api;

import lombok.Data;

@Data
public class ExchangeFileFormat {
    private Long Id;
    private String videoUrl;
    private String downloadedUrl;

    public ExchangeFileFormat() {
    }

    public ExchangeFileFormat(Long id, String videoUrl) {
        Id = id;
        this.videoUrl = videoUrl;
    }
}
