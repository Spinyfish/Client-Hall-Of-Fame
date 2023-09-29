package de.verschwiegener.atero.module.modules.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.lang3.Validate;
import org.lwjgl.input.Keyboard;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.inventory.Group;
import de.verschwiegener.atero.util.inventory.InventoryUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class InventoryManager extends Module {

    public static InventoryManager instance;
    private boolean inventoryChanged;
    Minecraft mc = Minecraft.getMinecraft();
    private TimeUtils timer = new TimeUtils();
    private boolean autoarmor;
    private boolean random;
    private boolean hotbar;
    private boolean sort;
    private boolean openInv;
    private float hotbarDelay;
    private float delay;
    private Setting setting;
    public InventoryManager() {
        super("InventoryManager", "InventoryManager", Keyboard.KEY_NONE, Category.Player);
        instance = this;
    }
    @Override
    public void setup() {
        super.setup();
        final ArrayList<SettingsItem> items = new ArrayList<>();
        items.add(new SettingsItem("Delay", 0, 500, 0, ""));
        items.add(new SettingsItem("OpenInv", true, ""));
        items.add(new SettingsItem("Random", true, ""));
        items.add(new SettingsItem("AutoArmor", true, ""));
        items.add(new SettingsItem("Sort", true, ""));
        items.add(new SettingsItem("Hotbar", false, "HotbarDelay"));
        items.add(new SettingsItem("HotbarDelay", 0, 500, 0, ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
        setting = Management.instance.settingsmgr.getSettingByName(getName());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        setting = Management.instance.settingsmgr.getSettingByName(getName());
        timer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        setExtraTag(""+ setting.getItemByName("Delay").getCurrentValue());
        random = setting.getItemByName("Random").isState();
        autoarmor = setting.getItemByName("AutoArmor").isState();
        openInv = setting.getItemByName("OpenInv").isState();
        hotbar = setting.getItemByName("Hotbar").isState();
        sort = setting.getItemByName("Sort").isState();
        delay = setting.getItemByName("Delay").getCurrentValue();
        hotbarDelay = setting.getItemByName("HotbarDelay").getCurrentValue();

        if(openInv) {
            if(!(mc.currentScreen instanceof GuiContainer)) {
                if(hotbar) {
                    runLogic(true);
                }
                return;
            }
        }
        runLogic(false);
    }

    public void runLogic(boolean hotbar) {
        if (timer.hasReached((hotbar ? hotbarDelay : delay))) {
            timer.reset();
            ArrayList<Integer> garbageIDs = InventoryUtil.getGarbageItems(hotbar);
            if (!garbageIDs.isEmpty()) {
                if (random) {
                    Collections.shuffle(garbageIDs);
                }
                mc.playerController.windowClick(0, garbageIDs.get(0), 1, 4, mc.thePlayer);

            } else {

                if (autoarmor) {
                    InventoryUtil.putArmor(hotbar);
                }
                if(sort) {
                    InventoryUtil.putInBestSlot(hotbar);
                }

            }
        }

    }
}
