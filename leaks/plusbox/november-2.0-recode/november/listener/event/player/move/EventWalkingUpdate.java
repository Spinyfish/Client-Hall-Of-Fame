/* November.lol © 2023 */
package lol.november.listener.event.player.move;

import lol.november.listener.bus.Cancelable;
import lol.november.listener.event.Stage;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public class EventWalkingUpdate extends Cancelable {

  private final Stage stage;
  private double x, y, z;
  private float yaw, pitch;
  private boolean onGround;
  private double minMove = 9.0E-4;

  public EventWalkingUpdate(
    Stage stage,
    double x,
    double y,
    double z,
    float yaw,
    float pitch,
    boolean onGround
  ) {
    this.stage = stage;
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
    this.onGround = onGround;
  }
}
