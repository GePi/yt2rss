package farm.giggle.yt2rss.atom;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class RssEntry {
    private String title;
    private String id;
    private RssLink link;
    private Date published;
    private Date updated;
}
