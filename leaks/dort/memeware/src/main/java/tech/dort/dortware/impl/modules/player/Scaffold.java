package tech.dort.dortware.impl.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.SliderUnit;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.RenderHUDEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.render.Hud;
import tech.dort.dortware.impl.modules.render.Rotate;
import tech.dort.dortware.impl.utils.combat.AimUtil;
import tech.dort.dortware.impl.utils.combat.extras.Rotation;
import tech.dort.dortware.impl.utils.movement.MotionUtils;
import tech.dort.dortware.impl.utils.networking.ServerUtils;
import tech.dort.dortware.impl.utils.pathfinding.Vec3d;
import tech.dort.dortware.impl.utils.render.ColorUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {
    private static final List<Block> invalidBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.tnt, Blocks.chest,
            Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.tnt, Blocks.enchanting_table, Blocks.carpet,
            Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice,
            Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch,
            Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore,
            Blocks.iron_ore, Blocks.lapis_ore, Blocks.sand, Blocks.lit_redstone_ore, Blocks.quartz_ore,
            Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button,
            Blocks.wooden_button, Blocks.lever, Blocks.enchanting_table, Blocks.red_flower, Blocks.double_plant,
            Blocks.yellow_flower, Blocks.bed, Blocks.ladder, Blocks.waterlily, Blocks.double_stone_slab, Blocks.stone_slab,
            Blocks.double_wooden_slab, Blocks.wooden_slab, Blocks.heavy_weighted_pressure_plate,
            Blocks.light_weighted_pressure_plate, Blocks.stone_pressure_plate, Blocks.wooden_pressure_plate, Blocks.stone_slab2,
            Blocks.double_stone_slab2, Blocks.tripwire, Blocks.tripwire_hook, Blocks.tallgrass, Blocks.dispenser,
            Blocks.command_block, Blocks.web);

    private final EnumValue<Scaffold.Mode> mode = new EnumValue<>("Mode", this, Scaffold.Mode.values());
    private final EnumValue<Scaffold.RotationMode> rotationMode = new EnumValue<>("Rotation Mode", this, Scaffold.RotationMode.values());
    private final BooleanValue packetSwing = new BooleanValue("Packet Swing", this, true);
    public final BooleanValue sprintCheck = new BooleanValue("Sprint Check", this, false);
    private final BooleanValue timerBoost = new BooleanValue("Timer", this, false);
    private final BooleanValue tower = new BooleanValue("Tower", this, true);
    private final NumberValue extend = new NumberValue("Extend", this, 0.2, 0, 6.0, SliderUnit.BLOCKS);
    private final NumberValue timerBoostValue = new NumberValue("Timer Amount", this, 1, 0.1, 5.0, false, timerBoost);
    private final NumberValue delay = new NumberValue("Delay", this, 0, 0, 500, SliderUnit.MS, true);
    private final Stopwatch timer = new Stopwatch();
    private BlockData blockInfo;
    private int slot, newSlot, oldSlot;

    final CustomFontRenderer font = Client.INSTANCE.getFontManager().getFont("Small1").getRenderer();

    public Scaffold(ModuleData moduleData) {
        super(moduleData);
        register(mode, rotationMode, timerBoostValue, extend, delay, packetSwing, sprintCheck, timerBoost, tower);
    }

    public static Vec3d getVec3d(BlockPos pos, EnumFacing face) {
        double rand = RandomUtils.nextDouble(.48D, .49D);
        double x = pos.getX() + rand;
        double y = pos.getY() + rand;
        double z = pos.getZ() + rand;

        x += face.getFrontOffsetX() / 2.0D;
        y += face.getFrontOffsetY() / 2.0D;
        z += face.getFrontOffsetZ() / 2.0D;

        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += rand;
            z += rand;
        } else {
            y += rand;
            if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
                x += rand;
            } else if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
                z += rand;
            }
        }
        return new Vec3d(x, y, z);
    }

    @Subscribe
    public void onRender(RenderHUDEvent event) {
        Hud hud = Client.INSTANCE.getModuleManager().get(Hud.class);
        ScaledResolution sr = new ScaledResolution(mc);
        if (hud.isToggled()) {
            font.drawStringWithShadow(getBlockCount() == 1 ? getBlockCount() + " \247fBlock" : getBlockCount() + " \247fBlocks", (sr.getScaledWidth() >> 1) - 12 - font.getWidth(Integer.toString(getBlockCount())) / 2, (sr.getScaledHeight() >> 1) + 12, ColorUtil.getModeColor());
        }
    }

    public static boolean isValid(ItemStack item) {
        if (isEmpty(item) || !(item.getItem() instanceof ItemBlock) || item.getUnlocalizedName().equalsIgnoreCase("tile.chest")) {
            return false;
        }

        return !invalidBlocks.contains(((ItemBlock) item.getItem()).getBlock());
    }

    public static boolean isEmpty(ItemStack stack) {
        return stack == null;
    }

    @Subscribe
    public void onUpdate(UpdateEvent eventUpdate) {
        if (timerBoost.getValue()) {
            mc.timer.timerSpeed = timerBoostValue.getValue().floatValue();
        }

        if (sprintCheck.getValue()) {
            mc.thePlayer.setSprinting(false);
        }

        if (eventUpdate.isPre()) {
            int tempSlot = getBlockSlot();

            if (invCheck()) {
                for (int i = 9; i < 36; ++i) {
                    Item item;
                    if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                            || !((item = mc.thePlayer.inventoryContainer.getSlot(i).getStack()
                            .getItem()) instanceof ItemBlock)
                            || invalidBlocks.contains(((ItemBlock) item).getBlock())
                            || ((ItemBlock) item).getBlock().getLocalizedName().toLowerCase().contains("chest"))
                        continue;
                    swap(i);
                    break;
                }
            }

            blockInfo = null;

            final double[] forward = MotionUtils.teleportForward(extend.getValue());

            final Rotation rotations = AimUtil.getScaffoldRotations(new BlockPos(mc.thePlayer.posX + forward[0], mc.thePlayer.posY - 1, mc.thePlayer.posZ + forward[1]));

            float newYaw = mc.thePlayer.rotationYaw, newPitch = mc.thePlayer.rotationPitch;

            switch (rotationMode.getValue()) {
                case NORMAL:
                    newYaw = rotations.getRotationYaw();
                    newPitch = rotations.getRotationPitch();
                    break;

                case REVERSE:
                    newYaw = AimUtil.wrapAngle(mc.thePlayer.rotationYaw + 180F);
                    newPitch = 70.0F;
                    break;

                case DOWN:
                    newPitch = 80.0F;
                    break;
            }

            eventUpdate.setRotationYaw(newYaw);
            eventUpdate.setRotationPitch(newPitch);
            slot = -1;
            if (tempSlot != -1) {
                newSlot = getBlockSlot();
                if (!ServerUtils.onHypixel()) {
                    oldSlot = mc.thePlayer.inventory.currentItem;
                }
                mc.thePlayer.inventory.currentItem = newSlot;
                if (!ServerUtils.onHypixel()) {
                    mc.thePlayer.inventory.currentItem = oldSlot;
                }


                if (mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX + forward[0], mc.thePlayer.posY - 1, mc.thePlayer.posZ + forward[1])).getBlock() == Blocks.air) {
                    blockInfo = getBlockData(new BlockPos(mc.thePlayer.posX + forward[0], mc.thePlayer.posY - 1, mc.thePlayer.posZ + forward[1]));
                    slot = tempSlot;
                }
            }

            if (Client.INSTANCE.getModuleManager().get(Rotate.class).isToggled()) {
                mc.thePlayer.renderYawHead = newYaw;
                mc.thePlayer.renderYawOffset = newYaw;
                mc.thePlayer.renderPitchHead = newPitch;
            }
        } else {
            if (blockInfo != null && timer.timeElapsed(delay.getValue().longValue()) && slot != -1) {
                final Vec3d hitVec = getVec3d(blockInfo.position, blockInfo.face);
                final EntitySnowball snowball = new EntitySnowball(mc.theWorld, hitVec.xCoord, hitVec.yCoord, hitVec.zCoord);
                if (!mc.thePlayer.canEntityBeSeen(snowball))
                    return;

                mc.thePlayer.inventory.currentItem = newSlot;
                if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld,
                        mc.thePlayer.inventoryContainer.getSlot(36 + slot).getStack(), blockInfo.position, blockInfo.face,
                        new Vec3d(blockInfo.position.getX() + Math.random(), blockInfo.position.getY() + Math.random(), blockInfo.position.getZ() + Math.random()))) {

                    if (packetSwing.getValue()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                    } else {
                        mc.thePlayer.swingItem();
                    }
                }

                if (!ServerUtils.onHypixel()) {
                    mc.thePlayer.inventory.currentItem = oldSlot;
                }
                switch (mode.getValue()) {
                    case NORMAL:
                        mc.timer.timerSpeed = 1F;
                        if (tower.getValue() && mc.gameSettings.keyBindJump.pressed && !mc.thePlayer.isPotionActive(Potion.jump) && !mc.thePlayer.isMoving()) {
                            mc.thePlayer.motionX *= 0;
                            mc.thePlayer.motionZ *= 0;
                            mc.thePlayer.motionY = 0.42F;
                        }
                        timer.resetTime();
                        break;
                    case TOWER_MOVE:
                        mc.timer.timerSpeed = 1F;
                        if (tower.getValue() && mc.gameSettings.keyBindJump.pressed && !mc.thePlayer.isPotionActive(Potion.jump)) {
                            mc.thePlayer.motionY = 0.42F;
                        }
                        timer.resetTime();
                        break;
                }
            }
        }
    }

    private int getBlockCount() {
        int blockCount = 0;
        for (int i = 0; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = itemStack.getItem();
            if (!(itemStack.getItem() instanceof ItemBlock) || invalidBlocks.contains(((ItemBlock) item).getBlock()))
                continue;
            blockCount += itemStack.stackSize;
        }
        return blockCount;
    }

    public float[] doRotations(Vec3d vec) {
        double diffX = vec.xCoord - mc.thePlayer.posX;
        double diffY = vec.yCoord - (mc.thePlayer.getEntityBoundingBox().minY);
        double diffZ = vec.zCoord - mc.thePlayer.posZ;
        double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)));
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));
        return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
                mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)};
    }

    private BlockData getBlockData(BlockPos var1) {
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(var1.add(0, -1, 0)).getBlock()))
            return new BlockData(var1.add(0, -1, 0), EnumFacing.UP);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(var1.add(-1, 0, 0)).getBlock()))
            return new BlockData(var1.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(var1.add(1, 0, 0)).getBlock()))
            return new BlockData(var1.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(var1.add(0, 0, -1)).getBlock()))
            return new BlockData(var1.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(var1.add(0, 0, 1)).getBlock()))
            return new BlockData(var1.add(0, 0, 1), EnumFacing.NORTH);
        BlockPos add = var1.add(-1, 0, 0);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add.add(-1, 0, 0)).getBlock()))
            return new BlockData(add.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add.add(1, 0, 0)).getBlock()))
            return new BlockData(add.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add.add(0, 0, -1)).getBlock()))
            return new BlockData(add.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add.add(0, 0, 1)).getBlock()))
            return new BlockData(add.add(0, 0, 1), EnumFacing.NORTH);
        BlockPos add2 = var1.add(1, 0, 0);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add2.add(-1, 0, 0)).getBlock()))
            return new BlockData(add2.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add2.add(1, 0, 0)).getBlock()))
            return new BlockData(add2.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add2.add(0, 0, -1)).getBlock()))
            return new BlockData(add2.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add2.add(0, 0, 1)).getBlock()))
            return new BlockData(add2.add(0, 0, 1), EnumFacing.NORTH);
        BlockPos add3 = var1.add(0, 0, -1);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add3.add(-1, 0, 0)).getBlock()))
            return new BlockData(add3.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add3.add(1, 0, 0)).getBlock()))
            return new BlockData(add3.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add3.add(0, 0, -1)).getBlock()))
            return new BlockData(add3.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add3.add(0, 0, 1)).getBlock()))
            return new BlockData(add3.add(0, 0, 1), EnumFacing.NORTH);
        BlockPos add4 = var1.add(0, 0, 1);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add4.add(-1, 0, 0)).getBlock()))
            return new BlockData(add4.add(-1, 0, 0), EnumFacing.EAST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add4.add(1, 0, 0)).getBlock()))
            return new BlockData(add4.add(1, 0, 0), EnumFacing.WEST);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add4.add(0, 0, -1)).getBlock()))
            return new BlockData(add4.add(0, 0, -1), EnumFacing.SOUTH);
        if (!invalidBlocks.contains(mc.theWorld.getBlockState(add4.add(0, 0, 1)).getBlock()))
            return new BlockData(add4.add(0, 0, 1), EnumFacing.NORTH);
        return null;
    }

    private int getBlockSlot() {
        for (int i = 36; i < 45; ++i) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof ItemBlock)
                if (!contains(((ItemBlock) stack.getItem()).getBlock()))
                    return i - 36;
        }
        return -1;
    }

    private boolean invCheck() {
        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()
                    || !isValid(mc.thePlayer.inventoryContainer.getSlot(i).getStack()))
                continue;
            return false;
        }
        return true;
    }

    private void swap(int slot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, 7, 2, mc.thePlayer);
    }

    private boolean contains(Block block) {
        return invalidBlocks.contains(block);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer.resetTime();
    }

    @Override
    public void onDisable() {
        super.onEnable();
        if (ServerUtils.onHypixel()) {
            mc.thePlayer.inventory.currentItem = 0;
        }
    }

    public static class BlockData {
        public BlockPos position;
        public EnumFacing face;

        public BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }

    private enum Mode implements INameable {
        NORMAL("Normal"), TOWER_MOVE("Tower Move");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
        }
    }

    private enum RotationMode implements INameable {
        NORMAL("Normal"), REVERSE("Reverse"), DOWN("Down"), NONE("None");

        private final String name;

        RotationMode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return this.name;
        }
    }
}