package farm.giggle.yt2rss.exportfeeds.atom.structure;

import farm.giggle.yt2rss.exceptions.AtomValidationException;
import farm.giggle.yt2rss.exportfeeds.OffsetDateTimeAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@NoArgsConstructor
@XmlRootElement(name = "feed")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomFeed {
    private String id;

    private String title;
    private String subtitle;
    @XmlJavaTypeAdapter(OffsetDateTimeAdapter.class)
    private OffsetDateTime updated;

    private AtomLink link;

    @XmlElement(name = "entry")
    private List<AtomEntry> atomEntries = new ArrayList<>();

    public void addRssEntry(AtomEntry atomEntry) {
        if (atomEntry == null) {
            return;
        }
        atomEntries.add(atomEntry);
    }

    @Override
    public String toString() {
        return "AtomFeed{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private AtomFeed feed;

        public Builder() {
            this.feed = new AtomFeed();
        }

        public Builder setId(String id) {
            feed.id = id;
            return this;
        }

        public Builder setTitle(String title) {
            feed.title = title;
            return this;
        }

        public Builder setSubtitle(String subtitle) {
            feed.subtitle = subtitle;
            return this;
        }

        public Builder setLink(AtomLink link) {
            feed.link = link;
            return this;
        }

        public Builder setUpdated(OffsetDateTime updated) {
            feed.updated = updated;
            return this;
        }

        public Builder setEntries(Collection<AtomEntry> atomEntries) {
            feed.atomEntries.addAll(atomEntries);
            return this;
        }

        public AtomFeed build() {
            if (feed.id == null || feed.id.isBlank()) {
                throw new AtomValidationException("Обязательное поле AtomFeed:id не заполнено");
            }
            if (feed.updated == null) {
                throw new AtomValidationException("Обязательное поле AtomFeed:updated не заполнено");
            }
            return feed;
        }
    }
}
