package farm.giggle.yt2rss.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "yt2rss")
public class ApplicationConfiguration {
    @Data
    public static class ListOnPage {
        private Integer numberEntriesOnPage;
        private SortOrder sortOrder;
    }

    public static enum SortOrder {
        TITLE
    }

    private ListOnPage channelPage;
}
