package farm.giggle.yt2rss.exportfeeds;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.OffsetDateTime;

public class OffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {

    @Override
    public OffsetDateTime unmarshal(String s) {
        return OffsetDateTime.parse(s);
    }

    @Override
    public String marshal(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toString();
    }
}
