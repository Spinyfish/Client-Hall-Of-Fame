package dev.eternal.client.ui.clickgui.primordial;

import dev.eternal.client.property.Property;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class Component {

  protected final Minecraft MC = Minecraft.getMinecraft();
  protected final FontRenderer FR = MC.fontRendererObj;
  @Getter
  protected Property<?> setting;
  @Getter
  protected Type type;

  public abstract void render(double x, double y, int mouseX, int mouseY);

  public abstract boolean mouseClicked(int mouseX, int mouseY, int mouseButton);

  public abstract void mouseReleased(int mouseX, int mouseY);

  public void keyTyped(char typedChar, int keyCode) {

  }

  public float offset() {
    return 12f;
  }

  public enum Type {
    BOOLEANBOX,
    OPTIONBOX,
    VALUESLIDER,
    TEXTFIELD;
  }
}
