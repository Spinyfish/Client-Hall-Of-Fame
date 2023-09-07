package kevin.module.modules.world;

import kevin.event.*;
import kevin.module.*;
import kevin.utils.*;
import kotlin.Pair;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.LinkedList;

public class BlockFly extends Module {
    public BlockFly() {
        super("BlockFly", "Auto place blocks under your feet.", Keyboard.KEY_NONE, ModuleCategory.WORLD);
    }

    private Rotation rot = new Rotation(0, 0), currentRot = new Rotation(0, 0);
    private final FloatValue maxHorizontalRotationSpeed = new FloatValue("MaxHorizontalRotationSpeed", 50, 0.01f, 180f);
    private final FloatValue maxVerticalRotationSpeed = new FloatValue("MaxVerticalRotationSpeed", 50, 0.01f, 180f);
    private final BooleanValue sneak = new BooleanValue("Sneak", true);
    private final BooleanValue rotationSneak = new BooleanValue("RotationSneak", true);
    private final BooleanValue swing = new BooleanValue("VisualSwing", true);
    private final ListValue silentMode = new ListValue("SilentMode", new String[] {"None", "Spoof", "Switch"}, "Spoof");
    private final BooleanValue esp = new BooleanValue("ESP", true);

    private Vec3 lastFeetVec = new Vec3(0.0, 0.0, 0.0);
    private int slotID = 0, lastSlotID = 0;

    private int sneakWaitTick = 0;
    private BlockPos blockPos = null;
//    private boolean tickUpdated = false;


    @Override
    public void onDisable() {
        if(mc.thePlayer.inventory.currentItem != this.slotID) {
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        }
        RotationUtils.setTargetRotation(new Rotation(mc.thePlayer.rotationYaw + currentRot.getYaw() / 2, mc.thePlayer.rotationPitch + currentRot.getPitch() / 2));
        mc.thePlayer.setSprinting(false);

        this.slotID = mc.thePlayer.inventory.currentItem;
        this.lastSlotID = mc.thePlayer.inventory.currentItem;
    }

    @Override
    public void onEnable() {
        rot = new Rotation((float) MovementUtils.getDirection() + 179.3512f, 80.64f);
        this.slotID = mc.thePlayer.inventory.currentItem;
        this.lastSlotID = mc.thePlayer.inventory.currentItem;
        rot.setYaw((float) (MovementUtils.getDirection() + 179.212f));
        blockPos = null;
        currentRot = RotationUtils.bestServerRotation();
    }

