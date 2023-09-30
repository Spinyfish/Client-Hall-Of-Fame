package dev.echo.module.impl.player;

import dev.echo.listener.Link;
import dev.echo.listener.Listener;
import dev.echo.listener.event.impl.player.MotionEvent;
import dev.echo.listener.event.impl.player.StrafeEvent;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.settings.impl.BooleanSetting;
import dev.echo.module.settings.impl.MultipleBoolSetting;
import dev.echo.module.settings.impl.NumberSetting;
import dev.echo.utils.player.RotationUtils;
import net.minecraft.block.BlockBed;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Mouse;

public final class Breaker extends Module {
    public Breaker() {
        super("Breaker", Category.EXPLOIT, "Breaks Beds Throw Walls");
        this.addSettings(range, hypixel, extras);
    }

    boolean found = false;
    BlockPos bedPos = null;
    BlockPos abovePos = null;
    boolean brokeone = false;
    public final NumberSetting range = new NumberSetting("Range", 3, 8, 3, 1);
    public final BooleanSetting hypixel = new BooleanSetting("Hypixel", true);

    public MultipleBoolSetting extras = new MultipleBoolSetting(
            "Extras",
            new BooleanSetting("Rotate", true),
            new BooleanSetting("Move fix", true),
            new BooleanSetting("Spoof Ground", false),
            new BooleanSetting("Auto Disable", false)
    );
    float[] rotations;
    @Link
    public Listener<MotionEvent> motionEventListener = e -> {
        int detectionRange = range.getValue().intValue();
        for (int x = (int) mc.thePlayer.posX - detectionRange; x <= mc.thePlayer.posX + detectionRange; x++) {
            for (int y = (int) mc.thePlayer.posY - detectionRange; y <= mc.thePlayer.posY + detectionRange; y++) {
                for (int z = (int) mc.thePlayer.posZ - detectionRange; z <= mc.thePlayer.posZ + detectionRange; z++) {
                    BlockPos position = new BlockPos(x, y, z);
                    if (position.getBlock() instanceof BlockBed) {
                        found = true;
                        abovePos = new BlockPos(x, y + 1, z);
                        bedPos = position;
                        if (hypixel.isEnabled()) {
                            abovePos = new BlockPos(x, y + 1, z);
                            if (abovePos.getBlock().isFullBlock()) {
                                if (brokeone == false) {
                                    nuke(abovePos);
                                }
                                rotations = RotationUtils.getRotations(abovePos, EnumFacing.UP);
                                if (extras.getSetting("Rotate").isEnabled()) {
                                    e.setRotations(rotations[0], rotations[1]);
                                    RotationUtils.setVisualRotations(rotations[0], rotations[1]);
                                }
                                brokeone = true;
                            } else {
                                bedPos = position;
                                rotations = RotationUtils.getRotations(bedPos, EnumFacing.UP);
                                if (extras.getSetting("Rotate").isEnabled()) {
                                    e.setRotations(rotations[0], rotations[1]);
                                    RotationUtils.setVisualRotations(rotations[0], rotations[1]);
                                }
                                nuke(bedPos);
                            }
                        } else {
                            bedPos = position;
                            rotations = RotationUtils.getRotations(bedPos, EnumFacing.UP);
                            if (extras.getSetting("Rotate").isEnabled()) {
                                e.setRotations(rotations[0], rotations[1]);
                                RotationUtils.setVisualRotations(rotations[0], rotations[1]);
                            }
                            nuke(bedPos);
                        }
                    } else {
                        if (extras.getSetting("Auto Disable").isEnabled() && found == true) {
                            found = false;
                            this.toggle();
                            return;
                        }
                        if (found == true) {
                            found = false;
                        }
                    }
                }
            }
        }
        if (extras.getSetting("Spoof Ground").isEnabled() && bedPos != null && bedPos.getBlock() instanceof BlockBed) {
            e.setOnGround(true);
        }
    };
    @Link
    public Listener<StrafeEvent> strafeEventListener = e -> {
        if (extras.getSetting("Move Fix").isEnabled() && bedPos != null && bedPos.getBlock() instanceof BlockBed) {
            e.setYaw(rotations[0]);
        }
    };

    private void nuke(BlockPos p) {
        mc.thePlayer.swingItem();
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, p, EnumFacing.UP));
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, p, EnumFacing.UP));
    }

    @Override
    public void onDisable() {
        brokeone = false;
        mc.gameSettings.keyBindAttack.pressed = Mouse.isButtonDown(0);
        found = false;
        super.onDisable();
    }
}