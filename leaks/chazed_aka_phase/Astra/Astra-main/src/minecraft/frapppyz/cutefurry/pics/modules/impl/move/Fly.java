package frapppyz.cutefurry.pics.modules.impl.move;

import com.mojang.realmsclient.gui.ChatFormatting;
import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.*;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.MathUtil;
import frapppyz.cutefurry.pics.util.MoveUtil;
import frapppyz.cutefurry.pics.util.PacketUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;

public class Fly extends Mod {

    private ArrayList<Packet> packets = new ArrayList<Packet>();
    private Mode mode = new Mode("Mode", "Vanilla", "Vanilla", "Funcraft", "FuncraftVanilla", "BlocksMC", "AAC5", "Vulcan", "Sparky", "Matrix");
    private Mode speed = new Mode("Speed", "1.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", "2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9", "3.0");

    private Mode matrixMode = new Mode("Matrix Mode", "Boost", "Boost", "Vanilla");
    private Mode vulcanMode = new Mode("Vulcan Mode", "Fast", "Fast", "Safe", "Airwalk");
    private boolean started;
    private int ticks;
    private double x,y,z;

    private double sped;
    public Fly() {
        super("Fly", "Defy gravity.", 0, Category.MOVE);
        addSettings(mode, speed, vulcanMode, matrixMode);
    }

    public void onDisable(){
        mc.timer.timerSpeed = 1;
        ticks = 0;
        sped = 0;
        started = false;

        if(mode.is("AAC5")){
            mc.thePlayer.capabilities.isFlying = false;
            MoveUtil.setSpeed(0);
        }
        if(mode.is("Vanilla") || mode.is("Vulcan") || mode.is("Matrix")){
            MoveUtil.setSpeed(0);
        }
        if(mode.is("BlocksMC")){
            MoveUtil.setSpeed(0.5);
        }

        if(mode.is("FuncraftVanilla")){
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.lastTickPosY, mc.thePlayer.posZ);
            mc.thePlayer.motionY = 0;
        }

