package me.jinthium.straight.impl.modules.combat;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.event.movement.PlayerMoveEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.event.render.Render3DEvent;
import me.jinthium.straight.impl.modules.movement.Flight;
import me.jinthium.straight.impl.modules.movement.Speed;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.ColorSetting;
import me.jinthium.straight.impl.settings.MultiBoolSetting;
import me.jinthium.straight.impl.settings.NumberSetting;
import me.jinthium.straight.impl.utils.animation.Direction;
import me.jinthium.straight.impl.utils.animation.impl.DecelerateAnimation;
import me.jinthium.straight.impl.utils.player.MovementUtil;
import me.jinthium.straight.impl.utils.player.RotationUtils;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjglx.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class TargetStrafe extends Module {
    private static final MultiBoolSetting adaptiveSettings = new MultiBoolSetting("Adaptive",
            new BooleanSetting("Edges", false),
            new BooleanSetting("Behind", false),
            new BooleanSetting("Liquids", false),
            new BooleanSetting("Controllable", true)
    );
    public static final NumberSetting radius = new NumberSetting("Radius", 2, 0.5, 8, 0.5);
    private static final NumberSetting points = new NumberSetting("Points", 12, 3, 16, 1);
    public static final BooleanSetting space = new BooleanSetting("Require space key", true);
    public static final BooleanSetting auto3rdPerson = new BooleanSetting("Auto 3rd Person", false);
    private final BooleanSetting render = new BooleanSetting("Render", true);
    private final ColorSetting color = new ColorSetting("Color", new Color(-16711712));

    private static int strafe = 1;
    private static int position;

    private final DecelerateAnimation animation = new DecelerateAnimation(250, radius.getValue(), Direction.FORWARDS);
    private boolean returnState;

    public TargetStrafe() {
        super("TargetStrafe", Category.COMBAT);
        addSettings(adaptiveSettings, radius, points, space, auto3rdPerson, render, color);
        color.addParent(render, ParentAttribute.BOOLEAN_CONDITION);
    }

    @Callback
    final EventCallback<PlayerUpdateEvent> playerUpdateEventCallback = event ->  {
        if (canStrafe()) {
            if (auto3rdPerson.isEnabled() && mc.gameSettings.thirdPersonView == 0) {
                mc.gameSettings.thirdPersonView = 1;
                returnState = true;
            }
            boolean updatePosition = false, positive = true;
            if (mc.thePlayer.isCollidedHorizontally) {
                strafe = -strafe;
                updatePosition = true;
                positive = strafe == 1;
            } else {
                if (adaptiveSettings.getSetting("Controllable").isEnabled()) {
                    if (mc.gameSettings.keyBindLeft.isPressed()) {
                        strafe = 1;
                        updatePosition = true;
                    }
                    if (mc.gameSettings.keyBindRight.isPressed()) {
                        strafe = -1;
                        updatePosition = true;
                        positive = false;
                    }
                }
                if (adaptiveSettings.getSetting("Edges").isEnabled() && isInVoid()) {
                    strafe = -strafe;
                    updatePosition = true;
                    positive = false;
                }
                if (adaptiveSettings.getSetting("Liquids").isEnabled() && isInLiquid()) {
                    strafe = -strafe;
                    updatePosition = true;
                    positive = false;
                }
            }
            if (updatePosition) {
                position = (position + (positive ? 1 : -1)) % points.getValue().intValue();
            }
        } else if (auto3rdPerson.isEnabled() && mc.gameSettings.thirdPersonView != 0 && returnState) {
            mc.gameSettings.thirdPersonView = 0;
            returnState = false;
        }
    };

    @Callback
    final EventCallback<Render3DEvent> render3DEventEventCallback = event -> {
        if (render.isEnabled()) {
            if (animation.getEndPoint() != radius.getValue()) animation.setEndPoint(radius.getValue());
            boolean canStrafe = canStrafe();
            animation.setDirection(canStrafe ? Direction.FORWARDS : Direction.BACKWARDS);
            if (canStrafe || !animation.isDone()) {
                drawCircle(5, 0xFF000000);
                drawCircle(3, color.getColor().getRGB());
            }
        }
    };

    public static boolean strafe(PlayerMoveEvent e) {
        return strafe(e, MovementUtil.getSpeed());
    }

    public static boolean strafe(PlayerMoveEvent e, double moveSpeed) {
        if (canStrafe()) {
            KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
            if(killAura.target == null)
                return false;

            setSpeed(e, moveSpeed, RotationUtils.getYaw(killAura.target.getPositionVector()), strafe,
                    mc.thePlayer.getDistanceToEntity(killAura.target) <= radius.getValue() ? 0 : 1);
            return true;
        }
        return false;
    }

    public static boolean strafe(PlayerMoveUpdateEvent e) {
        return strafe(e, MovementUtil.getSpeed());
    }

    public static boolean strafe(PlayerMoveUpdateEvent e, double moveSpeed) {
        if (canStrafe()) {
            KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
            if(killAura.target == null)
                return false;

            setSpeed(e, moveSpeed, RotationUtils.getYaw(killAura.target.getPositionVector()), strafe,
                    mc.thePlayer.getDistanceToEntity(killAura.target) <= radius.getValue() ? 0 : 1);
            return true;
        }
        return false;
    }

    public static boolean canStrafe() {
        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        if (!Client.INSTANCE.getModuleManager().getModule(TargetStrafe.class).isEnabled() || !killAura.isEnabled()
                || !mc.thePlayer.isMoving() || (space.isEnabled() && !Keyboard.isKeyDown(Keyboard.KEY_SPACE))) {
            return false;
        }
        if (!(Client.INSTANCE.getModuleManager().getModule(Speed.class).isEnabled() || Client.INSTANCE.getModuleManager().getModule(Flight.class).isEnabled())) {
            return false;
        }
        return killAura.target != null;
    }

    public static void setSpeed(PlayerMoveEvent moveEvent, double speed, float yaw, double strafe, double forward) {
        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        if(killAura.target == null)
            return;

        EntityLivingBase target = killAura.target;
        double rad = radius.getValue();
        int count = points.getValue().intValue();

        double a = (Math.PI * 2.0) / (double) count;
        double posX = StrictMath.sin(a * position) * rad * strafe, posY = StrictMath.cos(a * position) * rad;

        if (forward == 0 && strafe == 0) {
            moveEvent.setX(0);
            moveEvent.setZ(0);
        } else {
            boolean skip = false;
            if (adaptiveSettings.getSetting("Edges").isEnabled()) {
                Vec3 pos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
                Vec3 vec = RotationUtils.getVecRotations(0, 90);
                if (mc.theWorld.rayTraceBlocks(pos, pos.addVector(vec.xCoord * 5, vec.yCoord * 5, vec.zCoord * 5), false, false, false) == null) {
                    moveEvent.setX(0);
                    moveEvent.setZ(0);
                    skip = true;
                }
            }

            if (!skip) {
                double d;
                if (adaptiveSettings.getSetting("Behind").isEnabled()) {
                    double x = target.posX + -StrictMath.sin(StrictMath.toRadians(target.rotationYaw)) * -2,
                            z = target.posZ + StrictMath.cos(StrictMath.toRadians(target.rotationYaw)) * -2;
                    d = StrictMath.toRadians(RotationUtils.getRotations(x, target.posY, z)[0]);
                } else {
                    d = StrictMath.toRadians(RotationUtils.getRotations(target.posX + posX, target.posY, target.posZ + posY)[0]);
                }
                moveEvent.setX(speed * -StrictMath.sin(d));
                moveEvent.setZ(speed * StrictMath.cos(d));
            }
        }

        double x = Math.abs(target.posX + posX - mc.thePlayer.posX), z = Math.abs(target.posZ + posY - mc.thePlayer.posZ);
        double dist = StrictMath.sqrt(x * x + z * z);
        if (dist <= 0.7) {
            position = (position + TargetStrafe.strafe) % count;
        } else if (dist > 3) {
            position = getClosestPoint(target);
        }
    }

    public static void setSpeed(PlayerMoveUpdateEvent moveEvent, double speed, float yaw, double strafe, double forward) {
        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        if(killAura.target == null)
            return;

        EntityLivingBase target = killAura.target;
        double rad = radius.getValue();
        int count = points.getValue().intValue();

        double a = (Math.PI * 2.0) / (double) count;
        double posX = StrictMath.sin(a * position) * rad * strafe, posY = StrictMath.cos(a * position) * rad;

//        moveEvent.setForward((float) speed);
        moveEvent.setYaw(MovementUtil.getMoveYaw(yaw));
        moveEvent.setStrafe((float) strafe);


        moveEvent.setForward((float) MovementUtil.getMoveYaw(yaw) < 180 ? -1 : 1);

        double x = Math.abs(target.posX + posX - mc.thePlayer.posX), z = Math.abs(target.posZ + posY - mc.thePlayer.posZ);
        double dist = StrictMath.sqrt(x * x + z * z);
        if (dist <= 0.7) {
            position = (position + TargetStrafe.strafe) % count;
        } else if (dist > 3) {
            position = getClosestPoint(target);
        }
    }

    private void drawCircle(float lineWidth, int color) {
        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        EntityLivingBase entity = killAura.target;
        if (entity == null) return;

        glPushMatrix();
        RenderUtil.color(color, (float) ((animation.getOutput().floatValue() / radius.getValue()) / 2F));
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        glLineWidth(lineWidth);
        glEnable(GL_BLEND);
        glEnable(GL_LINE_SMOOTH);

        glBegin(GL_LINE_STRIP);
        EntityLivingBase target = killAura.target;
        float partialTicks = mc.timer.elapsedPartialTicks;
        double rad = radius.getValue();
        double d = (Math.PI * 2.0) / points.getValue();

        double posX = target.posX, posY = target.posY, posZ = target.posZ;
        double lastTickX = target.lastTickPosX, lastTickY = target.lastTickPosY, lastTickZ = target.lastTickPosZ;
        double renderPosX = mc.getRenderManager().renderPosX, renderPosY = mc.getRenderManager().renderPosY, renderPosZ = mc.getRenderManager().renderPosZ;

        double y = lastTickY + (posY - lastTickY) * partialTicks - renderPosY;
        for (double i = 0; i < Math.PI * 2.0; i += d) {
            double x = lastTickX + (posX - lastTickX) * partialTicks + StrictMath.sin(i) * rad - renderPosX,
                    z = lastTickZ + (posZ - lastTickZ) * partialTicks + StrictMath.cos(i) * rad - renderPosZ;
            glVertex3d(x, y, z);
        }
        double x = lastTickX + (posX - lastTickX) * partialTicks - renderPosX,
                z = lastTickZ + (posZ - lastTickZ) * partialTicks + rad - renderPosZ;
        glVertex3d(x, y, z);
        glEnd();

        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glColor4f(1, 1, 1, 1);
        glPopMatrix();
    }

    private boolean isInVoid() {
        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        if(killAura.target == null)
            return false;

        double yaw = Math.toRadians(RotationUtils.getYaw(killAura.target.getPositionVector()));
        double xValue = -Math.sin(yaw) * 2;
        double zValue = Math.cos(yaw) * 2;
        for (int i = 0; i <= 256; i++) {
            BlockPos b = new BlockPos(mc.thePlayer.posX + xValue, mc.thePlayer.posY - i, mc.thePlayer.posZ + zValue);
            if (mc.theWorld.getBlockState(b).getBlock() instanceof BlockAir) {
                if (b.getY() == 0) {
                    return true;
                }
            } else {
                return false;
            }
        }
        return !mc.thePlayer.isCollidedVertically && !mc.thePlayer.onGround && mc.thePlayer.fallDistance != 0 && mc.thePlayer.motionY != 0 && mc.thePlayer.isAirBorne && !mc.thePlayer.capabilities.isFlying && !mc.thePlayer.isInWater() && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isPotionActive(Potion.invisibility.id);
    }

    private boolean isInLiquid() {
        KillAura killAura = Client.INSTANCE.getModuleManager().getModule(KillAura.class);
        if(killAura.target == null)
            return false;

        double yaw = Math.toRadians(RotationUtils.getYaw(killAura.target.getPositionVector()));
        double xValue = -Math.sin(yaw) * 2;
        double zValue = Math.cos(yaw) * 2;
        BlockPos b = new BlockPos(mc.thePlayer.posX + xValue, mc.thePlayer.posY, mc.thePlayer.posZ + zValue);
        return mc.theWorld.getBlockState(b).getBlock() instanceof BlockLiquid;
    }

    private static int getClosestPoint(Entity target) {
        double playerX = mc.thePlayer.posX, playerZ = mc.thePlayer.posZ;
        return getPoints(target).stream().min(Comparator.comparingDouble(p -> p.getDistance(playerX, playerZ))).get().iteration;
    }

    private static List<Point> getPoints(Entity target) {
        double radius = TargetStrafe.radius.getValue();
        List<Point> pointList = new ArrayList<>();
        int count = points.getValue().intValue();
        double posX = target.posX, posZ = target.posZ;
        double d = (Math.PI * 2.0) / count;
        for (int i = 0; i <= count; i++) {
            double x = radius * StrictMath.cos(i * d);
            double z = radius * StrictMath.sin(i * d);
            pointList.add(new Point(posX + x, posZ + z, i));
        }
        return pointList;
    }


    private static class Point {
        private final double x, z;
        private final int iteration;

        private double getDistance(double posX, double posZ) {
            double x2 = Math.abs(posX - x), z2 = Math.abs(posZ - z);
            return StrictMath.sqrt(x2 * x2 + z2 * z2);
        }

        public Point(double x, double z, int iteration) {
            this.x = x;
            this.z = z;
            this.iteration = iteration;
        }

        public double getX() {
            return x;
        }

        public double getZ() {
            return z;
        }
    }

}
