package felix.module.impl.world;

import felix.events.player.EventCollide;
import felix.util.other.MathUtils;
import felix.util.other.PlayerUtil;
import felix.util.player.MovementUtils;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.VanillaFontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.world.World;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.realmsclient.gui.ChatFormatting;

import felix.Client;
import felix.api.annotations.Handler;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventMotionUpdate;
import felix.events.render.EventRender2D;
import felix.events.render.EventRender3D;
import felix.gui.notification.Notifications;
import felix.module.Module;
import felix.util.other.TimeHelper;
import felix.util.visual.Opacity;
import felix.util.visual.RenderUtil;
import felix.value.impl.BooleanValue;
import felix.value.impl.ColorValue;
import felix.value.impl.EnumValue;
import felix.value.impl.NumberValue;

public class Scaffold extends Module {
    public static List<Block> invalid;
    private static final BlockPos[] BLOCK_POSITIONS = new BlockPos[]{
            new BlockPos(-1, 0, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 0, 1)};

    private static final EnumFacing[] FACINGS = new EnumFacing[]{
            EnumFacing.EAST,
            EnumFacing.WEST,
            EnumFacing.SOUTH,
            EnumFacing.NORTH};


    private EnumValue<Mode> rotationsMode = new EnumValue<>("Rotations Mode", Mode.Normal);

    private BooleanValue tower = new BooleanValue("Tower", true);
    private BooleanValue switcher = new BooleanValue("Silent", true);
    private BooleanValue blockAmount = new BooleanValue("Counter", false);
    private BooleanValue rotationYaw = new BooleanValue("Advanced Rotations", true);
    private BooleanValue towerMove = new BooleanValue("Tower Move", true);
    private BooleanValue timerBoost = new BooleanValue("Tower Boost", false);
    private BooleanValue safety = new BooleanValue("Safety", true);
    private BooleanValue esp = new BooleanValue("ESP", true);
    private BooleanValue sprint = new BooleanValue("Sprint", true);
    private BooleanValue picker = new BooleanValue("Picker", true);
    private BooleanValue noSwing = new BooleanValue("No Swing", true);
    private BooleanValue keepRotations = new BooleanValue("Keep Rotations", true);
    private BooleanValue autoSwitch = new BooleanValue("Auto Switch", false);

    private NumberValue<Double> expand = new NumberValue<>("Expand Distance", 0.3, 0.1, 10.0, 0.1);

    private ColorValue espColor = new ColorValue("ESP Color", new Color(0, 130, 255).getRGB());

    private final MouseFilter pitchMouseFilter = new MouseFilter();
    private final MouseFilter yawMouseFilter = new MouseFilter();

    private BlockData lastBlockData;

    private boolean placing;
    
    private int lastItem = 0;

