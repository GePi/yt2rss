package farm.giggle.yt2rss.producedRssStructure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class RssEntry {
    private String title;
    private String id;
    private RssLink link;
    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    private OffsetDateTime published;
    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    private OffsetDateTime updated;

    public RssEntry(String title, String id, RssLink link, OffsetDateTime published, OffsetDateTime updated) {
        this.title = title;
        this.id = id;
        this.link = link;
        this.published = published;
        this.updated = updated;
    }
}
