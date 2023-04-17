package me.levansj01.verus.gui.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.gui.GUI;
import me.levansj01.verus.gui.impl.TypeGUI;
import me.levansj01.verus.gui.manager.GUIManager;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.util.item.ItemBuilder;
import me.levansj01.verus.util.item.MaterialList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TypeGUI
        extends GUI {
    private final Map<String, Integer> bansById;
    private final List<Check> checks;
    private final Map<Integer, Check> checksById = new HashMap();
    private final CheckType type;
    private static final ItemStack previousPage = new ItemBuilder().setType(MaterialList.STAINED_GLASS_PANE).setName(ChatColor.RED + "Previous Page").setLore(Collections.singletonList((Object)(ChatColor.GRAY + "Click to go back a page"))).build();
    private boolean loadedBans;

    public void openGui(Player player) {
        if (this.inventory != null) {
            if (!this.loadedBans) {
                StorageEngine storageEngine = StorageEngine.getInstance();
                if (storageEngine.isConnected()) {
                    CheckManager checkManager = CheckManager.getInstance();
                    int n = 9;
                    for (Check check : this.checks) {
                        int n3;
                        if ((n3 = n++) >= this.inventory.getSize()) {
                        }
                        ItemStack itemStack = this.inventory.getContents()[n3];
                        if (itemStack != null) {
                            storageEngine.getDatabase().getCheckBans(check, n2 -> {
                                this.bansById.put(check.identifier(), n2);
                                NMSManager.getInstance().postToMainThread(() -> this.updateLore(itemStack, n3, check, checkManager, (int)n2));
                            });
                        }
                    }
                }
                this.loadedBans = true;
            }
            player.openInventory(this.inventory);
        }
    }

    private void updateLore(ItemStack itemStack, int n, Check check, CheckManager checkManager, int n2) {
        ChatColor chatColor;
        ChatColor chatColor2;
        String string;
        String string2;
        ItemMeta itemMeta = itemStack.getItemMeta();
        Object[] objectArray = new String[12];
        objectArray[0] = "";
        objectArray[1] = VerusPlugin.COLOR + "Display: " + ChatColor.WHITE + check.getFriendlyName();
        objectArray[2] = "";
        StringBuilder stringBuilder = new StringBuilder().append((Object)VerusPlugin.COLOR).append("Total Bans: ").append((Object)ChatColor.WHITE);
        if (n2 == 0) {
            string2 = "None";
        } else {
            string2 = String.valueOf(Integer.valueOf((int)n2));
        }
        objectArray[3] = stringBuilder.append((Object)string2).toString();
        StringBuilder stringBuilder2 = new StringBuilder().append((Object)VerusPlugin.COLOR).append("Ban VL: ").append((Object)ChatColor.WHITE);
        if (checkManager.getMaxViolation(check) == Integer.MAX_VALUE) {
            string = "None";
        } else {
            string = String.valueOf(Integer.valueOf(checkManager.getMaxViolation(check)));
        }
        objectArray[4] = stringBuilder2.append((Object)string).toString();
        objectArray[5] = "";
        StringBuilder stringBuilder3 = new StringBuilder();
        if (checkManager.isEnabled(check)) {
            chatColor2 = ChatColor.GREEN;
        } else {
            chatColor2 = ChatColor.RED;
        }
        objectArray[6] = stringBuilder3.append((Object)chatColor2).append("Alerts").toString();
        StringBuilder stringBuilder4 = new StringBuilder();
        if (checkManager.isAutoban(check)) {
            chatColor = ChatColor.GREEN;

        } else {
            chatColor = ChatColor.RED;
        }
        objectArray[7] = stringBuilder4.append((Object)chatColor).append("Bannable").toString();
        objectArray[8] = "";
        objectArray[9] = ChatColor.GRAY + "Left-Click to toggle alerts for this check";
        objectArray[10] = ChatColor.GRAY + "Middle-Click to update ban count";
        objectArray[11] = ChatColor.GRAY + "Right-Click to toggle auto banning for this check";
        itemMeta.setLore(Arrays.asList(objectArray));
        itemStack.setItemMeta(itemMeta);
        this.inventory.setItem(n, itemStack);
    }

    public void clear() {
        super.clear();
        this.checks.clear();
        this.bansById.clear();
        this.checksById.clear();
    }

    public void onClick(InventoryClickEvent inventoryClickEvent) {
        Check check;
        int n = inventoryClickEvent.getSlot();
        CheckType checkType = this.type.next();
        CheckType checkType2 = this.type.previous();
        if (n == 0 && checkType2 != this.type) {
            ((GUI)GUIManager.getInstance().getTypeGuis().get((Object)checkType2)).openGui((Player)inventoryClickEvent.getWhoClicked());
            return;
        }
        if (n == 8 && checkType != this.type) {
            ((GUI)GUIManager.getInstance().getTypeGuis().get((Object)checkType)).openGui((Player)inventoryClickEvent.getWhoClicked());
            return;
        }
        if (n >= 0 && n < 9) {
            GUIManager.getInstance().getCheckGui().openGui((Player)inventoryClickEvent.getWhoClicked());
            return;
        }
        ClickType clickType = inventoryClickEvent.getClick();
        ItemStack itemStack = inventoryClickEvent.getCurrentItem();
        if (itemStack != null && itemStack.getItemMeta() != null && (check = (Check)this.checksById.get((Object)n)) != null && ChatColor.stripColor((String)itemStack.getItemMeta().getDisplayName()).equalsIgnoreCase(check.name())) {
            CheckManager checkManager = CheckManager.getInstance();
            switch (clickType.ordinal()) {
                case 1: {
                    boolean bl = checkManager.isEnabled(check);
                    if (!this.loadedBans) {
                        return;
                    }
                    checkManager.setEnabled(check, bl);
                }
                case 2: {
                    boolean bl = checkManager.isAutoban(check);
                    if (!this.loadedBans) {
                        return;
                    }
                    checkManager.setAutoban(check, bl);
                }
                case 3: {
                    StorageEngine storageEngine = StorageEngine.getInstance();
                    if (storageEngine.isConnected()) {
                        storageEngine.getDatabase().getCheckBans(check, n2 -> {
                            this.bansById.put(check.identifier(), n2);
                            NMSManager.getInstance().postToMainThread(() -> this.updateLore(itemStack, n, check, checkManager, (int)n2));
                        });
                    }
                }
                default: {
                    return;
                }
            }
            this.updateLore(itemStack, n, check, checkManager, (Integer)this.bansById.getOrDefault((Object)check.identifier(), (Object)0));
        }
    }

    public TypeGUI(CheckType checkType, List<Check> list) throws ClassNotFoundException {
        super(VerusPlugin.COLOR + checkType.getName() + " Checks (" + list.size() + ")", Integer.valueOf((int)45));
        this.bansById = new HashMap();
        this.type = checkType;
        this.checks = list;
        if (!list.isEmpty()) {
            for (int i = 0; i < 9; ++i) {
                this.inventory.setItem(i, previousPage);
            }
            CheckType checkType2 = checkType.previous();
            CheckType checkType3 = checkType.next();
            if (checkType2 != checkType) {
                this.inventory.setItem(0, new ItemBuilder(Material.ARROW).setName(ChatColor.RED + checkType2.getName() + " Checks").setLore(Collections.singletonList((Object)(ChatColor.GRAY + "Click to view " + checkType2.getName() + " checks"))).build());
            }
            if (checkType3 != checkType) {
                this.inventory.setItem(8, new ItemBuilder(Material.ARROW).setName(ChatColor.RED + checkType3.getName() + " Checks").setLore(Collections.singletonList((Object)(ChatColor.GRAY + "Click to view " + checkType3.getName() + " checks"))).build());
            }
            list.sort(Comparator.comparing(Check::getSubType));
            CheckManager checkManager = CheckManager.getInstance();
            int n = 9;
            for (Check check : list) {
                ItemBuilder itemBuilder = new ItemBuilder().setType(MaterialList.PAPER).setName(VerusPlugin.COLOR + check.name());
                this.updateLore(itemBuilder.build(), n, check, checkManager, 0);
                this.checksById.put(n++, check);
            }
        }
        Class.forName("me.levansj01.launcher.VerusLauncher");
        Class.forName("me.levansj01.launcher.VerusLaunch");
    }
}
