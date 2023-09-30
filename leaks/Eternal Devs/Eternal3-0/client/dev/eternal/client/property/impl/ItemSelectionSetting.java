package dev.eternal.client.property.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemSelectionSetting extends Property<List<ItemStack>> {

  public ItemSelectionSetting(IToggleable owner, String name) {
    super(owner, name, new ArrayList<>(), PropertyType.ITEM_SELECTION);
  }

  public void add(Item item) {
    value().add(new ItemStack(item, 1));
  }

  public void remove(Item item) {
    value().removeIf(itemStack -> itemStack.getItem().equals(item));
  }

  @Override
  public ItemSelectionSetting value(List<ItemStack> list) {
    throw new RuntimeException("Please use ItemSelectionSetting#add(Item)/ItemSelectionSetting#remove(Item) " +
        "instead of ItemSelectionSetting#setValue(List<ItemStack>)");
  }

  @Override
  public JsonElement getJsonElement() {
    JsonArray jsonArray = new JsonArray();
    value().stream().map(ItemStack::getItem).map(Item::getUnlocalizedName).forEach(jsonArray::add);
    return jsonArray;
  }

  @Override
  public void loadJsonElement(JsonElement jsonElement) {
    //TODO implement when needed
  }

}
