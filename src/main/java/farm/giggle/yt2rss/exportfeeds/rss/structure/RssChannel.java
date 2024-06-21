package farm.giggle.yt2rss.exportfeeds.rss.structure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class RssChannel {
    private String title;
    private String link = "https://y2rss.ru";
    private String language = "ru";
    private String description = "";
    @XmlElement(name = "item")
    List<RssItem> items = new ArrayList<>();

    public RssChannel(String title) {
        this.title = title;
    }

    public void addItem(@NonNull RssItem item) {
        items.add(item);
    }

}
