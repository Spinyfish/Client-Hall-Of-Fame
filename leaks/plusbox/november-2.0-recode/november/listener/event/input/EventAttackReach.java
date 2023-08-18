/* November.lol © 2023 */
package lol.november.listener.event.input;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Setter
@Getter
public class EventAttackReach {

  private double reach;

  public EventAttackReach(double reach) {
    this.reach = reach;
  }
}
