package dev.africa.pandaware.impl.module.combat;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.module.misc.disabler.DisablerModule;
import dev.africa.pandaware.impl.module.misc.disabler.modes.HypixelDisabler;
import dev.africa.pandaware.impl.module.movement.ScaffoldModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.math.MathUtils;
import dev.africa.pandaware.utils.math.TimeHelper;
import dev.africa.pandaware.utils.player.block.BlockUtils;
import lombok.Getter;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@ModuleInfo(name = "Auto Pot", category = Category.COMBAT)
public class AutoPotModule extends Module {
    private final NumberSetting health = new NumberSetting("Health", 20, 1, 10);

    private final BooleanSetting packet = new BooleanSetting("Packet", true);
    private final BooleanSetting inCombat = new BooleanSetting("In Combat", false);
    private final BooleanSetting legit = new BooleanSetting("Legit", false);
    private final BooleanSetting jumpUp = new BooleanSetting("Jump Up", false);
    private final BooleanSetting teleportUp = new BooleanSetting("Teleport Up", true);

    private final TimeHelper timer = new TimeHelper();
    private final List<Potion> validPotions = Arrays.asList(
            Potion.heal,
            Potion.regeneration,
            Potion.moveSpeed
    );

    private boolean potting;
    private int rotationTicks;

    public AutoPotModule() {
        this.registerSettings(
                this.health,
                this.packet,
                this.inCombat,
                this.legit,
                this.jumpUp,
                this.teleportUp
        );
    }

    @Override
    public void onEnable() {
        this.potting = false;
        this.rotationTicks = 0;
    }

    @EventHandler
    EventCallback<MotionEvent> onPre = event -> {
        if (event.getEventState() == Event.EventState.POST) return;

        if (!this.inCombat.getValue() && mc.thePlayer.isSwingInProgress) {
            return;
        }

        DisablerModule disabler = Client.getInstance().getModuleManager().getByClass(DisablerModule.class);
        if (!(mc.currentScreen instanceof GuiMultiplayer) && !(mc.getCurrentServerData() == null) &&
                disabler.getCurrentMode() instanceof HypixelDisabler && mc.thePlayer.ticksExisted < 100) {
            return;
        }

        if (BlockUtils.getBlockAtPos(new BlockPos(mc.thePlayer.posX,
                mc.thePlayer.posY - 0.5, mc.thePlayer.posZ)) == Blocks.air) {
            return;
        }

        if (!mc.thePlayer.onGround || !BlockUtils.getBlockAtPos(new BlockPos(mc.thePlayer.posX,
                mc.thePlayer.posY - 1, mc.thePlayer.posZ)).isFullCube()
                || Client.getInstance().getModuleManager().getByClass(ScaffoldModule.class).getData().isEnabled()) {
            return;
        }

        if (this.timer.reach(200L)) {
            this.potting = false;
        }

        AtomicInteger slot = new AtomicInteger(5);

        for (int i = 36; i < 45; i++) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() ||
                    mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() instanceof ItemPotion) {
                slot.set(i - 36);
                break;
            }
        }

        this.validPotions.forEach(validPotion -> {
            int potionID = validPotion.getId();

            if (validPotion == Potion.regeneration || validPotion == Potion.heal) {
                if (this.timer.reach(600) && !mc.thePlayer.isPotionActive(potionID)) {
                    if (mc.thePlayer.getHealth() < this.health.getValue().doubleValue()) {
                        this.getBestPotion(slot.get(), potionID, event);
                    }
                }
            } else if (this.timer.reach(700) && !mc.thePlayer.isPotionActive(potionID)) {
                this.getBestPotion(slot.get(), potionID, event);
            }
        });
    };

    public void getBestPotion(int slot, int potionID, MotionEvent event) {
        if (mc.currentScreen != null) {
            return;
        }

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory)) {
                ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (itemStack != null && itemStack.getItem() instanceof ItemPotion) {
                    ItemPotion potion = (ItemPotion) itemStack.getItem();

                    if (potion.getEffects(itemStack) != null && potion.getEffects(itemStack).isEmpty()) {
                        return;
                    }

                    if (potion.getEffects(itemStack).get(0).getPotionID() == potionID) {
                        if (ItemPotion.isSplash(itemStack.getItemDamage()) && isItemBestPotion(potion, itemStack)) {
                            if (36 + slot != i) {
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                                        i, slot, 2, mc.thePlayer);
                            }

                            int oldSlot = mc.thePlayer.inventory.currentItem;

                            if (this.packet.getValue()) {
                                if (this.legit.getValue()) {
                                    mc.thePlayer.inventory.currentItem = slot;
                                } else {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                                }

                                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer
                                        .C05PacketPlayerLook(mc.thePlayer.rotationYaw,
                                        90, mc.thePlayer.onGround));
                            }

                            event.setPitch(90);

                            if (this.packet.getValue() || this.rotationTicks > 1) {
                                if (!this.packet.getValue()) {
                                    if (this.legit.getValue()) {
                                        mc.thePlayer.inventory.currentItem = slot;
                                    } else {
                                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                                    }
                                }

                                mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer
                                        .inventory.getStackInSlot(slot)));

                                if (this.teleportUp.getValue() && mc.thePlayer.onGround) {
                                    mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX,
                                            mc.thePlayer.posY + 0.42f, mc.thePlayer.posZ);
                                }

                                if (this.jumpUp.getValue() && mc.thePlayer.onGround) {
                                    mc.thePlayer.jump();
                                }

                                if (!this.legit.getValue()) {
                                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(oldSlot));
                                } else {
                                    mc.thePlayer.inventory.currentItem = oldSlot;
                                }

                                this.potting = true;
                                this.timer.reset();

                                this.rotationTicks = 0;
                            }

                            this.rotationTicks++;
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean isItemBestPotion(ItemPotion potion, ItemStack stack) {
        if (potion.getEffects(stack) == null || potion.getEffects(stack).size() != 1) {
            return false;
        }

        PotionEffect effect = potion.getEffects(stack).get(0);

        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

                if (itemStack.getItem() instanceof ItemPotion) {
                    ItemPotion itemPotion = (ItemPotion) itemStack.getItem();

                    if (itemPotion.getEffects(itemStack) != null) {
                        for (PotionEffect potionEffect : itemPotion.getEffects(itemStack)) {
                            int potionID = potionEffect.getPotionID();
                            int amplifier = potionEffect.getAmplifier();
                            int duration = potionEffect.getDuration();

                            if (potionID == effect.getPotionID() && ItemPotion.isSplash(itemStack.getItemDamage())) {
                                if (amplifier > effect.getAmplifier() ||
                                        amplifier == effect.getAmplifier() && duration > effect.getDuration()) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    @Override
    public String getSuffix() {
        return String.valueOf(MathUtils.roundToDecimal(this.health.getValue().doubleValue(), 2));
    }
}