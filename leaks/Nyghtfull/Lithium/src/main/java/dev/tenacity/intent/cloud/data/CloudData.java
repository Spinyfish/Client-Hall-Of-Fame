package dev.tenacity.intent.cloud.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class CloudData extends Upvoteable {

    private final String name, description, shareCode, author, version, lastUpdated;
    private final boolean ownership;
    private boolean pinned;
    private Votes votes = new Votes(0, 0);

    public int minutesSinceLastUpdate() {
        return (int) (((System.currentTimeMillis() / 1000f) - Long.parseLong(lastUpdated)) / 60f);
    }

    public int daysSinceLastUpdate() {
        return (int) (((System.currentTimeMillis() / 1000f) - Long.parseLong(lastUpdated)) / 60f / 60f / 24f);
    }

}
