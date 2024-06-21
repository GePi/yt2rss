package farm.giggle.yt2rss.exportfeeds.rss.structure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "enclosure")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@NoArgsConstructor
public class RssEnclosure {
    @XmlAttribute
    private long length;
    @XmlAttribute
    private String type = "audio/mp3";
    @XmlAttribute
    private String url;

    public RssEnclosure(long length, String url) {
        this.length = length;
        this.url = url;
    }
}
