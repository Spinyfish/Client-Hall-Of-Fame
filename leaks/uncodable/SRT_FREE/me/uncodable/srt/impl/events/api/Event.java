package me.uncodable.srt.impl.events.api;

import net.minecraft.client.Minecraft;

public abstract class Event {
   protected static final Minecraft MC = Minecraft.getMinecraft();
   private boolean cancelled;

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }
}
