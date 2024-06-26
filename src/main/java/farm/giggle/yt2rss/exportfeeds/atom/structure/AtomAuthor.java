package farm.giggle.yt2rss.exportfeeds.atom.structure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "author")
public class AtomAuthor {
    public static final String AUTHOR_NAME_PLACEHOLDER = "y2rss@yandex.ru(transmutator)";
    private String name;

    public AtomAuthor() {
    }

    public AtomAuthor(String name) {
        this.name = name;
    }
}
