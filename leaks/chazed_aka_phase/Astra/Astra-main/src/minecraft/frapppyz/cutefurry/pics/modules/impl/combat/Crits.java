package frapppyz.cutefurry.pics.modules.impl.combat;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Crits extends Mod {
    public Mode mode = new Mode("Mode", "Vulcan", "Vulcan", "Packet", "NCP", "NCPPacket");
    public Crits() {
        super("Crits", "Crits for u", 0, Category.COMBAT);
        addSettings(mode);
    }

    public void onEvent(Event e){
        if(e instanceof Render){
            this.setSuffix(mode.getMode());
        }
        if(e instanceof Update){
            if(Killaura.entity != null){
                if(Killaura.entity.hurtTime <= 2 || mode.is("Vulcan")) { //so you dont get gayed
                    switch (mode.getMode()){
                        case "Vulcan":
                            if(mc.thePlayer.onGround) mc.thePlayer.motionY=0;
                            break;
                        case "Packet":
                            if(mc.thePlayer.onGround) {
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ, false));
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
                            }
                            break;

                        case "NCP":
                            if(mc.thePlayer.onGround) {
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.065, mc.thePlayer.posZ);
                                mc.thePlayer.motionY = -0.005f;
                            }
                            break;
                        case "NCPPacket":
                            if(mc.thePlayer.onGround) {
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.0065, mc.thePlayer.posZ, false));
                            }
                            break;
                    }
                }
            }
        }
    }
}
