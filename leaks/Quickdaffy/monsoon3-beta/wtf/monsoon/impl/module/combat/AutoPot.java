/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.api.util.misc.PacketUtil;
import wtf.monsoon.impl.event.EventPreMotion;

public class AutoPot
extends Module {
    private final Setting<Double> healthToUseRegen = new Setting<Double>("Maximum Health", 10.0).minimum(1.0).maximum(20.0).incrementation(1.0).describedBy("At what health to use a regeneration pot.");
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        for (int i = 0; i < 9; ++i) {
            if (this.mc.thePlayer.inventory.getStackInSlot(i) == null || this.mc.thePlayer.inventory.getStackInSlot(i).getItem() == null || !(this.mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemPotion)) continue;
            ItemPotion potion = (ItemPotion)this.mc.thePlayer.inventory.getStackInSlot(i).getItem();
            ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
            if (!ItemPotion.isSplash(stack.getMetadata())) continue;
            boolean shouldSplash = false;
            for (PotionEffect potionEffect : potion.getEffects(stack.getMetadata())) {
                if (!this.isValidEffect(potionEffect)) continue;
                shouldSplash = true;
            }
            if (!shouldSplash) continue;
            this.switchToSlot(i);
            this.mc.thePlayer.rotationPitchHead = 90.0f;
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C05PacketPlayerLook(this.mc.thePlayer.rotationYaw, 90.0f, this.mc.thePlayer.onGround));
            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(stack, new BlockPos(-1, -1, -1)));
            this.switchToSlot(this.mc.thePlayer.inventory.currentItem);
        }
    };

    public AutoPot() {
        super("Auto Pot", "Automatically throw useful potions at your feet.", Category.COMBAT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    private void switchToSlot(int slot) {
        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(slot));
    }

    public boolean isValidEffect(PotionEffect potionEffect) {
        switch (potionEffect.getPotionID()) {
            case 1: {
                return !this.mc.thePlayer.isPotionActive(Potion.moveSpeed);
            }
            case 10: {
                return !this.mc.thePlayer.isPotionActive(Potion.regeneration) && (double)this.mc.thePlayer.getHealth() <= this.healthToUseRegen.getValue();
            }
            case 21: {
                return (double)this.mc.thePlayer.getHealth() <= this.healthToUseRegen.getValue();
            }
        }
        return false;
    }
}

