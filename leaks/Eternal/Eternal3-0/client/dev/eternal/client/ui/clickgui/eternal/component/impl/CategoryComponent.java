package dev.eternal.client.ui.clickgui.eternal.component.impl;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.ui.clickgui.eternal.EternalClickGui;
import dev.eternal.client.ui.clickgui.eternal.component.Component;
import dev.eternal.client.util.render.BloomUtil;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import static net.minecraft.client.gui.Gui.drawRect;

/**
 * @author Hazsi
 */
public class CategoryComponent extends Component {
  private final Module.Category category;

  private boolean dragging;
  private float lastMouseX, lastMouseY;
  private double lastRotationAngle;

  // Constants
  protected final int moduleWidth = 125;
  protected final int moduleHeight = 16;
  protected final double originalX;
  protected final double originalY;

  private final ArrayList<ModuleComponent> moduleComponents = new ArrayList<>();

  public CategoryComponent(Module.Category category, double x, double y) {
    super(x, y);
    this.category = category;
    x(this.originalX = x);
    y(this.originalY = y);
    // Add our modules
    Client.singleton().moduleManager()
        .getModulesByCategory(category)
        .forEach(module -> moduleComponents.add(new ModuleComponent(module, this)));

  }

  @Override
  public void draw(int mouseX, int mouseY, float partialTicks) {

    GL11.glPushMatrix();
    // Rotate when dragging
    final double deltaMouseX = mouseX - lastMouseX;
    final double deltaMouseY = mouseY - lastMouseY;
    double goalRotationAngle = dragging ? MathHelper.clamp_double(deltaMouseX * 4, -45, 45) : 0;
    goalRotationAngle = lastRotationAngle + (goalRotationAngle - lastRotationAngle) / 15D;

    GL11.glTranslated(mouseX, mouseY, 0);
    GL11.glRotated(goalRotationAngle, 0, 0, 1);
    GL11.glTranslated(-mouseX, -mouseY, 0);

    lastRotationAngle = goalRotationAngle;

    // Update the position of our category if we're dragging, and update the last frame mouseX and mouseY
    if (dragging) {
      x(x() + deltaMouseX);
      y(y() + deltaMouseY);
    }

    lastMouseX = mouseX;
    lastMouseY = mouseY;

    // Draw the bloom/shadow
    BloomUtil.bloom((int) x() - 5, (int) y() - 5, moduleWidth + 10, (moduleComponents.size() + 1) * moduleHeight + 10, 25, 100);

    // Draw category title background
    drawRect(x(), y(), x() + moduleWidth, y() + moduleHeight, EternalClickGui.categoryTitleBackground.getRGB());

    // Draw category title text
    iCiel.drawString(category.name(), x() + 8, y() + 5.5, -1);

    // Draw the modules
    int moduleY = moduleHeight;
    for (ModuleComponent moduleComponent : moduleComponents) {
      moduleComponent.x(x());
      moduleComponent.y(y() + moduleY);
      moduleComponent.draw(mouseX, mouseY, partialTicks);
      moduleY += moduleComponent.expanded() ? moduleHeight + moduleComponent.expandY() + moduleComponent.settingComponentList().size() * 16 : moduleHeight + moduleComponent.expandY();
    }

    GL11.glPopMatrix();
  }

  @Override
  public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    if (isInTitleBar(mouseX, mouseY) && mouseButton == 0) dragging = true;

    // Send the click to all of our modules
    for (ModuleComponent component : moduleComponents) {
      component.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY, int state) {
    if (isInTitleBar(mouseX, mouseY)) dragging = false;
    // Send the click to all of our modules
    for (ModuleComponent component : moduleComponents) {
      component.mouseReleased(mouseX, mouseY, state);
    }
  }

  @Override
  public void keyPress(char typedChar, int keyPressed) {
    for (ModuleComponent component : moduleComponents) {
      component.keyPress(typedChar, keyPressed);
    }
  }

  private boolean isInTitleBar(int mouseX, int mouseY) {
    return (x() < mouseX && mouseX < x() + moduleWidth && y() < mouseY && mouseY < y() + moduleHeight);
  }

  private boolean isInColumn(int mouseX, int mouseY) {
    return (x() < mouseX && mouseX < x() + moduleWidth);
  }
}
