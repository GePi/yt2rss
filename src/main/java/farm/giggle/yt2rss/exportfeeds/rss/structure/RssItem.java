package farm.giggle.yt2rss.exportfeeds.rss.structure;

import farm.giggle.yt2rss.exportfeeds.OffsetRFC822DateTimeAdapter;
import farm.giggle.yt2rss.model.File;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@XmlRootElement(name = "item")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class RssItem {
    static final String AUTHOR_NAME_PLACEHOLDER = "y2rss@yandex.ru(transmutator)";
    private String title;
    private String description;
    private String link;
    private String guid;
    @XmlJavaTypeAdapter(OffsetRFC822DateTimeAdapter.class)
    private OffsetDateTime pubDate;
    private RssEnclosure enclosure;
    private String author = AUTHOR_NAME_PLACEHOLDER;

    public RssItem(File file) {
        this.title = file.getTitle();
        this.description = "";
        this.guid = file.getDownloadedFileUrl();
        this.pubDate = OffsetDateTime.of(file.getDowloadedAt(), ZoneOffset.UTC);
        this.link = file.getDownloadedFileUrl();
        this.enclosure = new RssEnclosure(file.getDownloadedSize(), file.getDownloadedFileUrl());
    }
}
