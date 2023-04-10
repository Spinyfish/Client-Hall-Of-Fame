package crispy.features.hacks.impl.render;

import crispy.features.event.Event;
import crispy.features.event.impl.player.EventPacket;
import crispy.features.event.impl.player.EventUpdate;
import crispy.features.event.impl.render.Event3D;
import crispy.features.hacks.Category;
import crispy.features.hacks.Hack;
import crispy.features.hacks.HackInfo;
import crispy.util.render.RenderUtils;
import crispy.util.render.gui.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import sun.util.locale.provider.TimeZoneNameUtility;

import java.util.ArrayList;

@HackInfo(name = "BackTrace", category = Category.RENDER)
public class BackTrace extends Hack {

    private final ArrayList<Vec3> locations = new ArrayList<>();
    private EntityPlayer player, lastPlayer;
    private long lastAttacked;


    @Override
    public void onEvent(Event e) {
        if(e instanceof Event3D) {
            synchronized (locations) {
                if(Math.abs(System.currentTimeMillis() - lastAttacked) <= 1000) {
                    GL11.glPushMatrix();
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glEnable(GL11.GL_LINE_SMOOTH);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    mc.entityRenderer.disableLightmap();
                    GL11.glBegin(GL11.GL_LINE_STRIP);
                    RenderUtil.glColor(color);
                    double renderPosX = mc.getRenderManager().viewerPosX;
                    double renderPosY = mc.getRenderManager().viewerPosY;
                    double renderPosZ = mc.getRenderManager().viewerPosZ;

                    for (Vec3 pos : locations) {

                        GL11.glVertex3d(pos.getXCoord() - renderPosX, pos.getYCoord() - renderPosY, pos.getZCoord() - renderPosZ);

                    }
                    GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
                    GL11.glEnd();
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glDisable(GL11.GL_LINE_SMOOTH);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glPopMatrix();
                }
            }
        } else if(e instanceof EventPacket) {
            Packet packet = ((EventPacket) e).getPacket();
            if(packet instanceof C02PacketUseEntity) {
                C02PacketUseEntity c02PacketUseEntity = (C02PacketUseEntity) packet;
                if(c02PacketUseEntity.getAction() == C02PacketUseEntity.Action.ATTACK) {
                    Entity entity = c02PacketUseEntity.getEntityFromWorld(Minecraft.theWorld);
                    if(entity instanceof EntityPlayer) {
                        player = (EntityPlayer) entity;
                        if(player != lastPlayer) {
                            locations.clear();
                        }
                        lastAttacked = System.currentTimeMillis();
                        lastPlayer = player;
                    }
                }
            }
        } else if(e instanceof EventUpdate) {
            if(player != null) {
                synchronized (locations) {
                    locations.add(new Vec3(player.posX, player.getEntityBoundingBox().minY, player.posZ));
                }
                if (locations.size() > 20) {
                    locations.remove(0);
                }
            }
        }
    }
}
