package byron.Mono.module.impl.movement;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

@ModuleInterface(name = "AntiVoid", description = "No void.", category = Category.Movement)
public class AntiVoid extends Module {

    private boolean isAirUnderneath() {
        EntityPlayerSP var1 = this.mc.thePlayer;
        WorldClient var2 = this.mc.theWorld;
        AxisAlignedBB var3 = var1.getEntityBoundingBox();
        double var4 = var1.posY + (double)var1.getEyeHeight();

        for(int var6 = 0; (double)var6 < var4; var6 += 2) {
            if (!var2.getCollidingBoundingBoxes(var1, var3.offset(0.0D, (double)(-var6), 0.0D)).isEmpty()) {
                return true;
            }
        }

        return false;
    }
    
    @Subscribe
    public void onUpdate(EventUpdate e)
    {
        if (this.mc.thePlayer.fallDistance > 6.0F) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 7.0D, this.mc.thePlayer.posZ, true));
            this.mc.thePlayer.setPosition(this.mc.thePlayer.posX, this.mc.thePlayer.posY + 2.2D, this.mc.thePlayer.posZ);
        }
    }


    @Override
    public void onEnable() 
    {
     super.onEnable();
    }
    
    @Override
    public void onDisable()
    {
        super.onDisable();
    }
}
