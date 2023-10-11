package farm.giggle.yt2rss.producedRssStructure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssLink {
    @XmlAttribute
    private String rel;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private String href;

    public RssLink() {
    }

    public RssLink(String rel, String type, String href) {
        this.rel = rel;
        this.type = type;
        this.href = href;
    }
}
