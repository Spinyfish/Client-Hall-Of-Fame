package me.uncodable.srt.impl.events.events.render;

import me.uncodable.srt.impl.events.api.Event;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

public class EventEntityRender extends Event {
   private ICamera iCamera;
   private Entity renderedEntity;
   private float partialTicks;

   public EventEntityRender(Entity renderedEntity, ICamera iCamera, float partialTicks) {
      this.renderedEntity = renderedEntity;
      this.iCamera = iCamera;
      this.partialTicks = partialTicks;
   }

   public ICamera getICamera() {
      return this.iCamera;
   }

   public Entity getRenderedEntity() {
      return this.renderedEntity;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public void setICamera(ICamera iCamera) {
      this.iCamera = iCamera;
   }

   public void setRenderedEntity(Entity renderedEntity) {
      this.renderedEntity = renderedEntity;
   }

   public void setPartialTicks(float partialTicks) {
      this.partialTicks = partialTicks;
   }
}
