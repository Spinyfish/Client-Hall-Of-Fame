package club.pulsive.impl.module.impl.visual;

import club.pulsive.api.event.eventBus.handler.EventHandler;
import club.pulsive.api.event.eventBus.handler.Listener;
import club.pulsive.api.font.Fonts;
import club.pulsive.api.main.Pulsive;
import club.pulsive.impl.event.client.ShaderEvent;
import club.pulsive.impl.event.render.Render2DEvent;
import club.pulsive.impl.module.Category;
import club.pulsive.impl.module.Module;
import club.pulsive.impl.module.ModuleInfo;
import club.pulsive.impl.module.impl.misc.ClientSettings;
import club.pulsive.impl.util.render.*;
import club.pulsive.impl.util.render.secondary.ShaderRound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;

@ModuleInfo(name = "Radar", renderName = "Radar", category = Category.VISUALS)

public class Radar extends Module {
    public Draggable draggable = Pulsive.INSTANCE.getDraggablesManager().createNewDraggable(this, "radar", 100, 100);
    @EventHandler
    private final Listener<Render2DEvent> render2DEventListener = event -> {
        draggable.setWidth(100);
        draggable.setHeight(80);
        RoundedUtil.drawRoundedRect(draggable.getX(),draggable.getY() - 10, draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8,  new Color(0,0,0, 100).getRGB());

        // RoundedUtil.drawGradientRoundedRect(draggable.getX(), draggable.getY() - 10, draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, RenderUtil.applyOpacity(new Color(HUD.getColor()), (float) 0.1).getRGB(), RenderUtil.applyOpacity(new Color(HUD.getColor()).darker(), (float) 0.1f).getRGB());

        GlStateManager.pushMatrix();
        float ez = 0;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        if (sr.getScaleFactor() == 2) {
            ez = 2;
        }
        int posX = (int) draggable.getX() * 2, posY = (int) draggable.getY() * 2,
                width = (int) draggable.getWidth() * 2, height = (int) draggable.getHeight() * 2;
        if (sr.getScaleFactor() == 2) {
            GlStateManager.scale(0.5, 0.5, 0.5);
        } else if (sr.getScaleFactor() == 3) {
            GlStateManager.scale(0.3325, 0.3325, 0.3325);
        } else if (sr.getScaleFactor() == 4) {
            GlStateManager.scale(0.25, 0.25, 0.25);
        }

        Draw.drawRectangle(posX + width / 2, posY + 2, posX + width / 2 + 1, posY + height - 1,  new Color(230,230,230, 200).getRGB());

        Draw.drawRectangle(posX + 2, posY + height / 2, posX + width - 1, posY + height / 2 + 1,  new Color(230,230,230, 200).getRGB());
        float rotation = -((mc.thePlayer.prevRotationYawHead
                + (mc.thePlayer.rotationYawHead - mc.thePlayer.prevRotationYawHead) * mc.timer.renderPartialTicks));
        GlStateManager.translate(posX + width / 2, posY + height / 2, 0);
        GlStateManager.rotate(rotation, 0, 0, 1);
        GlStateManager.translate(-(posX + width / 2), -(posY + height / 2), 0);
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase))
                continue;
            if (entity instanceof EntityPlayer) {

                int diffX = (int) (Minecraft.getMinecraft().thePlayer.posX - entity.posX);
                int diffZ = (int) (Minecraft.getMinecraft().thePlayer.posZ - entity.posZ);

                if (diffX > width / 2 || diffX < -width / 2)
                    continue;
                if (diffZ > width / 2 || diffZ < -width / 2)
                    continue;

                renderPlayer(posX + width / 2 + diffX, posY + height / 2 + diffZ, (EntityLivingBase) entity);

            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
        Fonts.icons30.drawCenteredString("F", (posX + 21) / ez + 3 , (posY - 16) / ez + 3 + 3, new Color(230,230,230, 200).getRGB());
        Fonts.icons15.drawCenteredString("L", (posX + width) / ez - 10 , (posY + height - 16) / ez + 3 - 5, new Color(230,230,230, 200).getRGB());
        Fonts.icons15.drawCenteredString("K", (posX + width - 20) / ez - 10 , (posY + height - 16) / ez + 3 - 5, new Color(230,230,230, 200).getRGB());
        if(ClientSettings.uiOutlines.getValue())
            RoundedUtil.drawRoundedOutline(draggable.getX(),draggable.getY() - 10, draggable.getX() + draggable.getWidth(), draggable.getY() + draggable.getHeight(), 8, 3f, new Color(HUD.getColor()).getRGB());
    };
    public void renderPlayer(int x, int y, EntityLivingBase entity) {
        // Draw.drawBorderedRectangle(x - 5, y - 5, x + 6, y + 6, 2,entity.hurtTime > 0
        // ? new Color(135, 4, 4, 255).getRGB() : entity.hurtTime > 0 ? new Color(135,
        // 4, 4, 255).getRGB() : entity == mc.thePlayer ? 0xFFFF0000 : 0xFFFFFF00,
        // 0xFF000000, true);


        RoundedUtil.drawSmoothRoundedRect(x,y,x + 2, y + 2, 2, -1);
    }

    @EventHandler
    private final Listener<ShaderEvent> shaderEventListener = event -> {
        RoundedUtil.drawRoundedRect(draggable.getX(),draggable.getY() - 10, draggable.getX() + draggable.getWidth(), draggable.getY() - 10 + draggable.getHeight() + 10,10, new Color(0,0,0,255).getRGB());
    };

}