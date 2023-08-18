/* November.lol © 2023 */
package lol.november.listener.event.render;

import lol.november.listener.bus.Cancelable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Setter
public class EventRenderItemUse extends Cancelable {

  private final ItemStack itemStack;
  private EnumAction action;

  public EventRenderItemUse(ItemStack itemStack, EnumAction action) {
    this.itemStack = itemStack;
    this.action = action;
  }
}
