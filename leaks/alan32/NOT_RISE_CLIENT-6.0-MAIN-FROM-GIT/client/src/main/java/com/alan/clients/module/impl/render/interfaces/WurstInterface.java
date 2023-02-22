package com.alan.clients.module.impl.render.interfaces;

import com.alan.clients.Client;
import com.alan.clients.module.impl.render.Interface;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.value.Mode;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class WurstInterface extends Mode<Interface> {

    private Font font;
    private ResourceLocation resourceLocation = new ResourceLocation("rise/logo/wurst.png");

    public WurstInterface(String name, Interface parent) {
        super(name, parent);
        font = mc.fontRendererObj;
    }

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        if (mc == null || mc.gameSettings.showDebugInfo || mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        this.getParent().setModuleSpacing(this.font.height() + 1);
        this.getParent().setWidthComparator(this.font);
        this.getParent().setEdgeOffset(4);

        // modules in the top right corner of the screen
        for (final ModuleComponent moduleComponent : this.getParent().getActiveModuleComponents()) {
            if (moduleComponent.animationTime == 0) {
                continue;
            }

            final double x = moduleComponent.getPosition().getX();
            final double y = moduleComponent.getPosition().getY();
            final Color finalColor = Color.WHITE;

            font.drawStringWithShadow(moduleComponent.getTranslatedName(), x, y, finalColor.getRGB());
        }

        RenderUtil.rectangle(0, 10, 185, 12, ColorUtil.withAlpha(Color.WHITE, 100));
        RenderUtil.image(resourceLocation, 2, 5.5, 758 / 8.5f, 192 / 8.5f);

        font.drawString("v" + Client.VERSION + " MC 1.8.9", 95, 14, Color.BLACK.getRGB());
    };


    @EventLink()
    public final Listener<TickEvent> onTick = event -> {
        threadPool.execute(() -> {
            // modules in the top right corner of the screen
            for (final ModuleComponent moduleComponent : this.getParent().getActiveModuleComponents()) {
                if (moduleComponent.animationTime == 0) {
                    continue;
                }

                String name = moduleComponent.getTranslatedName();

                moduleComponent.setNameWidth(font.width(name));
                moduleComponent.setTagWidth(0);
                moduleComponent.setTag("");
            }
        });
    };
}
