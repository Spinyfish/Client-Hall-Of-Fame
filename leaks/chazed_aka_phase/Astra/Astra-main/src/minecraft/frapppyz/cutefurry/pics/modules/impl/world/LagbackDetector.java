package frapppyz.cutefurry.pics.modules.impl.world;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.ReceivePacket;
import frapppyz.cutefurry.pics.event.impl.SendPacket;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Boolean;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class LagbackDetector extends Mod {

    private Boolean disableMods = new Boolean("Disable Mods", true);
    private int count = 0;

    public LagbackDetector() {
        super("LagbackDetector", "Lagback the detector.", 0, Category.WORLD);
        addSettings(disableMods);
    }

    public void onEnable(){
        count = 0;
    }

    public void onEvent(Event e){
        if(mc.thePlayer != null){
            if(e instanceof ReceivePacket && mc.thePlayer.ticksExisted > 100 && mc.thePlayer.isEntityAlive()){
                if(((ReceivePacket) e).getPacket() instanceof S08PacketPlayerPosLook){
                    Wrapper.getLogger().addChat(disableMods.isToggled() ? "You have been setback, so we have disabled some mods! (x" + count + ")" : "You have been setback! (x" + count + ")");

                    if(disableMods.isToggled()){
                        Wrapper.getModManager().getModByName("Fly").setToggled(false);
                        Wrapper.getModManager().getModByName("Speed").setToggled(false);
                        Wrapper.getModManager().getModByName("Longjump").setToggled(false);
                    }

                    count++;
                }
            }
            if(e instanceof SendPacket){
                if(((SendPacket) e).getPacket() instanceof C00PacketLoginStart){
                    count=0;
                }
            }
        }

    }
}