    public Scaffold() {
        super("Scaffold", 0, ModuleCategory.WORLD);
        addValues(rotationsMode, expand, espColor, tower, switcher, towerMove, autoSwitch, blockAmount, rotationYaw, keepRotations, picker, timerBoost, safety, sprint, noSwing, esp);
        invalid = Arrays.asList(Blocks.anvil, Blocks.wooden_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab,
                Blocks.stone_slab2, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.snow_layer,
                Blocks.heavy_weighted_pressure_plate, Blocks.sapling, Blocks.air, Blocks.water, Blocks.fire, Blocks.beacon, Blocks.yellow_flower, Blocks.cactus,
                Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.chest, Blocks.anvil, Blocks.sand, Blocks.web,
                Blocks.enchanting_table, Blocks.chest, Blocks.ender_chest, Blocks.gravel, Blocks.crafting_table, Blocks.tallgrass, Blocks.dispenser, Blocks.slime_block);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lastItem = mc.thePlayer.inventory.currentItem;
        lastBlockData = null;
        mc.timer.timerSpeed = 1.0f;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.thePlayer == null) return;
        mc.timer.timerSpeed = 1.0f;
        mc.thePlayer.inventory.currentItem = lastItem;
    }

    private enum Mode {
        Normal, Smooth, AAC;
    }

    public boolean hasSafetyEnabled() {
        return safety.isEnabled();
    }

    @Handler
    public void a(EventCollide event){

    }

    @Handler
    public void onRender2D(final EventRender2D event) {
        if (blockAmount.isEnabled()) {
            ScaledResolution sr = new ScaledResolution(mc);
            int color = new Color(255, 0, 0).getRGB();
            int bgcolor = new Color(1,1,1).getRGB();
            if (getBlockCount() >= 64 && 128 > getBlockCount()) {
                color = new Color(255, 255, 0).getRGB();
            } else if (getBlockCount() >= 128) {
                color = new Color(0, 255, 0).getRGB();
            }

            GlStateManager.pushMatrix();
            mc.fontRendererObj.drawString(Integer.toString(getBlockCount()), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(getBlockCount())) / 2 + 1, (sr.getScaledHeight() >> 1) - 15, bgcolor);
            mc.fontRendererObj.drawString(Integer.toString(getBlockCount()), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(getBlockCount())) / 2 - 1, (sr.getScaledHeight() >> 1) - 15, bgcolor);
            mc.fontRendererObj.drawString(Integer.toString(getBlockCount()), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(getBlockCount())) / 2, (sr.getScaledHeight() >> 1) - 15 + 1, bgcolor);
            mc.fontRendererObj.drawString(Integer.toString(getBlockCount()), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(getBlockCount())) / 2, (sr.getScaledHeight() >> 1) - 15 - 1, bgcolor);
            mc.fontRendererObj.drawString(Integer.toString(getBlockCount()), (sr.getScaledWidth() >> 1)  - mc.fontRendererObj.getStringWidth(Integer.toString(getBlockCount())) / 2, (sr.getScaledHeight() >> 1) - 15, color);
            GlStateManager.popMatrix();
        }
    }

    @Handler
    public void onRender3D(final EventRender3D event) {
        if (esp.isEnabled() && lastBlockData != null) {
            final RenderManager renderManager = mc.getRenderManager();
            RenderUtil.pre3D();
            mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 1);
            final int color = new Color(espColor.getValue()).getRGB();
            RenderUtil.glColor(color);
            BlockPos place = lastBlockData.position;
            EnumFacing face = lastBlockData.face;
            double x1 = place.getX() - renderManager.getRenderPosX();
            double x2 = place.getX() - renderManager.getRenderPosX() + 1;
            double y1 = place.getY() - renderManager.getRenderPosY();
            double y2 = place.getY() - renderManager.getRenderPosY() + 1;
            double z1 = place.getZ() - renderManager.getRenderPosZ();
            double z2 = place.getZ() - renderManager.getRenderPosZ() + 1;
            y1 += face.getFrontOffsetY();
            if(face.getFrontOffsetX() < 0){
                x2 += face.getFrontOffsetX();
            }else{
                x1 += face.getFrontOffsetX();
            }
            if(face.getFrontOffsetZ() < 0){
                z2 += face.getFrontOffsetZ();
            }else{
                z1 += face.getFrontOffsetZ();
            }

            RenderHelper.drawBox(new AxisAlignedBB(x1, y1, z1, x2, y2, z2));
            GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
            RenderUtil.post3D();
        }
    }

    @Handler
    public void onMotionUpdate(final EventMotionUpdate event) {
        if (!sprint.isEnabled()) {
            mc.thePlayer.setSprinting(false);
        }
        
        if (event.isPre()) {
        	final NetHandlerPlayClient netHandler = mc.getNetHandler();
        	netHandler.addToSendQueueNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        }

        double addition = expand.getValue();
        final double x2 = Math.cos(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
        final double z2 = Math.sin(Math.toRadians(mc.thePlayer.rotationYaw + 90.0f));
        final double xOffset = MovementInput.moveForward * addition * x2 + MovementInput.moveStrafe * addition * z2;
        final double zOffset = MovementInput.moveForward * addition * z2 - MovementInput.moveStrafe * addition * x2;

        BlockPos blockBelow = new BlockPos(mc.thePlayer.posX + xOffset, mc.thePlayer.posY - 1, mc.thePlayer.posZ + zOffset);

        BlockData blockEntry = mc.theWorld.getBlockState(blockBelow).getBlock() == Blocks.air ? blockEntry = getBlockData2(blockBelow) : null;

        float speed = 0.4f;
        float yaw = 0;
        float pitch = 0;

        placing = mc.thePlayer.ticksExisted % 3 == 0;
        if (blockEntry == null) {
            if (lastBlockData != null && event.isPre()) {
                float[] rotations = getRotationsNeeded(lastBlockData);
                switch (rotationsMode.getValue()) {
                    case Normal: {
                        yaw = this.yawMouseFilter.smooth((float) (getAACYaw() + ThreadLocalRandom.current().nextDouble(-1, 1)), speed);
                        pitch = !keepRotations.isEnabled() && mc.thePlayer.isMoving() || !keepRotations.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown() ? placing ? mc.thePlayer.rotationPitch : pitchMouseFilter.smooth((float) (rotations[1] + ThreadLocalRandom.current().nextDouble(-1.20f, 3.50f)), speed) :
                                pitchMouseFilter.smooth((float) (rotations[1] + ThreadLocalRandom.current().nextDouble(-1.20f, 3.50f)), speed);
                        break;
                    }
                    case Smooth: {
                        break;
                    }
                    case AAC: {
                        break;
                    }
                }
                event.setPitch(rotations[1]);
                if (rotationYaw.isEnabled()) {
                    event.setYaw(rotations[0]);
                }
            }
        }
        if (blockEntry == null)
            return;

        if (event.isPre()) {
            float[] rotations = getRotationsNeeded(blockEntry);

            switch (rotationsMode.getValue()) {
                case AAC: {
                    break;
                }
                case Normal: {
                    yaw = this.yawMouseFilter.smooth((float) (getAACYaw() + ThreadLocalRandom.current().nextDouble(-1, 1)), speed);
                    pitch = !keepRotations.isEnabled() && mc.thePlayer.isMoving() || !keepRotations.isEnabled() && mc.gameSettings.keyBindJump.isKeyDown() ? placing ? mc.thePlayer.rotationPitch : pitchMouseFilter.smooth((float) (rotations[1] + ThreadLocalRandom.current().nextDouble(-1.20f, 3.50f)), speed) : pitchMouseFilter.smooth((float) (rotations[1] + ThreadLocalRandom.current().nextDouble(-1.20f, 3.50f)), speed);
                    break;
                }
                case Smooth: {
                    break;
                }
            }

        }
        else {
            if (getBlockCount() <= 0) {
                return;
            }
            final int heldItem = mc.thePlayer.inventory.currentItem;
            boolean hasBlock = false;
            if (switcher.isEnabled() || autoSwitch.isEnabled()) {
                for (int i = 0; i < 9; ++i) {
                    ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                    int blockCount = 0;
                    if (itemStack != null && itemStack.stackSize != 0 && itemStack.getItem() instanceof ItemBlock
                            && !invalid.contains(((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {

                       // mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(    C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = i));
                        //mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging( C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        hasBlock = true;
                        break;
                    }
                }
                if (!hasBlock) {
                    for (int i = 0; i < 45; ++i) {
                        ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                        if (mc.thePlayer.inventory.getStackInSlot(i) != null && mc.thePlayer.inventory.getStackInSlot(i).stackSize != 0
                                && mc.thePlayer.inventory.getStackInSlot(i).getItem() instanceof ItemBlock
                                && !invalid.contains(
                                ((ItemBlock) mc.thePlayer.inventory.getStackInSlot(i).getItem()).getBlock())) {
                            mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, i, 8, 2,
                                    mc.thePlayer);
                            break;
                        }
                    }
                }
            }

            if (tower.isEnabled()) {
                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.isPotionActive(Potion.jump)) {
                    if (!mc.thePlayer.isMoving()) {
                        mc.thePlayer.motionY = 0.42f;
                        mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
                    } else {
                        if (mc.thePlayer.onGround && towerMove.isEnabled()) {
                            mc.thePlayer.motionY = 0.42f;
                        } else if (mc.thePlayer.motionY < 0.17D && mc.thePlayer.motionY > 0.16D && towerMove.isEnabled()) {
                            mc.thePlayer.motionY = -0.01f;
                        }
                    }
                }
            }

            if (timerBoost.isEnabled()) {
                if (mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.isMoving()) {
                    mc.timer.timerSpeed = 1.6f;
                }
                else {
                    mc.timer.timerSpeed = 1.0f;
                }
            }

            mc.playerController.onPlayerRightClick3d(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem(),
                    blockEntry.position.add(0, 0, 0),
                    blockEntry.face,
                    new Vec3d(blockEntry.position.getX(), blockEntry.position.getY(), blockEntry.position.getZ()));

            lastBlockData = blockEntry;


            if (noSwing.isEnabled()) {
                mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0APacketAnimation());
            }
            else {
                mc.thePlayer.swingItem();
            }

            if (switcher.isEnabled()) {
               mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = heldItem));
            }
        }
    }

    private float getAACYaw() {
    	boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
    	boolean right = mc.gameSettings.keyBindRight.isKeyDown();
    	boolean forward = mc.gameSettings.keyBindBack.isKeyDown();
		switch (mc.getRenderViewEntity().getHorizontalFacing()) {
		case EAST:
			return forward ? 270 : left ? 360 : right ? 180 : 90;

		case SOUTH:
			return forward ? 360 : left ? 90 : right ? 270 : 180;

		case WEST:
			return forward ? 90 : left ? 180 : right ? 360 : 270;

		default:
			return forward ? 180 : left ? 270 : right ? 90 : 0;
		}
	}

	private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack())
                continue;
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = is.getItem();
            if (!(is.getItem() instanceof ItemBlock) || invalid.contains(((ItemBlock) item).getBlock()))
                continue;
            blockCount += is.stackSize;
        }
        return blockCount;
    }

    public BlockData findDown(Vec3 offset3) {
        EnumFacing[] invert = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH,
                EnumFacing.EAST, EnumFacing.WEST};
        BlockPos position = new BlockPos(mc.thePlayer.getPositionVector().add(offset3)).offset(EnumFacing.DOWN);
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset = position.offset(facing);
            if (mc.theWorld.getBlockState(offset).getBlock() instanceof BlockAir
                    || !rayTrace(mc.thePlayer.getLook(0.0f), getPositionByFace(offset, invert[facing.ordinal()])))
                continue;
            return new BlockData(offset, invert[facing.ordinal()]);
        }
        BlockPos[] offsets = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1),
                new BlockPos(0, 0, 1)};
        for (BlockPos offset : offsets) {
            BlockPos offsetPos = position.add(offset.getX(), 0, offset.getZ());
            if (!(mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir))
                continue;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos offset2 = offsetPos.offset(facing);
                if (mc.theWorld.getBlockState(offset2).getBlock() instanceof BlockAir
                        || !rayTrace(mc.thePlayer.getLook(0.0f), getPositionByFace(offset, invert[facing.ordinal()])))
                    continue;
                return new BlockData(offset2, invert[facing.ordinal()]);
            }
        }
        return null;
    }

    public BlockData find(Vec3 offset3) {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        EnumFacing[] invert = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH,
                EnumFacing.EAST, EnumFacing.WEST};
        BlockPos position = new BlockPos(new Vec3(x, y, z).add(offset3)).offset(EnumFacing.DOWN);
        for (EnumFacing facing : EnumFacing.values()) {
            BlockPos offset = position.offset(facing);
            if (mc.theWorld.getBlockState(offset).getBlock() instanceof BlockAir
                    || !rayTrace(mc.thePlayer.getLook(0.0f), getPositionByFace(offset, invert[facing.ordinal()])))
                continue;
            return new BlockData(offset, invert[facing.ordinal()]);
        }
        BlockPos[] offsets = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1),
                new BlockPos(0, 0, 1), new BlockPos(0, 0, 2), new BlockPos(0, 0, -2), new BlockPos(2, 0, 0),
                new BlockPos(-2, 0, 0), new BlockPos(-3, 0, 0), new BlockPos(3, 0, 0), new BlockPos(2, -1, 0),
                new BlockPos(-2, -1, 0), new BlockPos(0, -1, 2), new BlockPos(0, -1, -2)};
        for (BlockPos offset : offsets) {
            BlockPos offsetPos = position.add(offset.getX(), 0, offset.getZ());
            if (!(mc.theWorld.getBlockState(offsetPos).getBlock() instanceof BlockAir))
                continue;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos offset2 = offsetPos.offset(facing);
                if (mc.theWorld.getBlockState(offset2).getBlock() instanceof BlockAir
                        || !rayTrace(mc.thePlayer.getLook(0.01f), getPositionByFace(offset, invert[facing.ordinal()])))
                    continue;
                return new BlockData(offset2, invert[facing.ordinal()]);
            }
        }
        return null;
    }

    public boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            if (block instanceof BlockSnow && block.getBlockBoundsMaxY() > 0.125) {
                return false;
            }
            return true;
        }

        return false;
    }

    public Vec3 getPositionByFace(BlockPos position, EnumFacing facing) {
        Vec3 offset = new Vec3((double) facing.getDirectionVec().getX() / 2.0,
                (double) facing.getDirectionVec().getY() / 2.0, (double) facing.getDirectionVec().getZ() / 2.0);
        Vec3 point = new Vec3((double) position.getX() + 0.5, (double) position.getY() + 0.75,
                (double) position.getZ() + 0.5);
        return point.add(offset);
    }

    public boolean rayTrace(Vec3 origin, Vec3 position) {
        Vec3 difference = position.subtract(origin);
        int steps = 10;
        double x = difference.xCoord / (double) steps;
        double y = difference.yCoord / (double) steps;
        double z = difference.zCoord / (double) steps;
        Vec3 point = origin;
        for (int i = 0; i < steps; ++i) {
            BlockPos blockPosition = new BlockPos(point = point.addVector(x, y, z));
            IBlockState blockState = mc.theWorld.getBlockState(blockPosition);
            if (blockState.getBlock() instanceof BlockLiquid || blockState.getBlock() instanceof BlockAir)
                continue;
            AxisAlignedBB boundingBox = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, blockPosition,
                    blockState);
            if (boundingBox == null) {
                boundingBox = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
            }
            if (!(boundingBox = boundingBox.offset(blockPosition)).isVecInside(point))
                continue;
            return false;
        }
        return true;
    }

    private int getBlockColor(int count) {
        float f = count;
        float f1 = 64;
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }

    public static float[] getRotationsNeeded(final BlockData data) {
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;

        final Vec3 hitVec = data.hitVec;

        final double xDist = hitVec.xCoord - player.posX;
        final double zDist = hitVec.zCoord - player.posZ;

        final double yDist = hitVec.yCoord - (player.posY + player.getEyeHeight());
        final double fDist = MathHelper.sqrt_double(xDist * xDist + zDist * zDist);
        final float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        final float var1 = MovementUtils.getMovementDirection() - 180.0F;

        final float yaw = rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
        final float rotationPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;

        if (data.face != EnumFacing.DOWN && data.face != EnumFacing.UP) {
            final double yDistFeet = hitVec.yCoord - player.posY;
            final double totalAbsDist = Math.abs(xDist * xDist + yDistFeet * yDistFeet + zDist * zDist);

            if (totalAbsDist < 1.0)
                return new float[]{yaw, MathUtils.getRandom(80, 90)};
        }

        final float var2 = (float) (-(StrictMath.atan2(yDist, fDist) * 180.0D / Math.PI));
        final float pitch = rotationPitch + MathHelper.wrapAngleTo180_float(var2 - rotationPitch);

        return new float[]{yaw, MathHelper.clamp_float(pitch, -90.0F, 90.0F)};
    }

    public static class BlockData {
        public BlockPos position;
        public EnumFacing face;
        public Vec3 hitVec;

        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
            this.hitVec = getHitVec();
        }

        private Vec3 getHitVec() {
            final Vec3i directionVec = face.getDirectionVec();
            double x = directionVec.getX() * 0.5D;
            double z = directionVec.getZ() * 0.5D;

            if (face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                x = -x;
                z = -z;
            }

            final Vec3 hitVec = new Vec3(position).addVector(x + z, directionVec.getY() * 0.5D, x + z);

            final Vec3 src = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0F);
            final MovingObjectPosition obj = Minecraft.getMinecraft().theWorld.rayTraceBlocks(src,
                    hitVec,
                    false,
                    false,
                    true);

            if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
                return null;

            switch (face.getAxis()) {
                case Z:
                    obj.hitVec = new Vec3(obj.hitVec.xCoord, obj.hitVec.yCoord, Math.round(obj.hitVec.zCoord));
                    break;
                case X:
                    obj.hitVec = new Vec3(Math.round(obj.hitVec.xCoord), obj.hitVec.yCoord, obj.hitVec.zCoord);
                    break;
            }

            if (face != EnumFacing.DOWN && face != EnumFacing.UP) {
                final IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(obj.getBlockPos());
                final Block blockAtPos = blockState.getBlock();

                double blockFaceOffset;

                if (blockAtPos instanceof BlockSlab && !((BlockSlab) blockAtPos).isDouble()) {
                    final BlockSlab.EnumBlockHalf half = blockState.getValue(BlockSlab.HALF);

                    blockFaceOffset = RandomUtils.nextDouble(0.1, 0.4);

                    if (half == BlockSlab.EnumBlockHalf.TOP) {
                        blockFaceOffset += 0.5;
                    }
                } else {
                    blockFaceOffset = RandomUtils.nextDouble(0.1, 0.9);
                }

                obj.hitVec = obj.hitVec.addVector(0.0D, -blockFaceOffset, 0.0D);
            }

            return obj.hitVec;
        }
    }

    private static boolean validateBlockRange(final BlockData data) {
        final Vec3 pos = data.hitVec;
        if (pos == null)
            return false;
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        final double x = (pos.xCoord - player.posX);
        final double y = (pos.yCoord - (player.posY + player.getEyeHeight()));
        final double z = (pos.zCoord - player.posZ);
        return StrictMath.sqrt(x * x + y * y + z * z) <= 5.0D;
    }

    private static boolean validateReplaceable(final BlockData data) {
        final BlockPos pos = data.position.offset(data.face);
        final World world = Minecraft.getMinecraft().theWorld;
        return world.getBlockState(pos)
                .getBlock()
                .isReplaceable(world, pos);
    }

    private static BlockData getBlockData2(final BlockPos pos) {
        final BlockPos[] blockPositions = BLOCK_POSITIONS;
        final EnumFacing[] facings = FACINGS;
        final WorldClient world = Minecraft.getMinecraft().theWorld;

        // 1 of the 4 directions around player
        for (int i = 0; i < blockPositions.length; i++) {
            final BlockPos blockPos = pos.add(blockPositions[i]);
            if (PlayerUtil.isValidBlock(world.getBlockState(blockPos).getBlock(), false)) {
                final BlockData data = new BlockData(blockPos, facings[i]);
                if (validateBlockRange(data))
                    return data;
            }
        }

        // 2 Blocks Under e.g. When jumping
        final BlockPos posBelow = pos.add(0, -1, 0);
        if (PlayerUtil.isValidBlock(world.getBlockState(posBelow).getBlock(), false)) {
            final BlockData data = new BlockData(posBelow, EnumFacing.UP);
            if (validateBlockRange(data))
                return data;
        }

        // 2 Block extension & diagonal
        for (BlockPos blockPosition : blockPositions) {
            final BlockPos blockPos = pos.add(blockPosition);
            for (int i = 0; i < blockPositions.length; i++) {
                final BlockPos blockPos1 = blockPos.add(blockPositions[i]);
                if (PlayerUtil.isValidBlock(world.getBlockState(blockPos1).getBlock(), false)) {
                    final BlockData data = new BlockData(blockPos1, facings[i]);
                    if (validateBlockRange(data))
                        return data;
                }
            }
        }

        // 3 Block extension
        for (final BlockPos blockPosition : blockPositions) {
            final BlockPos blockPos = pos.add(blockPosition);
            for (final BlockPos position : blockPositions) {
                final BlockPos blockPos1 = blockPos.add(position);
                for (int i = 0; i < blockPositions.length; i++) {
                    final BlockPos blockPos2 = blockPos1.add(blockPositions[i]);
                    if (PlayerUtil.isValidBlock(world.getBlockState(blockPos2).getBlock(), false)) {
                        final BlockData data = new BlockData(blockPos2, facings[i]);
                        if (validateBlockRange(data))
                            return data;
                    }
                }
            }
        }
        // 4 Block extension
        for (final BlockPos blackPosition : blockPositions) {
            final BlockPos blockPos = pos.add(blackPosition);
            for (final BlockPos blockPosition : blockPositions) {
                final BlockPos blockPos1 = blockPos.add(blockPosition);
                for (final BlockPos position : blockPositions) {
                    final BlockPos blockPos2 = blockPos1.add(position);
                    for (int i = 0; i < blockPositions.length; i++) {
                        final BlockPos blockPos3 = blockPos2.add(blockPositions[i]);
                        if (PlayerUtil.isValidBlock(world.getBlockState(blockPos3).getBlock(), false)) {
                            final BlockData data = new BlockData(blockPos3, facings[i]);
                            if (validateBlockRange(data))
                                return data;
                        }
                    }
                }
            }
        }
        return null;
    }
}