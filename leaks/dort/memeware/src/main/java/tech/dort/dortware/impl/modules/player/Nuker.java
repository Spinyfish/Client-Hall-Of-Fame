package tech.dort.dortware.impl.modules.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.module.Module;
import tech.dort.dortware.api.module.ModuleData;
import tech.dort.dortware.api.property.impl.BooleanValue;
import tech.dort.dortware.api.property.impl.EnumValue;
import tech.dort.dortware.api.property.impl.NumberValue;
import tech.dort.dortware.api.property.impl.interfaces.INameable;
import tech.dort.dortware.impl.events.PacketEvent;
import tech.dort.dortware.impl.events.UpdateEvent;
import tech.dort.dortware.impl.modules.render.FreeCam;
import tech.dort.dortware.impl.modules.render.Rotate;
import tech.dort.dortware.impl.utils.networking.PacketUtil;
import tech.dort.dortware.impl.utils.networking.ServerUtils;
import tech.dort.dortware.impl.utils.pathfinding.DortPathFinder;
import tech.dort.dortware.impl.utils.pathfinding.Vec3;
import tech.dort.dortware.impl.utils.player.ChatUtil;
import tech.dort.dortware.impl.utils.time.Stopwatch;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Auth
 */

public class Nuker extends Module {

    public final Stopwatch stopwatch = new Stopwatch();

    public final EnumValue<Nuker.Mode> mode = new EnumValue<>("Mode", this, Nuker.Mode.values());
    private final NumberValue rad = new NumberValue("Range", this, 3, 1, 6, true);
    public final BooleanValue silent = new BooleanValue("Silent", this, true);

    public Nuker(ModuleData moduleData) {
        super(moduleData);
        register(mode, rad, silent);
    }

