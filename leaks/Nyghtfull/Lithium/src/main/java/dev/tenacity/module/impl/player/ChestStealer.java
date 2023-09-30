package dev.tenacity.module.impl.player;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.world.WorldEvent;
import dev.tenacity.event.impl.player.movement.MotionEvent;
import dev.tenacity.event.impl.render.Render2DEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.settings.ParentAttribute;
import dev.tenacity.module.settings.impl.BooleanSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import dev.tenacity.utils.font.AbstractFontRenderer;
import dev.tenacity.utils.player.RotationUtils;
import dev.tenacity.utils.time.TimerUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChestStealer extends Module {

    private final NumberSetting delay = new NumberSetting("Delay", 80, 300, 0, 10);
    private final BooleanSetting aura = new BooleanSetting("Aura", false);
    private final NumberSetting auraRange = new NumberSetting("Aura Range", 3, 6, 1, 1);
    public static BooleanSetting stealingIndicator = new BooleanSetting("Stealing Indicator", false);
    public static BooleanSetting titleCheck = new BooleanSetting("Title Check", true);
    public static BooleanSetting freeLook = new BooleanSetting("Free Look", true);
    private final BooleanSetting reverse = new BooleanSetting("Reverse", false);
    public static final BooleanSetting silent = new BooleanSetting("Silent", false);
    private final BooleanSetting smart = new BooleanSetting("Smart", false);

    private final List<BlockPosition> openedChests = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private final TimerUtil timer = new TimerUtil();
    public static boolean stealing;
    private InvManager invManager;
    private boolean clear;

    public ChestStealer() {
        super("ChestStealer", Category.PLAYER, "auto loot chests");
        auraRange.addParent(aura, ParentAttribute.BOOLEAN_CONDITION);
        stealingIndicator.addParent(silent, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(delay, aura, auraRange, stealingIndicator, titleCheck, freeLook, reverse, silent, smart);
    }

    @Override
    public void onMotionEvent(MotionEvent e) {
        if (e.isPre()) {
            setSuffix(smart.isEnabled() ? "Smart" : null);
            if (invManager == null) invManager = Tenacity.INSTANCE.getModuleCollection().getModule(InvManager.class);
            if (aura.isEnabled()) {
                final int radius = auraRange.getValue().intValue();
                for (int x = -radius; x < radius; x++) {
                    for (int y = -radius; y < radius; y++) {
                        for (int z = -radius; z < radius; z++) {
                            final BlockPosition pos = new BlockPosition(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                            if (pos.getBlock() == Blocks.chest && !openedChests.contains(pos)) {
                                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), pos, EnumFacing.UP, new Vec3(pos))) {
                                    mc.thePlayer.swingItem();
                                    final float[] rotations = RotationUtils.getFacingRotations2(pos.getX(), pos.getY(), pos.getZ());
                                    e.setRotations(rotations[0], rotations[1]);
                                    RotationUtils.setVisualRotations(rotations[0], rotations[1]);
                                    openedChests.add(pos);
                                }
                            }
                        }
                    }
                }
            }
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
                IInventory chestInv = chest.getLowerChestInventory();
                if (titleCheck.isEnabled() && (!(chestInv instanceof ContainerLocalMenu) || !((ContainerLocalMenu) chestInv).realChest))
                    return;
                clear = true;

                List<Integer> slots = new ArrayList<>();
                for (int i = 0; i < chestInv.getSizeInventory(); i++) {
                    ItemStack is = chestInv.getStackInSlot(i);
                    if (is != null && (!smart.isEnabled() || !(invManager.isBadItem(is, -1, true) || items.contains(is.getItem())))) {
                        slots.add(i);
                    }
                }

                if (reverse.isEnabled()) {
                    Collections.reverse(slots);
                }

                slots.forEach(s -> {
                    ItemStack is = chestInv.getStackInSlot(s);
                    Item item = is != null ? is.getItem() : null;
                    if (item != null && !items.contains(item) && (delay.getValue() == 0 || timer.hasTimeElapsed(delay.getValue().longValue(), true))) {
                        if (smart.isEnabled() && !(item instanceof ItemBlock)) {
                            items.add(is.getItem());
                        }
                        mc.playerController.windowClick(chest.windowId, s, 0, 1, mc.thePlayer);
                    }
                });

                if (slots.isEmpty() || isInventoryFull()) {
                    items.clear();
                    clear = false;
                    stealing = false;
                    mc.thePlayer.closeScreen();
                }
            } else if (clear) {
                items.clear();
                clear = false;
            }
        }
    }

    @Override
    public void onRender2DEvent(Render2DEvent event) {
        if (stealingIndicator.isEnabled() && stealing) {
            ScaledResolution sr = new ScaledResolution(mc);
            AbstractFontRenderer fr = HUDMod.customFont.isEnabled() ? lithiumFont20 : mc.fontRendererObj;
            fr.drawStringWithShadow("§lStealing...", sr.getScaledWidth() / 2.0F - fr.getStringWidth("§lStealing...") / 2.0F, sr.getScaledHeight() / 2.0F + 10, HUDMod.getClientColors().getFirst());
        }
    }

    @Override
    public void onEnable() {
        openedChests.clear();
        super.onEnable();
    }

    private boolean isInventoryFull() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() == null) {
                return false;
            }
        }
        return true;
    }

    public static boolean canSteal() {
        if (Tenacity.INSTANCE.isEnabled(ChestStealer.class) && mc.currentScreen instanceof GuiChest) {
            ContainerChest chest = (ContainerChest) mc.thePlayer.openContainer;
            IInventory chestInv = chest.getLowerChestInventory();
            return !titleCheck.isEnabled() || (chestInv instanceof ContainerLocalMenu && ((ContainerLocalMenu) chestInv).realChest);
        }
        return false;
    }

    @Override
    public void onWorldEvent(WorldEvent event) {
        if (event instanceof WorldEvent.Load) {
            openedChests.clear();
        }
    }

}
