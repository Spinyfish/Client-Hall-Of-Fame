package frapppyz.cutefurry.pics.modules.impl.render;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.ReceivePacket;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.event.impl.SendPacket;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.status.client.C01PacketPing;

public class Debugger extends Mod {
    private long tickTime;
    private String lastp;
    public Debugger() {
        super("Debugger", "Debugs packets.", 0, Category.RENDER);
    }

    public void onEvent(Event e){
        if(e instanceof ReceivePacket){
            tickTime = System.currentTimeMillis();
        }
        if(e instanceof Render){
            FontRenderer fr = mc.fontRendererObj;
            fr.drawString("Last packet: " + lastp, (int) (mc.displayWidth/4 - (float) (mc.fontRendererObj.getStringWidth("Last packet: " + lastp) / 2)), 40, -1);
        }
        if(e instanceof SendPacket && mc.thePlayer != null){
            if(!(((SendPacket) e).getPacket() instanceof C03PacketPlayer)){
                lastp = ((SendPacket) e).getPacket().toString().replace("net.minecraft.network.play.client.", "");
            }

            if(((SendPacket) e).getPacket() instanceof C0FPacketConfirmTransaction){
                C0FPacketConfirmTransaction packet = (C0FPacketConfirmTransaction)((SendPacket) e).getPacket();
                Wrapper.getLogger().addChat("C0FPacketConfirmTransaction UID=" + packet.uid + " WINDOWID=" + packet.getWindowId());
            }else if(((SendPacket) e).getPacket() instanceof C00PacketKeepAlive){
                C00PacketKeepAlive packet = (C00PacketKeepAlive)((SendPacket) e).getPacket();
                Wrapper.getLogger().addChat("C00PacketKeepAlive KEY=" + packet.getKey());
            }else if(((SendPacket) e).getPacket() instanceof C01PacketPing){
                C01PacketPing packet = (C01PacketPing)((SendPacket) e).getPacket();
                Wrapper.getLogger().addChat("C01PacketPing CTIME=" + packet.getClientTime());
            }
        }
    }
}
