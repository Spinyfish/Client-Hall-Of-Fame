package wtf.automn.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiButtonTexture extends GuiButton {

  protected ResourceLocation texture;

  public GuiButtonTexture(int buttonId, int x, int y, int widthIn, int heightIn, String texture) {
    super(buttonId, x, y, widthIn, heightIn, "");
    this.texture = new ResourceLocation(texture);
  }

  public GuiButtonTexture(int buttonId, int x, int y, String texture) {
    super(buttonId, x, y, "");
    this.texture = new ResourceLocation(texture);
  }

  public void drawButton(Minecraft mc, int cX, int cY, int mouseX, int mouseY) {
    if (this.visible) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.hovered = mouseX >= this.xPosition + cX && mouseY >= this.yPosition + cY && mouseX < this.xPosition + cX + this.width && mouseY < this.yPosition + cY + this.height;
      int i = this.getHoverState(this.hovered);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.blendFunc(770, 771);

      Color unhover = new Color(0, 0, 255, 150);
      Color hover = new Color(0, 100, 255, 150);
      mc.getTextureManager().bindTexture(texture);
      drawModalRectWithCustomSizedTexture(this.xPosition + cX + 5 - 0.5F, this.yPosition + cY + 5 - 0.5F, 0, 0, width - 10, height - 10, width - 10, height - 10);
      GL11.glColor4f(1, 1, 1, 1);
    }
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    this.drawButton(mc, 0, 0, mouseX, mouseY);
  }

}
