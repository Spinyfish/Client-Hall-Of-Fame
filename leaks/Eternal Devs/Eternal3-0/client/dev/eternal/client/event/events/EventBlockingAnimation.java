package dev.eternal.client.event.events;

import dev.eternal.client.event.AbstractEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.ItemRenderer;

@Getter
@Setter
@AllArgsConstructor
public class EventBlockingAnimation extends AbstractEvent {

  private final float swingProgress, useProgress;
  private final ItemRenderer itemRenderer;

}
