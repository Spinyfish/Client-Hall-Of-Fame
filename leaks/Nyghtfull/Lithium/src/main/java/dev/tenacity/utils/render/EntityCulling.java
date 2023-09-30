package dev.tenacity.utils.render;

import dev.tenacity.Tenacity;
import dev.tenacity.event.impl.game.RenderTickEvent;
import dev.tenacity.event.impl.game.world.TickEvent;
import dev.tenacity.event.impl.render.RendererLivingEntityEvent;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.impl.ModeSetting;
import dev.tenacity.module.settings.impl.NumberSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author cedo
 * @since 03/29/2022
 */
public class EntityCulling extends Module {

    private static final NumberSetting cullingDelay = new NumberSetting("Culling Delay", 2, 3, 1, 1);
    private static final ModeSetting cullingMode = new ModeSetting("Culling Mode", "Grouped", "Grouped", "Custom");
    private static final NumberSetting entityCullingDis = new NumberSetting("Culling Distance", 45, 150, 10, 1);
    private static final NumberSetting mobCullingDis = new NumberSetting("Mob Cull Distance", 40, 150, 10, 1);
    private static final NumberSetting playerCullingDis = new NumberSetting("Player Cull Distance", 45, 150, 10, 1);
    private static final NumberSetting passiveCullingDis = new NumberSetting("Passive Cull Distance", 30, 150, 10, 1);

    public EntityCulling() {
        super("Entity Culling", Category.RENDER, "Culls entities that are out of range");
        entityCullingDis.addParent(cullingMode, modeSetting -> modeSetting.is("Grouped"));
        playerCullingDis.addParent(cullingMode, modeSetting -> modeSetting.is("Custom"));
        mobCullingDis.addParent(cullingMode, modeSetting -> modeSetting.is("Custom"));
        passiveCullingDis.addParent(cullingMode, modeSetting -> modeSetting.is("Custom"));
        addSettings(cullingDelay, cullingMode, entityCullingDis, playerCullingDis, mobCullingDis, passiveCullingDis);
    }

    private static final RenderManager renderManager = mc.getRenderManager();
    private static final ConcurrentHashMap<UUID, OcclusionQuery> queries = new ConcurrentHashMap<>();
    private static final boolean SUPPORT_NEW_GL = GLContext.getCapabilities().OpenGL33;
    public static boolean shouldPerformCulling = false;
    private int destroyTimer;


    public static boolean renderItem(Entity stack) {
        if (!Tenacity.INSTANCE.isEnabled(EntityCulling.class)) return false;
        //needs to be called from RenderEntityItem#doRender and RenderItemFrame#doRender. Returning true means it should cancel the render event
        return shouldPerformCulling && stack.worldObj == mc.thePlayer.worldObj && checkEntity(stack);
    }


    @Override
    public void onRendererLivingEntityEvent(RendererLivingEntityEvent e) {
        if (e.isPost() || !shouldPerformCulling) return;

        //#if MC==10809
        EntityLivingBase entity = e.getEntity();
        //#else
        //$$ EntityLivingBase entity = event.getEntity();
        //#endif
        boolean armorstand = entity instanceof EntityArmorStand;
        if (entity == mc.thePlayer || entity.worldObj != mc.thePlayer.worldObj ||
               /* (armorstand && ((EntityArmorStand) entity).hasMarker()) ||*/
                (entity.isInvisibleToPlayer(mc.thePlayer))) {
            return;
        }

        if (checkEntity(entity)) {
            e.cancel();
            if (!canRenderName(entity)) {
                return;
            }

            //#if MC==10809
            double x = e.getX();
            double y = e.getY();
            double z = e.getZ();
            RendererLivingEntity<EntityLivingBase> renderer = (RendererLivingEntity<EntityLivingBase>) e.getRenderer();
            //#else
            //$$ double x = event.getX();
            //$$ double y = event.getY();
            //$$ double z = event.getZ();
            //$$ RenderLivingBase<EntityLivingBase> renderer = event.getRenderer();
            //#endif

            renderer.renderName(entity, x, y, z);
        }

        //#if MC==10809
//        EntityLivingBase entity = e.getEntity();
        //#else
        //$$ EntityLivingBase entity = event.getEntity();
        //#endif
        if (/*(entity instanceof EntityArmorStand) ||*/ (entity.isInvisible() && entity instanceof EntityPlayer)) {
            e.cancel();
        }

        if (EntityCulling.shouldPerformCulling) {
            final float entityDistance = entity.getDistanceToEntity(mc.thePlayer);

            switch (cullingMode.getMode()) {
                case "Grouped":
                    if (entityDistance > entityCullingDis.getValue()) {
                        e.cancel();
                    }
                    break;
                case "Custom":
                    if (entity instanceof IMob && entityDistance > mobCullingDis.getValue()) {
                        e.cancel();
                        //passive entity render distance
                    } else if ((entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature || entity instanceof EntityWaterMob) && entityDistance > passiveCullingDis.getValue()) {
                        e.cancel();
                        //player entity render distance
                    } else if (entity instanceof EntityPlayer && entityDistance > playerCullingDis.getValue()) {
                        e.cancel();
                    }
                    break;
            }
        }
    }


