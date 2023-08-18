/* November.lol © 2023 */
package lol.november.listener.event.player;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public class EventHitbox {

  private final Entity entity;
  private AxisAlignedBB box;

  public EventHitbox(Entity entity, AxisAlignedBB box) {
    this.entity = entity;
    this.box = box;
  }
}
