package dev.eternal.client.ui.clickgui.primordial.components.button;

import dev.eternal.client.ui.clickgui.primordial.Component;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class PrimordialIconButton extends Component {

  private final ResourceLocation resourceLocation;

  public PrimordialIconButton(ResourceLocation resourceLocation) {
    this.resourceLocation = resourceLocation;
  }

  @Override
  public void render(double x, double y, int mouseX, int mouseY) {
    MC.getTextureManager().bindTexture(resourceLocation);
    GlStateManager.pushMatrix();

    GlStateManager.popMatrix();
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    return false;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY) {

  }
}
