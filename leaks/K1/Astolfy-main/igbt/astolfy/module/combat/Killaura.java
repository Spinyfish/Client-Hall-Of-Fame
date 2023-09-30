package igbt.astolfy.module.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import igbt.astolfy.events.listeners.EventRender2D;
import igbt.astolfy.module.visuals.Hud;
import igbt.astolfy.ui.inGame.GuiElement.GuiElement;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import igbt.astolfy.Astolfy;
import igbt.astolfy.events.Event;
import igbt.astolfy.events.EventType;
import igbt.astolfy.events.listeners.EventMotion;
import igbt.astolfy.events.listeners.EventPacket;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.module.exploit.Disabler;
import igbt.astolfy.settings.settings.ModeSetting;
import igbt.astolfy.settings.settings.NumberSetting;
import igbt.astolfy.utils.EntityUtils;
import igbt.astolfy.utils.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

public class Killaura extends ModuleBase {

    public NumberSetting cps = new NumberSetting("CPS", 9.5, 0.25, 1, 15);
    public NumberSetting reach = new NumberSetting("Reach", 4.2, 0.1, 2, 6);

    public Killaura() {
        super("Killaura", Keyboard.KEY_X, Category.COMBAT);
        addSettings(cps, reach);
    }

    private TimerUtils timer = new TimerUtils();
    public static Entity currentTarget;
    public Entity lastTarget;
    public TimerUtils healthTimer = new TimerUtils();
    public double sliderTargetHealth = 0;
    public GuiElement targetHudElement = new GuiElement("TargetHUD", this, mc.displayWidth / 2, mc.displayHeight / 2, 150, 95);

    public void onEvent(Event baseEvent) {
        if (Astolfy.i.moduleManager.getModuleByName("Scaffold").isToggled())
            return;
        if (baseEvent instanceof EventPacket) {
            EventPacket event = (EventPacket) baseEvent;
            if (event.getPacket() instanceof C0BPacketEntityAction) {

            }
        }
        if (baseEvent instanceof EventRender2D) {
            if (currentTarget != null)
                renderTargetHud((EntityLivingBase) currentTarget);
            else if (mc.currentScreen instanceof GuiChat) {
                renderTargetHud(mc.thePlayer);
            } else {
                targetHudElement.setHidden(true);
            }
        }
        if (baseEvent instanceof EventMotion && ((EventMotion) baseEvent).eventType.equals(EventType.PRE) && mc.thePlayer.ticksExisted > 40) {
            if (mc.thePlayer.isBlocking()) {
                mc.gameSettings.keyBindUseItem.pressed = false;
            }
            setSuffix(Math.floor(cps.getValue()) + " | " + Math.floor(reach.getValue()));

            EventMotion event = (EventMotion) baseEvent;

            double x = event.getX();
            double y = event.getY();
            double z = event.getZ();

            CopyOnWriteArrayList<Entity> ent = AntiBot.getEntities();
            Entity target = getMainEntity(EntityUtils.distanceSort(ent));
            if (target != null) {
                float[] rots = ncpRotations(target, event);
                if (target.getDistance(x, y, z) <= (reach.getValue() + 1)) {
                    currentTarget = target;

                    event.yaw = rots[0];
                    event.pitch = rots[1];
                } else {
                    currentTarget = null;
                }
                if(lastTarget != target){
                    timer.reset();
                    lastTarget = target;
                }
                if (target.getDistance(x, y, z) <= reach.getValue() && timer.hasReached(1000 / (cps.getValue() + (Math.random() * 5)))) {
                    event.yaw = rots[0];
                    event.pitch = rots[1];
                    mc.thePlayer.swingItem();
                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                    timer.reset();
                }
            } else {
                currentTarget = null;
            }
        }
    }

