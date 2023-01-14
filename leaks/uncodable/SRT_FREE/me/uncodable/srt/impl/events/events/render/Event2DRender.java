package me.uncodable.srt.impl.events.events.render;

import me.uncodable.srt.impl.events.api.Event;
import net.minecraft.client.gui.ScaledResolution;

public class Event2DRender extends Event {
   private final float partialTicks;
   private final ScaledResolution scaledResolution;

   public Event2DRender(float partialTicks, ScaledResolution scaledResolution) {
      this.partialTicks = partialTicks;
      this.scaledResolution = scaledResolution;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public ScaledResolution getScaledResolution() {
      return this.scaledResolution;
   }
}
