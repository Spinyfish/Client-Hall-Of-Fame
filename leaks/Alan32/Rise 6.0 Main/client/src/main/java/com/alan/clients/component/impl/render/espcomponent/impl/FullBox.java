package com.alan.clients.component.impl.render.espcomponent.impl;

import com.alan.clients.component.impl.render.espcomponent.api.ESP;
import com.alan.clients.component.impl.render.espcomponent.api.ESPColor;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class FullBox extends ESP implements InstanceAccess {

    public FullBox(ESPColor espColor) {
        super(espColor);
    }

    @Override
    public void render3D() {
        EntityLivingBase player = (EntityLivingBase) this.target;


        if (mc.getRenderManager() == null || player == null) return;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glLineWidth(1.8F);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GlStateManager.depthMask(true);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        float partialTicks = mc.timer.renderPartialTicks;
        double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks;
        double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks;
        double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks;

        float width = target.width;
        float height = target.height + (target.isSneaking() ? -0.2F : 0.1F);

        RenderUtil.color(ColorUtil.withAlpha(getTheme().getFirstColor(), 50));
        RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width + 0.25D, y + height + .1, z - width + 0.25D, x + width - 0.25D, y, z + width - 0.25D));
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
        RenderUtil.color(Color.WHITE);

    }

}
