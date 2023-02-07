package org.crystalpvp.anticheat.gui.checks;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.crystalpvp.anticheat.api.util.*;
import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.gui.MainGUI;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AuthorGUI implements Listener {

    @Getter
    private GUI AuthorGUI = new GUI(null, "» Staff «", 27);

    private CrystalAC crystal;


    public AuthorGUI(CrystalAC crystal) {
        this.crystal = crystal;
        this.crystal.registerListener(this);

        loadItems();
    }

    public void loadItems() {
        this.AuthorGUI.setItem(12, ItemUtil.guiItem(Material.PAPER, "&cOwner » CrystalPvP_YT «"));
        this.AuthorGUI.setItem(14, ItemUtil.guiItem(Material.PAPER, "&cOwner » Rory Renfro «"));
        this.AuthorGUI.setItem(22, ItemUtil.guiItem(Material.PAPER, "&4Admin » Army «"));
        this.AuthorGUI.setItem(20, ItemUtil.guiItem(Material.PAPER, "&eTester » Cabb «"));
        this.AuthorGUI.setItem(24, ItemUtil.guiItem(Material.PAPER, "&eTester » yDriven «"));
        this.AuthorGUI.setItem(26, ItemUtil.guiItem(Material.ARROW, "&b&lBACK"));
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals(AuthorGUI.getInventory().getName())) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player) event.getWhoClicked();
                if (CrystalUtil.hasPermission(player, "crystalac.staff")) {
                    if (!event.getCurrentItem().hasItemMeta())
                        return;

                    MainGUI.ToggleType type = crystal.getMainGUI().getEditType().get(player.getDisplayName());
                    String display = event.getCurrentItem().getItemMeta().getDisplayName();

                    if (display.contains("BACK")) {
                        player.openInventory(crystal.getMainGUI().getGui().getInventory());
                    }
                }
            }
        }
    }
}
