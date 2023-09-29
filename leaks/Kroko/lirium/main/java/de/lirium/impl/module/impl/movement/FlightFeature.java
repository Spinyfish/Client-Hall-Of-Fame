package de.lirium.impl.module.impl.movement;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.helper.TimeHelper;
import de.lirium.base.setting.Dependency;
import de.lirium.base.setting.Value;
import de.lirium.base.setting.impl.CheckBox;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.base.transform.Transformation;
import de.lirium.impl.events.*;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.misc.ServerUtil;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.shader.ShaderProgram;
import god.buddy.aot.BCompiler;
import me.felix.shader.access.ShaderAccess;
import me.felix.shader.render.ingame.RenderableShaders;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.*;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@ModuleFeature.Info(name = "Flight", description = "I believe i can fly", category = ModuleFeature.Category.MOVEMENT)
public class FlightFeature extends ModuleFeature {

    @Value(name = "Mode")
    private final ComboBox<String> mode = new ComboBox<>("Sentinel", new String[]{"Motion", "Block Drop", "Minemora"});

    @Value(name = "Motion Speed", displayName = "Speed")
    private final SliderSetting<Double> motionSpeed = new SliderSetting<>(4.0, 0.1, 10.0, new Dependency<>(mode, "Motion"));

    @Value(name = "Block Drop - Visualize", visual = true)
    private final CheckBox blockDropVisualize = new CheckBox(true, new Dependency<>(mode, "Block Drop"));

    @Value(name = "Block Drop - Blur", visual = true)
    private final CheckBox blockDropBlur = new CheckBox(true, new Dependency<>(blockDropVisualize, true));

    @Value(name = "Sentinel - Mode")
    private final ComboBox<String> sentinelMode = new ComboBox<>("Disabler", new String[]{}, new Dependency<>(mode, "Sentinel"));

    @Value(name = "Sentinel (Disabler) - Fast Fly", displayName = "Fast Fly")
    private final CheckBox sentinelDisablerFast = new CheckBox(false, new Dependency<>(sentinelMode, "Disabler"));

    @Value(name = "Sentinel (Disabler) - Fast Fly - Boost Mode", displayName = "Boost Mode")
    private final ComboBox<String> sentinelBoostMode = new ComboBox<>("Damage", new String[]{"Old", "Zoom ROFL"}, new Dependency<>(sentinelDisablerFast, true));

    @Value(name = "Sentinel (Disabler) - Fast Fly - Boost Damage", displayName = "Boost Damage")
    private final CheckBox sentinelBoostDamage = new CheckBox(false, new Dependency<>(sentinelBoostMode, "Old"));

    @Value(name = "Sentinel (Disabler) - Fast Fly - Boost Speed", displayName = "Boost Speed")
    private final SliderSetting<Integer> sentinelBoostAmount = new SliderSetting<>(4, 1, 12, new Dependency<>(sentinelBoostMode, "Old"));

    @Value(name = "Stop Motion")
    private final CheckBox stopMotion = new CheckBox(false);

    private final TimeHelper minemoraTimeHelper = new TimeHelper();
    private final Transformation transformation = new Transformation();
    private FontRenderer blockDropFont = null;
    private double moveSpeed;
    private int jumps;

    @EventHandler
    public final Listener<UpdateEvent> eventUpdateListener = e -> {
        setSuffix(mode.getValue());
        switch (mode.getValue()) {
            case "Minemora":
                if (isMoving()) {
                    if (getPlayer().onGround) {
                        minemoraTimeHelper.reset();
                        getPlayer().jump();
                    }
                    getPlayer().motionY -= 0.010;
                    getTimer().timerSpeed = 1.2f;
                    if (minemoraTimeHelper.hasReached(240L)) {
                        getTimer().timerSpeed = 0.3f;
                        getPlayer().motionY = 0.030;
                        minemoraTimeHelper.reset();
                    }
                }
                break;
            case "Sentinel":
                if (doSentinelBypass())
                    switch (sentinelMode.getValue()) {
                        case "Disabler":
                            doSentinelDisabler();
                            break;
                    }
                break;
        }
    };


