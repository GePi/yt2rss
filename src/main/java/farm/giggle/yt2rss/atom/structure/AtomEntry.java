package farm.giggle.yt2rss.atom.structure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomEntry {
    private String id;
    private String title;
    private AtomLink link;
    private String content;
    private AtomAuthor author = new AtomAuthor(AtomAuthor.AUTHOR_NAME_PLACEHOLDER);
    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    private OffsetDateTime published;
    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    private OffsetDateTime updated;

    public AtomEntry(String title, String id, AtomLink link, OffsetDateTime published, OffsetDateTime updated) {
        this.title = title;
        this.id = id;
        this.link = link;
        this.published = published;
        this.updated = updated;
        this.content  = title;
    }
}
