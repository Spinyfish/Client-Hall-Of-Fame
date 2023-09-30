package dev.eternal.client.module.impl.render;

import dev.eternal.client.event.Subscribe;;
import dev.eternal.client.event.events.EventRenderEntity;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "Chams", category = Module.Category.RENDER)
public class Chams extends Module {
  @Subscribe
  public void handleRenderingEntity(EventRenderEntity eventRenderEntity) {
    if (eventRenderEntity.pre()) {
      GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
      GL11.glPolygonOffset(1.0F, -6900000.0f);
    } else {
      GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
      GL11.glPolygonOffset(1.0F, 6900000.0f);
    }
  }
}
