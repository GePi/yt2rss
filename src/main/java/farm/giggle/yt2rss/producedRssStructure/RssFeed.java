package farm.giggle.yt2rss.producedRssStructure;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@XmlRootElement(name = "feed")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssFeed {
    private String title;
    private String subtitle;

    //@XmlElementWrapper(name = "authList" )
    @XmlElement(name = "entry")
    private List<RssEntry> rssEntries = new ArrayList<>();

    public void addRssEntry(RssEntry rssEntry) {
        if (rssEntry == null) {
            return;
        }
        rssEntries.add(rssEntry);
    }

    @Override
    public String toString() {
        return "<feed>" + "<title>" + title + "</title>" + "<subtitle>" + subtitle + "</subtitle>" + "</feed>";
    }
}
