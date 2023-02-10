/**
 * @project Myth
 * @author CodeMan
 * @at 06.08.22, 17:30
 */
package dev.myth.features.combat;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.event.EventState;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.MovementUtil;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.events.MoveEvent;
import dev.myth.events.Render3DEvent;
import dev.myth.events.UpdateEvent;
import dev.myth.features.movement.FlightFeature;
import dev.myth.features.movement.SpeedFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.FeatureManager;
import dev.myth.settings.BooleanSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import lombok.Getter;
import net.minecraft.block.BlockAir;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import javax.vecmath.Vector2d;
import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

@Feature.Info(name = "TargetStrafe", category = Feature.Category.COMBAT)
public class TargetStrafeFeature extends Feature {

    private Entity entityLivingBase;
    private double moveDirection = 1.0D;
    @Getter private static boolean strafing;

    private Point currentPoint;
    private int currentIndex = 0;
    private ArrayList<Point> points = new ArrayList<>(), lastPoints = new ArrayList<>();

    public EnumSetting<RenderMode> renderValue = new EnumSetting<>("Render Mode", RenderMode.CIRCLE);
    public EnumSetting<PathMode> pathMode = new EnumSetting<>("Path Mode", PathMode.NORMAL);

    public NumberSetting lineWidth = new NumberSetting("Line Width", 1.8, 0.1, 3, 0.1);
    public NumberSetting radius = new NumberSetting("Radius", 1.6, 0, 3, 0.1).setSuffix("m");
    public NumberSetting pointAmount = new NumberSetting("Points", 11, 2, 48, 1);
    public NumberSetting slowdown = new NumberSetting("Slowdown",  0, 0, 100, 1).setSuffix("%").addValueAlias(0, "Off");

    public BooleanSetting pressSpaceOnly = new BooleanSetting("Press Space Only", false);
    public BooleanSetting always = new BooleanSetting("Always", false);
    public BooleanSetting checkVoid = new BooleanSetting("Check Void", false);

    @Handler
    public final Listener<UpdateEvent> updateEventListener = event -> {
        if (event.getState() == EventState.PRE) {

            if (MC.thePlayer == null || MC.theWorld == null) return;

            boolean newTarget = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(KillAuraFeature.class).target != entityLivingBase;
            if (newTarget) {
                this.entityLivingBase = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(KillAuraFeature.class).target;
                lastPoints = new ArrayList<>(points);
            }

            if (entityLivingBase != null) {
                updatePoints();
                if(currentIndex > points.size() - 1) {
                    currentIndex = points.size() - 1 - currentIndex;
                }
                if(currentIndex < 0) {
                    currentIndex = points.size() + currentIndex;
                }
                if(currentIndex < 0 || currentIndex > points.size() - 1) {
                    currentPoint = points.get(0);
                } else {
                    currentPoint = points.get(currentIndex);
                }

                boolean switchedDir = false;
                if (MC.gameSettings.keyBindLeft.isKeyDown()) {
                    moveDirection = 1;
                    switchedDir = true;
                }
                if (MC.gameSettings.keyBindRight.isKeyDown()) {
                    moveDirection = -1;
                    switchedDir = true;
                }
                if (currentPoint == null || newTarget || !shouldStrafe()) {
                    currentPoint = getBestPoint();
                    currentIndex = points.indexOf(currentPoint);
                } else {
                    if ((this.checkVoid.getValue() && !MovementUtil.isBlockUnder()) || getPlayer().isCollidedHorizontally) {
                        this.moveDirection = -this.moveDirection;
                        switchedDir = true;
                    }
                    int j = 0;
                    while ((getDistanceTo(currentPoint) < MovementUtil.getSpeedDistance() || switchedDir) && j < points.size() - 1) {
                        switchedDir = false;
                        currentIndex += moveDirection;
                        if (currentIndex < 0) currentIndex = points.size() - 1;
                        if (currentIndex > points.size() - 1) currentIndex = 0;
                        if (!points.get(currentIndex).valid) {
                            this.moveDirection = -this.moveDirection;
                            switchedDir = true;
                        } else {
                            currentPoint = points.get(currentIndex);
                        }
                        j++;
                    }
                    if(j >= points.size() - 1) {
//                        doLog("No valid points found!");
                        currentPoint = null;
                    }
                }
            }

        }
    };

