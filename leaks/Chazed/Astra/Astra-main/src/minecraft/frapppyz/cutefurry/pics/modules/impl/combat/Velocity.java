package frapppyz.cutefurry.pics.modules.impl.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.*;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.notifications.Type;
import frapppyz.cutefurry.pics.util.MoveUtil;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Velocity extends Mod {
    public static Mode mode = new Mode("Mode", "Cancel", "Cancel", "Hypixel", "Minemen", "AAC5", "Grim", "GrimStuck");
    public Velocity() {
        super("Velocity", "Uses the 10 litres of cum in your thigh highs to stick to the floor :flushed:", 0, Category.COMBAT);
        addSettings(mode);
    }

    public int cumthium;

    public void onEvent(Event e){

        if(e instanceof Render){
            this.setSuffix(mode.getMode());
        }

        if(e instanceof Motion){
            if(mc.thePlayer.hurtTime > 0 && mode.is("GrimStuck")){
                ((Motion) e).setX(0);
                ((Motion) e).setZ(0);
                ((Motion) e).setY(-69420);
            }
        }

        if(e instanceof SendPacket){
            if(mode.is("Grim")){
                e.setCancelled(((SendPacket) e).getPacket() instanceof C0FPacketConfirmTransaction && cumthium >= 16 && cumthium <= 27);
            }

            if(mode.is("GrimStuck")){
                e.setCancelled(((SendPacket) e).getPacket() instanceof C0FPacketConfirmTransaction && mc.thePlayer.hurtTime != 0);
            }
        }

        if(e instanceof Update){
            cumthium++;

            if(mc.thePlayer.hurtTime == 8 && mode.is("Minemen")){
                mc.thePlayer.motionX = 0;
                mc.thePlayer.motionZ = 0;
            }
        }
        if(e instanceof ReceivePacket){

            if(((ReceivePacket) e).getPacket() instanceof S12PacketEntityVelocity){
                if(cumthium >= 20) cumthium = 0;
                S12PacketEntityVelocity p = (S12PacketEntityVelocity)((ReceivePacket) e).getPacket();
                if(p.getEntityID() == mc.thePlayer.getEntityId()){
                    if(mode.is("Cancel"))
                        e.setCancelled(true);
                    else if(mode.is("Hypixel") && !Wrapper.getModManager().getModByName("Longjump").isToggled()){
                        e.setCancelled(true);
                        mc.thePlayer.motionY = p.getMotionY()/8000.0;
                        //for(int i = 0; i < 2; i++) PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + p.getMotionY()/8000000d, mc.thePlayer.posZ, false));
                    }else if(mode.is("Grim")){
                        if(Wrapper.getModManager().getModByName("Blink").isToggled()){
                            e.setCancelled(true);
                        }else if(cumthium >= 5 && cumthium <= 27){
                            e.setCancelled(true);
                            mc.thePlayer.motionX = p.getMotionY()/8000d;
                            mc.thePlayer.motionX = p.getMotionX()/56000d;
                            mc.thePlayer.motionZ = p.getMotionZ()/56000d;
                            cumthium = 0;
                        }else if(cumthium >= 150){
                            e.setCancelled(true);
                            cumthium = 0;
                        }

                    }else if(mode.is("GrimStuck")){
                        e.setCancelled(true);
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition());
                    }else if(mode.is("AAC5")){
                        cumthium = 0;
                        e.setCancelled(true);
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + p.getMotionY()/8000D, mc.thePlayer.posZ);
                    }
                }

            }
        }
    }
}
