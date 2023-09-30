/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 06.07.2022
 */
package de.lirium.impl.module.impl.ui;

import best.azura.eventbus.handler.EventHandler;
import best.azura.eventbus.handler.Listener;
import de.lirium.Client;
import de.lirium.base.drag.DragHandler;
import de.lirium.base.drag.Draggable;
import de.lirium.impl.events.Render2DEvent;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import me.felix.shader.access.ShaderAccess;

import java.awt.*;

@ModuleFeature.Info(name = "Weather", description = "Show your local weather information", category = ModuleFeature.Category.UI)
public class WeatherFeature extends ModuleFeature {

    public Draggable drag = DragHandler.setupDrag(this, "Weather", 100, 400, false);

    public FontRenderer fontRenderer = null;

    public final Color color = new Color(24, 24, 26), color2 = new Color(24, 24, 26, 120);

    @EventHandler
    public final Listener<Render2DEvent> render2DEventListener = e -> {
        if (fontRenderer == null)
            fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 18);

        drag.setObjectWidth(95);
        drag.setObjectHeight(45);

        ShaderAccess.blurShaderRunnables.add(() -> RenderUtil.drawRoundedRect(drag.getXPosition(), drag.getYPosition(), 95, 50, 3, color));

        RenderUtil.drawRoundedRect(drag.getXPosition(), drag.getYPosition(), 95, 50, 3, color2);

        final String weather = "Weather";
        fontRenderer.drawString(weather, drag.getXPosition() + 95 / 2F - fontRenderer.getStringWidth(weather) / 2.0F, drag.getYPosition() + 5, -1);

        final String temp = (Client.INSTANCE.getWeatherManager().lastWeatherTemp == null ?
                "N/A" : Client.INSTANCE.getWeatherManager().lastWeatherTemp + "°C");
        final String windSpeed = (Client.INSTANCE.getWeatherManager().lastWeatherWindSpeed == null ?
                "N/A" : Client.INSTANCE.getWeatherManager().lastWeatherWindSpeed + " km/h");

        fontRenderer.drawString(temp, drag.getXPosition() + 95 / 2F - fontRenderer.getStringWidth(temp) / 2.0F, drag.getYPosition() + 5 + 5 + fontRenderer.FONT_HEIGHT, -1);
        fontRenderer.drawString(windSpeed, drag.getXPosition() + 95 / 2F - fontRenderer.getStringWidth(windSpeed) / 2.0F, drag.getYPosition() + 5 + 5 + (fontRenderer.FONT_HEIGHT * 2), -1);
       /* if (fontRenderer == null)
            fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 38);

        drag.setObjectWidth(95);
        drag.setObjectHeight(45);

        JHLabsShaderRenderer.renderShadowOnObject((int) drag.getXPosition(), (int) drag.getYPosition(), 95, 48, 12, Color.BLACK, () -> RenderUtil.drawRoundedRect(drag.getXPosition(), drag.getYPosition(), 95, 45, 3, new Color(24, 24, 26)));

        RenderUtil.drawAcrylicBlurStencil();
        RenderUtil.drawRoundedRect(drag.getXPosition(), drag.getYPosition(), 95, 50, 3, new Color(24, 24, 26));
        RenderUtil.stopAcrylicBlurStencil();
        RenderUtil.drawRoundedRect(drag.getXPosition(), drag.getYPosition(), 95, 50, 3, new Color(255, 255, 255, 255));

        final String weather = "Weather";
        fontRenderer.drawString(weather, drag.getXPosition() + 95 / 2 - fontRenderer.getStringWidth(weather) / 2.0F, drag.getYPosition() + 5, -1);

        final String temp = (Client.INSTANCE.getWeatherManager().lastWeatherTemp == null ? "N/A" : Client.INSTANCE.getWeatherManager().lastWeatherTemp + "°");
        final String windSpeed = (Client.INSTANCE.getWeatherManager().lastWeatherWindSpeed == null ? "N/A" : Client.INSTANCE.getWeatherManager().lastWeatherWindSpeed + " km/h");

        fontRenderer.drawString(temp, drag.getXPosition() + 2, drag.getYPosition() + 5 , new Color(30, 30, 30).getRGB());*/
        // fontRenderer.drawString(windSpeed, drag.getXPosition() + 95 / 2 - fontRenderer.getStringWidth(windSpeed) / 2.0F, drag.getYPosition() + 5 + 5 + (fontRenderer.FONT_HEIGHT * 2), -1);
    };
}