    @Handler
    public final Listener<Render3DEvent> render3DEventListener = event -> {
        FeatureManager fm = ClientMain.INSTANCE.manager.getManager(FeatureManager.class);
        if (entityLivingBase != null && fm.getFeature(KillAuraFeature.class).isEnabled()) {
            if (renderValue.is(RenderMode.CIRCLE)) {
                this.drawCircle(this.entityLivingBase, this.lineWidth.getValue().floatValue() + 2, this.radius.getValue().floatValue() - 0.3, Color.BLACK);
                this.drawCircle(this.entityLivingBase, this.lineWidth.getValue().floatValue(), this.radius.getValue().floatValue() - 0.3, shouldStrafe() ? ColorUtil.GREEN : Color.WHITE);
            }

            if (renderValue.is(RenderMode.POINTS)) {

                if(points.isEmpty() || lastPoints.isEmpty() || lastPoints.size() != points.size()) return;

                double[] position = RenderUtil.getInterpolatedPosition(entityLivingBase);

                glPushMatrix();
                glDisable(GL_TEXTURE_2D);
                glDisable(GL_DEPTH_TEST);
                glEnable(GL_POINT_SMOOTH);
                glDepthMask(false);
                glLineWidth(lineWidth.getValue().floatValue());
                glBegin(GL_LINE_LOOP);
                for (int i = 0; i < points.size(); i++) {
                    Point point = points.get(i);
                    Point pointBefore = lastPoints.get(i);
                    ColorUtil.doColor(point == currentPoint ? ColorUtil.GREEN.getRGB() : !point.valid ? Color.RED.getRGB() : ColorUtil.GRAY_29.getRGB());
                    glVertex3d(point.position.x - MC.getRenderManager().viewerPosX + (point.position.x - pointBefore.position.x) * event.getPartialTicks(), position[1], point.position.y - MC.getRenderManager().viewerPosZ + (point.position.y - pointBefore.position.y) * event.getPartialTicks());
                }
                glVertex3d(points.get(0).position.x - MC.getRenderManager().viewerPosX + (points.get(0).position.x - lastPoints.get(0).position.x) * event.getPartialTicks(), position[1], points.get(0).position.y - MC.getRenderManager().viewerPosZ + (points.get(0).position.y - lastPoints.get(0).position.y) * event.getPartialTicks());
                glEnd();
                glDepthMask(true);
                glDisable(GL_POINT_SMOOTH);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_TEXTURE_2D);
                glPopMatrix();

            }
        }
    };

    @Override
    public String getSuffix() {
        return pathMode.getValue().toString();
    }

    public boolean shouldStrafe() {

        if(currentPoint == null || !currentPoint.valid) return false;

        if (MC.gameSettings.keyBindLeft.isKeyDown() || MC.gameSettings.keyBindRight.isKeyDown()) {
            return false;
        }
        FeatureManager fm = ClientMain.INSTANCE.manager.getManager(FeatureManager.class);
        return (always.getValue() || (fm.getFeature(SpeedFeature.class).isEnabled() || fm.getFeature(FlightFeature.class).isEnabled()))
                && (!this.pressSpaceOnly.getValue() || getGameSettings().isKeyDown(getGameSettings().keyBindJump))
                && this.entityLivingBase != null && fm.getFeature(KillAuraFeature.class).isEnabled();
    }

    public MoveEvent editMovement(double x, double y, double z) {
        double movementSpeed = Math.sqrt(x * x + z * z) * (1 - slowdown.getValue() / 100);

        if (shouldStrafe()) {
            if (!MovementUtil.isInLiquid() && !MC.thePlayer.isOnLadder()) {
                strafing = true;

                float yaw = getYawTo(currentPoint);

                yaw = MovementUtil.getDirection(1, 0, yaw);

                x = -Math.sin(Math.toRadians(yaw)) * movementSpeed;
                z = Math.cos(Math.toRadians(yaw)) * movementSpeed;
            }
        }

        return new MoveEvent(x, y, z);
    }


    private void updatePoints() {
        lastPoints = new ArrayList<>(points);
        points.clear();

        int size = pointAmount.getValue().intValue();

        for (int i = 0; i < size; i++) {
            double cos = radius.getValue() * Math.cos(i * (Math.PI * 2) / size);
            double sin = radius.getValue() * Math.sin(i * (Math.PI * 2) / size);

            double pointX = entityLivingBase.posX + cos;
            double pointZ = entityLivingBase.posZ + sin;

            Point point = new Point(new Vector2d(pointX, pointZ));

            if(pathMode.is(PathMode.ADAPTIVE)) {
                double funny = 0.33;
                double dist = 0;

                if(!point.valid) {
                    point = new Point(new Vector2d(entityLivingBase.posX - cos * funny, entityLivingBase.posZ - sin * funny));
                }

                int j = 0;
                while (!point.valid && (dist = j * funny) < 2) {
                    pointX = entityLivingBase.posX + cos * dist;
                    pointZ = entityLivingBase.posZ + sin * dist;
                    point = new Point(new Vector2d(pointX, pointZ));
                    j++;
                }
            }

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
        double xDist = point.position.x - MC.thePlayer.posX;
        double zDist = point.position.y - MC.thePlayer.posZ;
        return Math.sqrt(xDist * xDist + zDist * zDist);
    }

    private float getYawTo(Point point) {
        if (point == null) return MC.thePlayer.rotationYaw;
        double xDist = point.position.x - MC.thePlayer.posX;
        double zDist = point.position.y - MC.thePlayer.posZ;
        float rotationYaw = MC.thePlayer.rotationYaw;
        float var1 = (float) (StrictMath.atan2(zDist, xDist) * 180.0D / StrictMath.PI) - 90.0F;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }

    private void drawCircle(Entity entity, float lineWidth, double radius, Color color) {
        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GL11.glPushMatrix();
        MC.entityRenderer.disableLightmap();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);

        double[] position = RenderUtil.getInterpolatedPosition(entity);

        GL11.glPushMatrix();
        GL11.glLineWidth(lineWidth);
        Cylinder c = new Cylinder();
        GL11.glTranslated(position[0], position[1], position[2]);
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        c.setDrawStyle(100011);
        GlStateManager.resetColor();
        float f = (color.getRGB() >> 24 & 0xFF) / 255.0F;
        float f1 = (color.getRGB() >> 16 & 0xFF) / 255.0F;
        float f2 = (color.getRGB() >> 8 & 0xFF) / 255.0F;
        float f3 = (color.getRGB() & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
        c.draw((float) (radius + 0.25), (float) (radius + 0.25), 0.0f, pointAmount.getValue().intValue(), 0);
        c.draw((float) (radius + 0.25), (float) (radius + 0.25), 0.0f, pointAmount.getValue().intValue(), 0);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        MC.entityRenderer.enableLightmap();
        GL11.glPopMatrix();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public enum RenderMode {
        NONE("None"),
        CIRCLE("Circle"),
        POINTS("Points");

        private final String name;

        RenderMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum PathMode {
        NORMAL("Normal"),
        ADAPTIVE("Adaptive");

        private final String name;

        PathMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class Point {
        Vector2d position;
        boolean valid;

        public Point(Vector2d position) {
            this.position = position;
            this.valid = isPointValid(position);
        }

        private boolean isPointValid(Vector2d position) {
            Vec3 pointVec = new Vec3(position.x, MC.thePlayer.posY, position.y);
            IBlockState blockState = MC.theWorld.getBlockState(new BlockPos(pointVec));

            boolean canBeSeen = MC.theWorld.rayTraceBlocks(MC.thePlayer.getPositionVector(), pointVec,
                    false, true, false) == null;

            boolean isAboveVoid = isBlockUnder(position.x, position.y, 3);

            boolean doesPlayerFit = MC.theWorld.getCollidingBoundingBoxes(MC.thePlayer, MC.thePlayer.getEntityBoundingBox().expand(0.3, 0, 0.3).offset(-MC.thePlayer.posX + position.x, 0, -MC.thePlayer.posZ + position.y)).isEmpty();

            return !blockState.getBlock().isFullBlock() && doesPlayerFit && canBeSeen
                    /*&& (!isAboveVoid || ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(FlightFeature.class).isEnabled())*/;
        }

        private boolean isBlockUnder(double posX, double posZ, double height) {
            for (int i = (int) MC.thePlayer.posY; i >= MC.thePlayer.posY - height; i--) {
                if (!(MC.theWorld.getBlockState(new BlockPos(posX, i, posZ)).getBlock() instanceof BlockAir)) {
                    return false;
                }
            }
            return true;
        }
    }
}
