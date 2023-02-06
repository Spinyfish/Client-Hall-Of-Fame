package dev.elysium.client.mods.impl.player;

import dev.elysium.base.events.types.EventTarget;
import dev.elysium.base.mods.Category;
import dev.elysium.base.mods.Mod;
import dev.elysium.base.mods.settings.BooleanSetting;
import dev.elysium.base.mods.settings.ModeSetting;
import dev.elysium.base.mods.settings.NumberSetting;
import dev.elysium.client.Elysium;
import dev.elysium.client.events.EventMotion;
import dev.elysium.client.events.EventMove;
import dev.elysium.client.utils.Timer;
import dev.elysium.client.utils.player.RotationUtil;
import net.minecraft.block.*;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

import java.util.Objects;

public class Scaffold extends Mod {

    public BlockPos prevBlock;
    public float[] prevRots;
    public Timer timer = new Timer();

    public ModeSetting tower = new ModeSetting("Tower",this,"None","Hypixel","Verus","Test");
    public ModeSetting switchmodes = new ModeSetting("Switch Mode",this,"Spoof","Switch Spoof","Switch");
    public ModeSetting rot = new ModeSetting("Rotation",this,"Hypixel","Reverse","Normal","Snap","None");
    public ModeSetting hypixelmode = new ModeSetting("Hypixel Mode",this,"Safe","Unsafe");
    public NumberSetting accuracy = new NumberSetting("Accuracy",0,180,175,1,this);
    public NumberSetting expandsize = new NumberSetting("Expand Size",0,6,0,0.05,this);
    public ModeSetting safewalk = new ModeSetting("Safe Walk",this,"OnGround Only","InAir Only","Both","Eagle","None");
    public NumberSetting delay = new NumberSetting("Delay",0,2000,75,1,this);
    public ModeSetting swingmode = new ModeSetting("Swing",this,"Silent","Normal","None");
    public NumberSetting timervalue = new NumberSetting("Timer",0.01,10,1,0.01,this);
    public BooleanSetting keepy = new BooleanSetting("Keep Y",true, this);
    public BooleanSetting sprint = new BooleanSetting("Sprint",false, this);

    public BlockPos lastFrom;
    public int startslot,slot;
    public boolean hasblocks, isslotted;
    private double groundedposy;

    public Scaffold() {
        super("Scaffold","Bridges for you", Category.PLAYER);
    }

