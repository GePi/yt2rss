package farm.giggle.yt2rss.api;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public enum RssTimeIntervalEnum {
    LAST_DAY(1440L, ChronoUnit.MINUTES),
    LAST_HOUR(60L, ChronoUnit.MINUTES),
    FAR_FAR_AWAY();

    private final Long minusChronUnitsVal;
    private final ChronoUnit chronoUnit;

    RssTimeIntervalEnum(Long minusChronUnitsVal, ChronoUnit chronoUnit) {
        this.minusChronUnitsVal = minusChronUnitsVal;
        this.chronoUnit = chronoUnit;
    }

    RssTimeIntervalEnum() {
        this.minusChronUnitsVal = 0L;
        this.chronoUnit = ChronoUnit.FOREVER;
    }

    public LocalDateTime getDateFrom() {
        return (this == FAR_FAR_AWAY) ? LocalDateTime.of(1900, 1, 1, 0, 0) : LocalDateTime.now(ZoneOffset.UTC).minus(minusChronUnitsVal, chronoUnit);
    }
}
