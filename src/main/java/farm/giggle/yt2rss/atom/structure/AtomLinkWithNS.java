package farm.giggle.yt2rss.atom.structure;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "link")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomLinkWithNS {
    public static final String REL_RELATED = "related";
    public static final String REL_SELF = "self";
    public static final String TYPE_AUDIO = "audio/mpeg";
    public static final String TYPE_RSS = "application/rss+xml";
    @XmlAttribute
    private String rel;
    @XmlAttribute
    private String type;
    @XmlAttribute
    private String href;

    public AtomLinkWithNS() {
    }

    public AtomLinkWithNS(String rel, String type, String href) {
        this.rel = rel;
        this.type = type;
        this.href = href;
    }

    public AtomLinkWithNS(String href) {
        this.type = TYPE_AUDIO;
        this.rel = REL_RELATED;
        this.href = href;
    }
}
