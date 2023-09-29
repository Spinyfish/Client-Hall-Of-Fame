package de.lirium.util.playtime;

import de.lirium.Client;
import god.buddy.aot.BCompiler;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.StringJoiner;
import java.util.TimeZone;

public class PlayTimeUtil {
    public static LocalDateTime getLocalTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
    }

    //TODO: Recode completely
    public static String getPlayTime(long currentTime) {
        final StringJoiner joiner = new StringJoiner(", ");
        final Duration duration = Duration.between(getLocalTime(Client.INSTANCE.initTime), getLocalTime(currentTime));
        long seconds = duration.getSeconds();
        long nano = 0;
        if (seconds <= 0) {
            nano = duration.getNano();
        }
        if (nano <= 0) {
            long minutes = seconds / 60;
            final long hours = minutes / 60;
            if (hours > 0) {
                joiner.add(hours + "h");
            }
            while (minutes >= 60)
                minutes -= 60;
            if (minutes > 0) {
                joiner.add(minutes + "min");
            }
            while (seconds >= 60) {
                seconds -= 60;
            }
            if (seconds > 0) {
                joiner.add(seconds + "s");
            }
        } else {
            if (nano > 1000000)
                joiner.add((nano / 1000000) + "ms");
            else
                joiner.add(nano + "ns");
        }
        return joiner.toString();
    }
}
