package rip.athena.client.modules.impl.mods;

import rip.athena.client.config.*;
import rip.athena.client.modules.*;
import net.minecraft.util.*;
import net.minecraft.client.entity.*;
import net.minecraft.potion.*;

public class OldAnimations extends Module
{
    @ConfigValue.Boolean(name = "Blocking")
    public boolean OLD_BLOCKING;
    @ConfigValue.Boolean(name = "Blocking Hitting")
    public boolean OLD_BLOCKING_HITTING;
    @ConfigValue.Boolean(name = "Item Held")
    public boolean OLD_ITEM_HELD;
    @ConfigValue.Boolean(name = "Bow")
    public boolean OLD_BOW;
    @ConfigValue.Boolean(name = "Eat/Break Animation")
    public boolean OLD_EAT_USE_ANIMATION;
    @ConfigValue.Boolean(name = "Armor Damage Flash")
    public boolean ARMOR;
    @ConfigValue.Boolean(name = "Disable Health Flash")
    public boolean DISABLE_HEALTH_FLASH;
    
    public OldAnimations() {
        super("Old Animations", Category.MODS, "Athena/gui/mods/animation.png");
        this.OLD_BLOCKING = true;
        this.OLD_BLOCKING_HITTING = true;
        this.OLD_ITEM_HELD = true;
        this.OLD_BOW = true;
        this.OLD_EAT_USE_ANIMATION = true;
        this.ARMOR = true;
        this.DISABLE_HEALTH_FLASH = true;
    }
    
    public void attemptSwing() {
        if (OldAnimations.mc.thePlayer.getHeldItem() != null && this.isToggled() && this.OLD_EAT_USE_ANIMATION) {
            final boolean mouseDown = OldAnimations.mc.gameSettings.keyBindAttack.isKeyDown() && OldAnimations.mc.gameSettings.keyBindUseItem.isKeyDown();
            if (mouseDown && OldAnimations.mc.objectMouseOver != null && OldAnimations.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                this.swingItem(OldAnimations.mc.thePlayer);
            }
        }
    }
    
    private void swingItem(final EntityPlayerSP entityplayersp) {
        final int swingAnimationEnd = entityplayersp.isPotionActive(Potion.digSpeed) ? (6 - (1 + entityplayersp.getActivePotionEffect(Potion.digSpeed).getAmplifier()) * 1) : (entityplayersp.isPotionActive(Potion.digSlowdown) ? (6 + (1 + entityplayersp.getActivePotionEffect(Potion.digSlowdown).getAmplifier()) * 2) : 6);
        if (!entityplayersp.isSwingInProgress || entityplayersp.swingProgressInt >= swingAnimationEnd / 2 || entityplayersp.swingProgressInt < 0) {
            entityplayersp.swingProgressInt = -1;
            entityplayersp.isSwingInProgress = true;
        }
    }
}
