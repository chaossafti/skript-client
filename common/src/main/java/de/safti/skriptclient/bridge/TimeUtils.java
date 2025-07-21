package de.safti.skriptclient.bridge;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public class TimeUtils {
    public static TemporalUnit UNIT_TICK = new TickUnit();

    public static class TickUnit implements TemporalUnit {
        @Override
        public Duration getDuration() {
            return Duration.ofMillis(50);
        }

        @Override
        public boolean isDurationEstimated() {
            return false;
        }

        @Override
        public boolean isDateBased() {
            return false;
        }

        @Override
        public boolean isTimeBased() {
            return false;
        }

        @Override
        public <R extends Temporal> R addTo(R temporal, long amount) {
            return (R) temporal.plus(Duration.ofMillis(50*amount));
        }

        @Override
        public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
            return temporal1Inclusive.until(temporal2Exclusive, ChronoUnit.MILLIS)/50;
        }
    }
}
