package Reality.Realii.mods.modules.combat;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import Reality.Realii.event.EventHandler;
import Reality.Realii.event.events.rendering.EventRender3D;
import Reality.Realii.event.events.world.EventPostUpdate;
import Reality.Realii.event.value.Numbers;
import Reality.Realii.mods.Module;
import Reality.Realii.mods.ModuleType;
import Reality.Realii.mods.modules.player.Teams;
import Reality.Realii.utils.cheats.tpaura.AStarCustomPathFinder;
import Reality.Realii.utils.cheats.world.TimerUtil;
import Reality.Realii.utils.render.RenderUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class TpAura extends Module {
    Numbers<Number> range = new Numbers("Range","Range", 50, 1, 100,1);
    Numbers<Number> delay = new Numbers("Delay","Delay", 100, 10, 1000,10);

    ArrayList<Vec3> vec3s = new ArrayList<>();

    public TpAura() {
        super("TpAura", ModuleType.Combat);

        addValues(range, delay);
    }

    TimerUtil timer = new TimerUtil();

    @EventHandler
    public void onUpdate(EventPostUpdate e) {
        if (timer.delay(delay.getValue().floatValue())) {
            if (mc.theWorld.loadedEntityList.size() == 0) {
                vec3s = new ArrayList<>();
            }
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if(Teams.isOnSameTeam(entity))
                    return;
                if (entity instanceof EntityPlayer && !entity.isDead && (mc.thePlayer.getDistanceToEntity(entity) < range.getValue().floatValue()) && (entity != mc.thePlayer)) {
                    EntityLivingBase T = (EntityLivingBase) entity;
                    Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                    vec3s = computePath(topFrom, to);
                    float n = 1;
                    for (Vec3 pathElm : vec3s) {
                        mc.thePlayer.setPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ());
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));//+ 4 - ((n + 1) >= vec3s.size() ? 1 : 0)
                        n++;
                    }
                    mc.thePlayer.swingItem();
                    mc.playerController.attackEntity(mc.thePlayer, T);
                    Collections.reverse(vec3s);
                    for (Vec3 pathElm : vec3s) {
                        mc.thePlayer.setPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ());
                    }
                }

            }

            timer.reset();
        }
    }

    @EventHandler
    public void onRender(EventRender3D e) {
        int i = 0;
        for (Vec3 v : vec3s) {
            if (i != vec3s.size() - 1) {
                Vec3 vec = vec3s.get(i);
                double x = vec.getX() - RenderManager.renderPosX;
                double y = vec.getY() - RenderManager.renderPosY;
                double z = vec.getZ() - RenderManager.renderPosZ;
                RenderUtil.drawEntityESP(x, y, z, 0.5, 1.85, 1, 1, 1, 0, 1f, 1f, 1f, 1, 1);
            }
            i++;
        }
    }

    public void drawPath(Vec3 vec, Vec3 vec2) {
        double x = vec.getX() - RenderManager.renderPosX;
        double y = vec.getY() - RenderManager.renderPosY;
        double z = vec.getZ() - RenderManager.renderPosZ;

        double x2 = vec2.getX() - RenderManager.renderPosX;
        double y2 = vec2.getY() - RenderManager.renderPosY;
        double z2 = vec2.getZ() - RenderManager.renderPosZ;
        double width = 0.3;
        double height = mc.thePlayer.getEyeHeight();
        RenderUtil.pre3D();
        GL11.glLoadIdentity();
        mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
        RenderUtil.glColor(new Color(255,0,0).getRGB());

        GL11.glLineWidth(2);

        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glVertex3d(x, y, z);
        GL11.glVertex3d(x2, y2, z2);

        GL11.glEnd();


        RenderUtil.post3D();
    }


    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        if (!canPassThrow(new BlockPos(topFrom))) {
            topFrom = topFrom.addVector(0, 1, 0);
        }

        AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();

        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<Vec3>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > 5 * 5) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }

}