    public void onEnable() {
        slot = -1;
        startslot = mc.thePlayer.inventory.currentItem;
        prevRots = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };
        mc.timer.timerSpeed = (float) timervalue.getValue();
        super.onEnable();
    }

    public void onDisable() {
        startslot = mc.thePlayer.inventory.currentItem;
        prevRots = new float[] {
                mc.thePlayer.rotationYaw,
                mc.thePlayer.rotationPitch
        };
        if(switchmodes.is("Switch Spoof") && slot != -1)
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        mc.timer.timerSpeed = 1F;
        slot = -1;
        super.onEnable();
    }

    @EventTarget
    public void onEventMove(EventMove e) {
        if(!safewalk.is("None") && toggled) {
            //"OnGround Only","InAir Only","Both","Eagle","None"
            switch(safewalk.getMode()) {
                case "OnGround Only":
                    if(mc.thePlayer.onGround)
                        e.setSafewalk(true);
                    break;
                case "InAir Only":
                    if(!mc.thePlayer.onGround)
                        e.setSafewalk(true);
                    break;
                case "Both":
                    e.setSafewalk(true);
                    break;
                case "Eagle":
                    break;
            }
        }
    }

    @EventTarget
    public void onEventMotion(EventMotion e) {
        if(e.isPost() || !toggled)
            return;

        double xOffset = mc.thePlayer.motionX*(2*expandsize.getValue());
        double zOffset = mc.thePlayer.motionZ*(2*expandsize.getValue());


        if(Math.abs(zOffset) > Math.abs(Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * expandsize.getValue()))
            zOffset = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw)) * expandsize.getValue();

        if(Math.abs(xOffset) > Math.abs(Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * -expandsize.getValue()))
            xOffset = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw)) * -expandsize.getValue();

        mc.thePlayer.posX += xOffset;
        mc.thePlayer.posZ += zOffset;

        if(sprint.isEnabled())
            mc.gameSettings.keyBindSprint.pressed = true;
        else {
            mc.thePlayer.setSprinting(false);
            mc.gameSettings.keyBindSprint.pressed = false;
        }

        hasblocks = false;

        for(int x = 0; x <= 8; x++) {
            ItemStack stack = mc.thePlayer.inventory.getStackInSlot(x);

            if(stack != null && stack.getItem() != null && stack.getItem() instanceof ItemBlock) {
                ItemBlock blockitem = (ItemBlock) stack.getItem(); Block block = blockitem.getBlock();
                if(block instanceof BlockEnchantmentTable || block instanceof BlockWorkbench || block instanceof BlockFurnace || block instanceof BlockTNT || stack.stackSize < 1) continue;

                hasblocks = true;
                if(switchmodes.is("Switch")) {
                    mc.thePlayer.inventory.currentItem = x;
                    mc.playerController.updateController();
                } else if(switchmodes.is("Switch Spoof")) {
                    if(slot != x)
                        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(x));
                }

                slot = x;
                break;
            }
        }

        if(prevRots == null)
            prevRots = new float[] {mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitchHead};

        BlockPos bu = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ);

        float[] rot = rotatePre(bu);
        rot[1] = Math.min(rot[1],90);
        if(!Objects.equals(this.rot.getMode(), "None")) {
            e.setYaw(rot[0]);
            e.setPitch(rot[1]);
            mc.thePlayer.renderYawOffset = rot[0];
            mc.thePlayer.rotationYawHead = rot[0];
            mc.thePlayer.rotationPitchHead = rot[1];
        }
        if(bu.getBlock() instanceof BlockAir && slot > -1) {
            BlockPos temp = BlockPos.ORIGIN; Double distance = 500D;
            BlockPos from = BlockPos.ORIGIN;

            for(int x = -3; x <= 3; x++) {
                for(int y = -3; y <= 3; y++) {
                    for(int z = -3; z <= 3; z++) {
                        temp = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                        if(temp.getBlock() instanceof BlockAir || mc.thePlayer.getDistance(temp.getX()+0.5, temp.getY()+1.5, temp.getZ()+0.5) > distance) continue;
                        lastFrom = from = temp; distance = mc.thePlayer.getDistance(temp.getX()+0.5, temp.getY()+1.5, temp.getZ()+0.5);
                    }
                }
            }
            EnumFacing facing = mc.thePlayer.getHorizontalFacing(getRots(from, new Vec3(.5,0,.5))[0]+180);

            if(bu.down().getBlock() instanceof BlockAir) {
                rot = rotatePost(from, facing);
                rot[1] = Math.min(rot[1],90);
                prevRots = rot;
                if(!Objects.equals(this.rot.getMode(), "None")) {
                    e.setYaw(rot[0]);
                    e.setPitch(rot[1]);
                    mc.thePlayer.renderYawOffset = rot[0];
                    mc.thePlayer.rotationYawHead = rot[0];
                    mc.thePlayer.rotationPitchHead = rot[1];
                }
                if(timer.hasTimeElapsed((long) delay.getValue(), true) &&
                        (this.rot.is("Reverse") ||
                                (180 - accuracy.getValue()) >= Math.abs(RotationUtil.getYawChange(prevRots[0],getRots(from,getOffsets(facing,from).addVector(-from.getX(),-from.getY(),-from.getZ()))[0])))) {
                    Vec3 pos = getOffsets(facing, from);
                    int start_slot = mc.thePlayer.inventory.currentItem;
                    boolean spoof = switchmodes.is("Spoof");

                    if(swingmode.is("Silent"))
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                    else if(swingmode.is("Normal"))
                        mc.thePlayer.swingItem();

                    if(spoof) mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                    placeBlock(facing, mc.thePlayer.inventory.getStackInSlot(slot), from, pos);
                    if(spoof) mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(start_slot));
                }

            } // Scaffold
            else if(!keepy.isEnabled() || !mc.thePlayer.isMoving() || mc.thePlayer.onGround)
            { // Tower
                rot = rotatePost(from, facing);
                rot[1] = Math.min(rot[1],90);
                prevRots = rot;
                e.setYaw(rot[0]);
                e.setPitch(rot[1]);
                mc.thePlayer.renderYawOffset = rot[0];
                mc.thePlayer.rotationYawHead = rot[0];
                mc.thePlayer.rotationPitchHead = rot[1];

                if(timer.hasTimeElapsed((long) delay.getValue(), true)) {
                    facing = EnumFacing.UP;
                    Vec3 pos = getOffsets(facing, from);
                    int start_slot = mc.thePlayer.inventory.currentItem;
                    boolean spoof = switchmodes.is("Spoof");
                    if(swingmode.is("Silent"))
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                    else if(swingmode.is("Normal"))
                        mc.thePlayer.swingItem();
                    if(spoof) mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                    placeBlock(facing, mc.thePlayer.inventory.getStackInSlot(slot), from, pos);
                    if(spoof) mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(start_slot));
                }
            }

        } else
            timer.reset();

        if(mc.gameSettings.keyBindJump.pressed && !mc.thePlayer.isMoving() && hasblocks) {
            mc.thePlayer.posX -= xOffset;
            mc.thePlayer.posZ -= zOffset;
            mc.thePlayer.cameraPitch = 0;

            mc.thePlayer.isAirBorne = true;
            mc.thePlayer.triggerAchievement((StatList.jumpStat));
            switch (tower.getMode()) {
                case "Hypixel":
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        groundedposy = mc.thePlayer.posY;
                    } else if(mc.thePlayer.posY > groundedposy+1 && mc.thePlayer.posY - lastFrom.getY() <= 2.1) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                        groundedposy = mc.thePlayer.posY;
                        mc.thePlayer.posY += 0.17;
                        mc.thePlayer.motionY = 0.41;
                    }
                    break;

                case "Verus":
                    if(mc.thePlayer.onGround) {
                        mc.thePlayer.jump();
                        groundedposy = mc.thePlayer.posY;
                    } else if(mc.thePlayer.posY > groundedposy+1 && mc.thePlayer.posY - lastFrom.getY() <= 2.8) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.floor(mc.thePlayer.posY), mc.thePlayer.posZ);
                        groundedposy = mc.thePlayer.posY;
                        mc.thePlayer.posY += 0.15725;
                        mc.thePlayer.jump();
                    }
                    break;

                case "Test":
                    if(mc.thePlayer.onGround && e.isPre())
                        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.2, mc.thePlayer.posZ);
                    break;

                case "Dev":
                    if(mc.thePlayer.onGround) mc.thePlayer.motionY = 0.42;
                    mc.thePlayer.onGround = true;
                    break;

            }
        } else {
            mc.thePlayer.posX -= xOffset;
            mc.thePlayer.posZ -= zOffset;
        }
    }

    Vec3 getOffsets(EnumFacing facing, BlockPos b)
    {
        switch (facing) {
            case EAST:
                return new Vec3(b.getX()+1, b.getY()+0.5, b.getZ()+0.5);

            case WEST:
                return new Vec3(b.getX(), b.getY()+0.5, b.getZ()+0.5);

            case SOUTH:
                return new Vec3(b.getX()+0.5, b.getY()+0.5, b.getZ()+1);

            case NORTH:
                return new Vec3(b.getX()+0.5, b.getY()+0.5, b.getZ());

            case UP:
                return new Vec3(b.getX()+0.5, b.getY()+1, b.getZ()+0.5);

            case DOWN:
                return new Vec3(b.getX()+0.5, b.getY(), b.getZ()+0.5);

        }
        return null;
    }

    public void placeBlock(EnumFacing face, ItemStack stack, BlockPos bpos, Vec3 pos) {
        mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, bpos, face, pos);
    }

    public float[] rotatePost(BlockPos b, EnumFacing facing) {

        float[] rots = getRots(b,getOffsets(facing,b).addVector(-b.getX(),-b.getY(),-b.getZ()));
        float yaw = prevRots[0];
        float pitch = prevRots[1];

        switch(rot.getMode()) {
            case "Reverse":
                yaw = mc.thePlayer.rotationYaw + 180;
                pitch = 88;

                if(mc.thePlayer.getDistance(b.getX(), b.getZ()) > 1.3 && false) {
                    yaw = rots[0];
                    pitch = rots[1];
                }
                break;
            case "Hypixel":
                yaw += RotationUtil.getYawChange(yaw,rots[0])/(hypixelmode.is("Safe") ? 2 : 0.925);
                pitch = rots[1]+1+(hypixelmode.is("Safe") ? 0 : -2);
                Elysium.getInstance().addChatMessage("PITCH : " + pitch);
                break;
            case "Snap":
            case "Normal":
                yaw = rots[0];
                pitch = rots[1] + 2;
                break;
            case "None":
                yaw = rots[0];
                pitch = rots[1] + 1;
                break;
        }

        float gcd = mc.gameSettings.mouseSensitivity * 0.8F + 0.2F;
        gcd = gcd * gcd * gcd;

        pitch -= pitch % gcd;
        yaw -= yaw % gcd;

        return new float[] {yaw,pitch};
    }

    public float[] rotatePre(BlockPos b) {

        float[] rots = getRots(b,new Vec3(.5,0,.5));
        float yaw = prevRots[0];
        float pitch = prevRots[1];

        switch(rot.getMode()) {
            case "Reverse":
                yaw = mc.thePlayer.rotationYaw + 180;
                pitch = 88;
                break;
            case "Normal":
                pitch = rots[1];
                break;
            case "Hypixel":
                if(hypixelmode.is("Safe")) {
                    yaw += RotationUtil.getYawChange(yaw,rots[0])/20;
                    pitch = (float) (rots[1] - (Math.random() * 2));
                }
                break;
        }

        float gcd = mc.gameSettings.mouseSensitivity * 0.8F + 0.2F;
        gcd = gcd * gcd * gcd;

        pitch -= pitch % gcd;
        yaw -= yaw % gcd;

        return new float[] {yaw,pitch};
    }

    public float[] getRots(BlockPos b, Vec3 offset) {
        //if(offset.xCoord != 0 && offset.yCoord != 0 && offset.zCoord != 0)
        //Elysium.getInstance().addChatMessage(offset + " | " + b);
        return new float[] {
                mc.thePlayer.rotationYaw + RotationUtil.getYawChange(mc.thePlayer.rotationYaw, b.getX() + offset.xCoord,b.getZ()+offset.zCoord),
                mc.thePlayer.rotationPitch + RotationUtil.getPitchChange(mc.thePlayer.rotationPitch, b.getX() + offset.xCoord,b.getY()+offset.yCoord,b.getZ()+offset.zCoord)
        };
    }
}