    @EventHandler
    public final Listener<PacketEvent> packetEventListener = e -> {
        setSuffix(mode.getValue());
        switch (mode.getValue()) {
            case "Block Drop":
                if (e.packet instanceof SPacketPlayerPosLook && getPlayer().ticksExisted > 5 && !getPlayer().isDead) {
                    e.setCancelled(true);
                    final SPacketPlayerPosLook s08 = (SPacketPlayerPosLook) e.packet;
                    getPlayer().setPosition(s08.x, s08.y, s08.z);
                    sendPacket(new CPacketConfirmTeleport(s08.getTeleportId()));
                    sendPacket(new CPacketPlayer.PositionRotation(s08.x, s08.y, s08.z, s08.yaw, s08.pitch, true));
                }
                break;
            case "Sentinel":
        }
    };

    @EventHandler
    public final Listener<Render2DEvent> render2DEventListener = e -> {
        switch (mode.getValue()) {
            case "Block Drop":
                if (blockDropVisualize.getValue() && blockDropBlur.getValue()) {
                    final RayTraceResult position = getPlayer().rayTrace(100.0, getTimer().partialTicks);
                    if (position != null && position.typeOfHit.equals(RayTraceResult.Type.BLOCK) && !getWorld().isAirBlock(position.getBlockPos())) {
                        final AxisAlignedBB alignedBB = getWorld().getBlockState(position.getBlockPos()).getCollisionBoundingBox(getWorld(), position.getBlockPos());
                        if (alignedBB == null) return;
                        GlStateManager.pushMatrix();
                        transformation.collect();
                        RenderUtil.drawBox(position.getBlockPos().getX() - mc.getRenderManager().renderPosX, position.getBlockPos().getY() - mc.getRenderManager().renderPosY, position.getBlockPos().getZ() - mc.getRenderManager().renderPosZ, 1, alignedBB.maxY - alignedBB.minY);
                        ShaderAccess.blurShaderRunnables.add(transformation::draw);
                        /*ShaderAccess.bloomRunnables.add(() -> {
                            transformation.draw();
                        });*/
                        transformation.release();
                        GlStateManager.popMatrix();

                        mc.getFramebuffer().bindFramebuffer(true);
                        mc.entityRenderer.setupOverlayRendering();
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                    }
                }
                break;
        }
    };

