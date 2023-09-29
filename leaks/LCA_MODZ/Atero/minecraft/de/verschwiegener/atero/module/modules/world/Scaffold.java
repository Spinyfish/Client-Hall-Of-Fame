package de.verschwiegener.atero.module.modules.world;

import com.darkmagician6.eventapi.EventTarget;
import com.darkmagician6.eventapi.events.callables.EventPostMotionUpdate;
import com.darkmagician6.eventapi.events.callables.EventPreMotionUpdate;
import com.darkmagician6.eventapi.events.callables.EventSycItem;
import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.module.Category;
import de.verschwiegener.atero.module.Module;
import de.verschwiegener.atero.settings.Setting;
import de.verschwiegener.atero.settings.SettingsItem;
import de.verschwiegener.atero.util.RotationRecode2;
import de.verschwiegener.atero.util.TimeUtils;
import de.verschwiegener.atero.util.Util;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Scaffold extends Module {

    public static Scaffold instance;
    public static Setting setting;
    public static float[] lastRot;
    public static float lastYaw, lastPitch;
    public static Scaffold.BlockData data;
    public boolean allowdown;
    public float yaw;
    private float[] lastrotation;
    private int[] forbiddenBlocks = {5};
    private TimeUtils timer = new TimeUtils();
    private int slot;
    private double posY;

    public Scaffold() {
        super("Scaffold", "Scaffold", Keyboard.KEY_NONE, Category.World);
        instance = this;
    }

    public static double getSpeed() {
        return Math.sqrt(Minecraft.getMinecraft().thePlayer.motionX * Minecraft.getMinecraft().thePlayer.motionX + Minecraft.getMinecraft().thePlayer.motionZ * Minecraft.getMinecraft().thePlayer.motionZ);
    }

    public void setup() {
        final ArrayList<SettingsItem> items = new ArrayList<>();
        items.add(new SettingsItem("Sprint", false, ""));
        items.add(new SettingsItem("Swing", false, ""));
        ArrayList<String> modes = new ArrayList<>();
        ArrayList<String> modess = new ArrayList<>();
        //RotationModes
        modes.add("NCPStatic");
        modes.add("MemePlex");
        modes.add("AAC");
        modes.add("NCP");
        modes.add("Watchdog");
        modes.add("WatchdogFast");
        //TowerModes
        modess.add("Watchdog");
        modess.add("NCP");
        modess.add("TP");
        modess.add("TimerTP");
        modess.add("Legit");
        items.add(new SettingsItem("RotationModes", modes, "NCP", "", ""));
        items.add(new SettingsItem("TowerModes", modess, "Watchdog", "", ""));
        items.add(new SettingsItem("SameY", false, ""));
        Management.instance.settingsmgr.addSetting(new Setting(this, items));
    }

    @Override
    public void onEnable() {
        try {
            String mode = setting.getItemByName("RotationModes").getCurrent();
            switch (mode) {
                case "WatchdogFast":
                    Util.setSpeed(-0.7);
                    break;

            }
        } catch (NullPointerException e) {

        }
        //  Management.instance.modulemgr.getModuleByName("Speed").setEnabled(true);
        super.onEnable();

        setting = Management.instance.settingsmgr.getSettingByName(getName());

        this.posY = mc.thePlayer.posY;

        //  silentSlot = -1;
        //mc.getCurrentServerData().serverIP.equalsIgnoreCase("Cubecraft.net")

    }

    public void onDisable() {
        try {
            mc.gameSettings.keyBindSneak.pressed = false;
            String mode = setting.getItemByName("RotationModes").getCurrent();
            switch (mode) {
                case "WatchdogFast":
                    Util.setSpeed(-0.5);
                    break;

            }
        } catch (NullPointerException e) {

        }
        mc.timer.timerSpeed = 1F;

        super.onDisable();
    }

    @EventTarget
    public void onEventSync(EventSycItem sync) {
        if (getBlockSlot() != -1) {
            sync.slot = this.slot = getBlockSlot();
        }
    }

    @Override
    public void onUpdateClick() {
        super.onUpdateClick();
        if (setting.getItemByName("Sprint").isState()) {
            mc.thePlayer.setSprinting(true);
            if (mc.gameSettings.keyBindJump.pressed) {
                mc.thePlayer.setSprinting(false);
            }
        }
        String modes = setting.getItemByName("TowerModes").getCurrent();

        switch (modes) {
            case "TP":
                if (mc.gameSettings.keyBindJump.pressed) {
                    Minecraft.thePlayer.setPosition(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY + 0.6D, Minecraft.thePlayer.posZ);
                    Minecraft.thePlayer.motionY = 0.4D;
                }
                break;
            case "TimerTP":
                if (mc.gameSettings.keyBindJump.pressed) {
                    Minecraft.thePlayer.setPosition(Minecraft.thePlayer.posX, Minecraft.thePlayer.posY + 0.6D, Minecraft.thePlayer.posZ);
                    Minecraft.thePlayer.motionY = 0.4D;

                }
                break;
        }
        String mode = setting.getItemByName("RotationModes").getCurrent();
        switch (mode) {
            case "WatchdogFast":
                final float timer = (float) MathHelper.getRandomDoubleInRange(new Random(), 2.6, 2.7);
                mc.timer.timerSpeed = timer;
                break;
        }

    }


    @EventTarget
    public void onUpdate() {
        super.onUpdate();
        String modes = setting.getItemByName("TowerModes").getCurrent();
        switch (modes) {
            case "TimerTP":
                if (mc.gameSettings.keyBindJump.pressed) {
                    mc.timer.timerSpeed = 2F;
                }
                break;
        }

        data = find(new Vec3(0, 0, 0));
        if (this.data != null && getBlockSlot() != -1) {
            mc.playerController.updateController();
            Vec3 hitVec = (new Vec3(BlockData.getPos())).addVector(0.5D, 0.5D, 0.5D)
                    .add((new Vec3(BlockData.getFacing().getDirectionVec())).multi(0.5D));
            if (slot != -1) {
                if (mc.playerController.onPlayerRightClick(Minecraft.thePlayer,
                        mc.theWorld, mc.thePlayer.inventory.getStackInSlot(slot),
                        BlockData.getPos(), BlockData.getFacing(), hitVec)) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                    if (setting.getItemByName("Swing").isState()) {
                        mc.thePlayer.swingItem();
                    }
                }
            }

            switch (modes) {
                case "Watchdog":
                    final float aaaaa = (float) MathHelper.getRandomDoubleInRange(new Random(), 3.2, 3.5);
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.timer.timerSpeed = aaaaa;
                        if (timer.hasReached(1350)) {
                            timer.reset();
                        } else if (mc.thePlayer.ticksExisted % 6 == 0)
                            mc.thePlayer.motionY = 0.4196;
                    }
                    timer.reset();

                    break;
                case "NCP":
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.timer.timerSpeed = 1.0F;
                        if (timer.hasReached(1500)) {
                            timer.reset();
                        } else if (mc.thePlayer.ticksExisted % 3 == 0)
                            mc.thePlayer.motionY = 0.4196;
                    }
                    timer.reset();

                    break;
                case "Legit":
                    if (mc.gameSettings.keyBindJump.pressed) {
                        mc.timer.timerSpeed = 1.0F;
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.motionY = 0.42F;
                        }
                    }
                    break;
            }
        }

        Minecraft.getMinecraft().thePlayer.setSprinting(false);
    }

    @EventTarget
    public void onPost(EventPostMotionUpdate post) {

        BlockPos blockPos = new BlockPos(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY - 1, Minecraft.getMinecraft().thePlayer.posZ);
        if (mc.theWorld.getBlockState(blockPos).getBlock() == Blocks.air) {
            //  mc.gameSettings.keyBindSneak.pressed = true;
        } else {
            //  mc.gameSettings.keyBindSneak.pressed = false;
        }
    }

    @EventTarget
    public void onPre(EventPreMotionUpdate pre) {
        float[] rotation = data == null ? lastRot : RotationRecode2.rotationrecode7(this.data);
        float[] rotation2 = data == null ? lastRot : RotationRecode2.rotationrecodeMEME(this.data);
        float[] rotation3 = data == null ? lastRot : RotationRecode2.rotationrecodeWatchdog(this.data);

        //if (!Management.instance.modulemgr.getModuleByName("TEst").isEnabled()) {
        final float pitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 85, 100);

        String mode = setting.getItemByName("RotationModes").getCurrent();
        setExtraTag(mode);

        switch (mode) {
            case "NCP":
                mc.timer.timerSpeed = 1;
                //  if(!mc.thePlayer.onGround) {
                pre.setYaw(rotation[0]);
                // }
                lastYaw = rotation[0];
                pre.setPitch(rotation[1]);
                lastPitch = (rotation[1]);
                break;
            case "Watchdog":
                mc.timer.timerSpeed = 1;
                //  if(!mc.thePlayer.onGround) {
                pre.setYaw(rotation3[0]);
                // }
                lastYaw = rotation3[0];
                pre.setPitch(rotation3[1]);
                lastPitch = (rotation3[1]);
                break;
            case "MemePlex":
                mc.gameSettings.keyBindSneak.pressed = true;
                if (mc.gameSettings.keyBindJump.pressed) {
                    mc.thePlayer.setSprinting(false);
                }
                pre.setYaw(rotation2[0]);
                lastYaw = rotation2[0];
                pre.setPitch(rotation2[1]);
                lastPitch = (rotation2[1]);

                break;

            case "WatchdogFast":

                pre.setYaw(rotation[0]);
                lastYaw = rotation[0];
                pre.setPitch(rotation[1]);
                lastPitch = (rotation[1]);


                break;


            case "AAC":
                mc.timer.timerSpeed = 1;
                pre.setYaw((Minecraft.thePlayer.rotationYaw + 180F));
                lastYaw = rotation[0];
                pre.setPitch(rotation[1]);
                lastPitch = (rotation[1]);
                break;


            case "NCPStatic":
                mc.timer.timerSpeed = 1;
                pre.setYaw(rotation[0]);
                lastYaw = rotation[0];
                pre.setPitch(85);
                lastPitch = (rotation[1]);
                break;
        }

    }

    private Vec3 getPositionByFace(BlockPos position, EnumFacing facing) {
        Vec3 offset = new Vec3((double) facing.getDirectionVec().getX() / 2.0, (double) facing.getDirectionVec().getY() / 2.0, (double) facing.getDirectionVec().getZ() / 2.0);
        Vec3 point = new Vec3((double) position.getX() + 0.5, (double) position.getY() + 0.5, (double) position.getZ() + 0.5);
        return point.add(offset);
    }

    private boolean rayTrace(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        int steps = 10;
        double x = difference.xCoord / (double) steps;
        double y = difference.yCoord / (double) steps;
        double z = difference.zCoord / (double) steps;
        Vec3 point = origin;
        for (int i = 0; i < steps; ++i) {
            BlockPos blockPosition = new BlockPos(point = point.addVector(x, y, z));
            IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(blockPosition);
            if (blockState.getBlock() instanceof BlockLiquid || blockState.getBlock() instanceof BlockAir) continue;
            AxisAlignedBB boundingBox = blockState.getBlock().getCollisionBoundingBox(Minecraft.getMinecraft().theWorld, blockPosition, blockState);
            if (boundingBox == null) {
                boundingBox = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }
            if (!boundingBox.offset(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()).isVecInside(point))
                continue;
            return true;
        }
        return false;
    }

    private Scaffold.BlockData find(Vec3 offset3) {
        if (setting.getItemByName("SameY").isState() && mc.thePlayer.onGround && !Management.instance.modulemgr.getModuleByName("Speed").isEnabled()) {

        }
        double x = Minecraft.getMinecraft().thePlayer.posX;
        double y = setting.getItemByName("SameY").isState() ? posY : mc.thePlayer.posY;

        double z = Minecraft.getMinecraft().thePlayer.posZ;

        EnumFacing[] invert = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        BlockPos position = new BlockPos(new Vec3(x, y, z).add(offset3)).offset(EnumFacing.DOWN);
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset = position.offset(facing);
            if (Minecraft.getMinecraft().theWorld.getBlockState(offset).getBlock() instanceof BlockAir || rayTrace(Minecraft.getMinecraft().thePlayer.getLook(0.0f), getPositionByFace(offset, invert[facing.ordinal()])))
                continue;
            return new Scaffold.BlockData(invert[facing.ordinal()], offset);
        }
        BlockPos[] offsets = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2), new BlockPos(2, 0, 0), new BlockPos(-2, 0, 0)};
        for (BlockPos offset : offsets) {
            BlockPos offsetPos = position.add(offset.getX(), 0, offset.getZ());
            if (!(Minecraft.getMinecraft().theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir)) continue;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos offset2 = offsetPos.offset(facing);
                if (Minecraft.getMinecraft().theWorld.getBlockState(offset2).getBlock() instanceof BlockAir || rayTrace(Minecraft.getMinecraft().thePlayer.getLook(0.01f), getPositionByFace(offset, invert[facing.ordinal()])))
                    continue;
                return new Scaffold.BlockData(invert[facing.ordinal()], offset2);
            }
        }
        return null;
    }

    public int getBlockSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack s = Minecraft.thePlayer.inventory.getStackInSlot(i);
            if (s != null && s.getItem() instanceof ItemBlock && !Arrays.asList(forbiddenBlocks).contains(s.getItem().getBlockId()))
                return i;
        }
        return -1;
    }

    public static class BlockData {
        public static EnumFacing facing;

        public static BlockPos pos;

        public BlockData(EnumFacing facing, BlockPos pos) {
            BlockData.facing = facing;
            BlockData.pos = pos;
        }

        public static EnumFacing getFacing() {
            return facing;
        }

        public static BlockPos getPos() {
            return pos;
        }
    }
}
