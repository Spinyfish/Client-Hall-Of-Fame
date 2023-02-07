package org.crystalpvp.anticheat.api.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI {

    private Player owner;
    private int size;
    private String displayName;
    private Inventory inventory;

    /**
     * GUI API
     *
     * @param owner - Owner of the inventory, nulling is fine
     * @param displayName - The title of the inventory, seen in top right
     * @param size - The size of the inventory, must be multiples of 9
     */
    public GUI(Player owner, String displayName, int size) {
        this.owner = owner;
        this.size = size;
        this.displayName = displayName;
        this.inventory = Bukkit.createInventory(owner, size, CC.translate(displayName));
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void displayMenu(Player player) {
        player.openInventory(this.inventory);
    }

    public void setItem(int i, ItemStack itemStack) {
        this.inventory.setItem(i, itemStack);
    }

    public void clearInventory() {
        this.inventory.clear();
    }

    public void setContents(Inventory inv) {
        this.inventory.setContents(inv.getContents());
    }

    public ItemStack[] getContents() {
        return this.inventory.getContents();
    }

    public void setRows(int rows) {
        this.inventory = Bukkit.createInventory(owner, rows * 9, CC.translate(displayName));
    }
}
