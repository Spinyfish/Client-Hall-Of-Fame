/**
 * @project hakarware
 * @author CodeMan
 * @at 10.08.23, 17:01
 */

package cc.swift.module.impl.combat;

import cc.swift.Swift;
import cc.swift.events.EventState;
import cc.swift.events.UpdateEvent;
import cc.swift.events.UpdateWalkingPlayerEvent;
import cc.swift.module.Module;
import cc.swift.util.player.MovementUtil;
import cc.swift.util.player.RotationUtil;
import cc.swift.value.impl.BooleanValue;
import cc.swift.value.impl.DoubleValue;
import cc.swift.value.impl.ModeValue;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;

public final class AutoHealModule extends Module {

    public final DoubleValue throwDelay = new DoubleValue("Throw Delay", 300D, 0D, 1000D, 50D);
    public final BooleanValue healthPot = new BooleanValue("Health", true);
    public final BooleanValue regenPot = new BooleanValue("Regen", true);
    public final DoubleValue healHealth = new DoubleValue("Heal at %", 70D, 0, 100, 1);
    public final BooleanValue speedPot = new BooleanValue("Speed", true);
    public final BooleanValue jumpBoostPot = new BooleanValue("Jump Boost", true);

    private long nextThrow;
    private boolean shouldThrow, shouldRotate;
    private int oldSlot, targetSlot;

    private float[] targetRotation;

    public AutoHealModule() {
        super("AutoHeal", Category.COMBAT);
        this.registerValues(throwDelay, healthPot, regenPot, healHealth, speedPot, jumpBoostPot);
    }

    @Handler
    public final Listener<UpdateWalkingPlayerEvent> updateWalkingPlayerEventListener = event -> {
        if (event.getState() != EventState.PRE)
            return;

        if (System.currentTimeMillis() < this.nextThrow)
            return;
        mc.gameSettings.keyBindSneak.setKeyDown(true);
        if (!this.shouldThrow) {
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
                if (stack == null || stack.getItem() == null)
                    continue;
                if (!(stack.getItem() instanceof ItemPotion) || !ItemPotion.isSplash(stack.getItemDamage()))
                    continue;

                ItemPotion itemPotion = (ItemPotion) stack.getItem();

                boolean hasBadEffect = false;
                for (PotionEffect effect : itemPotion.getEffects(stack)) {
                    int potionID = effect.getPotionID();
                    for (Potion potion : Potion.potionTypes) {
                        if (potion == null)
                            continue;
                        if (potion.id == potionID && potion.isBadEffect()) {
                            hasBadEffect = true;
                            break;
                        }
                    }
                    if (hasBadEffect)
                        break;

                    if ((potionID == Potion.heal.id && !healthPot.getValue())
                            || (potionID == Potion.regeneration.id && !regenPot.getValue())
                            || (potionID == Potion.moveSpeed.id && !speedPot.getValue())
                            || (potionID == Potion.jump.id && !jumpBoostPot.getValue())) {
                        hasBadEffect = true;
                        break;
                    }

                    if ((potionID == Potion.regeneration.id || potionID == Potion.heal.id) && mc.thePlayer.getHealth() / mc.thePlayer.getMaxHealth() > this.healHealth.getValue() / 100D)
                        hasBadEffect = true;

                    if (mc.thePlayer.isPotionActive(potionID) && mc.thePlayer.getActivePotionEffect(Potion.potionTypes[potionID]).getAmplifier() >= effect.getAmplifier())
                        hasBadEffect = true;
                }
                if (hasBadEffect)
                    continue;

                this.oldSlot = mc.thePlayer.inventory.currentItem;
                this.targetSlot = i;
                this.shouldThrow = true;
                this.shouldRotate = true;
                break;
            }
        } else {
            mc.thePlayer.inventory.currentItem = this.targetSlot;
            if (this.shouldRotate) {

                if (!MovementUtil.isOnGround())
                    return;

                Vec3 target = mc.thePlayer.getPositionVector();

                if (MovementUtil.isMoving()) {
                    target = target.addVector(mc.thePlayer.motionX * 2, 0, mc.thePlayer.motionZ * 2);
                }

                targetRotation = RotationUtil.getRotationsToVector(RotationUtil.getEyePosition(), target);

                event.setYaw(targetRotation[0]);
                event.setPitch(targetRotation[1]);

                this.shouldRotate = false;
            } else {
                if (event.getLastYaw() != targetRotation[0] || event.getLastPitch() != targetRotation[1]) {
                    this.shouldRotate = true;
                    return;
                }

                mc.rightClickMouse();
                this.nextThrow = System.currentTimeMillis() + this.throwDelay.getValue().longValue();
                this.shouldThrow = false;

                mc.thePlayer.inventory.currentItem = this.oldSlot;
            }
        }
    };

}
