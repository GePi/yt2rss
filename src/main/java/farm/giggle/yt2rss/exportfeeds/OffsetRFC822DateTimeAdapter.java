package farm.giggle.yt2rss.exportfeeds;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class OffsetRFC822DateTimeAdapter extends XmlAdapter<String, OffsetDateTime> {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    @Override
    public OffsetDateTime unmarshal(String s) {
        return OffsetDateTime.parse(s);
    }

    @Override
    public String marshal(OffsetDateTime offsetDateTime) {
        return offsetDateTime.format(dateTimeFormatter);
    }
}
