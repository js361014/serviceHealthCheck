package service.health.check.updater.time;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeMachine {
    private static Clock clock = Clock.systemDefaultZone();

    public static Instant getInstant() {
        return clock.instant();
    }

    public static ZoneId getZone() {
        return clock.getZone();
    }

    public static Timestamp nowTimestamp() {
        return Timestamp.from(getInstant());
    }

    public static void useFixedClockAt(Instant instant) {
        clock = Clock.fixed(instant, getZone());
    }

    public static void useUtcClock() {
        clock = Clock.systemUTC();
    }

    public static void useSystemDefaultZoneClock() {
        clock = Clock.systemDefaultZone();
    }
}
