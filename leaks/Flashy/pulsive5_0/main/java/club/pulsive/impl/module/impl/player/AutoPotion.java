package club.pulsive.impl.module.impl.player;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.player.PlayerMotionEvent;
import club.pulsive.impl.event.player.windowClick.WindowClickRequest;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.combat.Aura;
import club.pulsive.impl.module.impl.player.autopot.Requirement;
import club.pulsive.impl.property.Property;
import club.pulsive.impl.property.implementations.DoubleProperty;
import club.pulsive.impl.property.implementations.EnumProperty;
import club.pulsive.impl.property.implementations.MultiSelectEnumProperty;
import club.pulsive.impl.util.client.TimerUtil;
import club.pulsive.impl.util.math.MathUtil;
import club.pulsive.impl.util.network.PacketUtil;
import club.pulsive.impl.util.player.InventoryUtils;
import com.google.common.collect.Lists;
import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ModuleInfo(name = "AutoPotion", renderName = "Auto Potion", aliases = "AutoPotion", description = "Automatically potions the potion items.", category = Category.PLAYER)

public class AutoPotion extends Module {

    private int ticksSinceLastPot;
    private final Map<Integer, Requirement[]> potionRequirementMap = new HashMap<>();
    private final DoubleProperty healthProperty = new DoubleProperty("Health", 6.0, 1.0, 10.0, 0.5);
    private final Property<Boolean> headsProperty = new Property<>("Heads", true);
    public boolean potting;
    private WindowClickRequest lastRequest;

    @Override
    public void init() {
        super.init();
        final Requirement healthBelowRequirement = new HealthBelowRequirement();
        final Requirement betterThanCurrentRequirement = new BetterThanCurrentRequirement();

        this.potionRequirementMap.put(Potion.moveSpeed.getId(), new Requirement[]{betterThanCurrentRequirement});
        this.potionRequirementMap.put(Potion.regeneration.getId(), new Requirement[]{betterThanCurrentRequirement, healthBelowRequirement});
        this.potionRequirementMap.put(Potion.heal.getId(), new Requirement[]{healthBelowRequirement});
    }
    
    @EventHandler
    private final Listener<PlayerMotionEvent> playerMotionEventListener = event -> {
        if (event.isPre()) {
            this.ticksSinceLastPot++;

            if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof GuiInventory))
                return;

            if (!mc.thePlayer.onGround) return;

            // 1s pot/head delay
            if (this.ticksSinceLastPot <= 20) return;

