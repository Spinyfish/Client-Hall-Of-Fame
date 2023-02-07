package org.crystalpvp.anticheat.gui.checks;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.checks.CheckCategory;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.GUI;
import org.crystalpvp.anticheat.api.util.ItemUtil;
import org.crystalpvp.anticheat.api.util.CrystalUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class CombatGUI implements Listener {

    @Getter
    private GUI checkToggleGUI = new GUI(null, "Toggling C Checks", 54);
    @Getter private GUI banToggleGUI = new GUI(null, "Toggling C Bans", 54);

    private CrystalAC crystal;


    List<String> ChecksList = new ArrayList<>();


    public CombatGUI(CrystalAC crystal) {
        this.crystal = crystal;
        this.crystal.registerListener(this);

        loadItems();
    }

    public void loadItems() {

        ChecksList = ConfigurationManager.getChecksList();

        int s = 0;
        this.checkToggleGUI.clearInventory();
        this.banToggleGUI.clearInventory();
        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ConfigurationManager.getCheckCategory(i) == CheckCategory.COMBAT) {
                this.checkToggleGUI.setItem(s, ConfigurationManager.isEnabled(ChecksList.get(i)) ? ItemUtil.guiItem(Material.EMERALD_BLOCK, "&a"+ChecksList.get(i)) : ItemUtil.guiItem(Material.REDSTONE_BLOCK, "&c" + ChecksList.get(i)));
                this.banToggleGUI.setItem(s, ConfigurationManager.isAutoBanning(ChecksList.get(i)) ? ItemUtil.guiItem(Material.EMERALD_BLOCK, "&a"+ChecksList.get(i)) : ItemUtil.guiItem(Material.REDSTONE_BLOCK, "&c" + ChecksList.get(i)));
                s++;
            }
        }
        this.checkToggleGUI.setItem(53, ItemUtil.guiItem(Material.ARROW, "&b&lBACK"));
        this.banToggleGUI.setItem(53, ItemUtil.guiItem(Material.ARROW, "&b&lBACK"));
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {

        ChecksList = ConfigurationManager.getChecksList();

        String invName = event.getInventory().getName();
        if (invName.equals(checkToggleGUI.getInventory().getName()) || invName.equals(banToggleGUI.getInventory().getName())) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player)event.getWhoClicked();
                if (CrystalUtil.hasPermission(player, "crystalac.dev")) {
                    if (!event.getCurrentItem().hasItemMeta())
                        return;

                    String display = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

                    if (display != null) {
                        if (display.contains("BACK")) {
                            player.openInventory(crystal.getMainGUI().getGui().getInventory());
                        }
                        else if (invName.contains("Checks")) {
                            if (display.contains("BACK")) {
                                player.openInventory(crystal.getMainGUI().getGui().getInventory());
                            } else {
                                ConfigurationManager.toggleCheck(player, display);
                            }
                        }
                        else if (invName.contains("Bans")) {
                            if (display.contains("BACK")) {
                                player.openInventory(crystal.getMainGUI().getGui().getInventory());
                            } else {
                                ConfigurationManager.toggleAutoBan(player, display);
                            }
                        }
                        loadItems();
                    }
                }
            }
        }
    }
}
