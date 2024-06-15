package farm.giggle.yt2rss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Data
@EnableScheduling
@Configuration
@ConfigurationProperties(prefix = "yt2rss")
public class ApplicationConfig {
    @Data
    public static class ListOnPage {
        private Integer numberEntriesOnPage;
        private SortOrder sortOrder;
    }

    public enum SortOrder {
        TITLE,
        UPDATED,
        CREATED
    }

    private ListOnPage channelPage;
    private int downloadablePortion = 10;
}
