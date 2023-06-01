package frapppyz.cutefurry.pics.modules.impl.player;

import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.Render;
import frapppyz.cutefurry.pics.event.impl.Update;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class AntiVoid extends Mod {

    Mode mode = new Mode("Mode", "TP", "TP", "Packet", "Motion");
    private Vec3 vec;
    public AntiVoid() {
        super("AntiVoid", "no void anymore", 0, Category.PLAYER);
        addSettings(mode);
    }

    public void onEvent(Event e){
        if(e instanceof Render){
            this.setSuffix(mode.getMode());
        }
        if(e instanceof Update){
            if(mc.thePlayer.onGround && isBlockUnderPlayer()){
                vec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
            }



            if(shouldSetBack()){
                switch (mode.getMode()){
                    case "TP":
                        if(mc.thePlayer.getDistanceSq(vec.xCoord, vec.yCoord, vec.zCoord) < 10){
                            mc.thePlayer.setPosition(vec.xCoord, vec.yCoord, vec.zCoord);
                        }else{
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ, true));
                        }

                       break;
                    case "Packet":
                        for(int i = 0; i < 5; i++){
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + (i*Math.random()), mc.thePlayer.posZ, true));
                        }
                        break;
                    case "Motion":
                        mc.thePlayer.motionY = 1.42f;
                        mc.thePlayer.fallDistance = 0;
                        break;
                }
            }
        }
    }

    private boolean isBlockUnderPlayer() {
        for(int i = 0; i < (int) mc.thePlayer.posY; i++) {
            if(!(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, i, mc.thePlayer.posZ)).getBlock() instanceof BlockAir)) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldSetBack() {
        return !mc.thePlayer.onGround && !isBlockUnderPlayer() && mc.thePlayer.fallDistance >= 5 && !mc.thePlayer.isInWater();
    }
}
