package farm.giggle.yt2rss.producedRssStructure;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.OffsetDateTime;

public class OffsetDateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {

    @Override
    public OffsetDateTime unmarshal(String s) throws Exception {
        return OffsetDateTime.parse(s);
    }

    @Override
    public String marshal(OffsetDateTime offsetDateTime) throws Exception {
        return offsetDateTime.toString();
    }
}
