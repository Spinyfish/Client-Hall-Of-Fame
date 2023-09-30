package dev.africa.pandaware.impl.module.movement;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.api.module.interfaces.ModuleInfo;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.event.player.UpdateEvent;
import dev.africa.pandaware.impl.event.render.RenderEvent;
import dev.africa.pandaware.impl.module.combat.KillAuraModule;
import dev.africa.pandaware.impl.module.movement.flight.FlightModule;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.impl.setting.NumberSetting;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.math.vector.Vec2f;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import dev.africa.pandaware.utils.player.RotationUtils;
import dev.africa.pandaware.utils.render.ColorUtils;
import lombok.Getter;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import javax.vecmath.Vector2d;
import java.awt.*;
import java.util.ArrayList;

@ModuleInfo(name = "Target Strafe", category = Category.MOVEMENT)
public class TargetStrafeModule extends Module {
    private Entity entityLivingBase;
    private boolean shouldStrafe, canStrafe;
    private double moveDirection = 1.0D;
    @Getter
    private static boolean strafing;

    private Point currentPoint;
    private int currentIndex = 0;
    private ArrayList<Point> points = new ArrayList<>();

    private final NumberSetting lineWidth = new NumberSetting("Line Width", 3, 0.1, 1.8).setSaveConfig(false);
    private final NumberSetting radius = new NumberSetting("Radius", 3, 0, 1.6).setSaveConfig(false);
    private final NumberSetting pointAmount = new NumberSetting("Points", 48, 2, 11, 1).setSaveConfig(false);
    private final NumberSetting slowdown = new NumberSetting("Slowdown", 1, 0.1, 1);

    private final BooleanSetting pressSpaceOnly = new BooleanSetting("Press Space Only", false).setSaveConfig(false);
    private final BooleanSetting checkVoid = new BooleanSetting("Check Void", false);

    public TargetStrafeModule() {
        this.registerSettings(
                this.lineWidth,
                this.radius,
                this.pointAmount,
                this.slowdown,
                this.pressSpaceOnly,
                this.checkVoid
        );
    }

    @EventHandler
    private final EventCallback<MotionEvent> update = event -> {
        if (event.getEventState() == Event.EventState.PRE) {

            if(mc.thePlayer == null || mc.theWorld == null) return;

            KillAuraModule killAuraModule = Client.getInstance().getModuleManager().getByClass(KillAuraModule.class);
            boolean newTarget = killAuraModule.getTarget() != entityLivingBase;
            if (newTarget) {
                this.entityLivingBase = killAuraModule.getTarget();
            }

            if (entityLivingBase != null) {
                updatePoints();
//                currentIndex = 1;
//                currentPoint = points.get(currentIndex);

                if (currentPoint == null || newTarget) {
                    currentPoint = getBestPoint();
                    currentIndex = points.indexOf(currentPoint);
                } else {
                    boolean switchedDir = false;
                    if (mc.thePlayer.isCollidedHorizontally || this.checkVoid.getValue() && !PlayerUtils.isBlockUnder()) {
                        if (this.moveDirection == -1) {
                            this.moveDirection = 1;
                        } else {
                            this.moveDirection = -1;
                        }
                        switchedDir = true;
                    }
                    if(mc.gameSettings.keyBindLeft.isKeyDown()) {
                        moveDirection = 1;
                        switchedDir = true;
                    }
                    if(mc.gameSettings.keyBindRight.isKeyDown()) {
                        moveDirection = -1;
                        switchedDir = true;
                    }
                    if (getDistanceTo(currentPoint) < MovementUtils.getSpeedDistance() || switchedDir) {
                        currentIndex += moveDirection;
                        if (currentIndex < 0) currentIndex = points.size() - 1;
                        if (currentIndex > points.size() - 1) currentIndex = 0;
                        int i = 0;
                        while (!points.get(currentIndex).valid && i < points.size()) {
                            currentIndex += moveDirection;
                            if (currentIndex < 0) currentIndex = points.size() - 1;
                            if (currentIndex > points.size() - 1) currentIndex = 0;
                            i++;
                        }
                    }
                }
                if (currentIndex < 0) currentIndex = points.size() - 1;
                if (currentIndex > points.size() - 1) currentIndex = 0;
                currentPoint = points.get(currentIndex);
            }

        }
    };

