package dev.eternal.client.ui.clickgui.primordial.components.checkbox;

import dev.eternal.client.module.Module;

public class PrimordialModuleCheckBox extends AbstractPrimordialCheckBox {

  private final Module module;

  public PrimordialModuleCheckBox(Module module) {
    super(module.moduleInfo().name());
    this.state = module.isEnabled();
    this.module = module;
  }

  @Override
  public void render(double x, double y, int mouseX, int mouseY) {
    super.render(x, y, mouseX, mouseY);
  }

  @Override
  public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
    boolean b = super.mouseClicked(mouseX, mouseY, mouseButton);
    module.enabled(state);
    return b;
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY) {

  }
}
