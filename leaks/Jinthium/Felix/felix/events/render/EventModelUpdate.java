package felix.events.render;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;

public final class EventModelUpdate {
   private final EntityPlayer player;
   private final ModelPlayer model;

   public EventModelUpdate(EntityPlayer player, ModelPlayer model) {
      this.player = player;
      this.model = model;
   }

   public final EntityPlayer getPlayer() {
      return this.player;
   }

   public final ModelPlayer getModel() {
      return this.model;
   }
}