    @EventHandler
    private final EventCallback<RenderEvent> onRender = event -> {
        if (event.getType() == RenderEvent.Type.RENDER_3D) {
            KillAuraModule killAuraModule = Client.getInstance().getModuleManager()
                    .getByClass(KillAuraModule.class);

            this.canStrafe = GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || !this.pressSpaceOnly.getValue();
            this.shouldStrafe = this.entityLivingBase != null && killAuraModule.getData().isEnabled();
            if (this.shouldStrafe) {
                this.drawCircle(
                        this.entityLivingBase,
                        this.lineWidth.getValue().floatValue(),
                        this.radius.getValue().floatValue() - 0.3
                );
            }
        }
    };

    public MoveEvent editMovement(double x, double y, double z) {
        double movementSpeed = ApacheMath.sqrt(x * x + z * z) * slowdown.getValue().doubleValue();
        boolean modulesEnabled = Client.getInstance().getModuleManager()
                .getByClass(FlightModule.class).getData().isEnabled() ||
                Client.getInstance().getModuleManager()
                        .getByClass(SpeedModule.class).getData().isEnabled();

        if (this.canStrafe && MovementUtils.isMoving() && modulesEnabled && this.shouldStrafe) {
            if (!PlayerUtils.inLiquid() && !mc.thePlayer.isOnLadder()) {
                strafing = true;

                float forward = 1;
                float strafe = 0;
                float yaw = getYawTo(currentPoint);

                yaw = MovementUtils.getDirection(forward, strafe, yaw);

                x = -ApacheMath.sin(ApacheMath.toRadians(yaw)) * movementSpeed;
                z = ApacheMath.cos(ApacheMath.toRadians(yaw)) * movementSpeed;
            }
        }

        return new MoveEvent(x, y, z);
    }

//    public MoveEvent editMovement(double x, double y, double z) {
//        double movementSpeed = ApacheMath.sqrt(x * x + z * z) * slowdown.getValue().doubleValue();
//        boolean modulesEnabled = Client.getInstance().getModuleManager()
//                        .getByClass(FlightModule.class).getData().isEnabled() ||
//                Client.getInstance().getModuleManager()
//                        .getByClass(SpeedModule.class).getData().isEnabled();
//
//        if (this.canStrafe && MovementUtils.isMoving() && modulesEnabled && this.shouldStrafe) {
//            if (!PlayerUtils.inLiquid() && !mc.thePlayer.isOnLadder()) {
//                strafing = true;
//
//                float strafeYaw;
//                double strafeSpeed;
//                double strafeForward;
//
//                if (mc.thePlayer.isCollidedHorizontally || this.checkVoid.getValue() && !PlayerUtils.isBlockUnder()) {
//                    if (this.moveDirection == -1) {
//                        this.moveDirection = 1;
//                    } else {
//                        this.moveDirection = -1;
//                    }
//                }
//
//                float rotations = RotationUtils.getRotations((EntityLivingBase) this.entityLivingBase).getX();
//                double distanceDouble = mc.thePlayer.getDistanceSqToEntity(this.entityLivingBase);
//
//                double forward = strafeForward = (distanceDouble <= (this.radius.getValue().doubleValue() * 2) ? 0 : 1);
//                double strafe = strafeSpeed = moveDirection;
//
//                float yaw = strafeYaw = rotations;
//
//                if (strafeForward != 0.0D) {
//                    if (strafeSpeed > 0.0D) {
//                        yaw = strafeYaw + (float) -45;
//                    } else if (strafeSpeed < 0.0D) {
//                        yaw = strafeYaw + (float) 45;
//                    }
//
//                    strafe = 0.0D;
//                }
//
//                if (strafe > 0.0D) {
//                    strafe = 1D;
//                } else if (strafe < 0.0D) {
//                    strafe = -1D;
//                }
//
//                double mx = ApacheMath.cos(ApacheMath.toRadians(yaw + 90));
//                double mz = ApacheMath.sin(ApacheMath.toRadians(yaw + 90));
//
//                x = mc.thePlayer.motionX = (forward * movementSpeed * mx + strafe * movementSpeed * mz) * 0.987;
//                z = mc.thePlayer.motionZ = (forward * movementSpeed * mz - strafe * movementSpeed * mx) * 0.98;
//            }
//        }
//
//        return new MoveEvent(x, y, z);
//    }

