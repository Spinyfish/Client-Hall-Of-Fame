package frapppyz.cutefurry.pics.modules.impl.player;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Motion;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall extends Mod {

    private Mode mode = new Mode("Mode", "Packet", "Packet", "Edit", "Flag");
    public NoFall() {
        super("NoFall", "Fall the no", 0, Category.PLAYER);
        addSettings(mode);
    }

    public void onEvent(Event e){
        if(e instanceof Render){
            this.setSuffix(mode.getMode());
        }

        if(e instanceof Motion){
            switch (mode.getMode()){
                case "Edit":
                    if(mc.thePlayer.fallDistance >= 2 && mc.thePlayer.fallDistance % 2 == 0){
                        ((Motion) e).setOnGround(true);
                    }
                    break;
            }
        }

        if(e instanceof Update){
            switch (mode.getMode()){
                case "Packet":
                    if(mc.thePlayer.fallDistance >= 2 && mc.thePlayer.fallDistance % 2 == 0){
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
                    }
                    break;
                case "Flag":
                    if(mc.thePlayer.fallDistance >= 5 && mc.thePlayer.fallDistance % 5 == 0){
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition());
                    }
                    break;
            }

        }
    }
}
