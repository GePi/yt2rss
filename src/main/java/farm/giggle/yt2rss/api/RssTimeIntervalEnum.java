package farm.giggle.yt2rss.api;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public enum RssTimeIntervalEnum {
    LAST_DAY(1440L),
    LAST_HOUR(60L),
    FAR_FAR_AWAY(0L);

    private final Long minusMinutes;

    RssTimeIntervalEnum(Long minusMinutes) {
        this.minusMinutes = minusMinutes;
    }

    public Date getDateFrom() {
        if (this == FAR_FAR_AWAY) {
            return new Date(0L);
        }
        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(minusMinutes);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
