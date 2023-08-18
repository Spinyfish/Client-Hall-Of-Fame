package me.jinthium.straight.impl.components;

import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.component.Component;
import me.jinthium.straight.impl.event.render.Render2DEvent;
import me.jinthium.straight.impl.utils.Multithreading;
import me.jinthium.straight.impl.utils.misc.TimerUtil;
import me.jinthium.straight.impl.utils.vector.Vector3d;
import me.jinthium.straight.impl.utils.vector.Vector4d;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjglx.opengl.Display;
import org.lwjglx.util.glu.GLU;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectionComponent extends Component {

    public static ConcurrentMap<Entity, Vector4d> finalisedProjections = new ConcurrentHashMap<>();
    public static final ConcurrentMap<Entity, Vector4d> concurrentProjections = new ConcurrentHashMap<>();
    public static boolean progress;
    public static TimerUtil stopWatch = new TimerUtil();

    @Callback
    final EventCallback<Render2DEvent> render2DEventEventCallback = event -> {
        if (mc.theWorld == null)
            return;

        Multithreading.runAsync(() -> {

            if (progress && !stopWatch.hasTimeElapsed(1000)) {
                return;
            }

            stopWatch.reset();

            progress = true;

            final double renderX = mc.getRenderManager().renderPosX;
            final double renderY = mc.getRenderManager().renderPosY;
            final double renderZ = mc.getRenderManager().renderPosZ;
            final int factor = event.getScaledResolution().getScaleFactor();
            final float partialTicks = event.getPartialTicks();

            for (Entity entity : mc.theWorld.loadedEntityList) {
                try {
                        try {
                            final double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderX;
                            final double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks) - renderY;
                            final double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderZ;
                            final double width = (entity.width + 0.2) / 2;
                            final double height = entity.height + (entity.isSneaking() ? -0.3D : 0.2D) + 0.05;
                            final AxisAlignedBB aabb = new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width);
                            final List<Vector3d> vectors = Arrays.asList(new Vector3d(aabb.minX, aabb.minY, aabb.minZ), new Vector3d(aabb.minX, aabb.maxY, aabb.minZ), new Vector3d(aabb.maxX, aabb.minY, aabb.minZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.minZ), new Vector3d(aabb.minX, aabb.minY, aabb.maxZ), new Vector3d(aabb.minX, aabb.maxY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.minY, aabb.maxZ), new Vector3d(aabb.maxX, aabb.maxY, aabb.maxZ));

                            Vector4d position = null;
                            for (Vector3d vector : vectors) {
                                vector = project(factor, vector.getX(), vector.getY(), vector.getZ());

                                if (vector != null && vector.getZ() >= 0.0D && vector.getZ() < 1.0D) {
                                    if (position == null) {
                                        position = new Vector4d(vector.getX(), vector.getY(), vector.getZ(), 0.0D);
                                    }

                                    position = new Vector4d(Math.min(vector.getX(), position.x), Math.min(vector.getY(), position.y), Math.max(vector.getX(), position.z), Math.max(vector.getY(), position.w));
                                }
                            }

                            // create a new triplet with the new vector
                            concurrentProjections.put(entity, position);
                        } catch (Exception ignored) {
                            concurrentProjections.remove(entity);
                        }
                } catch (Exception ignored) {
                    concurrentProjections.remove(entity);
                }
            }

            progress = false;
            finalisedProjections = concurrentProjections;
        });
    };

    public static Vector4d get(Entity entity) {
        if (entity == null) return null;

        return finalisedProjections.get(entity);
    }

    private Vector3d project(final int factor, final double x, final double y, final double z) {
        if (GLU.gluProject((float) x, (float) y, (float) z, ActiveRenderInfo.MODELVIEW, ActiveRenderInfo.PROJECTION, ActiveRenderInfo.VIEWPORT, ActiveRenderInfo.OBJECTCOORDS)) {
            return new Vector3d((ActiveRenderInfo.OBJECTCOORDS.get(0) / factor), ((Display.getHeight() - ActiveRenderInfo.OBJECTCOORDS.get(1)) / factor), ActiveRenderInfo.OBJECTCOORDS.get(2));
        }

        return null;
    }
}