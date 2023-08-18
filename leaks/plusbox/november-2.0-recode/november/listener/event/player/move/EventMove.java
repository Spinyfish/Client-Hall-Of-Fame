/* November.lol © 2023 */
package lol.november.listener.event.player.move;

import lol.november.listener.bus.Cancelable;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public class EventMove extends Cancelable {

  private double x, y, z;

  public EventMove(double x, double y, double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
