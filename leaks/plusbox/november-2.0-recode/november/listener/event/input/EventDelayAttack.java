/* November.lol © 2023 */
package lol.november.listener.event.input;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public class EventDelayAttack {

  private int leftClickCounter;

  public EventDelayAttack(int leftClickCounter) {
    this.leftClickCounter = leftClickCounter;
  }
}