    @EventHandler
    public final Listener<Render3DEvent> render3DEventListener = e -> {
        if (blockDropFont == null)
            blockDropFont = Client.INSTANCE.fontLoader.get("Arial Bold", 30);
        switch (mode.getValue()) {
            case "Block Drop":
                if (blockDropVisualize.getValue()) {
                    final String text = "§c§l<Rightclick to teleport>";
                    final RayTraceResult position = getPlayer().rayTrace(100.0, getTimer().partialTicks);
                    if (position != null && position.typeOfHit.equals(RayTraceResult.Type.BLOCK) && !getWorld().isAirBlock(position.getBlockPos())) {
                        final Vec3d pos = new Vec3d(position.getBlockPos()).addVector(0.5, 1.5, 0.5).addVector(-mc.getRenderManager().renderPosX, -mc.getRenderManager().renderPosY, -mc.getRenderManager().renderPosZ);

                        final double scale = MathHelper.clamp(pos.distanceTo(getPlayer().getLookVec()) / 10, 1, 10);
                        final AxisAlignedBB alignedBB = getWorld().getBlockState(position.getBlockPos()).getCollisionBoundingBox(getWorld(), position.getBlockPos());
                        if (alignedBB == null) return;

                        GlStateManager.pushMatrix();
                        GlStateManager.color(1F, 0F, 1F, 0.2F);
                        RenderUtil.drawBox(position.getBlockPos().getX() - mc.getRenderManager().renderPosX, position.getBlockPos().getY() - mc.getRenderManager().renderPosY, position.getBlockPos().getZ() - mc.getRenderManager().renderPosZ, 1, alignedBB.maxY - alignedBB.minY);
                        GlStateManager.popMatrix();

                        GlStateManager.pushMatrix();
                        GlStateManager.translate(pos.xCoord, pos.yCoord, pos.zCoord);
                        GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-getYaw(), 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate((float) (getGameSettings().thirdPersonView == 2 ? -1 : 1) * getPitch(), 1.0F, 0.0F, 0.0F);
                        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
                        GlStateManager.translate(-blockDropFont.getStringWidth(text) / 2F * scale, -blockDropFont.FONT_HEIGHT / 2F * scale, 0F);
                        GlStateManager.scale(scale, scale, 0);
                        blockDropFont.drawString(text, 0, 0, -1);
                        GlStateManager.popMatrix();

                        GL11.glColor4f(1, 1, 1, 1);
                        GlStateManager.enableAlpha();
                        GlStateManager.disableBlend();
                    }
                }
                break;
        }
    };

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public boolean doSentinelBypass() {
        if (!ServerUtil.isCubeCraft()) {
            setTimer(10.0F);
            if (jumps == 0) {
                for (int i = 0; i < 420; i++) {
                    sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 0.008D, getZ(), false));
                    sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
                }
                sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), true));
                for (int i = 0; i < 2; i++) getPlayer().jump();
                getPlayer().setPosition(getX(), getY() + 1.0D, getZ());

                final double speed = 0.49D;
                setPosition(getX() + -Math.sin(Math.toRadians(getYaw())) * speed, getY(), getZ() + Math.cos(Math.toRadians(getYaw())) * speed);

                jumps = 1;
            }

            if (jumps == 1 && getPlayer().hurtTime > 0) {
                moveSpeed = 0.6D;
                jumps = 2;
            }

            if (this.moveSpeed >= 0.26D)
                this.moveSpeed = this.moveSpeed - 0.001D;

            setSpeed(this.moveSpeed);

            if (!isMoving())
                setSpeed(0);
            getPlayer().motionY = 0;
            return false;
        }
        return true;
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    private void doSentinelDisabler() {
        final float niere = 4.5f;
        if (sentinelDisablerFast.getValue()) {
            switch (sentinelBoostMode.getValue()) {
                case "Damage":
                    switch (jumps) {
                        case 0:
                            sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 3.001D, getZ(), false));
                            sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
                            sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), true));
                            getPlayer().setPosition(getX(), getY() + 1F, getZ());
                            moveSpeed = 1.5;
                            jumps++;
                            break;
                        case 1:
                            if (!isMoving()) {
                                setSpeed(0);
                                moveSpeed = 0;
                                break;
                            }
                            if (getPlayer().isCollidedHorizontally)
                                moveSpeed = 0;
                            setSpeed(moveSpeed = Math.max(getBaseSpeed(), moveSpeed * 0.992));
                            /*setSpeed(0.25);
                            if (boostTicks++ % 3 == 0 && moveSpeed > 0) {
                                sendPacket(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
                                moveSpeed -= 0.25;
                            }
                            for (int i = 0; i < moveSpeed / 0.25; i++) {
                                final double x = getX() - Math.sin(getDirection()) * 0.25, y = getY(), z = getZ() + Math.cos(getDirection()) * 0.25;
                                if (!getWorld().getCollisionBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(-getX(), -getY(), -getZ()).offset(x, y, z)).isEmpty())
                                    break;
                                sendPacket(new CPacketPlayer.Position(x, y, z, false));
                                setPosition(x, y, z);
                            }*/
                            break;
                    }
                    /*setTimer(niere - (boostTicks * 0.02f));
                    if (getTimer().timerSpeed < 1f) setTimer(1f);
                    boostTicks++;
                    if (jumps == 0) {
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 3.001D, getZ(), false));
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), true));
                        for (int i = 0; i < 2; i++) getPlayer().jump();
                        getPlayer().setPosition(getX(), getY() + 1.0D, getZ());
                        final double speed = 0.49D;
                        setPosition(getX() + -Math.sin(Math.toRadians(getYaw())) * speed, getY(), getZ() + Math.cos(Math.toRadians(getYaw())) * speed);
                        jumps = 1;
                    }
                    if (jumps == 1 && getPlayer().hurtTime > 0) {
                        moveSpeed = 0.6D;
                        jumps = 2;
                    }
                    if (this.moveSpeed >= 0.26D)
                        this.moveSpeed = this.moveSpeed - 0.01D;
                    setSpeed(this.moveSpeed);
                    if (!isMoving())
                        setSpeed(0);*/
                    break;
                case "Old":
                    setSpeed(getSpeed());
                    if (sentinelBoostDamage.getValue() && jumps == 0) {
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 3.001D, getZ(), false));
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), true));
                        jumps++;
                    }
                    if (isMoving() && jumps++ % 3 == 0) {
                        for (int i = 0; i < sentinelBoostAmount.getValue(); i++) {
                            final double x = getX() - Math.sin(getDirection()) * getSpeed(), y = getY(), z = getZ() + Math.cos(getDirection()) * getSpeed();
                            if (!getWorld().getCollisionBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(-getX(), -getY(), -getZ()).offset(x, y, z)).isEmpty())
                                break;
                            sendPacket(new CPacketPlayer.Position(x, y, z, false));
                            setPosition(x, y, z);
                        }
                    }
                    if (!isMoving())
                        setSpeed(0);
                    break;
                case "Zoom ROFL":
                    if (jumps == 0) {
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY() + 3.001D, getZ(), false));
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), false));
                        sendPacketUnlogged(new CPacketPlayer.Position(getX(), getY(), getZ(), true));
                        jumps++;
                    }
                    final double speed = 2.0;
                    if (isMoving()) {
                        setSpeed(0.25);
                        for (int i = 0; i < Math.max(0, speed / 0.25); i++) {
                            double x = getX() - Math.sin(getDirection()) * 0.25, y = getY(), z = getZ() + Math.cos(getDirection()) * 0.25;
                            if (getGameSettings().keyBindJump.pressed) y += 0.25;
                            if (getGameSettings().keyBindSneak.pressed) y -= 0.25;
                            if (!getWorld().getCollisionBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(-getX(), -getY(), -getZ()).offset(x, y, z)).isEmpty())
                                break;
                            sendPacket(new CPacketPlayer.Position(x, y, z, true));
                            setPosition(x, y, z);
                        }
                    } else {
                        setSpeed(0);
                        for (int i = 0; i < Math.max(0, speed / 0.25); i++) {
                            double x = getX(), y = getY(), z = getZ();
                            if (getGameSettings().keyBindJump.pressed) y += 0.25;
                            if (getGameSettings().keyBindSneak.pressed) y -= 0.25;
                            if (!getWorld().getCollisionBoxes(getPlayer(), getPlayer().getEntityBoundingBox().offset(-getX(), -getY(), -getZ()).offset(x, y, z)).isEmpty())
                                break;
                            sendPacket(new CPacketPlayer.Position(x, y, z, true));
                            setPosition(x, y, z);
                        }
                    }
                    break;
            }
        }
        getPlayer().motionY = 0;
    }

    @EventHandler
    public final Listener<PlayerUpdateEvent> playerUpdateEventListener = e -> {
        switch (mode.getValue()) {
            case "Sentinel":
                if (doSentinelBypass() && sentinelDisablerFast.getValue() && sentinelBoostMode.getValue().equals("Zoom ROFL")) {
                    e.setSprinting(false);
                }
                break;
            case "Block Drop":
                if (!e.getState().equals(PlayerUpdateEvent.State.POST))
                    break;

                getPlayer().fallDistance = 0;

                double x = e.getX(), y = e.getY(), z = e.getZ();
                if (isMoving()) {
                    x -= Math.sin(getDirection()) * 30.0;
                    z += Math.cos(getDirection()) * 30.0;
                }
                if (getGameSettings().keyBindJump.pressed) y += 10.0;
                if (getGameSettings().keyBindSneak.pressed) y -= 10.0;
                Vec3d tempPos = new Vec3d(x, y, z);

                if (getGameSettings().keyBindUseItem.pressed) {
                    final RayTraceResult position = getPlayer().rayTrace(100.0, getTimer().partialTicks);
                    if (position != null && position.typeOfHit.equals(RayTraceResult.Type.BLOCK) && !getWorld().isAirBlock(position.getBlockPos())) {
                        tempPos = new Vec3d(position.getBlockPos()).addVector(0.5, 1.0, 0.5);
                    }

                    if (getPlayer().getPositionVector().distanceTo(tempPos) > 30) {
                        final double distance = Math.ceil(getPlayer().getPositionVector().distanceTo(tempPos) / 30.0);

                        x = tempPos.xCoord - getPlayer().getPositionVector().xCoord;
                        y = tempPos.yCoord - getPlayer().getPositionVector().yCoord;
                        z = tempPos.zCoord - getPlayer().getPositionVector().zCoord;


                        tempPos = new Vec3d(getPlayer().getPositionVector().xCoord + x / distance,
                                getPlayer().getPositionVector().yCoord + y / distance,
                                getPlayer().getPositionVector().zCoord + z / distance);
                    }
                }

                for (int i = 0; i < 3; i++)
                    sendPacket(new CPacketPlayer.Position(e.getX(), e.getY(), e.getZ(), true));
                for (int i = 0; i < 7; i++)
                    sendPacket(new CPacketPlayer.Position(tempPos.xCoord, tempPos.yCoord, tempPos.zCoord, true));
                break;
        }
    };

    @EventHandler
    public final Listener<PlayerMoveEvent> playerMoveEventListener = e -> {
        switch (mode.getValue()) {
            case "Sentinel":
                break;
            case "Block Drop":
                e.setX(getPlayer().motionX = 0);
                e.setY(getPlayer().motionY = 0);
                e.setZ(getPlayer().motionZ = 0);
                break;
            case "Motion":
                if (getPlayer().isInWeb) {
                    getPlayer().setSprinting(false);
                    return;
                }
                e.setX(getPlayer().motionX = 0);
                e.setY(getPlayer().motionY = 0);
                e.setZ(getPlayer().motionZ = 0);
                if (isMoving()) setSpeed(e, this.motionSpeed.getValue());
                else setSpeed(e, 0);
                if (getGameSettings().keyBindJump.pressed) e.setY(e.getY() + this.motionSpeed.getValue());
                if (getGameSettings().keyBindSneak.pressed) e.setY(e.getY() - this.motionSpeed.getValue());
                break;
        }
    };

    @Override
    public void onEnable() {
        super.onEnable();

        if (getPlayer() == null) {
            setEnabled(false);
            return;
        }
        switch (mode.getValue()) {
            case "Sentinel":
                boolean hasBlockAbove = false;
                final Vec3d pos = getPlayer().getPositionVector();
                final BlockPos position = new BlockPos(pos.xCoord, pos.yCoord, pos.zCoord);
                for (int i = 0; i < 2; i++) {
                    if (getWorld().getBlockState(position.add(0, i + 2, 0)).getBlock() != Blocks.AIR) {
                        hasBlockAbove = true;
                        break;
                    }
                }
                if (hasBlockAbove) {
                    setEnabled(false);
                    return;
                }
                break;
        }

        moveSpeed = 0;
        jumps = 0;
    }

    @Override
    public void onDisable() {
        setTimer(1.0F);
        if (stopMotion.getValue())
            setSpeed(0);
        super.onDisable();
    }
}