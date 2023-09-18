package farm.giggle.yt2rss.producedRssStructure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.FIELD)
public class RssLink {
    @XmlAttribute
    private String rel;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private String href;
}
