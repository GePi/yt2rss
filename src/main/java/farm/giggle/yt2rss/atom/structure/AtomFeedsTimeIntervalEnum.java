package farm.giggle.yt2rss.atom.structure;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public enum AtomFeedsTimeIntervalEnum {
    LAST_DAY(1440L, ChronoUnit.MINUTES),
    LAST_THREE_DAYS(3L, ChronoUnit.DAYS),
    LAST_HOUR(60L, ChronoUnit.MINUTES),
    FAR_FAR_AWAY();

    private final Long minusChronUnitsVal;
    private final ChronoUnit chronoUnit;

    AtomFeedsTimeIntervalEnum(Long minusChronUnitsVal, ChronoUnit chronoUnit) {
        this.minusChronUnitsVal = minusChronUnitsVal;
        this.chronoUnit = chronoUnit;
    }

    AtomFeedsTimeIntervalEnum() {
        this.minusChronUnitsVal = 0L;
        this.chronoUnit = ChronoUnit.FOREVER;
    }

    public LocalDateTime getDateFrom() {
        return (this == FAR_FAR_AWAY) ? LocalDateTime.of(1900, 1, 1, 0, 0) : LocalDateTime.now(ZoneOffset.UTC).minus(minusChronUnitsVal, chronoUnit);
    }
}
