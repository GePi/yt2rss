package farm.giggle.yt2rss.youtube;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class RssFile {
    private String videoId;
    private String videoUrl;
    private String title;
    private Date published;
    private Date updated;

    public Date getTimeOfLastPublication() {
        if (updated == null) {
            return published;
        }
        return (published.compareTo(updated) < 0) ? updated : published;
    }
}
