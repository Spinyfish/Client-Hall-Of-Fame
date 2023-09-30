package frapppyz.cutefurry.pics.modules.impl.move;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.*;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.MoveUtil;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelEnderMite;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;

import java.util.ArrayList;

public class Longjump extends Mod {

    private Mode mode = new Mode("Mode", "Vulcan", "Vulcan", "Hypixel");
    private Mode jumpCount = new Mode("Jumps", "3", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private int jumps, ticks, spoofy;
    private boolean memed, jumping;
    private ArrayList<Packet> packets = new ArrayList<>();
    public Longjump() {
        super("Longjump", "Jump longer than your dick", 0, Category.MOVE);
        addSettings(mode, jumpCount);
    }

    public void onEnable(){
        memed = false;
        jumping = false;
        jumps = 0;
        ticks = 0;
    }

    public void onDisable(){
        mc.timer.timerSpeed = 1;
        MoveUtil.setSpeed(0);
    }

    public void onRender(){
        jumpCount.setShowed(mode.is("Vulcan") || mode.is("Hypixel"));
    }

    public void onEvent(Event e){
        if(e instanceof Render && mode.is("Hypixel")){
            Gui.drawRect(mc.displayWidth/4 - 40, mc.displayHeight/3 - 15, mc.displayWidth/4 + 40, mc.displayHeight/3 + 15, 1342177280);
            mc.fontRendererObj.drawStringWithShadow("Wait for damage.", mc.displayWidth/4 - 35, mc.displayHeight/3 - 5, -1);
        }

        if(e instanceof SendPacket){
            if(mode.is("Hypixel")){
                if(!jumping && !mc.thePlayer.onGround){
                    e.setCancelled(true);
                    packets.add(((SendPacket) e).getPacket());
                }
            }
        }
        if(e instanceof Motion){
            switch (mode.getMode()){

                case "Hypixel":
                    this.ticks++;
                    ((Motion) e).setOnGround(ticks == 39);

                    if(!jumping){
                        mc.thePlayer.cameraYaw = 0;
                        mc.thePlayer.cameraPitch = 0;
                        mc.thePlayer.posY = mc.thePlayer.lastTickPosY;
                        spoofy = (int) mc.thePlayer.posY;
                    }else if(mc.thePlayer.posY >= spoofy){
                        mc.thePlayer.cameraYaw = 0.1f;
                        mc.thePlayer.posY = spoofy+0.42;
                    }

                    if(ticks == 35){
                        jumping = true;
                    }

                    if((jumping || mc.thePlayer.onGround) && !packets.isEmpty()){
                        Wrapper.getLogger().addChat("Bonk");
                        packets.forEach(PacketUtil::sendPacketNoEvent);
                        packets.clear();
                    }

                    if(ticks > 40 && mc.thePlayer.onGround){
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                        toggle();
                    }
                    if(jumps < 4) {
                        if(mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            jumps++;
                        }
                    }
                    break;
                case "Vulcan":
                        if(jumps == 0){
                            MoveUtil.setSpeed(MoveUtil.getSpeed()*1.45);
                            mc.thePlayer.jump();
                            jumps++;
                    }else{
                        if(mc.thePlayer.onGround){
                            toggle();
                        }
                        if(mc.thePlayer.fallDistance != 0){

                            if(mc.thePlayer.fallDistance >= 0.4 && jumps < 2){
                                jumps++;
                                mc.thePlayer.fallDistance = 0;
                                MoveUtil.setSpeed(MoveUtil.getSpeed()*1.28);
                                mc.thePlayer.motionY = 0.42f;
                            }else if(mc.thePlayer.fallDistance >= 1.2 && jumps < (Integer.parseInt(jumpCount.getMode()))){
                                jumps++;
                                mc.thePlayer.fallDistance = 0;
                                MoveUtil.setSpeed(MoveUtil.getSpeed()*1.2);
                                mc.thePlayer.motionY = 0.21f;
                            }else if(mc.thePlayer.ticksExisted % 3 != 0){
                                mc.thePlayer.motionY = -0.1;
                            }
                        }
                    }
                    break;
            }
        }
        if(e instanceof ReceivePacket){

            if(mode.is("Hypixel") && ((ReceivePacket) e).getPacket() instanceof S12PacketEntityVelocity){
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity)((ReceivePacket) e).getPacket();
                if(packet.getEntityID() == mc.thePlayer.getEntityId()) {
                    memed = true;
                    e.setCancelled(true);
                    mc.thePlayer.motionY = packet.getMotionY()/8000D;
                    MoveUtil.setSpeed(MoveUtil.getSpeed()*1.125);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                    mc.thePlayer.motionY = packet.getMotionY()/8000D;
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {}
                    MoveUtil.setSpeed(MoveUtil.getSpeed()*1.125);
                    mc.thePlayer.motionY = packet.getMotionY()/8000D;
                }
            }
        }
        if(e instanceof Move){
            if(mode.is("Hypixel")){
                if(!jumping){
                    ((Move) e).setX(0);
                    ((Move) e).setZ(0);
                }
            }

        }
    }
}