    @Override
    public void onRenderTickEvent(RenderTickEvent e) {
        if (e.isPre()) {
            mc.addScheduledTask(this::check);
        }
    }

    public static boolean canRenderName(EntityLivingBase entity) {
        final EntityPlayerSP player = mc.thePlayer;
        if (entity instanceof EntityPlayer && entity != player) {
            final Team otherEntityTeam = entity.getTeam();
            final Team playerTeam = player.getTeam();

            if (otherEntityTeam != null) {
                final Team.EnumVisible teamVisibilityRule = otherEntityTeam.getNameTagVisibility();

                switch (teamVisibilityRule) {
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return playerTeam == null || otherEntityTeam.isSameTeam(playerTeam);
                    case HIDE_FOR_OWN_TEAM:
                        return playerTeam == null || !otherEntityTeam.isSameTeam(playerTeam);
                    case ALWAYS:
                    default:
                        return true;
                }
            }
        }

        return Minecraft.isGuiEnabled()
                && entity != mc.getRenderManager().livingPlayer
                && ((entity instanceof EntityArmorStand) || !entity.isInvisibleToPlayer(player)) &&
                //#if MC==10809
                entity.riddenByEntity == null;
        //#else
        //$$ !entity.isBeingRidden();
        //#endif
    }

    @Override
    public void onTickEvent(TickEvent e) {
        if (e.isPost() || this.destroyTimer++ < 120) {
            return;
        }

        this.destroyTimer = 0;
        WorldClient theWorld = mc.theWorld;
        if (theWorld == null) {
            return;
        }

        List<UUID> remove = new ArrayList<>();
        Set<UUID> loaded = new HashSet<>();
        for (Entity entity : theWorld.loadedEntityList) {
            loaded.add(entity.getUniqueID());
        }

        for (OcclusionQuery value : queries.values()) {
            if (loaded.contains(value.uuid)) {
                continue;
            }

            remove.add(value.uuid);
            if (value.nextQuery != 0) {
                GL15.glDeleteQueries(value.nextQuery);
            }
        }

        for (UUID uuid : remove) {
            queries.remove(uuid);
        }
    }


    private void check() {
        long delay = 0;

        switch (cullingDelay.getValue().intValue() - 1) {
            case 0: {
                delay = 10;
                break;
            }
            case 1: {
                delay = 25;
                break;
            }
            case 2: {
                delay = 50;
                break;
            }
            default:
                break;

        }
        long nanoTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        for (OcclusionQuery query : queries.values()) {
            if (query.nextQuery != 0) {
                final long queryObject = GL15.glGetQueryObjecti(query.nextQuery, GL15.GL_QUERY_RESULT_AVAILABLE);
                if (queryObject != 0) {
                    query.occluded = GL15.glGetQueryObjecti(query.nextQuery, GL15.GL_QUERY_RESULT) == 0;
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

    /**
     * Used OpenGL queries in order to determine if the given is visible
     *
     * @param entity entity to check
     * @return true if the entity rendering should be skipped
     */
    private static boolean checkEntity(Entity entity) {
        OcclusionQuery query = queries.computeIfAbsent(entity.getUniqueID(), OcclusionQuery::new);
        if (query.refresh) {
            query.nextQuery = getQuery();
            query.refresh = false;
            int mode = SUPPORT_NEW_GL ? GL33.GL_ANY_SAMPLES_PASSED : GL15.GL_SAMPLES_PASSED;
            GL15.glBeginQuery(mode, query.nextQuery);
            drawSelectionBoundingBox(entity.getEntityBoundingBox()
                    .expand(.2, .2, .2)
                    .offset(-renderManager.renderPosX, -renderManager.renderPosY, -renderManager.renderPosZ)
            );
            GL15.glEndQuery(mode);
        }

        return query.occluded;
    }


    public static void drawSelectionBoundingBox(AxisAlignedBB b) {
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(false, false, false, false);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(GL11.GL_QUAD_STRIP, DefaultVertexFormats.POSITION);
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

    private static int getQuery() {
        try {
            return GL15.glGenQueries();
        } catch (Throwable throwable) {
            return 0;
        }
    }


    static class OcclusionQuery {
        private final UUID uuid; //Owner
        private int nextQuery;
        private boolean refresh = true;
        private boolean occluded;
        private long executionTime = 0;

        public OcclusionQuery(UUID uuid) {
            this.uuid = uuid;
        }
    }
}
