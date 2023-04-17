package me.levansj01.verus.util.item;

import org.bukkit.*;

import java.util.*;

import org.bukkit.material.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

public class ItemBuilder {
    private Material type;
    private int amount;
    private List<String> lore;
    private short damage;
    private String name;

    public ItemBuilder setTypeAndData(MaterialData materialData) {
        this.type = materialData.getItemType();
        this.damage = materialData.getData();
        return this;
    }

    public Material getType() {
        return this.type;
    }

    public short getDamage() {
        return this.damage;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public int getAmount() {
        return this.amount;
    }

    public ItemBuilder setType(Material type) {
        this.type = type;
        return this;
    }

    public ItemStack build() {
        ItemStack item = new ItemStack(this.type);
        item.setAmount(this.amount);
        item.setDurability(this.damage);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(this.name);
        meta.setLore(this.lore);
        item.setItemMeta(meta);
        return item;
    }

    public ItemBuilder() {
        this.amount = 1;
    }

    public List<String> getLore() {
        return this.lore;
    }

    public ItemBuilder(Material type) {
        this.amount = 1;
        this.type = type;
    }

    public ItemBuilder setDamage(int n) {
        this.damage = (short) n;
        return this;
    }

    public ItemBuilder setName(String name) {
        this.name = name;
        return this;
    }
}
