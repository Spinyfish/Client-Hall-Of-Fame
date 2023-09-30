package wtf.automn.events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import wtf.automn.events.events.Event;

@Data
@AllArgsConstructor
public class EventMotion implements Event {

  private double x, y, z;
  private float yaw, pitch;
  private boolean onGround;

}