    private void updatePoints() {
        points.clear();

        int size = pointAmount.getValue().intValue();

        for (int i = 0; i < size; i++) {
            double cos = radius.getValue().doubleValue() * ApacheMath.cos(i * (ApacheMath.PI * 2) / size);
            double sin = radius.getValue().doubleValue() * ApacheMath.sin(i * (ApacheMath.PI * 2) / size);

            double pointX = entityLivingBase.posX + cos;
            double pointZ = entityLivingBase.posZ + sin;

            Point point = new Point(new Vector2d(pointX, pointZ));

            points.add(point);
        }
    }

    private Point getBestPoint() {
        double closest = Double.MAX_VALUE;
        Point bestPoint = null;

        for (Point point : points) {
            if (point.valid) {
                final double dist = getDistanceTo(point);
                if (dist < closest) {
                    closest = dist;
                    bestPoint = point;
                }
            }
        }

        return bestPoint;
    }

    private double getDistanceTo(Point point) {
        double xDist = point.position.x - mc.thePlayer.posX;
        double zDist = point.position.y - mc.thePlayer.posZ;
        return ApacheMath.sqrt(xDist * xDist + zDist * zDist);
    }

    private float getYawTo(Point point) {
        if (point == null) return mc.thePlayer.rotationYaw;
        double xDist = point.position.x - mc.thePlayer.posX;
        double zDist = point.position.y - mc.thePlayer.posZ;
        float rotationYaw = mc.thePlayer.rotationYaw;
        float var1 = (float) (StrictMath.atan2(zDist, xDist) * 180.0D / StrictMath.PI) - 90.0F;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }

    private void drawCircle(Entity entity, float lineWidth, double radius) {
        GlStateManager.pushAttribAndMatrix();
        GL11.glPushMatrix();
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);

        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX)
                * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosX;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY)
                * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosY;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ)
                * mc.timer.renderPartialTicks - mc.getRenderManager().viewerPosZ;

        GL11.glPushMatrix();
        GL11.glLineWidth(lineWidth);
//        GL11.glBegin(GL11.GL_LINE_STRIP);

//        for (int i = 0; i <= 90; ++i) {
//            ColorUtils.glColor(Color.WHITE);
//            double div = pointAmount.getValue().intValue();
//
//            GL11.glVertex3d(posX + radius * ApacheMath.cos((double) i * ApacheMath.PI * 2 / div),
//                    posY, posZ + radius * ApacheMath.sin((double) i * ApacheMath.PI * 2 / div));
//        }

        Cylinder c = new Cylinder();
        GL11.glTranslated(posX, posY, posZ);
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        GlStateManager.resetColor();
        ColorUtils.glColor(Color.WHITE);
        c.draw((float) (radius + 0.25), (float) (radius + 0.25), 0.0f, pointAmount.getValue().intValue(), 0);
        c.draw((float) (radius + 0.25), (float) (radius + 0.25), 0.0f, pointAmount.getValue().intValue(), 0);

//        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        mc.entityRenderer.enableLightmap();
        GL11.glPopMatrix();
        GlStateManager.popAttribAndMatrix();
    }

    public static class Point {
        Vector2d position;
        boolean valid;

        public Point(Vector2d position) {
            this.position = position;
            this.valid = isPointValid(position);
        }

        private boolean isPointValid(Vector2d position) {
            Vec3 pointVec = new Vec3(position.x, mc.thePlayer.posY, position.y);
            IBlockState blockState = mc.theWorld.getBlockState(new BlockPos(pointVec));

            boolean canBeSeen = mc.theWorld.rayTraceBlocks(mc.thePlayer.getPositionVector(), pointVec,
                    false, true, false) == null;

            boolean isAboveVoid = isBlockUnder(position.x, position.y, 5);

            return !blockState.getBlock().isFullBlock() && canBeSeen && (!isAboveVoid || Client.getInstance().getModuleManager().getByClass(FlightModule.class).getData().isEnabled());
        }

        private boolean isBlockUnder(double posX, double posZ, double height) {
            for (int i = (int) mc.thePlayer.posY; i >= mc.thePlayer.posY - height; i--) {
                if (!(mc.theWorld.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof BlockAir)) {
                    return false;
                }
            }
            return true;
        }
    }
}