            if (!event.isRotating() || potting) {
                int mostImportantSlot = this.getMostImportantPotion();

                if (mostImportantSlot != -1) {
                    if (mostImportantSlot < InventoryUtils.ONLY_HOT_BAR_BEGIN) {
                        if (this.lastRequest == null || this.lastRequest.isCompleted()) {
                            // Scaffold uses the last hot bar slot
                            final int slotID = mostImportantSlot;
                            InventoryUtils.queueClickRequest(this.lastRequest = new WindowClickRequest() {
                                @Override
                                public void performRequest() {
                                    InventoryUtils.windowClick(mc, slotID, 7,
                                            InventoryUtils.ClickType.SWAP_WITH_HOT_BAR_SLOT);
                                }
                            });
                        }
                        return;
                    } else {
                        mostImportantSlot -= InventoryUtils.ONLY_HOT_BAR_BEGIN;
                    }

                    // Calculate distance moved
                    final double distMovedInTick = MathUtil.getDistance(this.mc.thePlayer.lastReportedPosX, this.mc.thePlayer.lastReportedPosZ,
                            this.mc.thePlayer.posX, this.mc.thePlayer.posZ);

                    // If not on ground ground or moving too fast
                    if (!this.mc.thePlayer.onGround || distMovedInTick > 0.2873 + 9.0E-4) return;
                    event.setPitch(90.f);
                    // Switch to potion slot server side
                    this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mostImportantSlot));
                    this.potting = true;
                    return;
                }
            }

            if (this.headsProperty.getValue() && this.mc.thePlayer.getHealth() < this.healthProperty.getValue() * 2.0) {
                for (int slot = InventoryUtils.ONLY_HOT_BAR_BEGIN; slot < InventoryUtils.END; slot++) {
                    final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(slot).getStack();

                    if (stack == null) continue;
                    final int itemID = Item.getIdFromItem(stack.getItem());
                    final boolean shouldEatID = itemID == 282 || itemID == Item.getIdFromItem(Items.skull) || itemID == Item.getIdFromItem(Items.baked_potato) || itemID == Item.getIdFromItem(Items.magma_cream) || itemID == Item.getIdFromItem(Items.mutton);

                    if (shouldEatID && !mc.thePlayer.isPotionActive(Potion.regeneration)) {
                        slot -= 36; // Normalize to hot bar slot
                        // Switch to head
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                        // Eat head
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
                        // Sync item slot
                        this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
                        // Reset pot timer
                        this.ticksSinceLastPot = 0;
                        return;
                    }
                }
            }
        } else if (this.potting && event.getPitch() == 90.f) {
            // Throw potion
            this.mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(null));
            // Sync item slot
            this.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(this.mc.thePlayer.inventory.currentItem));
            // Reset pot timer
            this.ticksSinceLastPot = 0;

            this.potting = false;
        }
    };

    private int getMostImportantPotion() {
        Map<Integer, PotionEffect> effectsInSlots;

        if ((effectsInSlots = doSearchPotion(InventoryUtils.ONLY_HOT_BAR_BEGIN, InventoryUtils.END)).isEmpty()) {
            effectsInSlots = doSearchPotion(InventoryUtils.EXCLUDE_ARMOR_BEGIN, InventoryUtils.ONLY_HOT_BAR_BEGIN);

            if (effectsInSlots.isEmpty()) {
                return -1;
            }
        }

        Map<Integer, Integer> potionIDAmplifiers = new HashMap<>();

        for (final Integer slot : effectsInSlots.keySet()) {
            final PotionEffect effect = effectsInSlots.get(slot);

            if (potionIDAmplifiers.containsKey(effect.getPotionID())) {
                final int amplifier = potionIDAmplifiers.get(effect.getPotionID());

                if (effect.getAmplifier() > amplifier) {
                    potionIDAmplifiers.put(effect.getPotionID(), effect.getAmplifier());
                }
            } else {
                potionIDAmplifiers.put(effect.getPotionID(), effect.getAmplifier());
            }
        }

        for (final Integer slot : effectsInSlots.keySet()) {
            final PotionEffect effect = effectsInSlots.get(slot);

            if (this.getMostImportantInList(potionIDAmplifiers, effect, Potion.heal.getId())) {
                return slot;
            }

            if (this.getMostImportantInList(potionIDAmplifiers, effect, Potion.regeneration.getId())) {
                return slot;
            }

            if (this.getMostImportantInList(potionIDAmplifiers, effect, Potion.moveSpeed.getId())) {
                return slot;
            }
        }


        return -1;
    }

    private boolean getMostImportantInList(final Map<Integer, Integer> potionIDAmplifiers,
                                           final PotionEffect effect,
                                           final int filterPotionID) {
        for (final Integer potionID : potionIDAmplifiers.keySet()) {
            final Integer amplifier = potionIDAmplifiers.get(potionID);

            if (effect.getPotionID() == potionID && effect.getAmplifier() == amplifier) {
                if (potionID == filterPotionID) return true;
            }
        }

        return false;
    }

    private Map<Integer, PotionEffect> doSearchPotion(final int start,
                                                      final int end) {
        final Map<Integer, PotionEffect> slotEffectMap = new HashMap<>();

        for (int i = start; i < end; i++) {
            final ItemStack stack = this.mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (stack != null && stack.getItem() instanceof ItemPotion &&
                    ItemPotion.isSplash(stack.getMetadata()) &&
                    InventoryUtils.isBuffPotion(stack)) {
                final ItemPotion potion = (ItemPotion) stack.getItem();

                final List<PotionEffect> effects = potion.getEffects(stack.getMetadata());
                if (effects != null) {
                    for (final PotionEffect effect : effects) {
                        for (final Integer potionID : this.potionRequirementMap.keySet()) {
                            if (effect.getPotionID() == potionID) {
                                final Requirement[] requirements = this.potionRequirementMap.get(potionID);

                                if (requirements.length == 1) {
                                    if (requirements[0].test(this.mc.thePlayer, this.healthProperty.getValue().floatValue() * 2.0F,
                                            effect.getAmplifier(), potionID)) {
                                        slotEffectMap.put(i, effect);
                                    }
                                } else if (requirements.length > 1) {
                                    boolean pass = false;

                                    for (final Requirement requirement : requirements) {
                                        pass = requirement.test(this.mc.thePlayer, this.healthProperty.getValue().floatValue() * 2.0F,
                                                effect.getAmplifier(), potionID);
                                    }

                                    if (pass) {
                                        slotEffectMap.put(i, effect);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return slotEffectMap;
    }


    private static class HealthBelowRequirement implements Requirement {
        @Override
        public boolean test(EntityPlayerSP player, float healthTarget, int currentAmplifier, int potionId) {
            return player.getHealth() < healthTarget;
        }
    }

    private static class BetterThanCurrentRequirement implements Requirement {
        @Override
        public boolean test(EntityPlayerSP player, float healthTarget, int currentAmplifier, int potionId) {
            final PotionEffect effect = player.getActivePotionEffect(potionId);
            return effect == null || effect.getAmplifier() < currentAmplifier;
        }
    }
}