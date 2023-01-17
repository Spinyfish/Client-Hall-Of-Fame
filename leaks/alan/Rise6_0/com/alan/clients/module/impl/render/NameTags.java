package com.alan.clients.module.impl.render;

import com.alan.clients.Client;
import com.alan.clients.api.Rise;
import com.alan.clients.component.impl.render.ProjectionComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.newevent.impl.render.RenderNameEvent;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.impl.minecraft.FontRenderer;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.value.impl.BooleanValue;
import net.minecraft.entity.EntityLivingBase;

import javax.vecmath.Vector4d;
import java.awt.*;

/**
 * @author Alan
 * @since 29/04/2022
 */
@Rise
@ModuleInfo(name = "Name Tags", description = "module.render.nametags.description", category = Category.RENDER)
public final class NameTags extends Module {

    private final BooleanValue health = new BooleanValue("Show Health", this, true);
    // Show health option doesn't work until we come up with a design that looks good without the health
    // To be honest I don't care alan
    // To be honest I do care patrick

    @EventLink()
    public final Listener<RenderNameEvent> onName = event -> {
        event.setCancelled(true);
    };

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        Font nunitoLight14 = FontManager.getNunitoLight(14);
        for (EntityLivingBase entity : Client.INSTANCE.getTargetManager()) {
            if (entity == mc.thePlayer || Client.INSTANCE.getBotManager().contains(entity)) {
                continue;
            }

            Vector4d position = ProjectionComponent.get(entity);

            if (position == null) {
                continue;
            }

            final String text = entity.getCommandSenderName();
            final double nameWidth = nunitoLightNormal.width(text);

            final double posX = (position.x + (position.z - position.x) / 2);
            final double posY = position.y - 2;
            final double margin = 2;

            final int multiplier = 2;
            final double nH = nunitoLightNormal.height() + nunitoLight14.height() + margin * multiplier;
            final double nY = posY - nH;

            NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
                RenderUtil.roundedRectangle(posX - margin - nameWidth / 2, nY, nameWidth + margin * multiplier, nH, getTheme().getRound(), getTheme().getDropShadow());
            });

            NORMAL_RENDER_RUNNABLES.add(() -> {
                RenderUtil.roundedRectangle(posX - margin - nameWidth / 2, nY, nameWidth + margin * multiplier, nH, getTheme().getRound(), getTheme().getBackgroundShade());
                nunitoLightNormal.drawCenteredString(text, posX, nY + margin * 2, getTheme().getFirstColor().getRGB());

                nunitoLight14.drawCenteredString(String.valueOf((int) entity.getHealth()), posX, posY + 1 + 3 - margin - FontRenderer.FONT_HEIGHT, Color.WHITE.hashCode());
            });

            NORMAL_BLUR_RUNNABLES.add(() -> {
                RenderUtil.roundedRectangle(posX - margin - nameWidth / 2, nY, nameWidth + margin * multiplier, nH, getTheme().getRound(), Color.BLACK);
            });
        }
    };
}