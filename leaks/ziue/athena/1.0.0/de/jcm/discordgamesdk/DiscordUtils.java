package de.jcm.discordgamesdk;

import java.util.concurrent.*;
import java.util.function.*;
import java.time.*;

public class DiscordUtils
{
    private DiscordUtils() {
        throw new RuntimeException("DiscordUtils is a static class and no instance of it can be obtained.");
    }
    
    public static Consumer<Result> completer(final CompletableFuture<Void> future) {
        return result -> {
            if (result == Result.OK) {
                future.complete(null);
            }
            else {
                future.completeExceptionally(new GameSDKException(result));
            }
        };
    }
    
    public static <T> BiConsumer<Result, T> returningCompleter(final CompletableFuture<T> future) {
        return (result, t) -> {
            if (result == Result.OK) {
                future.complete(t);
            }
            else {
                future.completeExceptionally(new GameSDKException(result));
            }
        };
    }
    
    public static Instant dateTimeFromSnowflake(final long snowflake) {
        final long discordTime = snowflake >> 22;
        final long unixTime = discordTime + 1420070400000L;
        return Instant.ofEpochMilli(unixTime);
    }
}
