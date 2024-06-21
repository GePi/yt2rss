package farm.giggle.yt2rss.exportfeeds.rss.structure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlRootElement(name = "rss")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class RssFeed {
    @XmlAttribute
    private String version = "2.0";
    @Setter
    private RssChannel channel;
}
