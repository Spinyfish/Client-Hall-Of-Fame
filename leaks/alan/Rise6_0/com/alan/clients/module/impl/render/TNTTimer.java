package com.alan.clients.module.impl.render;

import com.alan.clients.component.impl.render.ProjectionComponent;
import com.alan.clients.module.Module;
import com.alan.clients.module.api.Category;
import com.alan.clients.module.api.ModuleInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.render.LimitedRender2DEvent;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;

import javax.vecmath.Vector4d;
import java.awt.*;

/**
 * @author Alan
 * @since 29/04/2022
 */

@ModuleInfo(name = "TNT Timer", description = "module.render.tnttimer.description", category = Category.RENDER)
public final class TNTTimer extends Module {


    @EventLink()
    public final Listener<LimitedRender2DEvent> onLimitedRender2D = event -> {

        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityTNTPrimed) {
                final EntityTNTPrimed tnt = (EntityTNTPrimed) entity;
                final String name = MathUtil.round(tnt.fuse / 20.0F, 1) + "s";

                Vector4d position = ProjectionComponent.get(entity);

                if (position == null || !entity.inView) {
                    return;
                }

                double nameWidth = nunitoSmall.width(name);
                double width = nameWidth + 4;
                double height = nunitoSmall.height() + 3;

                NORMAL_POST_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle(position.x + (position.z - position.x) / 2 - width / 2, position.y - height,
                        width, height, getTheme().getRound(), getTheme().getDropShadow()));

                NORMAL_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle(position.x + (position.z - position.x) / 2 - width / 2, position.y - height,
                        width, height, getTheme().getRound(), Color.BLACK));

                NORMAL_RENDER_RUNNABLES.add(() -> {
                    RenderUtil.roundedRectangle(position.x + (position.z - position.x) / 2 - width / 2, position.y - height,
                            width, height, getTheme().getRound(), getTheme().getBackgroundShade());

                    nunitoSmall.drawString(name, position.x + (position.z - position.x) / 2 - nameWidth / 2, position.y - height + nunitoSmall.height() / 2f - 1,
                            getTheme().getFirstColor().hashCode());
                });
            }
        }
    };
}