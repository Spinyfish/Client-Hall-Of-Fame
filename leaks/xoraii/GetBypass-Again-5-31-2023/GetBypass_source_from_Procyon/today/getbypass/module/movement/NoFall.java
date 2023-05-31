// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.module.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import today.getbypass.module.Category;
import today.getbypass.module.Module;

public class NoFall extends Module
{
    public NoFall() {
        super("Nofall", 0, "Take 0 damage when you fall", Category.MOVEMENT);
    }
    
    @Override
    public void onUpdate() {
        if (this.isToggled()) {
            if (this.mc.thePlayer.fallDistance > 2.0f) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
            }
            super.onUpdate();
        }
    }
}
