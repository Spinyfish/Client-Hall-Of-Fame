package org.crystalpvp.anticheat.gui.checks;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.util.CrystalUtil;
import org.crystalpvp.anticheat.api.util.GUI;
import org.crystalpvp.anticheat.api.util.ItemUtil;
import org.crystalpvp.anticheat.gui.MainGUI;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class ChecksGUI implements Listener {

    @Getter
    private GUI gui = new GUI(null, "Checks", 27);
    @Getter CombatGUI combatGUI;
    @Getter MovementGUI movementGUI;
    @Getter OtherGUI otherGUI;

    private CrystalAC crystal;

    public ChecksGUI(CrystalAC crystal) {
        this.crystal = crystal;
        this.crystal.registerListener(this);

        this.combatGUI = new CombatGUI(crystal);
        this.movementGUI = new MovementGUI(crystal);
        this.otherGUI = new OtherGUI(crystal);

        loadItems();
    }

    public void loadItems() {
        this.gui.setItem(11, ItemUtil.guiItem(Material.DIAMOND_SWORD, "&eCombat Checks"));
        this.gui.setItem(13, ItemUtil.guiItem(Material.DIAMOND_BOOTS, "&eMovement Checks"));
        this.gui.setItem(15, ItemUtil.guiItem(Material.PAPER, "&eOther Checks"));
        this.gui.setItem(26, ItemUtil.guiItem(Material.WOOD_DOOR, "&b&lBACK"));
    }

    @EventHandler
    public void onGUIClick(InventoryClickEvent event) {
        if (event.getInventory().getName().equals(gui.getInventory().getName())) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player player = (Player)event.getWhoClicked();
                if (CrystalUtil.hasPermission(player, "crystalac.staff")) {
                    if (!event.getCurrentItem().hasItemMeta())
                        return;

                    MainGUI.ToggleType type = crystal.getMainGUI().getEditType().get(player.getDisplayName());
                    String display = event.getCurrentItem().getItemMeta().getDisplayName();

                    if (display.contains("Combat Checks")) {
                        player.openInventory(type == MainGUI.ToggleType.CHECK_TOGGLE ? combatGUI.getCheckToggleGUI().getInventory() : combatGUI.getBanToggleGUI().getInventory());
                    }
                    else if (display.contains("Movement Checks")) {
                        player.openInventory(type == MainGUI.ToggleType.CHECK_TOGGLE ? movementGUI.getCheckToggleGUI().getInventory() : movementGUI.getBanToggleGUI().getInventory());
                    }
                    else if (display.contains("Other Checks")) {
                        player.openInventory(type == MainGUI.ToggleType.CHECK_TOGGLE ? otherGUI.getCheckToggleGUI().getInventory() : otherGUI.getBanToggleGUI().getInventory());
                    }
                    else if (display.contains("BACK")) {
                        player.openInventory(crystal.getMainGUI().getGui().getInventory());
                    }
                }
            }
        }
        else if (event.getInventory().getName().contains("Toggling")) {
            if (event.getCurrentItem() != null) {
                if (event.getCurrentItem().getType() == Material.ARROW) {
                    event.getWhoClicked().openInventory(gui.getInventory());
                }
            }
        }
    }
}
