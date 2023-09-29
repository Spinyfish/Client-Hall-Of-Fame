package frapppyz.cutefurry.pics.modules.impl.world;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.event.Event;
import frapppyz.cutefurry.pics.event.impl.*;
import frapppyz.cutefurry.pics.modules.Category;
import frapppyz.cutefurry.pics.modules.Mod;
import frapppyz.cutefurry.pics.modules.settings.impl.Boolean;
import frapppyz.cutefurry.pics.modules.settings.impl.Mode;
import frapppyz.cutefurry.pics.util.MoveUtil;
import frapppyz.cutefurry.pics.util.PacketUtil;
import frapppyz.cutefurry.pics.util.RotUtil;
import lombok.Data;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Scaffold extends Mod {

    @Data
    static class BlockInfo {
        private final BlockPos pos;
        private final EnumFacing facing;
    }

    private Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    private BlockInfo getBlock() {
        List<BlockPos> blocks = new ArrayList<>(Collections.singletonList(new BlockPos(mc.thePlayer.posX, !keepY.is("Off") || keepY.is("Telly2") ? value : mc.thePlayer.posY - 1, mc.thePlayer.posZ)));
        BlockPos[] offsets = {new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};

        for(int i = 0; i < 8; i++) {
            List<BlockPos> copy = new ArrayList<>(blocks);
            blocks.clear();

            for(BlockPos origin : copy) {
                for(BlockPos offset : offsets) {
                    BlockPos block = origin.add(offset);
                    if (getBlock(block).isFullBlock() || getBlock(block) instanceof BlockStainedGlass || getBlock(block) instanceof BlockGlass) {
                        return new BlockInfo(block, EnumFacing.getFacingFromVector(-offset.getX(), -offset.getY(), -offset.getZ()));
                    }
                    blocks.add(block);
                }
            }
        }

        return null;
    }

    public Vec3 getVector() {
        double x = block.getPos().getX() + 0.5f + (0.5f * block.getFacing().getDirectionVec().getX()),
                y = block.getPos().getY() + 0.5f + (0.5f * block.getFacing().getDirectionVec().getY()),
                z = block.getPos().getZ() + 0.5f + (0.5f * block.getFacing().getDirectionVec().getZ());

        return new Vec3(x, y, z);
    }

    public Scaffold() {
        super("Scaffold", "Block.", 0, Category.WORLD);
        addSettings(rots, sprint, swing, tower, keepY);
    }

    private BlockInfo block;

    private double value;

    private final Mode sprint = new Mode("Sprint", "Vanilla", "Vanilla", "Matrix", "Gomme", "Hypixel", "Vulcan", "Grim", "None");
    private final Mode rots = new Mode("Rots", "Vanilla", "Vanilla", "Matrix", "Hypixel", "Grim", "GrimKEK", "None");
    private final Boolean swing = new Boolean("Swing", true);
    private final Mode tower = new Mode("Tower", "None", "None", "NCP", "Hypixel");
    private final Mode keepY = new Mode("KeepY", "Off", "Normal", "Telly", "Telly2", "Off");
    private final List<Packet> packets = new ArrayList<>();
    private double y;

    private boolean placeTick;
    private boolean placed;
    private boolean isPakiLoved;
    private static int stack;

    public void onEnable(){
        stack = mc.thePlayer.inventory.currentItem;
        isPakiLoved = false;
        value=mc.thePlayer.posY - 1;
        y = 0;
    }

    public void onDisable(){
        if(sprint.is("Gomme")){
            mc.gameSettings.keyBindSprint.pressed = false;
        }
        if(sprint.is("Grim")){
            if(!Wrapper.getModManager().getModByName("Blink").isToggled()){
                packets.forEach(PacketUtil::sendPacketNoEvent);
            }
            packets.clear();
        }
        mc.thePlayer.inventory.currentItem = stack;
        mc.timer.timerSpeed = 1;
    }


    public void onEvent(Event e) {

        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) tower(e);

        if(e instanceof Render){
        }

        if(e instanceof Motion){

            if(rots.is("Vanilla")){
                ((Motion) e).setYaw(mc.thePlayer.rotationYaw-180);
                ((Motion) e).setPitch(87);
                RotUtil.renderRots(mc.thePlayer.rotationYaw-180, 88);
            }
            if(rots.is("Hypixel")){
                ((Motion) e).setPitch((float) 93);
            }

            if(rots.is("Grim")){
                //((Motion) e).setPitch(85.5f);
                ((Motion) e).setPitch(85.5f);
                ((Motion) e).setYaw(mc.thePlayer.rotationYaw-180);
            }

            if(rots.is("Matrix")){
                RotUtil.renderRots((float) RotUtil.yaw, 0);
                ((Motion) e).setYaw(mc.thePlayer.ticksExisted % 10 == 0 ? (mc.thePlayer.rotationYaw + 7 - (float) (Math.random()*14)) : (float) RotUtil.yaw);
                ((Motion) e).setPitch(90);
            }

            if(rots.is("GrimKEK")){

                if(placeTick) isPakiLoved = true;
                if(!isPakiLoved) y=0;

                if(y > 5) ((Motion) e).setPitch((float) (95.79 - Math.random()/50)); else ((Motion) e).setPitch((float) (95.75));
                if(mc.thePlayer.ticksExisted % 5 == 0 && !mc.thePlayer.isSneaking()){
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                }

            }

        }

        if(e instanceof SendPacket){
            if(sprint.is("Grim")){
                e.setCancelled(true);
                packets.add(((SendPacket) e).getPacket());
            }
        }

        if(e instanceof Update){

            if(mc.thePlayer.onGround) value = mc.thePlayer.posY - 1;
            if(keepY.is("Telly2") && mc.thePlayer.onGround){value += 1;}

            for(int i = 0; i < 9; i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i) == null)
                    continue;
                if (mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock) {
                    mc.thePlayer.inventory.currentItem = i;
                    break;
                }
            }

            if(sprint.is("Vanilla")){
                mc.gameSettings.keyBindInventory.pressed = true;
            }else if(sprint.is("Grim")){
                mc.gameSettings.keyBindInventory.pressed = isPakiLoved && mc.thePlayer.onGround;
                mc.thePlayer.setSprinting(isPakiLoved && mc.thePlayer.onGround);
                if(packets.size() >= 23 && !Wrapper.getModManager().getModByName("Blink").isToggled()){
                    for(int i = 0; i < 5; i++){
                        isPakiLoved = true;
                        PacketUtil.sendPacketNoEvent(packets.get(0));
                        packets.remove(0);
                    }
                }

            }else if(sprint.is("Gomme")){
                mc.gameSettings.keyBindInventory.pressed = false;
                mc.thePlayer.setSprinting(false);
                mc.gameSettings.keyBindSprint.pressed = true;
                mc.thePlayer.motionX *= 1.562;
                mc.thePlayer.motionZ *= 1.562;
            }else if(sprint.is("Hypixel")) {
                mc.gameSettings.keyBindInventory.pressed = false;
                mc.thePlayer.setSprinting(false);
            }else if(sprint.is("Vulcan")){
                mc.gameSettings.keyBindInventory.pressed = false;
                mc.thePlayer.setSprinting(false);
                if(mc.thePlayer.ticksExisted % 3 != 0){
                    mc.timer.timerSpeed = 1.5f;
                }else{
                    mc.timer.timerSpeed = 0.75f;
                }

                if(mc.thePlayer.ticksExisted % 30 == 0){
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                }else if(mc.thePlayer.ticksExisted % 30 == 1){
                    PacketUtil.sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                }
            }else if(sprint.is("Matrix")) {
                mc.gameSettings.keyBindInventory.pressed = false;
                mc.thePlayer.setSprinting(false);
                MoveUtil.setSpeed(4.9/20);
            }else{
                mc.gameSettings.keyBindInventory.pressed = false;
                mc.thePlayer.setSprinting(false);
            }

            y++;
            block = getBlock();



            if(block != null) {
                placeTick = getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)) == Blocks.air;

                if(keepY.is("Telly")){
                    if(mc.thePlayer.fallDistance != 0 || mc.thePlayer.onGround){
                        place();
                    }
                }else{
                    place();
                }

            }
        }

        if(e instanceof Move){
            if(sprint.is("Hypixel") && mc.thePlayer.onGround){
                if(mc.thePlayer.isPotionActive(Potion.moveSpeed)){

                    MoveUtil.setSpeed((Move) e, 5.61/20);
                }else{
                    MoveUtil.setSpeed((Move) e, mc.thePlayer.ticksExisted % 3 == 0 ? 5.95/20 : 4.1/20);
                }

            }
            if(sprint.is("Vulcan")){
                if(mc.thePlayer.onGround){
                    MoveUtil.setSpeed((Move) e, 5.99999/20);
                }

            }
        }

    }

    public void tower(Event e){
        if(e instanceof Update){
                switch (tower.getMode()){
                    case "NCP":
                        if(mc.thePlayer.ticksExisted % 3 == 0) mc.thePlayer.motionY=0.42f; else mc.thePlayer.onGround=true;
                        break;
                    case "Hypixel":
                        if(mc.thePlayer.ticksExisted % 4 == 0){
                            mc.thePlayer.onGround = true;
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688698, mc.thePlayer.posZ, false));
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212, mc.thePlayer.posZ, false));
                            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.00597911214, mc.thePlayer.posZ, false));
                            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ);
                        }

                        break;
                }
        }

        if(e instanceof Move){
            switch (tower.getMode()){
                case "Hypixel":
                    ((Move) e).setY(mc.thePlayer.motionY = 0);
                    break;
            }
        }
    }

    public void place() {
        if(placeTick) {
            placed = mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(), block.getPos(), block.getFacing(), getVector());
            if(placed){
                if(!swing.isToggled())
                    PacketUtil.sendPacket(new C0APacketAnimation());
                else
                    mc.thePlayer.swingItem();
            }

        }
    }

}
