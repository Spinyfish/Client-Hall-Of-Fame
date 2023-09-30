package dev.eternal.client.module;

import dev.eternal.client.Client;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.ModeSetting;
import dev.eternal.client.property.impl.mode.Mode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

@Getter
@Setter
public class Module implements IToggleable {

  protected static final Client client = Client.singleton();
  protected static final Minecraft mc = Minecraft.getMinecraft();
  protected static final FontRenderer fr = mc.fontRendererObj;
  private ModuleInfo moduleInfo;
  private boolean enabled;
  private int keyBind;

  public Module() {
    if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
      this.moduleInfo = this.getClass().getAnnotation(ModuleInfo.class);
      this.keyBind(moduleInfo.defaultKey());
    } else {
      throw new RuntimeException("Required annotation missing on module " + this.getClass().getSimpleName() + "!");
    }
  }

  public void init() {
  }

  public String getSuffix() {
    Mode mode = Client.singleton().propertyManager()
        .getStream(this)
        .filter(property -> property instanceof ModeSetting)
        .map(property -> (ModeSetting) property)
        .map(ModeSetting::value)
        .findFirst()
        .orElse(null);
    return mode == null ? "" : mode.name();
  }

  public void toggle() {
    this.enabled = !this.enabled;
    if (this.enabled) {
      Client.singleton().eventBus().register(this);
      onEnable();
    } else {
      Client.singleton().eventBus().unregister(this);
      onDisable();
    }
    Client.singleton().propertyManager()
        .getStream(this)
        .filter(property -> property instanceof ModeSetting)
        .map(property -> (ModeSetting) property)
        .map(ModeSetting::value)
        .forEach(mode -> {
          if (this.isEnabled()) {
            mode.onEnable();
            Client.singleton().eventBus().register(mode);
          } else {
            Client.singleton().eventBus().unregister(mode);
            mode.onDisable();
          }
        });
  }

  protected void onEnable() {}

  protected void onDisable() {}

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Getter
  @AllArgsConstructor
  public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    MISC("Misc"),
    PLAYER("Player"),
    SERVER("Server"),
    EXPLOIT("Exploit");

    private final String categoryName;
  }
}