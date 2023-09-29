package dev.eternal.client.tracker;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventTeleport;
import lombok.Getter;

@Getter
public class Tracker {

  private long timeSinceLastFlag;

  @Subscribe
  public void handleTeleport(EventTeleport eventTeleport) {
    timeSinceLastFlag = System.currentTimeMillis();
  }

  public boolean isVLHigh(long maxDelta) {
    return System.currentTimeMillis() - timeSinceLastFlag < maxDelta;
  }
}