    @EventTarget
    public void onClick(ClickUpdateEvent event) {
        if (mc.currentScreen != null) {
            updateRotation();
            return;
        }
        ItemStack itemstack;

        if(!this.silentMode.get().equals("None")) {
            this.slotID = InventoryUtils.findAutoBlockBlock() - 36;
            if (slotID == -1) {
                updateRotation();
                return;
            }
            itemstack = mc.thePlayer.inventoryContainer.getSlot(this.slotID + 36).getStack();
            if(this.silentMode.get().equals("Spoof") && this.lastSlotID != this.slotID) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(slotID));
            }
        } else {
            this.slotID = mc.thePlayer.inventory.currentItem;
            this.lastSlotID = mc.thePlayer.inventory.currentItem;
            itemstack = mc.thePlayer.getHeldItem();
        }
        if (itemstack.getItem() instanceof ItemBlock) {
            boolean willFall = willThisTickOutOfEdge();
            Pair<Rotation, BlockData> pair = searchBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down(), willFall, (ItemBlock) itemstack.getItem(), itemstack);
            if (pair != null) {
                rot = pair.getFirst();
                updateRotation();
                BlockData data = pair.getSecond();
                blockPos = data.blockPos;
                // do raytrace to make sure everything is ok
                Vec3 eyes = mc.thePlayer.getPositionEyes(1f);
                Vec3 target = OtherExtensionsKt.multiply(currentRot.toDirection(), 4.5f).add(eyes);

                MovingObjectPosition m4 = mc.theWorld.rayTraceBlocks(eyes, target, false, false, true);
                if (m4 != null && m4.sideHit == data.facing && m4.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && m4.getBlockPos().equals(data.blockPos)) {
                    click(itemstack, data, m4.hitVec);
                }
            } else {
                rot = new Rotation((float) MovementUtils.getDirection() + 179.98f, 80.53f);
                updateRotation();

                Vec3 eyes = mc.thePlayer.getPositionEyes(1f);
                Vec3 target = OtherExtensionsKt.multiply(currentRot.toDirection(), 4.5f).add(eyes);
                MovingObjectPosition m4 = mc.theWorld.rayTraceBlocks(eyes, target, false, false, true);
                if (m4 != null && m4.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && m4.getBlockPos() != null && m4.sideHit != null && m4.getBlockPos().getY() <= mc.thePlayer.posY) {
                    click(itemstack, new BlockData(m4.getBlockPos(), m4.sideHit), m4.hitVec);
                    blockPos = m4.getBlockPos();
                } else {
                    updateRotation();
                    blockPos = null;
                }
            }
        } else {
            updateRotation();
            blockPos = null;
        }

        this.lastSlotID = this.slotID;
        event.cancelEvent();
        lastFeetVec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
    }

    @EventTarget
    public void onUpdate(UpdateEvent ignored) {
        setRotation();
    }

    @EventTarget
    public void onMotion(MotionEvent event) {
        setRotation();
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

    @EventTarget
    public void onInput(MovementInputUpdateEvent event) {
        if (sneakWaitTick > 0) {
            event.setSneak(true);
            --sneakWaitTick;
        }
    }

    @EventTarget
    public void onEventRender3D(Render3DEvent event) {
        if(this.esp.get() && blockPos != null) {
            GlStateManager.pushMatrix();
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
            GlStateManager.popMatrix();
        }

    }

    private void click(ItemStack stack, BlockData data, Vec3 hitVec) {
        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, stack, data.blockPos, data.facing, hitVec)) {
            if (swing.get()) mc.thePlayer.swingItem();
            else mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }

        if (stack != null && stack.stackSize == 0) {
            mc.thePlayer.inventory.mainInventory[this.slotID] = null;
        }
        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);
    }

    private void updateRotation() {
        currentRot = RotationUtils.limitAngleChange(currentRot != null ? currentRot : RotationUtils.bestServerRotation(), rot, maxHorizontalRotationSpeed.get(), maxVerticalRotationSpeed.get());
        currentRot.fixedSensitivity();
        if ((RotationUtils.getRotationDifference(currentRot, rot) > 0.3 || Math.abs(currentRot.getYaw() - rot.getYaw()) > 0.01) && (mc.thePlayer.onGround && shouldSneak(rotationSneak))) {
            sneakWaitTick = 3;
        }
        setRotation();
    }

    private void setRotation() {
        RotationUtils.setTargetRotation(currentRot, 5);
    }

    private boolean shouldSneak(BooleanValue value) {
        return sneak.get() && value.get();
    }

    private boolean willThisTickOutOfEdge() {
        EntityPlayerSP player = mc.thePlayer;
        WorldClient world = mc.theWorld;
        if (player == null || world == null) return false;
        if (!player.onGround) return player.motionY > 0;

        double motionX = player.motionX;
        double motionZ = player.motionZ;

        AxisAlignedBB bb = player.getCollisionBoundingBox();
        AxisAlignedBB offset = bb.offset(motionX * 0.91f * 0.6f, -0.08, motionZ * 0.91f * 0.6f);
        AxisAlignedBB abb = bb.offset(motionX * 0.91f * 0.6f, 0.0, motionZ * 0.91f * 0.6f);

        double realY = -0.08;

        for (AxisAlignedBB box : world.getCollidingBoundingBoxes(player, offset)) {
            realY = box.calculateYOffset(abb, realY);
        }

        if (realY <= -0.015625) return true;
        realY = -0.08;

        offset = bb.offset(motionX, -0.08, motionZ);
        abb = bb.offset(motionX, 0.0, motionZ);

        for (AxisAlignedBB box : world.getCollidingBoundingBoxes(player, offset)) {
            realY = box.calculateYOffset(abb, realY);
        }

        offset = bb.offset(motionX * 1.09, -0.08, motionZ * 1.09);
        abb = bb.offset(motionX * 1.09, 0.0, motionZ * 1.09);

        for (AxisAlignedBB box : world.getCollidingBoundingBoxes(player, offset)) {
            realY = box.calculateYOffset(abb, realY);
        }

        return realY <= -0.015625;
    }

    public Pair<Rotation, BlockData> searchBlock(BlockPos bp, boolean willFall, ItemBlock itemBlock, ItemStack itemStack) {
        LinkedList<BlockData> dataCollection = new LinkedList<>();
        if (isBlockSolid(bp)) return null;
        Vec3 vecEyes = mc.thePlayer.getPositionEyes(1f);
        Vec3 lookVec = OtherExtensionsKt.multiply(currentRot.toDirection(), 4.5f).add(vecEyes);
        MovingObjectPosition movingObjectPosition1 = mc.theWorld.rayTraceBlocks(vecEyes, lookVec, false, false, true);
        if (movingObjectPosition1 != null) {
            if (movingObjectPosition1.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (movingObjectPosition1.getBlockPos() != null) {
                    if (movingObjectPosition1.getBlockPos().getY() <= mc.thePlayer.posY) {
                        // the player don't need to update rotation currently
                        if (itemBlock.canPlaceBlockOnSide(mc.theWorld, movingObjectPosition1.getBlockPos(), movingObjectPosition1.sideHit, mc.thePlayer, itemStack) || !willFall) {
                            return new Pair<>(currentRot, new BlockData(movingObjectPosition1.getBlockPos(), movingObjectPosition1.sideHit));
                        } else {
                            // as a situation
                            dataCollection.add(new BlockData(movingObjectPosition1.getBlockPos(), movingObjectPosition1.sideHit));
                        }
                    }
                }
            }
        }

        for (BlockPos posLayer : layerOffset) {
            BlockPos layer = bp.add(posLayer);
            for (BlockPos offset : searchOffset) {
                BlockPos pos = layer.add(offset);
                for (BlockData data : searchBlockDataOffset) {
                    BlockPos added = pos.add(data.blockPos);
                    if (isBlockSolid(added)) {
                        if (itemBlock.canPlaceBlockOnSide(mc.theWorld, added, data.facing, mc.thePlayer, itemStack))
                            dataCollection.add(new BlockData(added, data.facing));
                    }
                }
            }
            if (!dataCollection.isEmpty() || (mc.thePlayer.onGround && !willFall)) break;
        }

        LinkedList<Pair<Rotation, BlockData>> rotationData = new LinkedList<>();

        // let's validate what data can use directly
        for (BlockData data : dataCollection) {
            Rotation rotation = getRotations(data.blockPos, data.facing);
            Vec3 look = OtherExtensionsKt.multiply(rotation.toDirection(), 4.5f).add(vecEyes);
            MovingObjectPosition mop = mc.theWorld.rayTraceBlocks(vecEyes, look, false, false, true);
            // this rotation will not ray any target
            if (mop == null) continue;
            // this rotation will not ray on block
            if (mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) continue;
            // this rotation's ray is not looking at the block we want
            if (mop.getBlockPos() == null) continue;
            if (!mop.getBlockPos().equals(data.blockPos)) continue;
            // to rotation's ray is looking at the block, but in wrong side
            // and please no downward
            if (mop.sideHit != data.facing || mop.sideHit == EnumFacing.DOWN) continue;
            // we don't need to up temporary
            if (mop.sideHit == EnumFacing.UP && (mc.thePlayer.onGround || !mc.gameSettings.keyBindJump.isKeyDown() || mop.getBlockPos().getY() + 2 >= mc.thePlayer.posY)) continue;
            // make sure again it is not invalid
            if (mc.theWorld.getBlockState(mop.getBlockPos()).getBlock().getMaterial() == Material.air) continue;
            // rotation up, is this scaffold?
            if (rotation.getPitch() <= 0) continue;
            // add to queue to wait for next calculation
            Pair<Rotation, BlockData> pair = new Pair<>(rotation, data);
            data.mop = mop;
            rotationData.add(pair);
        }

        // no block was found, discontinued
        if (rotationData.isEmpty()) return null;

        // compare the difference to current rotation, and choose the nearest one
        rotationData.sort(Comparator.comparingDouble(pair -> getDistanceToBlockPos(pair.getSecond().blockPos) * 3 + RotationUtils.getRotationDifference(pair.getFirst(), currentRot)));

        return rotationData.getFirst();
    }

    private static double getDistanceToBlockPos(BlockPos blockPos) {
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

    private static boolean isBlockSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return ((block.getMaterial().isSolid() || !block.isTranslucent() || block.isBlockNormalCube() || block instanceof BlockLadder || block instanceof BlockCarpet
                || block instanceof BlockSnow || block instanceof BlockSkull)
                && !(block.getMaterial().isLiquid() || block instanceof BlockContainer));
    }
    private static Rotation getRotations(BlockPos block, EnumFacing face) {
        double x = block.getX() + 0.5 - mc.thePlayer.posX + face.getFrontOffsetX() / 2.0;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ + face.getFrontOffsetZ() / 2.0;
        double y = block.getY() + 0.5;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360f;
        }
        return new Rotation(yaw, pitch);
    }

    private static final BlockData[] searchBlockDataOffset = {
            new BlockData(new BlockPos(0, -1, 0), EnumFacing.UP),
            new BlockData(new BlockPos(-1, 0, 0), EnumFacing.EAST),
            new BlockData(new BlockPos(1, 0, 0), EnumFacing.WEST),
            new BlockData(new BlockPos(0, 0, 1), EnumFacing.NORTH),
            new BlockData(new BlockPos(0, 0, -1), EnumFacing.SOUTH)
    };

    private static final BlockPos[] searchOffset = {
            new BlockPos(0, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
    };

    private static final BlockPos[] layerOffset = {
            new BlockPos(0, 0, 0),
            new BlockPos(0, -1, 0)
    };

    public static class BlockData {
        final BlockPos blockPos;
        final EnumFacing facing;
        MovingObjectPosition mop = null;

        public BlockData(BlockPos blockPos, EnumFacing facing) {
            this.blockPos = blockPos;
            this.facing = facing;
        }
    }
}
