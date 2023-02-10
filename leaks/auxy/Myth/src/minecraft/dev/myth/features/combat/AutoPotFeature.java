/**
 * @project Myth
 * @author CodeMan
 * @at 31.10.22, 22:05
 */
package dev.myth.features.combat;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.inventory.InventoryUtils;
import dev.myth.events.MoveEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.ListSetting;
import dev.myth.settings.NumberSetting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@Feature.Info(
        name = "AutoPot",
        description = "Automatically uses pots",
        category = Feature.Category.COMBAT
)
public class AutoPotFeature extends Feature {

    public final ListSetting<Potions> potions = new ListSetting<>("Potions", Potions.HEALTH);
    public final NumberSetting healPercentage = new NumberSetting("Heal Percentage", 70, 0, 100, 1).setSuffix("%");
    public final NumberSetting delaySetting = new NumberSetting("Delay", 20, 0, 40, 1).setSuffix("t");
    public final EnumSetting<Timing> potTiming = new EnumSetting<>("Pot Timing", Timing.PRE);
    public final BooleanSetting forceUpdate = new BooleanSetting("Force Update", false).addDependency(() -> potTiming.getValue() == Timing.PRE);
    public final BooleanSetting takeFromInv = new BooleanSetting("Take Potions from Inv", false);

    private int oldSlot, slot, delay;

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() == EventState.PRE) {
            slot = -1;
//            doLog("Delay: " + delay);

            if (delay > 0) {
                delay--;
                return;
            }


            boolean isHotbarFull = true;
            ArrayList<Integer> validIDs = new ArrayList<>();
            for (Enum<?> potion : potions.getSelected()) {
                validIDs.add(((Potions) potion).getPotionID());
            }
            PotionEffect bestEffect = null;
            for (int i = 0; i < 35; i++) {
                ItemStack itemInSlot = MC.thePlayer.inventory.getStackInSlot(i);
                if (itemInSlot == null || itemInSlot.getItem() == null) {
                    if (i < 9) isHotbarFull = false;
                }
                if (itemInSlot != null && itemInSlot.getItem() instanceof ItemPotion) {
                    if (!ItemPotion.isSplash(itemInSlot.getItemDamage())) {
                        continue;
                    }
                    ItemPotion ip = (ItemPotion) itemInSlot.getItem();
                    List<PotionEffect> effects = ip.getEffects(itemInSlot);
                    for (PotionEffect effect : effects) {
                        if (validIDs.contains(effect.getPotionID())) {
                            if (getPlayer().isPotionActive(effect.getPotionID())
                                    && getPlayer().getActivePotionEffect(effect.getPotionID()).getAmplifier() >= effect.getAmplifier()) {
                                continue;
                            }
                            if (bestEffect != null && effect.getPotionID() == bestEffect.getPotionID()) {
                                if (bestEffect.getAmplifier() > effect.getAmplifier()) {
                                    continue;
                                }
                            }
                            if (effect.getPotionID() == Potion.heal.id ||
                                    effect.getPotionID() == Potion.regeneration.id ||
                                    effect.getPotionID() == Potion.resistance.id) {
                                if (getPlayer().getHealth() / getPlayer().getMaxHealth() * 100 > healPercentage.getValue()) {
                                    continue;
                                }
                            }
                            slot = i;
                            bestEffect = effect;
                            break;
                        }
                    }
                }
                if(slot != -1 && slot < 9) break;
            }

            if (takeFromInv.getValue() /*&& MC.currentScreen instanceof GuiInventory*/) {
                if (slot > 8 && !isHotbarFull) {
                    MC.playerController.windowClick(MC.thePlayer.inventoryContainer.windowId, slot, 0, 1, MC.thePlayer);
                }
            }
            if (slot != -1 && slot < 9) {
                float pitch = (MovementUtil.isMoving() ? 70 : 90);
                if (!getPlayer().onGround) return;
                if (potTiming.is(Timing.PRE)) {
                    if (forceUpdate.getValue()) {
                        sendPacket(new C03PacketPlayer.C05PacketPlayerLook(getPlayer().rotationYaw, pitch, getPlayer().onGround));
                    }
                    if (forceUpdate.getValue() || event.getLastPitch() == pitch) {
                        oldSlot = getPlayer().inventory.currentItem;
                        sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem = slot));
                        sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().getHeldItem()));
                        sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem = oldSlot));
                        delay = delaySetting.getValue().intValue();
                    } else {
                        event.setPitch(pitch);
                    }
                } else {
                    event.setPitch(pitch);
                }
            }
        } else {
            if (slot != -1 && slot < 9) {
                if (!getPlayer().onGround || event.getPitch() != (MovementUtil.isMoving() ? 70 : 90)) return;
                if (potTiming.is(Timing.POST)) {
                    oldSlot = getPlayer().inventory.currentItem;
                    sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem = slot));
                    sendPacket(new C08PacketPlayerBlockPlacement(getPlayer().getHeldItem()));
                    sendPacket(new C09PacketHeldItemChange(getPlayer().inventory.currentItem = oldSlot));
                    delay = delaySetting.getValue().intValue();
                }
            }
        }
    };

    @Handler
    public final Listener<MoveEvent> moveEventListener = event -> {

    };

    public enum Potions {
        HEALTH("Health", Potion.heal.id),
        REGEN("Regeneration", Potion.regeneration.id),
        RESISTANCE("Resistance", Potion.resistance.id),
        SPEED("Speed", Potion.moveSpeed.id),
        JUMP("Jump boost", Potion.jump.id);

        private final String name;
        private final int potionID;

        Potions(String name, int potionID) {
            this.name = name;
            this.potionID = potionID;
        }

        @Override
        public String toString() {
            return name;
        }

        public int getPotionID() {
            return potionID;
        }
    }

    public enum Timing {
        PRE("Pre"),
        POST("Post");

        private final String name;

        Timing(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