        if(!packets.isEmpty() && !mode.is("Vulcan")){
                packets.forEach(PacketUtil::sendPacketNoEvent);
        }
        packets.clear();
    }

    public void onRender(){
        speed.setShowed(mode.is("Vanilla"));
        vulcanMode.setShowed(mode.is("Vulcan"));
        matrixMode.setShowed(mode.is("Matrix"));
    }

    public void onEvent(Event e){

        if(e instanceof Render){
            this.setSuffix(mode.getMode());


            if(mode.is("Vulcan")){
                Gui.drawRect(mc.displayWidth/4 - 40, mc.displayHeight/3 - 10, mc.displayWidth/4 + 40, mc.displayHeight/3 + 10, 1342177280);
                mc.fontRendererObj.drawStringWithShadow("Memes sent: " + packets.size(), mc.displayWidth/4 - 35, mc.displayHeight/3 - 5, -1);
            }
        }

        if(e instanceof Motion){
            mc.thePlayer.cameraYaw = 0.1f;

            if(mode.is("FuncraftVanilla")){
                ((Motion) e).setOnGround(true);
                mc.thePlayer.posY = mc.thePlayer.lastTickPosY;
            }

            if(mode.is("Matrix")){
                switch (matrixMode.getMode()){
                    case "Boost":
                        if(ticks <= 2) {
                            mc.thePlayer.motionY = 0.42f;
                            MoveUtil.setSpeed(MoveUtil.getSpeed()*2.6);
                        }
                        break;
                    case "Vanilla":
                        if(ticks < 80){
                            MoveUtil.setSpeed(0);
                            if(ticks % 2 == 0){
                                mc.thePlayer.motionY = 0.42f;
                                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition());
                            }
                        }else{
                            mc.timer.timerSpeed = 0.5f;
                            mc.thePlayer.motionY = 1e-3;
                            MoveUtil.setSpeed(3);
                        }
                        break;
                }
            }

            if(mode.is("Sparky")){
                if(ticks % 2 == 0){
                    MoveUtil.setSpeed(MoveUtil.getSpeed()*1.15);
                }
                if(ticks % 12 == 0){
                    mc.thePlayer.motionY = 0.42f;
                    MoveUtil.setSpeed(MoveUtil.getSpeed()*1.6);
                }else if(ticks % 12 == 6){
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition());
                }
            }

            if(mode.is("AAC5")){
                mc.thePlayer.cameraYaw = 0.4f;
                mc.timer.timerSpeed = 0.2f;

                mc.thePlayer.capabilities.isFlying = true;

                    if(mc.thePlayer.ticksExisted % 4 == 0){
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition());
                        MoveUtil.setSpeed(0);
                    }else{
                        MoveUtil.setSpeed(9);
                    }
            }

            if(mode.is("Funcraft")) ((Motion) e).setOnGround(true);
            else if(mode.is("FuncraftVanilla") && false) {
                if(ticks == 1){
                    for(int i = 0; i < 8; i++){
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.42f, mc.thePlayer.posZ, false));
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
                    }
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer(true));
                }else if(mc.thePlayer.hurtTime == 9){
                    mc.thePlayer.motionY = 0.42f;
                }
            }


            if(mode.is("Vanilla")){
                if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                    mc.thePlayer.motionY = 0.42f;
                }else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                    mc.thePlayer.motionY = -0.42f;
                }else{
                    mc.thePlayer.motionY = 0;
                }
            }

            if(mode.is("Vulcan")){
                if(vulcanMode.is("Airwalk")){
                    mc.timer.timerSpeed = 5.61f/20;
                }else if(vulcanMode.is("Safe")){
                    mc.timer.timerSpeed = 0.5f;
                }
                if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                    mc.thePlayer.motionY = 0.21f;
                }else if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)){
                    mc.thePlayer.motionY = -0.21f;
                }else{
                    mc.thePlayer.motionY = 0;
                }
            }
        }

        if(e instanceof Move){

            if(mode.is("Funcraft")){
                if(ticks == 1){
                    if(mc.thePlayer.onGround){
                        mc.timer.timerSpeed = 3.4f;
                        ((Move) e).setY(mc.thePlayer.motionY = 0.42f);
                        //sped = 1.875;
                        sped = 1.725;
                        if(mc.thePlayer.isPotionActive(Potion.moveSpeed)){
                            sped = 1.8;
                        }
                        MoveUtil.setSpeed((Move) e, (0.2873*2.1));
                    }else{
                        sped = MoveUtil.getSpeed();
                    }

                }else{
                    if(ticks == 12){
                        mc.timer.timerSpeed = 0.55f;
                    }
                    ((Move) e).setY(mc.thePlayer.motionY = -1e-10);
                    sped -= sped/159;
                    if(mc.thePlayer.isCollided) sped = 0;
                    MoveUtil.setSpeed((Move) e, Math.max(5.75/20, sped));
                }
            }

            if(mode.is("FuncraftVanilla") && false){
                if(mc.thePlayer.hurtTime != 0) MoveUtil.setSpeed((Move) e, MoveUtil.getSpeed()*1.15);
                if(ticks > 1 && mc.thePlayer.hurtTime < 8){
                    if(mc.thePlayer.hurtResistantTime >= 16){
                        mc.timer.timerSpeed = 6;
                    }else{
                        mc.timer.timerSpeed -= mc.timer.timerSpeed/1590;
                    }
                    ((Move) e).setY(mc.thePlayer.motionY = -1e-10);
                    sped -= sped/159;
                    if(mc.thePlayer.isCollided) sped = 0;
                    MoveUtil.setSpeed((Move) e, 6.5/20);
                }
                }
            if(mode.is("FuncraftVanilla")){
                if(mc.thePlayer.ticksExisted % 11 == 0){
                    if(!packets.isEmpty()){
                        packets.forEach(PacketUtil::sendPacketNoEvent);
                        packets.clear();
                    }
                    mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY), mc.thePlayer.posZ);
                    ((Move) e).setY(mc.thePlayer.motionY = 0.42f);
                    mc.thePlayer.jump();
                    if(sped > 6.2/20){
                        if(sped > 9.8/20){
                            MoveUtil.setSpeed(13.5/20);
                        }else{
                            MoveUtil.setSpeed(12.4/20);
                        }
                    }
                    sped = MoveUtil.getSpeed();
                }else{
                    sped -= sped/49;

                    if(mc.thePlayer.isCollidedHorizontally || mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0) sped = 0.35;

                    MoveUtil.setSpeed((Move) e,  sped);
                }
            }

            if(mode.is("Vanilla")){


                MoveUtil.setSpeed((Move) e, Double.parseDouble(speed.getMode()));
            }
            if(mode.is("Vulcan")){
                MoveUtil.setSpeed((Move) e, ticks >= 100 ? 1 : 0);
            }
        }

        if(e instanceof Update){
            ticks++;

            if(mode.is("Vulcan")){
                if(ticks == 1){
                    mc.timer.timerSpeed = 1;
                    PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1 - Math.random()*3, mc.thePlayer.posZ, false));
                }


            }

            if(mode.is("BlocksMC")){
                if(ticks == 1) {
                    mc.thePlayer.jump();
                    started = false;
                }else{
                    if(ticks != 0){
                        if(started){
                            if(ticks == 6){
                                MoveUtil.setSpeed(9.5);

                            }else{
                                if(ticks <= 8){
                                    MoveUtil.setSpeed(8.175);
                                }else if(ticks <= 12){
                                    MoveUtil.setSpeed(6.44554);
                                }else {
                                    MoveUtil.setSpeed(0.65);
                                }

                            }
                            mc.timer.timerSpeed = 0.5f;
                            //mc.timer.timerSpeed = 1;
                        }else{
                            if(mc.thePlayer.onGround){
                                mc.thePlayer.jump();
                                started = true;
                                ticks = 5;
                            }


                        }
                    }


                }
            }

        }

        if(e instanceof ReceivePacket){

            if(mode.is("Matrix") && ((ReceivePacket) e).getPacket() instanceof S08PacketPlayerPosLook){
                S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)((ReceivePacket) e).getPacket();
                e.setCancelled(true);
                mc.thePlayer.setPosition(packet.x, packet.y, packet.z);
                mc.thePlayer.rotationYaw = packet.getYaw();
                mc.thePlayer.rotationPitch = packet.getPitch();
            }

            if(mode.is("Vulcan") && ((ReceivePacket) e).getPacket() instanceof S08PacketPlayerPosLook && ticks < 100){
                ticks = 100;
                packets.clear();
            }
        }


        if(e instanceof SendPacket){

            if(mode.is("FuncraftVanilla")){
                if(((SendPacket) e).getPacket() instanceof C03PacketPlayer){
                    e.setCancelled(true);
                    packets.add(((SendPacket) e).getPacket());
                }
            }

            if(mode.is("Vulcan")) {
                if (((SendPacket) e).getPacket() instanceof C03PacketPlayer) {
                    if (ticks % 9 == 0 && packets.size() < 6) {
                        C03PacketPlayer pack = (C03PacketPlayer) ((SendPacket) e).getPacket();
                        //pack.y += Math.random() * 0.1;
                        pack.onGround = true;
                        packets.add(pack);

                    } else{
                        {
                            e.setCancelled(true);
                        }
                    }

                }
            }
            if(mode.is("BlocksMC") ){
                if(((SendPacket) e).getPacket() instanceof C0FPacketConfirmTransaction){
                    C0FPacketConfirmTransaction pack = (C0FPacketConfirmTransaction)((SendPacket) e).getPacket();
                    pack.uid = Short.MIN_VALUE;
                }
            }
        }

    }
}
