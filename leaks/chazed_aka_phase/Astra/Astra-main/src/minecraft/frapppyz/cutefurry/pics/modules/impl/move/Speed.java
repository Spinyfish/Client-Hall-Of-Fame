package frapppyz.cutefurry.pics.modules.impl.move;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.*;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.impl.combat.Killaura;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.modules.settings.impl.Number;
import frapppyz.cutefurry.pics.util.MoveUtil;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;

public class Speed extends Mod {
    private ArrayList<Packet> packets = new ArrayList<>();
    private double ypos;
    private double sped;
    private boolean prevOnGround;
    private Mode mode = new Mode("Mode", "Vanilla", "Vanilla", "Funcraft", "Funcraft2", "BlocksMC", "Hypixel", "Vulcan");
    //private Mode speed = new Mode("Speed", "0.5", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9", "3.0");

    private Number speed = new Number("Speed", 0.1, 0.5, 1, 0.1);
    private int timerTicks;

    public Speed() {
        super("Speed", "Bhop here", 0, Category.MOVE);
        addSettings(mode, speed);
    }

    public void onDisable() {
        sped = 0;
        mc.timer.timerSpeed = 1;
        mc.thePlayer.stepHeight = 0.6f;
        if(mode.is("Funcraft")) MoveUtil.setSpeed(0);
        if(mode.is("Hypixel")) {mc.thePlayer.setPosition(mc.thePlayer.posX, ypos, mc.thePlayer.posZ); mc.thePlayer.motionY = 0;}
        if(!packets.isEmpty()){packets.forEach(PacketUtil::sendPacketNoEvent); packets.clear();}
    }

    public void onRender(){
        speed.setShowed(mode.is("Vanilla"));
    }

    public void onEvent(Event e) {

        if (e instanceof Render) {
            this.setSuffix(mode.getMode());
        }

        if (e instanceof SendPacket) {
            if(mode.is("Hypixel") && ((SendPacket) e).getPacket() instanceof C03PacketPlayer){
                e.setCancelled(true);
                packets.add(((SendPacket) e).getPacket());
            }
        }

        if (e instanceof Motion) {
            switch (mode.getMode()){
                case "BlocksMC":
                    if(mc.thePlayer.onGround){
                        mc.thePlayer.jump();
                        MoveUtil.setSpeed(MoveUtil.getSpeed() + (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.2 : 0.05));
                    }
                    break;
                case "Hypixel":

                        if(mc.thePlayer.onGround){
                            packets.forEach(PacketUtil::sendPacketNoEvent);
                            packets.clear();
                            ypos = mc.thePlayer.posY;
                            mc.thePlayer.motionY = 0.32f;
                        }else if(mc.thePlayer.posY > ypos){
                            if(Killaura.entity != null){
                                mc.timer.timerSpeed = 0.9f;
                            }else{
                                mc.timer.timerSpeed = 1;
                            }

                            ((Motion) e).setY(ypos);
                            if(mc.thePlayer.hurtTime > 0) ((Motion) e).setOnGround(false); else ((Motion) e).setOnGround(true);


                        }

                    break;
                case "Vanilla":
                    if(mc.thePlayer.onGround){
                        mc.thePlayer.jump();
                    }
                    break;

                case "Funcraft2":
                    if(mc.thePlayer.onGround){
                        mc.thePlayer.jump();
                        if(sped > 6.2/20){
                            if(sped > 9.8/20){
                                MoveUtil.setSpeed(13.5/20);
                            }else{
                                MoveUtil.setSpeed(12.4/20);
                            }
                        }
                        sped = MoveUtil.getSpeed();
                    }
                    break;

                case "Funcraft":
                    if(mc.thePlayer.ticksExisted % 2 == 0){
                        mc.thePlayer.jump();
                        MoveUtil.setSpeed(0.5);
                        if(mc.thePlayer.isPotionActive(Potion.moveSpeed)){
                            MoveUtil.setSpeed(0.645);
                        }
                        mc.timer.timerSpeed = 1f;
                    }else{
                        mc.thePlayer.cameraYaw = 0.1f;
                        mc.thePlayer.posY = mc.thePlayer.lastTickPosY;
                        mc.thePlayer.motionY = -1;
                    }
                    break;
            }
        }

        if (e instanceof Move) {

            if(mode.is("BlocksMC")){
                MoveUtil.setSpeed((Move) e, MoveUtil.getSpeed());
            }

            if (mode.is("Funcraft2") && !mc.thePlayer.onGround) {
                sped -= sped/49;

                if(mc.thePlayer.isCollidedHorizontally || mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0) sped = 0.35;

                MoveUtil.setSpeed((Move) e,  sped);
            }
            if (mode.is("Vanilla")) {
                //MoveUtil.setSpeed((Move) e, Double.parseDouble(speed.getMode()));
                MoveUtil.setSpeed((Move) e, Double.parseDouble(speed.getValue()));
            }

            if(mode.is("Hypixel") && mc.thePlayer.posY >= ypos){
                if(mc.thePlayer.isPotionActive(Potion.moveSpeed)){
                    MoveUtil.setSpeed((Move) e, 8.01/20);
                }else{
                    if(mc.thePlayer.ticksExisted % 2 == 0){
                        MoveUtil.setSpeed((Move) e, 5.86/20);
                    }else{
                        MoveUtil.setSpeed((Move) e, 5.71/20);
                    }
                }

            }

            if(mode.is("Funcraft")){

                MoveUtil.setSpeed((Move)e, MoveUtil.getSpeed()*1.02);
            }

        }

        if (e instanceof Update) {
            timerTicks++;
            if(mc.thePlayer.onGround && mode.is("Vulcan")){
                mc.thePlayer.jump();

                MoveUtil.setSpeed(mc.thePlayer.isPotionActive(Potion.moveSpeed) ? MoveUtil.getSpeed() * 1.3 : MoveUtil.getSpeed());
                timerTicks = 0;
            }

            if(mode.is("Vulcan")){
                if(timerTicks == 4){
                        mc.thePlayer.motionX *= 0.96f;
                        mc.thePlayer.motionZ *= 0.96f;
                        mc.thePlayer.motionY = -0.16f;
                }
            }
        }
    }
}
