package rip.athena.client.modules.impl.fpssettings.impl;

import rip.athena.client.modules.impl.fpssettings.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.entity.*;
import net.minecraft.scoreboard.*;
import rip.athena.client.events.types.render.*;
import rip.athena.client.*;
import rip.athena.client.modules.*;
import optifine.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.*;
import rip.athena.client.events.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.*;
import java.util.function.*;
import java.util.concurrent.*;
import rip.athena.client.events.types.client.*;
import net.minecraft.client.multiplayer.*;
import java.util.*;
import org.lwjgl.opengl.*;

public class EntityCulling
{
    public static boolean shouldPerformCulling;
    private static final ConcurrentHashMap<UUID, OcclusionQuery> queries;
    private static final boolean SUPPORT_NEW_GL;
    private static OptimizerMod settings;
    
    public static boolean canRenderName(final EntityLivingBase entity) {
        final EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (entity instanceof EntityPlayer && entity != player) {
            final Team otherEntityTeam = entity.getTeam();
            final Team playerTeam = player.getTeam();
            if (otherEntityTeam != null) {
                final Team.EnumVisible teamVisibilityRule = otherEntityTeam.getNameTagVisibility();
                switch (teamVisibilityRule) {
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return playerTeam == null || otherEntityTeam.isSameTeam(playerTeam);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return playerTeam == null || !otherEntityTeam.isSameTeam(playerTeam);
                    }
                    default: {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @SubscribeEvent
    public void shouldRenderEntity(final RenderEntityEvent<EntityLivingBase> event) {
        if (EntityCulling.settings == null) {
            EntityCulling.settings = (OptimizerMod)Athena.INSTANCE.getModuleManager().get(OptimizerMod.class);
        }
        if (EntityCulling.settings.SMART_ENTITY_CULLING && Config.isShaders()) {
            return;
        }
        if (!EntityCulling.settings.ENTITY_CULLING || !EntityCulling.shouldPerformCulling) {
            return;
        }
        final EntityLivingBase entity = event.getEntity();
        if (entity.isMobSpawner) {
            return;
        }
        final boolean armorstand = entity instanceof EntityArmorStand;
        if (entity == Minecraft.getMinecraft().thePlayer || entity.worldObj != Minecraft.getMinecraft().thePlayer.worldObj || (entity.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer) && !armorstand)) {
            return;
        }
        if (checkEntity(entity)) {
            event.setCancelled(true);
            if (!canRenderName(entity)) {
                return;
            }
            if ((EntityCulling.settings.DONT_CULL_PLAYER_NAMETAGS && entity instanceof EntityPlayer) || (EntityCulling.settings.DONT_CULL_ENTITY_NAMETAGS && !armorstand) || (EntityCulling.settings.DONT_CULL_ARMOR_STANDS_NAMETAGS && armorstand)) {
                event.getRenderer().renderName(entity, event.getX(), event.getY(), event.getZ());
            }
        }
    }
    
    public static void drawSelectionBoundingBox(final AxisAlignedBB b) {
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(false, false, false, false);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(8, DefaultVertexFormats.POSITION);
        worldrenderer.pos(b.maxX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.maxY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.maxZ).endVertex();
        worldrenderer.pos(b.minX, b.minY, b.minZ).endVertex();
        worldrenderer.pos(b.maxX, b.minY, b.minZ).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.enableAlpha();
    }
    
    private static boolean checkEntity(final Entity entity) {
        final OcclusionQuery query = EntityCulling.queries.computeIfAbsent(entity.getUniqueID(), OcclusionQuery::new);
        if (query.refresh) {
            query.nextQuery = getQuery();
            query.refresh = false;
            GlStateManager.pushMatrix();
            GlStateManager.translate(-Minecraft.getMinecraft().getRenderManager().renderPosX, -Minecraft.getMinecraft().getRenderManager().renderPosY, -Minecraft.getMinecraft().getRenderManager().renderPosZ);
            final int mode = EntityCulling.SUPPORT_NEW_GL ? 35887 : 35092;
            GL15.glBeginQuery(mode, query.nextQuery);
            drawSelectionBoundingBox(entity.getEntityBoundingBox().expand(0.2, 0.2, 0.2));
            GL15.glEndQuery(mode);
            GlStateManager.popMatrix();
        }
        return query.occluded;
    }
    
    private static int getQuery() {
        try {
            return GL15.glGenQueries();
        }
        catch (Throwable throwable) {
            Athena.INSTANCE.getLog().error("Failed to run GL15.glGenQueries(). User's computer is likely too old to support OpenGL 1.5, Entity Culling has been force disabled." + throwable);
            EntityCulling.settings.ENTITY_CULLING = false;
            Athena.INSTANCE.getLog().info("Entity Culling has forcefully been disabled as your computer is too old and does not support the technology behind it.\nIf you believe this is a mistake, please contact us at discord.gg/sk1er");
            return 0;
        }
    }
    
    private void check() {
        if (EntityCulling.settings == null) {
            EntityCulling.settings = (OptimizerMod)Athena.INSTANCE.getModuleManager().get(OptimizerMod.class);
        }
        long delay = 0L;
        switch (EntityCulling.settings.ENTITY_CULLING_INTERVAL) {
            case 0: {
                delay = 50L;
                break;
            }
            case 1: {
                delay = 25L;
                break;
            }
            case 2: {
                delay = 10L;
                break;
            }
        }
        final long nanoTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        for (final OcclusionQuery query : EntityCulling.queries.values()) {
            if (query.nextQuery != 0) {
                final long queryObject = GL15.glGetQueryObjecti(query.nextQuery, 34919);
                if (queryObject != 0L) {
                    query.occluded = (GL15.glGetQueryObjecti(query.nextQuery, 34918) == 0);
                    GL15.glDeleteQueries(query.nextQuery);
                    query.nextQuery = 0;
                }
            }
            if (query.nextQuery == 0 && nanoTime - query.executionTime > delay) {
                query.executionTime = nanoTime;
                query.refresh = true;
            }
        }
    }
    
    @SubscribeEvent
    public void onTick(final ClientTickEvent event) {
        final WorldClient theWorld = Minecraft.getMinecraft().theWorld;
        if (theWorld == null) {
            return;
        }
        final List<UUID> remove = new ArrayList<UUID>();
    Label_0033:
        for (final OcclusionQuery value : EntityCulling.queries.values()) {
            for (final Entity entity : theWorld.loadedEntityList) {
                if (entity.getUniqueID() == value.uuid) {
                    continue Label_0033;
                }
            }
            remove.add(value.uuid);
            if (value.nextQuery != 0) {
                GL15.glDeleteQueries(value.nextQuery);
            }
        }
        for (final UUID uuid : remove) {
            EntityCulling.queries.remove(uuid);
        }
        Minecraft.getMinecraft().addScheduledTask(this::check);
    }
    
    static {
        EntityCulling.shouldPerformCulling = false;
        queries = new ConcurrentHashMap<UUID, OcclusionQuery>();
        SUPPORT_NEW_GL = GLContext.getCapabilities().OpenGL33;
    }
    
    static class OcclusionQuery
    {
        private final UUID uuid;
        private int nextQuery;
        private boolean refresh;
        private boolean occluded;
        private long executionTime;
        
        public OcclusionQuery(final UUID uuid) {
            this.refresh = true;
            this.executionTime = 0L;
            this.uuid = uuid;
        }
        
        public UUID getUuid() {
            return this.uuid;
        }
    }
}
