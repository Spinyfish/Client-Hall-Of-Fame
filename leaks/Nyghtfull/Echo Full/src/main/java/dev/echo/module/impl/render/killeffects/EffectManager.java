package dev.echo.module.impl.render.killeffects;

import dev.echo.utils.Utils;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.util.BlockPos;

/**
 * @author senoe
 * @since 6/14/2022
 */
public class EffectManager implements Utils {

    public void playKillEffect(KillEffects.Location location) {
        switch (KillEffects.killEffect.getMode()) {
            case "Blood Explosion":
                playBlockBreak(location.x, location.y, location.z, location.eyeHeight, 152);
                break;
            case "Lightning Bolt":
                mc.theWorld.addWeatherEffect(new EntityLightningBolt(mc.theWorld, location.x, location.y, location.z));
                mc.theWorld.playSoundAtPos(new BlockPos(location.x, location.y, location.z), "ambient.weather.thunder", 5.0F, 1.0F, false);
                break;
        }
    }

    private void playBlockBreak(double x, double y, double z, double eyeHeight, int blockId) {
        mc.renderGlobal.playAuxSFX(mc.thePlayer, 2001, new BlockPos(x, y + eyeHeight, z), blockId);
    }

}
