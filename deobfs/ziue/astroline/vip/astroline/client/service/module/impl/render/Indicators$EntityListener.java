/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.Vec3
 *  vip.astroline.client.service.event.impl.render.Event3D
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.service.module.impl.render;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;
import vip.astroline.client.service.event.impl.render.Event3D;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public static class Indicators.EntityListener {
    private final Map<Entity, Vec3> entityUpperBounds = Maps.newHashMap();
    private final Map<Entity, Vec3> entityLowerBounds = Maps.newHashMap();

    @EventTarget
    private void render3d(Event3D event) {
        if (!this.entityUpperBounds.isEmpty()) {
            this.entityUpperBounds.clear();
        }
        if (!this.entityLowerBounds.isEmpty()) {
            this.entityLowerBounds.clear();
        }
        Iterator iterator = Module.mc.theWorld.loadedEntityList.iterator();
        while (iterator.hasNext()) {
            Entity e = (Entity)iterator.next();
            Vec3 bound = this.getEntityRenderPosition(e);
            bound.add(new Vec3(0.0, (double)e.height + 0.2, 0.0));
            Vec3 upperBounds = RenderUtil.to2D((double)bound.xCoord, (double)bound.yCoord, (double)bound.zCoord);
            Vec3 lowerBounds = RenderUtil.to2D((double)bound.xCoord, (double)(bound.yCoord - 2.0), (double)bound.zCoord);
            if (upperBounds == null || lowerBounds == null) continue;
            this.entityUpperBounds.put(e, upperBounds);
            this.entityLowerBounds.put(e, lowerBounds);
        }
    }

    private Vec3 getEntityRenderPosition(Entity entity) {
        double partial = Module.mc.timer.renderPartialTicks;
        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partial - RenderManager.viewerPosX;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partial - RenderManager.viewerPosY;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partial - RenderManager.viewerPosZ;
        return new Vec3(x, y, z);
    }

    public Map<Entity, Vec3> getEntityLowerBounds() {
        return this.entityLowerBounds;
    }

    static /* synthetic */ void access$000(Indicators.EntityListener x0, Event3D x1) {
        x0.render3d(x1);
    }
}
