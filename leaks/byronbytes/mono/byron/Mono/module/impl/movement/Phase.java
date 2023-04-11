package byron.Mono.module.impl.movement;

import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleInterface(name = "Phase", description = "clip", category = Category.Movement)
public class Phase extends Module {

    @Override
    public void onEnable() {
        super.onEnable();
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3.0D, mc.thePlayer.posZ);
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 3.0D, mc.thePlayer.posZ, true));
    }


}