    @Subscribe
    public void onPacket(PacketEvent event) {
        if (silent.getValue() && event.getPacket() instanceof C0APacketAnimation) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onUpdate(UpdateEvent event) {
        final FreeCam freeCam = Client.INSTANCE.getModuleManager().get(FreeCam.class);
        final int radius = rad.getValue().intValue() - 1;

        if (silent.getValue()) {
            float newYaw = MathHelper.wrapAngleTo180_float(180F);
            float newPitch = MathHelper.wrapAngleTo180_float(90F);

            event.setRotationYaw(newYaw);
            event.setRotationPitch(newPitch);

            if (Client.INSTANCE.getModuleManager().get(Rotate.class).isToggled()) {
                mc.thePlayer.renderYawHead = newYaw;
                mc.thePlayer.renderYawOffset = newYaw;
                mc.thePlayer.renderPitchHead = newPitch;
            }
        }

        switch (mode.getValue()) {
            case TELEPORT: {
                ArrayList<Vec3> path;

                if (mc.gameSettings.keyBindUseItem.getIsKeyPressed() && stopwatch.timeElapsed(500L)) { // stopwatch to prevent packet limit kicks
                    BlockPos blockPosToNuke = mc.objectMouseOver.func_178782_a();
                    Block block = mc.theWorld.getBlockState(blockPosToNuke).getBlock();
                    int i = 0;

                    if (block instanceof BlockAir)
                        return;

                    path = DortPathFinder.computePath(freeCam.isToggled() ? new tech.dort.dortware.impl.utils.pathfinding.Vec3(freeCam.startX, freeCam.startY, freeCam.startZ) : new tech.dort.dortware.impl.utils.pathfinding.Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new tech.dort.dortware.impl.utils.pathfinding.Vec3(blockPosToNuke.getX(), blockPosToNuke.getY(), blockPosToNuke.getZ()));

                    for (tech.dort.dortware.impl.utils.pathfinding.Vec3 vector : path) {
                        i++;
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                    }

                    if (radius > 0) {
                        for (int x = -radius; x < radius; ++x) {
                            for (int y = radius; y > -radius; --y) {
                                for (int z = -radius; z < radius; ++z) {
                                    final double xPos = blockPosToNuke.getX() + x;
                                    final double yPos = blockPosToNuke.getY() + y;
                                    final double zPos = blockPosToNuke.getZ() + z;

                                    BlockPos blockPosToNuke1 = new BlockPos(xPos, yPos, zPos);

                                    if (ServerUtils.onHypixel()) {
                                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPosToNuke1, EnumFacing.NORTH));
                                    } else {
                                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPosToNuke1, EnumFacing.NORTH));
                                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPosToNuke1, EnumFacing.NORTH));
                                    }
                                }
                            }
                        }
                    } else {
                        final double xPos = blockPosToNuke.getX();
                        final double yPos = blockPosToNuke.getY();
                        final double zPos = blockPosToNuke.getZ();

                        BlockPos blockPosToNuke1 = new BlockPos(xPos, yPos, zPos);

                        if (ServerUtils.onHypixel()) {
                            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPosToNuke1, EnumFacing.NORTH));
                        } else {
                            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPosToNuke1, EnumFacing.NORTH));
                            PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPosToNuke1, EnumFacing.NORTH));
                        }
                    }

                    Collections.reverse(path);

                    for (Vec3 vector : path) {
                        i++;
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                    }

                    ChatUtil.displayChatMessage("Nuked " + blockPosToNuke.getX() + " " + blockPosToNuke.getY() + " " + blockPosToNuke.getZ() + " in " + i + " teleports.");

                    stopwatch.resetTime();
                }
            }
            break;

            case FIRE_TP: {
                ArrayList<Vec3> path;

                if (mc.gameSettings.keyBindUseItem.getIsKeyPressed() && stopwatch.timeElapsed(50L)) { // stopwatch to prevent packet limit kicks
                    BlockPos blockPosToNuke = mc.objectMouseOver.func_178782_a();
                    Block block = mc.theWorld.getBlockState(blockPosToNuke).getBlock();
                    int i = 0;

                    if (block instanceof BlockAir)
                        return;

                    path = DortPathFinder.computePath(freeCam.isToggled() ? new tech.dort.dortware.impl.utils.pathfinding.Vec3(freeCam.startX, freeCam.startY, freeCam.startZ) : new tech.dort.dortware.impl.utils.pathfinding.Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ), new tech.dort.dortware.impl.utils.pathfinding.Vec3(blockPosToNuke.getX(), blockPosToNuke.getY(), blockPosToNuke.getZ()));

                    for (tech.dort.dortware.impl.utils.pathfinding.Vec3 vector : path) {
                        i++;
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                    }

                    if (radius > 0) {
                        for (int x = -radius; x < radius; ++x) {
                            for (int z = -radius; z < radius; ++z) {
                                final double xPos = blockPosToNuke.getX() + x;
                                final double yPos = blockPosToNuke.getY();
                                final double zPos = blockPosToNuke.getZ() + z;

                                BlockPos blockPosToNuke1 = new BlockPos(xPos, yPos, zPos);

                                if (silent.getValue()) {
                                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem == 8 ? 0 : mc.thePlayer.inventory.currentItem + 1));
                                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(blockPosToNuke1, EnumFacing.UP.getIndex(), mc.thePlayer.getCurrentEquippedItem(), 0, 0, 0));
                                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                                } else {
                                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(blockPosToNuke1, EnumFacing.UP.getIndex(), mc.thePlayer.getCurrentEquippedItem(), 0, 0, 0));
                                }
                            }
                        }
                    } else {
                        final double xPos = blockPosToNuke.getX();
                        final double yPos = blockPosToNuke.getY();
                        final double zPos = blockPosToNuke.getZ();

                        BlockPos blockPosToNuke1 = new BlockPos(xPos, yPos, zPos);

                        if (silent.getValue()) {
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem == 8 ? 0 : mc.thePlayer.inventory.currentItem + 1));
                            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(blockPosToNuke1, EnumFacing.UP.getIndex(), mc.thePlayer.getCurrentEquippedItem(), 0, 0, 0));
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        } else {
                            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(blockPosToNuke1, EnumFacing.UP.getIndex(), mc.thePlayer.getCurrentEquippedItem(), 0, 0, 0));
                        }
                    }

                    Collections.reverse(path);

                    for (Vec3 vector : path) {
                        i++;
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(vector.getX(), vector.getY(), vector.getZ(), true));
                    }

                    ChatUtil.displayChatMessage("Used item at " + blockPosToNuke.getX() + " " + blockPosToNuke.getY() + " " + blockPosToNuke.getZ() + " in " + i + " teleports.");

                    stopwatch.resetTime();
                }
            }
            break;

            case NORMAL: {
                if (radius > 0) {
                    for (int x = -radius; x < radius; ++x) {
                        for (int y = radius; y > -radius; --y) {
                            for (int z = -radius; z < radius; ++z) {
                                final double xPos = mc.thePlayer.posX + x;
                                final double yPos = mc.thePlayer.posY + y;
                                final double zPos = mc.thePlayer.posZ + z;

                                BlockPos blockPosToNuke = new BlockPos(xPos, yPos, zPos);
                                Block block = mc.theWorld.getBlockState(blockPosToNuke).getBlock();

                                if (block instanceof BlockAir)
                                    continue;

                                if (ServerUtils.onHypixel()) {
                                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPosToNuke, EnumFacing.NORTH));
                                } else {
                                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPosToNuke, EnumFacing.NORTH));
                                    PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPosToNuke, EnumFacing.NORTH));
                                }
                            }
                        }
                    }
                } else {
                    final double xPos = mc.thePlayer.posX;
                    final double yPos = mc.thePlayer.posY;
                    final double zPos = mc.thePlayer.posZ;

                    BlockPos blockPosToNuke = new BlockPos(xPos, yPos, zPos);
                    Block block = mc.theWorld.getBlockState(blockPosToNuke).getBlock();

                    if (block instanceof BlockAir)
                        return;

                    if (ServerUtils.onHypixel()) {
                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPosToNuke, EnumFacing.NORTH));
                    } else {
                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPosToNuke, EnumFacing.NORTH));
                        PacketUtil.sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPosToNuke, EnumFacing.NORTH));
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mode.getValue() == Mode.FIRE_TP && silent.getValue()) {
            ChatUtil.displayChatMessage("Switch to a slot before the item that you want to use.");
        }
    }

    public enum Mode implements INameable {
        NORMAL("Normal"), TELEPORT("Teleport"), FIRE_TP("Use Teleport");
        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String getDisplayName() {
            return name;
        }
    }

    @Override
    public String getSuffix() {
        return " \2477" + mode.getValue().getDisplayName();
    }
}
