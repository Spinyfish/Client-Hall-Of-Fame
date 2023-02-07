package org.crystalpvp.anticheat.gui;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.GUI;
import org.crystalpvp.anticheat.api.util.ItemUtil;
import org.crystalpvp.anticheat.api.util.CrystalUtil;
import org.crystalpvp.anticheat.gui.checks.AuthorGUI;
import org.crystalpvp.anticheat.gui.checks.ChecksGUI;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.WeakHashMap;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class MainGUI implements Listener {

    @Getter
    private GUI gui = new GUI(null, "           &b&lCrystal&3&l AntiCheat", 27);
    @Getter private Map<String, ToggleType> editType;
    @Getter private ChecksGUI checksGUI;
    @Getter private AuthorGUI authorGUI;


    private CrystalAC crystal;
    private byte stainedNumber = 0;

    public MainGUI(CrystalAC crystal) {
        this.crystal = crystal;
        this.crystal.registerListener(this);

        this.editType = new WeakHashMap<>();
        this.checksGUI = new ChecksGUI(crystal);
        this.authorGUI = new AuthorGUI(crystal);

        loadItems();
    }

    public void loadItems() {
        byte stained = 15;

        this.gui.setItem(0, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(1, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(2, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(3, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(4, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(5, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(6, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(7, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(8, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(10, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(12, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(14, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(9, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(16, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(17, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(18, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(19, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(20, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(21, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(22, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(23, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(24, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(25, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(26, ItemUtil.guiItem(Material.STAINED_GLASS_PANE, " ", stained));
        this.gui.setItem(11, ItemUtil.guiItem(Material.ENCHANTED_BOOK, "&b&l» Toggle AutoBans «", "", "&7Click to toggle autobans"));
        this.gui.setItem(15, ItemUtil.guiItem(Material.ENCHANTED_BOOK, "&b&l» Toggle Checks «", "", "&7Click to toggle checks"));
        this.gui.setItem(13, ItemUtil.guiItem(Material.ENCHANTED_BOOK, "&b&lCrystalAC", "", CC.translate("&7Author > 4Remi#8652"), CC.translate("&7Discord > https://discord.gg/F9R2RXHBbN"), CC.translate("&7Version > ") + CrystalAC.BUILD_NAME + " - BUILD: " + CrystalAC.BUILD_NUM, ""));
        //this.gui.setItem(17, ItemUtil.guiItem(Material.BOOK_AND_QUILL, "&b» Restart CrystalAC «", "", "&7Restart CrystalAC Config"));
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

                    String display = event.getCurrentItem().getItemMeta().getDisplayName();
                    if (display.contains("» Toggle AutoBans «")) {
                        editType.put(player.getDisplayName(), ToggleType.PUNISHMENT_TOGGLE);
                        this.openCheck(player);
                    }
                    else if (display.contains("» Toggle Checks «")) {
                        editType.put(player.getDisplayName(), ToggleType.CHECK_TOGGLE);
                        this.openCheck(player);
                    }
                    else if (display.contains("» Staff «")){
                        editType.put(player.getDisplayName(), ToggleType.STAFF);
                        this.openAuthors(player);
                    }
                    else if (display.contains("» Restart CrystalAC «")){
                        player.performCommand("crystalac reload");
                    }
                    else if (display.contains("CrystalAC")) {
                        player.sendMessage(CC.translate("&nAuthor: 4Remi#8652"));
                    }
                }
            }
        }
    }


    public void openCheck(Player player) {
        player.openInventory(checksGUI.getGui().getInventory());
    }
    public void openAuthors(Player player) {
        player.openInventory(authorGUI.getAuthorGUI().getInventory());
    }

    public enum ToggleType {
        CHECK_TOGGLE, PUNISHMENT_TOGGLE, STAFF;
    }
}