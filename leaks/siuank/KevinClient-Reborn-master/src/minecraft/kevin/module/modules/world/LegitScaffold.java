package kevin.module.modules.world;


import kevin.event.*;
import kevin.module.*;
import kevin.utils.*;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class LegitScaffold extends Module {
    private final MSTimer startTimeHelper = new MSTimer();
    private final MSTimer startTimeHelper2 = new MSTimer();
    private final MSTimer adTimeHelper = new MSTimer();
    public final FloatValue yawSpeed = new FloatValue("YawSpeed", 40.0f, 0.0f, 180.0f);
    public final FloatValue pitchSpeed = new FloatValue("PitchSpeed", 40.0f, 0.0f, 180.0f);
    public final FloatValue yawOffset = new FloatValue("YawOffSet", -180f, -200f, 200f);
    public final BooleanValue moveFix = new BooleanValue("MoveFix", true);
    public final BooleanValue esp = new BooleanValue("ESP", true);
    public final BooleanValue adStrafe = new BooleanValue("AdStrafe", true);
    public final BooleanValue swing = new BooleanValue("Swing", false);
    public final BooleanValue extraClick = new BooleanValue("ExtraClickTime", true);
    public final BooleanValue sameY = new BooleanValue("SameY", false);
    public final BooleanValue preMotionClick = new BooleanValue("PreMotionClick", false);
    public final ListValue silentMode = new ListValue("SilentMode", new String[]{"Switch", "Spoof", "None"}, "Spoof");
    ArrayList<double[]> hitpoints = new ArrayList<>();
    private Rotation rots = new Rotation(0, 0);
    private EnumFacing enumFacing;
    private MovingObjectPosition objectPosition;
    private int slotID;
    private ItemStack block;
    private int lastSlotID;
    private BlockPos blockPos;
    private boolean start = true;
    private double[] xyz = new double[3];
    private final HashMap<Float, MovingObjectPosition> hashMap = new HashMap<>();

    public LegitScaffold() {
        super("LegitScaffold", "Allow you fast bridging as a legit player", Keyboard.KEY_NONE, ModuleCategory.WORLD);
    }

    public void onEnable() {
        if(mc.thePlayer != null && mc.theWorld != null) {
            rots.setPitch(mc.thePlayer.rotationPitch);
            rots.setYaw(mc.thePlayer.rotationYaw);
            this.objectPosition = null;
            this.blockPos = null;
            this.slotID = mc.thePlayer.inventory.currentItem;
            this.lastSlotID = mc.thePlayer.inventory.currentItem;
            this.start = true;
            this.startTimeHelper.reset();
        }
    }

    public void onDisable() {
        if(mc.thePlayer.inventory.currentItem != this.slotID) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
        RotationUtils.setTargetRotation(new Rotation(mc.thePlayer.rotationYaw + rots.getYaw() / 2, mc.thePlayer.rotationPitch + rots.getPitch() / 2));
        mc.thePlayer.setSprinting(false);

        this.slotID = mc.thePlayer.inventory.currentItem;
    }

    @EventTarget
    public void onUpdate(UpdateEvent ignored) {
        if (this.blockPos != null) this.setRotation();
        if(mc.thePlayer != null && mc.theWorld != null) {
            mc.thePlayer.setSprinting(false);

            if (extraClick.get()) click();
        }
    }

    @EventTarget
    public void onTick(TickEvent event) {
        this.blockPos = this.getAimBlockPos();
        this.start = mc.thePlayer.motionX == 0.0D && mc.thePlayer.motionZ == 0.0D && mc.thePlayer.onGround || !this.startTimeHelper.hasTimePassed(200L);
        if(this.start) {
            this.startTimeHelper2.reset();
        }

        if(this.blockPos != null) {
            this.rots = getNearestRotation();

            this.setRotation();
        }
        if (objectPosition != null) {
            mc.objectMouseOver = objectPosition;
        }
    }

    @EventTarget
    public void onClick(ClickUpdateEvent event) {
//        Vec3 vec3 = mc.thePlayer.getPositionEyes(1f);
//        Vec3 vec31 = OtherExtensionsKt.multiply(rots.toDirection(), 4.5f).add(vec3);
//        mc.objectMouseOver = mc.theWorld.rayTraceBlocks(vec3, vec31, false, false, true);
        event.cancelEvent();
        this.setRotation();
        click();
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        if (!preMotionClick.get()) return;
        if (event.getEventState() == EventState.PRE) {
            Vec3 vec3;
//          vec3 = mc.thePlayer.getPositionEyes(1f);
            // NOPE, we updated position this tick but not in server side.
            // so we need to...
            vec3 = new Vec3(mc.thePlayer.getLastReportedPosX(), mc.thePlayer.getLastReportedPosY() + mc.thePlayer.getEyeHeight(), mc.thePlayer.getLastReportedPosZ());
            Vec3 vec31 = OtherExtensionsKt.multiply(rots.toDirection(), 4.5f).add(vec3);
            mc.objectMouseOver = mc.theWorld.rayTraceBlocks(vec3, vec31, false, false, true);
            click();
        }
    }

    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof C09PacketHeldItemChange) {
            int id = ((C09PacketHeldItemChange) packet).getSlotId();
            if (id == lastSlotID) event.cancelEvent();
            lastSlotID = id;
        } else if (packet instanceof S09PacketHeldItemChange) {
            lastSlotID = ((S09PacketHeldItemChange) packet).getHeldItemHotbarIndex();
        }
    }

    private Rotation getNearestRotation() {
        this.objectPosition = null;
        Rotation rot = this.rots.cloneSelf();
        BlockPos b = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5D, mc.thePlayer.posZ);
        this.hashMap.clear();
        if(this.start) {
            rot.setPitch(80.34F);
            rot.setYaw(mc.thePlayer.rotationYaw + yawOffset.get());
            rot = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rot, yawSpeed.get() + RandomUtils.INSTANCE.nextFloat(0, 2), pitchSpeed.get() - RandomUtils.INSTANCE.nextFloat(0, 2));
        } else {
            rot.setYaw(mc.thePlayer.rotationYaw + yawOffset.get());
            rot = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rot, yawSpeed.get() + RandomUtils.INSTANCE.nextFloat(0, 2), pitchSpeed.get() + RandomUtils.INSTANCE.nextFloat(0, 2));
            double x = mc.thePlayer.posX;
            double z = mc.thePlayer.posZ;
            double add1 = 1.288D;
            double add2 = 0.288D;
            if(!this.buildForward()) {
                x += mc.thePlayer.posX - this.xyz[0];
                z += mc.thePlayer.posZ - this.xyz[2];
            }

            this.xyz = new double[]{mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ};
            double maX = (double)this.blockPos.getX() + add1;
            double miX = (double)this.blockPos.getX() - add2;
            double maZ = (double)this.blockPos.getZ() + add1;
            double miZ = (double)this.blockPos.getZ() - add2;
            if(x <= maX && x >= miX && z <= maZ && z >= miZ) {
                rot.setPitch(rots.getPitch());
            } else {
                ArrayList<MovingObjectPosition> movingObjectPositions = new ArrayList<>();
                ArrayList<Float> pitches = new ArrayList<>();
                Vec3 vec3 = mc.thePlayer.getPositionEyes(1f);
                for(float mm = Math.max(rots.getPitch() - 20.0F, -90.0F); mm < Math.min(rots.getPitch() + 20.0F, 90.0F); mm += 0.02F) {
                    rot.setPitch(mm);
                    rot.fixedSensitivity();
                    Vec3 vec31 = OtherExtensionsKt.multiply(rot.toDirection(), 4.5f).add(vec3);
                    final MovingObjectPosition m4 = mc.theWorld.rayTraceBlocks(vec3, vec31, false, false, true);
                    if(m4.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.isOkBlock(m4.getBlockPos()) && /*!movingObjectPositions.contains(m4) && */m4.getBlockPos().equals(this.blockPos) && m4.sideHit != EnumFacing.DOWN && (m4.sideHit != EnumFacing.UP || (!sameY.get() && mc.gameSettings.keyBindJump.isKeyDown())) && m4.getBlockPos().getY() <= b.getY()) {
                        movingObjectPositions.add(m4);
                        float rotPitch = rot.getPitch();
                        this.hashMap.put(rotPitch, m4);
                        pitches.add(rotPitch);
                    }
                }

                movingObjectPositions.sort(Comparator.comparingDouble((m) -> mc.thePlayer.getDistanceSq(m.getBlockPos().add(0.5D, 0.5D, 0.5D))));
                MovingObjectPosition mm1 = null;
                if(!movingObjectPositions.isEmpty()) {
                    mm1 = movingObjectPositions.get(0);
                }

                if(mm1 != null) {
                    pitches.sort(Comparator.comparingDouble(this::distanceToLastPitch));
                    if(!pitches.isEmpty()) {
                        float rotPitch = pitches.get(0);
                        rot.setPitch(rotPitch);
                        this.objectPosition = this.hashMap.get(rotPitch);
                        this.blockPos = this.objectPosition.getBlockPos();
                    }

                    return rot;
                }
            }
        }

        return rot;
    }

    private boolean canPlace(Rotation rotation) {
        BlockPos b = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5D, mc.thePlayer.posZ);
        Vec3 vec31 = mc.thePlayer.getPositionEyes(1f);
        Vec3 vec32 = OtherExtensionsKt.multiply(rotation.toDirection(), 4.5).add(vec31);
        MovingObjectPosition m4 = mc.theWorld.rayTraceBlocks(vec31, vec32, false, false, true);
        if(m4.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.isOkBlock(m4.getBlockPos()) && m4.getBlockPos().equals(this.blockPos) && m4.sideHit != EnumFacing.DOWN && m4.sideHit != EnumFacing.UP && m4.getBlockPos().getY() <= b.getY()) {
            this.hashMap.put(rotation.getPitch(), m4);
            return true;
        } else {
            return false;
        }
    }

    private double distanceToLastRots(Rotation predictRots) {
        float diff1 = Math.abs(predictRots.getYaw() - this.rots.getYaw());
        float diff2 = Math.abs(predictRots.getPitch() - this.rots.getPitch());
        return diff1 * diff1 + diff2 * diff2;
    }

    private double distanceToLastPitch(float pitch) {
        return Math.abs(pitch - this.rots.getPitch());
    }

    private double[] getAdvancedDiagonalExpandXZ(BlockPos blockPos) {
        double[] xz = new double[2];
        Vector2d difference = new Vector2d((double)blockPos.getX() - mc.thePlayer.posX, (double)blockPos.getZ() - mc.thePlayer.posZ);
        if(difference.x > -1.0D && difference.x < 0.0D && difference.y < -1.0D) {
            this.enumFacing = EnumFacing.SOUTH;
            xz[0] = difference.x * -1.0D;
            xz[1] = 1.0D;
        }

        if(difference.y < 0.0D && difference.y > -1.0D && difference.x < -1.0D) {
            this.enumFacing = EnumFacing.EAST;
            xz[0] = 1.0D;
            xz[1] = difference.y * -1.0D;
        }

        if(difference.x > -1.0D && difference.x < 0.0D && difference.y > 0.0D) {
            this.enumFacing = EnumFacing.NORTH;
            xz[0] = difference.x * -1.0D;
            xz[1] = 0.0D;
        }

        if(difference.y < 0.0D && difference.y > -1.0D && difference.x > 0.0D) {
            xz[0] = 0.0D;
            xz[1] = difference.y * -1.0D;
            this.enumFacing = EnumFacing.WEST;
        }

        if(difference.x >= 0.0D && difference.y < -1.0D) {
            xz[1] = 1.0D;
        }

        if(difference.y >= 0.0D & difference.x < -1.0D) {
            xz[0] = 1.0D;
        }

        if(difference.x >= 0.0D && difference.y > 0.0D) {
            ;
        }

        if(difference.y <= -1.0D && difference.x < -1.0D) {
            xz[0] = 1.0D;
            xz[1] = 1.0D;
        }

        return xz;
    }

    private EnumFacing getPlaceSide() {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5D, mc.thePlayer.posZ);
        if(playerPos.equals(this.blockPos)) {
            System.out.println("Error");
        }

        return playerPos.add(0, 1, 0).equals(this.blockPos)?EnumFacing.UP:(playerPos.add(0, -1, 0).equals(this.blockPos)?EnumFacing.DOWN:(playerPos.add(1, 0, 0).equals(this.blockPos)?EnumFacing.WEST:(playerPos.add(-1, 0, 0).equals(this.blockPos)?EnumFacing.EAST:(playerPos.add(0, 0, 1).equals(this.blockPos)?EnumFacing.NORTH:(playerPos.add(0, 0, -1).equals(this.blockPos)?EnumFacing.SOUTH:(playerPos.add(1, 0, 1).equals(this.blockPos)?EnumFacing.WEST:(playerPos.add(-1, 0, 1).equals(this.blockPos)?EnumFacing.EAST:(playerPos.add(-1, 0, 1).equals(this.blockPos)?EnumFacing.NORTH:(playerPos.add(-1, 0, -1).equals(this.blockPos)?EnumFacing.SOUTH:null)))))))));
    }

    public void click() {
        if(this.block == null) {
            this.block = mc.thePlayer.inventory.getCurrentItem();
        }

        if(this.blockPos != null && mc.currentScreen == null) {
            ItemStack lastItem = mc.thePlayer.inventory.getCurrentItem();
            ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
            if(!this.silentMode.get().equals("None")) {
                this.slotID = InventoryUtils.findAutoBlockBlock() - 36;
                if (slotID == -1) return;
                itemstack = mc.thePlayer.inventoryContainer.getSlot(this.slotID + 36).getStack();
                block = itemstack;
                if(this.silentMode.get().equals("Spoof") && this.lastSlotID != this.slotID) {
                    mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slotID));
                }
            } else {
                this.slotID = mc.thePlayer.inventory.currentItem;
                this.lastSlotID = mc.thePlayer.inventory.currentItem;
            }

            MovingObjectPosition var26 = objectPosition;
            if(var26 != null) {
                BlockPos var27 = var26.getBlockPos();
                if (var26.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && mc.theWorld.getBlockState(var27).getBlock().getMaterial() != Material.air) {
                    if (itemstack != null && !(itemstack.getItem() instanceof ItemBlock)) {
                        return;
                    }

                    this.hitpoints.add(new double[]{var26.hitVec.xCoord, var26.hitVec.yCoord, var26.hitVec.zCoord});
                    if (mc.thePlayer.posY < (double) var27.getY() + 1.5D) {
                        if (var26.sideHit != EnumFacing.UP && var26.sideHit != EnumFacing.DOWN) {
                            if (this.silentMode.get().equals("Switch")) {
                                mc.thePlayer.inventory.setCurrentItem(this.block.getItem(), 0, false, false);
                            }

                            if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, var27, var26.sideHit, var26.hitVec)) {
                                if (swing.get()) mc.thePlayer.swingItem();
                                else mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                            }

                            if (itemstack != null && itemstack.stackSize == 0) {
                                mc.thePlayer.inventory.mainInventory[this.slotID] = null;
                            }

                            mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
                        }
                    } else if (var26.sideHit != EnumFacing.DOWN && var26.getBlockPos().equals(this.blockPos) && mc.gameSettings.keyBindJump.isKeyDown()) {
                        if (this.silentMode.get().equals("Switch")) {
                            mc.thePlayer.inventory.setCurrentItem(this.block.getItem(), 0, false, false);
                        }

                        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemstack, var27, var26.sideHit, var26.hitVec)) {
                            if (swing.get()) mc.thePlayer.swingItem();
                            else mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                        }

                        if (itemstack != null && itemstack.stackSize == 0) {
                            mc.thePlayer.inventory.mainInventory[this.slotID] = null;
                        }

                        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
                    }
                }
            }

            if(lastItem != null && this.silentMode.get().equals("Switch")) {
                mc.thePlayer.inventory.setCurrentItem(lastItem.getItem(), 0, false, false);
            }

            this.lastSlotID = this.slotID;
        }
    }

    @EventTarget
    public void onEventStrafe(StrafeEvent event) {
        if(this.moveFix.get() && !event.isCancelled()) {
            event.cancelEvent();
            float strafe, forward, friction;
            strafe = event.getStrafe();
            forward = -event.getForward();
            friction = event.getFriction();
            if(this.adStrafe.get() && strafe == 0) {
                BlockPos b = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.5D, mc.thePlayer.posZ);
                if(mc.theWorld.getBlockState(b).getBlock().getMaterial() == Material.air && mc.currentScreen == null && /*!Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCodeDefault()) && */this.buildForward() && mc.thePlayer.movementInput.moveForward != 0.0F) {
                    if(mc.thePlayer.getHorizontalFacing(this.rots.getYaw()) == EnumFacing.EAST) {
                        if((double)b.getZ() + 0.5D > mc.thePlayer.posZ) {
                            strafe = 0.98F;
                        } else {
                            strafe = -0.98F;
                        }
                    } else if(mc.thePlayer.getHorizontalFacing(this.rots.getYaw()) == EnumFacing.WEST) {
                        if((double)b.getZ() + 0.5D < mc.thePlayer.posZ) {
                            strafe = 0.98F;
                        } else {
                            strafe = -0.98F;
                        }
                    } else if(mc.thePlayer.getHorizontalFacing(this.rots.getYaw()) == EnumFacing.SOUTH) {
                        if((double)b.getX() + 0.5D < mc.thePlayer.posX) {
                            strafe = 0.98F;
                        } else {
                            strafe = -0.98F;
                        }
                    } else if((double)b.getX() + 0.5D > mc.thePlayer.posX) {
                        strafe = 0.98F;
                    } else {
                        strafe = -0.98F;
                    }
                    if (mc.thePlayer.movementInput.sneak) {
                        strafe *= 0.3D;
                    }

                    this.adTimeHelper.reset();
                }
            }
            strafe = -strafe;
            float f = strafe * strafe + forward * forward;

            if (!(f < 1.0E-4F))
            {
                f = MathHelper.sqrt_float(f);

                if (f < 1.0F)
                {
                    f = 1.0F;
                }

                f = friction / f;
                strafe = strafe * f;
                forward = forward * f;
                float f1 = MathHelper.sin(RotationUtils.targetRotation.getYaw() * (float)Math.PI / 180.0F);
                float f2 = MathHelper.cos(RotationUtils.targetRotation.getYaw() * (float)Math.PI / 180.0F);
                mc.thePlayer.motionX += strafe * f2 - forward * f1;
                mc.thePlayer.motionZ += forward * f2 + strafe * f1;
            }
        }
    }

    @EventTarget
    public void onEventJump(JumpEvent event) {
        if (event.isCancelled()) return;
        if(this.moveFix.get()) {
            // no sprint jump
            event.cancelEvent();
            mc.thePlayer.motionY = event.getMotion();
            mc.thePlayer.triggerAchievement(StatList.jumpStat);
        }

    }

    private void setRotation() {
        if(mc.currentScreen == null) {
            RotationUtils.setTargetRotation(rots, 2);
        }
    }

    private boolean buildForward() {
        float realYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        return (double) realYaw > 77.5D && (double) realYaw < 102.5D || (!((double) realYaw <= 167.5D) || !(realYaw >= -167.0F) || ((double) realYaw < -77.5D && (double) realYaw > -102.5D || (double) realYaw > -12.5D && (double) realYaw < 12.5D));
    }

    private BlockPos getAimBlockPos() {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
        if((mc.gameSettings.keyBindJump.isKeyDown() || !mc.thePlayer.onGround) && mc.thePlayer.moveForward == 0.0F && mc.thePlayer.moveStrafing == 0.0F && this.isOkBlock(playerPos.add(0, -1, 0))) {
            return playerPos.add(0, -1, 0);
        } else {
            BlockPos blockPos = null;
            ArrayList<BlockPos> bp = this.getBlockPos();
            ArrayList<BlockPos> blockPositions = new ArrayList<>();
            if(!bp.isEmpty()) {
                for(int i = 0; i < Math.min(bp.size(), 18); ++i) {
                    blockPositions.add(bp.get(i));
                }

                blockPositions.sort(Comparator.comparingDouble(this::getDistanceToBlockPos));
                if(!blockPositions.isEmpty()) {
                    blockPos = blockPositions.get(0);
                }
            }

            return blockPos;
        }
    }

    private ArrayList<BlockPos> getBlockPos() {
        BlockPos playerPos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0D, mc.thePlayer.posZ);
        ArrayList<BlockPos> blockPoses = new ArrayList<>();

        for(int x = playerPos.getX() - 2; x <= playerPos.getX() + 2; ++x) {
            for(int y = playerPos.getY() - 1; y <= playerPos.getY(); ++y) {
                for(int z = playerPos.getZ() - 2; z <= playerPos.getZ() + 2; ++z) {
                    if(this.isOkBlock(new BlockPos(x, y, z))) {
                        blockPoses.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        if(!blockPoses.isEmpty()) {
            blockPoses.sort(Comparator.comparingDouble((blockPos) -> mc.thePlayer.getDistanceSq((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D)));
        }

        return blockPoses;
    }

    private double getDistanceToBlockPos(BlockPos blockPos) {
        double distance = 1337.0D;

        for(float x = (float)blockPos.getX(); x <= (float)(blockPos.getX() + 1); x = (float)((double)x + 0.2D)) {
            for(float y = (float)blockPos.getY(); y <= (float)(blockPos.getY() + 1); y = (float)((double)y + 0.2D)) {
                for(float z = (float)blockPos.getZ(); z <= (float)(blockPos.getZ() + 1); z = (float)((double)z + 0.2D)) {
                    double d0 = mc.thePlayer.getDistanceSq(x, y, z);
                    if(d0 < distance) {
                        distance = d0;
                    }
                }
            }
        }

        return distance;
    }

    private boolean isOkBlock(BlockPos blockPos) {
        Block block = mc.theWorld.getBlockState(blockPos).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace);
    }

    @EventTarget
    public void onEventRender3D(Render3DEvent event) {
        if(this.esp.get()) {
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDisable(3553);
            GlStateManager.disableCull();
            GL11.glDepthMask(false);
            float red = 0.16470589F;
            float green = 0.5686275F;
            float blue = 0.96862745F;
            float lineWidth = 0.0F;
            if(this.blockPos != null) {
                if(mc.thePlayer.getDistance(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()) > 1.0D) {
                    double d0 = 1.0D - mc.thePlayer.getDistance(this.blockPos.getX(), this.blockPos.getY(), this.blockPos.getZ()) / 20.0D;
                    if(d0 < 0.3D) {
                        d0 = 0.3D;
                    }

                    lineWidth = (float)((double)lineWidth * d0);
                }
                GL11.glLineWidth(lineWidth);
                GL11.glColor4f(red, green, blue, 0.39215687F);

                final RenderManager renderManager = mc.getRenderManager();
                RenderUtils.drawFilledBox(new AxisAlignedBB(this.blockPos.getX() - renderManager.getRenderPosX(), this.blockPos.getY() - renderManager.getRenderPosY(), this.blockPos.getZ() - renderManager.getRenderPosZ(), this.blockPos.getX() + 1 - renderManager.getRenderPosX(), this.blockPos.getY() + 1 - renderManager.getRenderPosY(), this.blockPos.getZ() + 1 - renderManager.getRenderPosZ()));
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDepthMask(true);
            GlStateManager.enableCull();
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDisable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2848);
        }

    }

    public int getSlotID() {
        return this.slotID;
    }
}
