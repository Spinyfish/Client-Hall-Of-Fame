package dev.eternal.client.property.impl.mode;


import dev.eternal.client.Client;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

import java.util.Objects;

@Getter
@Setter
public abstract class Mode implements IToggleable {

  protected final Client client = Client.singleton();
  protected final Minecraft mc = Minecraft.getMinecraft();
  private final IToggleable owner;
  private Property<?> property;
  private final String name;


  public Mode(IToggleable owner, String name) {
    this.owner = owner;
    this.name = name;
  }

  @SuppressWarnings("unchecked")
  public <T extends IToggleable> T getOwner() {
    return (T) owner;
  }

  @Override
  public boolean isEnabled() {
    return Objects.equals(property.value(), this);
  }

  public final void init() {
    for (Property<?> property : Client.singleton().propertyManager().get(this)) {
      property.visible(() -> this.property.value() == this);
    }
  }

  public void onEnable() {
  }

  public void onDisable() {
  }

}
