/**
 * @project Myth
 * @author CodeMan
 * @at 03.11.22, 19:49
 */
package dev.myth.api.utils;

import dev.myth.api.interfaces.IMethods;
import lombok.experimental.UtilityClass;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;

@UtilityClass
public class PlayerUtil implements IMethods {

    public double getMinFallDistForDamage() {
        return 3 + (MC.thePlayer.isPotionActive(Potion.jump) ? MC.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1 : 0);
    }
    
    public void damagePlayer() {
        for(int i = 3; i > 0; i--) {
            MovementUtil.fakeJump();
            double motion = MovementUtil.getJumpMotion();
            double pos = 0;
            while (true) {
                pos += motion;
                if(pos < 0) {
                    if(i > 1) MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(MC.thePlayer.posX, MC.thePlayer.posY, MC.thePlayer.posZ, false));
                    break;
                }
                MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(MC.thePlayer.posX, MC.thePlayer.posY + pos, MC.thePlayer.posZ, false));
                motion = MovementUtil.predictMotionY(motion);
            }
        }
    }

}
