package me.levansj01.verus.gui.impl;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.api.API;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.gui.GUI;
import me.levansj01.verus.gui.manager.GUIManager;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.database.DatabaseType;
import me.levansj01.verus.type.VerusTypeLoader;
import me.levansj01.verus.util.BukkitUtil;
import me.levansj01.verus.util.item.ItemBuilder;
import me.levansj01.verus.util.item.MaterialList;
import me.levansj01.verus.util.java.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.Metadatable;

public class MainGUI
        extends GUI {
    private static final String build = String.valueOf((long)VerusPlugin.getBuild());
    private static final ItemStack infoStack = new ItemStack(MaterialList.PAPER);
    private static final String verusType = WordUtils.capitalize((String)VerusTypeLoader.getVerusType().name().toLowerCase());
    public static final Set<UUID> ALLOWED_UUIDS = ImmutableSet.of(UUID.fromString("564b7f5e-9efc-4b18-9ff3-9ea62117b113"), UUID.fromString("34272ea9-008d-4cb0-9990-4085d7048ddb"), UUID.fromString("79295695-5fa7-4fa1-bc68-a16b09de873d"), UUID.fromString("954b39b2-2161-45ae-8b7d-b61c81109621"));
    private static final String format = VerusPlugin.COLOR + "Total %s %s";
    private static final ItemStack blank = new ItemBuilder().setType(MaterialList.STAINED_GLASS_PANE).setName(" ").build();
    private static final String implementation = Bukkit.getServer().getClass().getName().split("\\.")[3];

    public void updateInfoStack() {
        String string;
        String string2;
        String string3;
        String string4;
        ItemMeta itemMeta = infoStack.getItemMeta();
        itemMeta.setDisplayName(VerusPlugin.COLOR + VerusPlugin.getNameFormatted() + " Information");
        StorageEngine storageEngine = StorageEngine.getInstance();
        DatabaseType databaseType = storageEngine.getType();
        API aPI = API.getAPI();
        Object[] objectArray = new String[10];
        objectArray[0] = "";
        objectArray[1] = VerusPlugin.COLOR + "Type " + ChatColor.WHITE + verusType;
        StringBuilder stringBuilder = new StringBuilder().append((Object)VerusPlugin.COLOR).append("Build ").append((Object)ChatColor.WHITE).append(build);
        if (aPI == null) {
            string4 = "";
        } else {
            string4 = ChatColor.GRAY + " (API v" + aPI.getVersion() + ")";
        }
        objectArray[2] = stringBuilder.append(string4).toString();
        objectArray[3] = VerusPlugin.COLOR + "Implementation " + ChatColor.WHITE + implementation;
        StringBuilder stringBuilder2 = new StringBuilder().append((Object)VerusPlugin.COLOR).append("Storage: ").append((Object)ChatColor.WHITE);
        if (databaseType != null) {
            string3 = databaseType.name();
        } else {
            string3 = "None";
        }
        objectArray[4] = stringBuilder2.append(string3).toString();
        objectArray[5] = "";
        Object[] objectArray2 = new Object[2];
        objectArray2[0] = "Bans";
        if (storageEngine.isConnected()) {
            string2 = ChatColor.WHITE + String.valueOf((int)storageEngine.getDatabase().getTotalBans());
        } else {
            string2 = ChatColor.RED + "Not Connected";
        }
        objectArray2[1] = string2;
        objectArray[6] = String.format((String)format, (Object[])objectArray2);
        Object[] objectArray3 = new Object[2];
        objectArray3[0] = "Logs";
        if (storageEngine.isConnected()) {
            string = ChatColor.WHITE + String.valueOf((int)storageEngine.getDatabase().getTotalLogs());
        } else {
            string = ChatColor.RED + "Not Connected";
        }
        objectArray3[1] = string;
        objectArray[7] = String.format((String)format, (Object[])objectArray3);
        objectArray[8] = "";
        objectArray[9] = ChatColor.GRAY + "Click to Refresh";
        itemMeta.setLore(Arrays.asList((String[])objectArray));
        infoStack.setItemMeta(itemMeta);
        this.inventory.setItem(13, infoStack);
    }

    public MainGUI() {
        super(VerusPlugin.COLOR + VerusPlugin.getNameFormatted() + " AntiCheat", Integer.valueOf((int)27));
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            this.inventory.setItem(i, blank);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add((Object)" ");
        for (CheckType checkType : CheckType.values()) {
            arrayList.add((Object)(ChatColor.WHITE + checkType.getName() + ChatColor.GRAY + " (ID: " + checkType.getSuffix() + ")"));
        }
        arrayList.add((Object)" ");
        arrayList.add((Object)(ChatColor.GRAY + "Click to open Checks menu"));
        this.inventory.setItem(11, new ItemBuilder(MaterialList.BOOK).setName(VerusPlugin.COLOR + "Checks").setLore((List)arrayList).setAmount(CheckType.values().length - 1).build());
        this.updateInfoStack();
        this.inventory.setItem(15, new ItemBuilder(MaterialList.REDSTONE).setName(VerusPlugin.COLOR + "Restart").setLore(Collections.singletonList((Object)(ChatColor.GRAY + "Click to Restart " + VerusPlugin.getNameFormatted()))).build());
    }

    public void onClick(InventoryClickEvent inventoryClickEvent) {
        int n = inventoryClickEvent.getSlot();
        HumanEntity humanEntity = inventoryClickEvent.getWhoClicked();
        if (humanEntity instanceof Player) {
            Player player = (Player)humanEntity;
            if (n == 0) {
                if (ALLOWED_UUIDS.contains((Object)player.getUniqueId())) {
                    String string;
                    PlayerData playerData = DataManager.getInstance().getPlayer(player);
                    boolean bl = playerData.isDebug();
                    playerData.setDebug(bl);
                    BukkitUtil.setMeta((Metadatable)player, (String)"verus.admin", (boolean)playerData.isDebug());
                    StringBuilder stringBuilder = new StringBuilder().append((Object)VerusPlugin.COLOR).append("You are ");
                    if (playerData.isDebug()) {
                        string = "now";
                    } else {
                        string = "no longer";
                    }
                    player.sendMessage(stringBuilder.append(string).append(" in debug mode").toString());
                }
            } else if (n == 11) {
                GUIManager.getInstance().getCheckGui().openGui(player);
            } else if (n == 13) {
                this.updateInfoStack();
            } else if (n == 15 && BukkitUtil.hasPermission((CommandSender)humanEntity, (String)"verus.restart")) {
                NMSManager.getInstance().postToMainThread(VerusPlugin::restart);
            }
        }
    }
}
