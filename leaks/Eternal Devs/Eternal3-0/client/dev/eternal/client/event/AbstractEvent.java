package dev.eternal.client.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractEvent {
  private boolean cancelled;
}
