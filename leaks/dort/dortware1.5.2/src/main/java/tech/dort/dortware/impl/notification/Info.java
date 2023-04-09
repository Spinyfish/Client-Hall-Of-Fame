package tech.dort.dortware.impl.notification;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.notification.AbstractNotification;
import tech.dort.dortware.impl.modules.render.Hud;

import java.awt.*;

public class Info extends AbstractNotification {

//    private static final ResourceLocation WARNING_ICON = new ResourceLocation("dortware/warning.png");

    public Info(String message, int x, int y, long endTime) {
        super(message, x, y, endTime);
    }


    @Override
    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution resolution = new ScaledResolution(mc);
//        final CustomFontRenderer fontRenderer = Client.INSTANCE.getFontManager().getFont("Large").getRenderer();
        if (System.currentTimeMillis() >= getEndTime()) {
            setX(getX() + 1);
            if (getX() > resolution.getScaledWidth() + 155) {
                Client.INSTANCE.getNotificationManager().getObjects().remove(this);
            }
        }

        int height = 20;

        Gui.drawRect(getX(), getY() - height, resolution.getScaledWidth(), getY(), new Color(0, 0, 0, 125).getRGB());

        Hud hud = Client.INSTANCE.getModuleManager().get(Hud.class);
        int red = hud.red.getCastedValue().intValue();
        int green = hud.green.getCastedValue().intValue();
        int blue = hud.blue.getCastedValue().intValue();
        Gui.drawRect(getX() - 2, getY() - height, getX(), getY(), new Color(red, green, blue).getRGB());
        mc.fontRendererObj.drawString(this.getMessage(), getX() + 7.5F, getY() - height / 1.5f, -1);
//        fontRenderer.drawString("Info", getX() - 115, getY() - height / 1.5f, -1);
    }
}
