package dev.eternal.client.property;

import com.google.gson.JsonElement;
import dev.eternal.client.Client;
import dev.eternal.client.module.interfaces.IToggleable;
import lombok.Getter;
import lombok.Setter;

import java.util.function.BooleanSupplier;

@Getter
@Setter
public abstract class Property<T> {

  private final IToggleable owner;
  private final String name;
  private final String description;
  private final PropertyType propertyType;
  private T value;
  private BooleanSupplier visible;

  protected Property(IToggleable owner, String name, String description, T value, PropertyType propertyType) {
    this.owner = owner;
    this.name = name;
    this.description = description;
    this.value = value;
    this.visible = () -> true;
    this.propertyType = propertyType;
    Client.singleton().propertyManager().add(this);
  }

  protected Property(IToggleable owner, String name, T value, PropertyType propertyType) {
    this.owner = owner;
    this.name = name;
    this.description = "No description.";
    this.value = value;
    this.visible = () -> true;
    this.propertyType = propertyType;
    Client.singleton().propertyManager().add(this);
  }

  public enum PropertyType {
    BLOCK_SELECTION,
    BOOLEAN,
    COLOUR,
    ENUM,
    ITEM_SELECTION,
    MODE,
    NUMBER,
    TEXT
  }

  public abstract JsonElement getJsonElement();

  public abstract void loadJsonElement(JsonElement jsonElement);

}
