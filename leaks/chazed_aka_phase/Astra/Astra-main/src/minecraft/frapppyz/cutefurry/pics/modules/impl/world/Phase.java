package frapppyz.cutefurry.pics.modules.impl.world;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.ReceivePacket;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Phase extends Mod {

    public Mode mode = new Mode("Mode", "Vclip", "Vclip", "Insta");
    public Mode instaMode = new Mode("Insta Mode", "Down", "Down", "Up", "Mid");
    public Phase() {
        super("Phase", "what is a block", 0, Category.WORLD);
        addSettings(mode, instaMode);
    }

    public void onEnable(){
        if(mode.is("Vclip")){
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 4, mc.thePlayer.posZ);
            toggle();
        }
    }

    public void onRender(){
        instaMode.setShowed(mode.is("Insta"));
    }

    public void onEvent(Event event){
        if(event instanceof ReceivePacket){
            if(mode.is("Insta") && mc.thePlayer.ticksExisted < 20){
                if(((ReceivePacket) event).getPacket() instanceof S08PacketPlayerPosLook){
                    if(instaMode.is("Down"))
                        ((S08PacketPlayerPosLook) ((ReceivePacket) event).getPacket()).y = ((S08PacketPlayerPosLook) ((ReceivePacket) event).getPacket()).getY() - 4;
                    else if(instaMode.is("Up"))
                        ((S08PacketPlayerPosLook) ((ReceivePacket) event).getPacket()).y = ((S08PacketPlayerPosLook) ((ReceivePacket) event).getPacket()).getY() + 8;
                    else if(instaMode.is("Mid")){
                        ((S08PacketPlayerPosLook) ((ReceivePacket) event).getPacket()).x = ((S08PacketPlayerPosLook) ((ReceivePacket) event).getPacket()).z = 0;
                    }
                }
            }

        }
    }

}
