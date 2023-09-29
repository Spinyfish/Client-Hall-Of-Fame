// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class Step extends Module
{
    public Step() {
        super("Step", 0, "Walk on block", Category.MOVEMENT);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            if (this.mc.thePlayer.isCollidedHorizontally && this.mc.thePlayer.onGround) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.42, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 0.72, this.mc.thePlayer.posZ, this.mc.thePlayer.onGround));
                this.mc.thePlayer.stepHeight = 1.0f;
            }
        }
        else {
            this.mc.thePlayer.stepHeight = 0.5f;
        }
    }
}
