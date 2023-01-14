package me.uncodable.srt.impl.events.events.render;

import me.uncodable.srt.impl.events.api.Event;
import net.minecraft.entity.Entity;

public class EventNameTagRender extends Event {
   private final Entity entity;

   public EventNameTagRender(Entity entity) {
      this.entity = entity;
   }

   public Entity getEntity() {
      return this.entity;
   }
}