    private void renderTargetHud(EntityLivingBase currentTarget) {
        targetHudElement.setHidden(false);
        targetHudElement.renderStart();

        if (currentTarget instanceof EntityPlayer) {
            targetHudElement.setWidth(150);
            targetHudElement.setHeight(50);
            Gui.drawRect(0, 0, 150, 50, 0x80000000);
          //  Gui.drawRect(10, 10, 50, 50, -1);
            if (mc.getNetHandler() != null && currentTarget.getUniqueID() != null) {
                NetworkPlayerInfo i = mc.getNetHandler().getPlayerInfo(currentTarget.getUniqueID());
                if (i != null) {
                    mc.getTextureManager().bindTexture(i.getLocationSkin());
                    GlStateManager.color(1, 1, 1);
                    GL11.glEnable(GL11.GL_BLEND);
                    Gui.drawModalRectWithCustomSizedTexture(5.0, 5.0, (float) (50f/1.25), (float) (50f/1.25), (int) (50/1.25), (int) (50/1.25), (float) (400/1.25), (float) (400 / 1.25));
                    GL11.glDisable(GL11.GL_BLEND);
                    //Gui.drawImage(mc, 5,10,45,i.getLocationSkin());
                    //if (((EntityPlayer) currentTarget).isWearing(EnumPlayerModelParts.HAT)) {
                    //    Gui.drawImage(mc, 5,10,45,i.getLocationSkin());
                    //}
                }
            }
            if(currentTarget.getName().length() >= 2) {
                mc.customFont.drawString(currentTarget.getName().substring(0, 1), 50, 10, Hud.getColor(10));
                mc.customFont.drawString(currentTarget.getName().substring(1), 50 + mc.customFont.getStringWidth(currentTarget.getName().substring(0, 1)), 10, -1);
            }
            mc.customFont.drawString(""+Math.floor(currentTarget.getHealth()) /2 + "\2477HP", 50, 25, Hud.getColor(20));
        }

        targetHudElement.renderEnd();
    }

    public Entity getMainEntity(CopyOnWriteArrayList<Entity> entities) {
        if (entities.size() > 0)
            for (Entity ent : entities) {
                if (ent != mc.thePlayer && ent instanceof EntityLivingBase && ent.isEntityAlive()) {
                    if (ent instanceof EntityPlayer) {
                        return ent;
                    }

                    //Delete Line Below When Finished
                    return ent;
                }
            }

        return null;
    }

    /*
     * From Old Client <3
     */
    public static float[] ncpRotations(Entity e, EventMotion p) {
        double x = e.posX + (e.posX - e.lastTickPosX) - p.getX();
        double y = (e.posY + e.getEyeHeight()) - (p.getY() + mc.thePlayer.getEyeHeight()) - 0.1;
        double z = e.posZ + (e.posZ - e.lastTickPosZ) - p.getZ();
        double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(x / z));
        float pitch = (float) -Math.toDegrees(Math.atan(y / dist));

        if (x < 0 && z < 0)
            yaw = 90 + (float) Math.toDegrees(Math.atan(z / x));
        else if (x > 0 && z < 0)
            yaw = -90 + (float) Math.toDegrees(Math.atan(z / x));

        yaw += Math.random() * 5 - Math.random();
        pitch += Math.random() * 5 - Math.random();

        if (pitch > 90)
            pitch = 90;
        if (pitch < -90)
            pitch = -90;
        if (yaw > 180)
            yaw = 180;
        if (yaw < -180)
            yaw = -180;

        return new float[]{yaw, pitch};
    }

    public static float[] ncpRotations(Entity e, double x1, double y1, double z1) {
        double x = e.posX + (e.posX - e.lastTickPosX) - x1;
        double y = (e.posY + e.getEyeHeight()) - (y1 + mc.thePlayer.getEyeHeight()) - 0.1;
        double z = e.posZ + (e.posZ - e.lastTickPosZ) - z1;
        double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(x / z));
        float pitch = (float) -Math.toDegrees(Math.atan(y / dist));

        if (x < 0 && z < 0)
            yaw = 90 + (float) Math.toDegrees(Math.atan(z / x));
        else if (x > 0 && z < 0)
            yaw = -90 + (float) Math.toDegrees(Math.atan(z / x));

        yaw += Math.random() * 5 - Math.random();
        pitch += Math.random() * 5 - Math.random();

        if (pitch > 90)
            pitch = 90;
        if (pitch < -90)
            pitch = -90;
        if (yaw > 180)
            yaw = 180;
        if (yaw < -180)
            yaw = -180;

        return new float[]{yaw, pitch};
    }

}
