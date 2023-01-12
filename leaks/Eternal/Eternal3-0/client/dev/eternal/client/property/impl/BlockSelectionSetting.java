package dev.eternal.client.property.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockSelectionSetting extends Property<List<ItemStack>> {

  public BlockSelectionSetting(IToggleable owner, String name) {
    super(owner, name, new ArrayList<>(), PropertyType.BLOCK_SELECTION);
  }

  public void add(Block block) {
    value().add(new ItemStack(block, 1));
  }

  public void remove(Block block) {
    value().removeIf(itemStack -> itemStack.getItem().equals(block.getItem(null, null)));
  }

  @Override
  public BlockSelectionSetting value(List<ItemStack> list) {
    throw new RuntimeException("Please use BlockSelectionSetting#add(Block)/BlockSelectionSetting#remove(Block) " +
        "instead of BlockSelectionSetting#setValue(List<ItemStack>)");
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
