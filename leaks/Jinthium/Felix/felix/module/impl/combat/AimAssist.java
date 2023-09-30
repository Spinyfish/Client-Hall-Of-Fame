package felix.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import felix.api.annotations.Handler;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import felix.util.other.PlayerUtil;
import felix.util.other.TimeHelper;
import felix.util.player.RotationUtils;
import felix.value.impl.BooleanValue;
import felix.value.impl.NumberValue;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.MathHelper;

public class AimAssist extends Module
{
    public TimeHelper timer;
    
    private BooleanValue teams = new BooleanValue("Teams Check", true);
    private BooleanValue swordCheck = new BooleanValue("Sword Only", false);
    private BooleanValue clickAim = new BooleanValue("Click Aim", true);
    private NumberValue<Double> aimDist = new NumberValue<>("Aim Distance", 3.6, 1.0, 10.0);
    
    public AimAssist() {
        super("AimAssist",0, ModuleCategory.COMBAT);
        this.timer = new TimeHelper();
        addValues(teams, clickAim, swordCheck, aimDist);
    }
    
    public void onEnable() {
    	super.onEnable();
    }
    
    public void onDisable() {
    	super.onDisable();
    }
    
    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
        if (event.isPre()) {
            final float var = (float) ThreadLocalRandom.current().nextDouble(0.7f, 0.8f);
            for (final Object theObject : mc.theWorld.loadedEntityList) {
                if (theObject instanceof EntityPlayer && theObject != mc.thePlayer) {
                    final EntityPlayer entityplayer = (EntityPlayer)theObject;
                    if (swordCheck.isEnabled()) {
                        if (mc.thePlayer.getHeldItem() == null) {
                            continue;
                        }
                        if (!(mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
                            continue;
                        }
                    }
                    if (entityplayer == mc.thePlayer) {
                        continue;
                    }
                    if (clickAim.isEnabled() && !Mouse.isButtonDown(0)) {
                    	continue;
                    }
                    if (PlayerUtil.isOnSameTeam(entityplayer) && teams.isEnabled()) {
                        continue;
                    }
                    if (mc.thePlayer.getDistanceToEntity(entityplayer) > aimDist.getValue()) {
                        continue;
                    }
                    final float[] rot = RotationUtils.getNeededRotations(entityplayer);
                    if (mc.thePlayer.rotationYaw <= rot[0] - 12.0f || mc.thePlayer.rotationYaw >= rot[0] + 12.0f) {
                        if (mc.thePlayer.rotationYaw > rot[0]) {
                            mc.thePlayer.rotationYaw -= 1.0f / var;
                        }
                        else {
                            mc.thePlayer.rotationYaw += 1.0f / var;
                        }
                    }
                    mc.thePlayer.rotationYaw *= ThreadLocalRandom.current().nextDouble(0.9999f, 1.0001f);
                    mc.thePlayer.rotationPitch *= ThreadLocalRandom.current().nextDouble(0.99f, 1.01f);
                }
            }
        }
    }
}
