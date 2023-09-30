package felix.gui.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import felix.Client;
import felix.util.font.FontRenderer;
import felix.util.visual.Colors;
import felix.util.visual.RenderUtil;

import java.awt.*;
import java.util.List;

public class NotificationRenderer implements INotificationRenderer {

    @Override
    public void draw(List<INotification> notifications) {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledRes = new ScaledResolution(mc);
        float y = (float) (scaledRes.getScaledHeight()) - (notifications.size() * (22));
        for (INotification notification : notifications) {
            Notification not = (Notification) notification;
            not.translate.interpolate(not.getTarX(), y, 12);
            int s = scaledRes.getScaleFactor();
            float subHeaderWidth = mc.fontRendererObj.getStringWidth(not.getSubtext());
            float headerWidth = mc.fontRendererObj.getStringWidth(not.getHeader());
            float x = scaledRes.getScaledWidth() - 20 - (headerWidth > subHeaderWidth ? headerWidth : subHeaderWidth);

            GL11.glPushMatrix();
            //GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor((int) not.translate.getX() * s, (int) (scaledRes.getScaledWidth() - not.translate.getY() * s), scaledRes.getScaledWidth() * s, (int) ((not.translate.getY() + 50) * s));
            RenderUtil.drawRoundedRect2(x, not.translate.getY(), scaledRes.getScaledWidth(), not.translate.getY() + (22) - 3, 5, Colors.getColor(0, 0, 0, 170));
            RenderUtil.drawRoundedRect2(x, not.translate.getY(), scaledRes.getScaledWidth(), not.translate.getY() + (22) - 3, 5, Color.TRANSLUCENT);

            for (int i = 0; i < 11; i++) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(x + 10, (not.translate.getY() + 13), 0);
                GlStateManager.rotate(270, 0, 0, 90);
                RenderUtil.drawCircle(-3.0f, -6.9f, 13, getColor(not.getType()));
                GlStateManager.popMatrix();
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 10, (not.translate.getY() + 13), 0);
            GlStateManager.rotate(270, 0, 0, 90);
            //RenderUtil.drawUnfilledCircle(-5, -7, 15, Colors.getColor(0));
            GlStateManager.popMatrix();
            
            RenderUtil.drawRoundedRect2(x + 9, (not.translate.getY() + 4), x + 11.2, not.translate.getY() + 13, 3, Colors.getColor(0));
            RenderUtil.drawRoundedRect2(x + 9, (not.translate.getY() + 14), x + 11.2, not.translate.getY() + 16, 3, Colors.getColor(0));
            
            final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 18", false);
            fr.drawStringWithShadow(not.getHeader(), x + 18.2f, (float) (not.translate.getY() + 1), Colors.getColor(255));
            fr.drawStringWithShadow(not.getSubtext(), x + 18.5f, (float) (not.translate.getY() + 9), Colors.getColor(255));
            
            //GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glPopMatrix();

            if (not.checkTime() >= not.getDisplayTime() + not.getStart()) {
                not.setTarX(scaledRes.getScaledWidth() + 500);
                if (not.translate.getX() >= scaledRes.getScaledWidth()) {
                    notifications.remove(notification);
                }
            }
            y += (22);
        }
    }

    private int getColor(Notifications.Type type) {
        int color = 0;
        switch (type) {
            case INFO:
                color = new Color(255, 255, 255).getRGB();
                break;
            case NOTIFY:
                color = new Color(242, 206, 87).getRGB();
                break;
            case WARNING:
                color = Color.RED.getRGB();
                break;
            case SUCCESS:
            	color = new Color(0, 255, 35).getRGB();
            	break;
            default:
                break;
        }
        return color;
    }

}
