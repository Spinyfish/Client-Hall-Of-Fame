package com.alan.clients.module.impl.render.targetinfo;

import com.alan.clients.component.impl.render.ParticleComponent;
import com.alan.clients.module.impl.render.TargetInfo;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.TickEvent;
import com.alan.clients.newevent.impl.render.Render2DEvent;
import com.alan.clients.util.font.Font;
import com.alan.clients.util.localization.Localization;
import com.alan.clients.value.impl.BooleanValue;
import com.alan.clients.value.Mode;
import com.alan.clients.util.animation.Animation;
import com.alan.clients.util.font.FontManager;
import com.alan.clients.util.font.impl.rise.FontRenderer;
import com.alan.clients.util.math.MathUtil;
import com.alan.clients.util.render.ColorUtil;
import com.alan.clients.util.render.RenderUtil;
import com.alan.clients.util.render.StencilUtil;
import com.alan.clients.util.render.particle.Particle;
import com.alan.clients.util.vector.Vector2d;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.value.impl.ModeValue;
import com.alan.clients.value.impl.SubMode;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.alan.clients.util.animation.Easing.*;

public class ModernTargetInfo extends Mode<TargetInfo> {

    private final BooleanValue particles = new BooleanValue("Particles", this, true);

    private final Font productSansLight = FontManager.getProductSansLight(22);
    private final Font productSansMedium = FontManager.getProductSansMedium(22);

    private final ModeValue backgroundMode = new ModeValue("Background Mode", this) {{
        add(new SubMode("Glass"));
        add(new SubMode("Tint"));
        add(new SubMode("Solid"));
        setDefault("Glass");
    }};

    private TargetInfo targetInfoModule;
    private int EDGE_OFFSET = 8, PADDING = 7, INDENT = 4;

    private Animation openingAnimation = new Animation(EASE_OUT_ELASTIC, 500);
    private Animation healthAnimation = new Animation(EASE_OUT_SINE, 500);

    public ModernTargetInfo(String name, TargetInfo parent) {
        super(name, parent);
    }

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {

        if (this.targetInfoModule == null) {
            this.targetInfoModule = this.getModule(TargetInfo.class);
        }

        Entity target = this.targetInfoModule.target;
        if (target == null) return;

        boolean out = (!this.targetInfoModule.inWorld || this.targetInfoModule.stopwatch.finished(1000));
        openingAnimation.setDuration(out ? 400 : 850);
        openingAnimation.setEasing(out ? EASE_IN_BACK : EASE_OUT_ELASTIC);
        openingAnimation.run(out ? 0 : 1);

        if (openingAnimation.getValue() <= 0) return;

        String name = target.getCommandSenderName();

        double x = this.targetInfoModule.position.x;
        double y = this.targetInfoModule.position.y;

        double nameWidth = productSansMedium.width(name);
        double health = !this.targetInfoModule.inWorld ? 0 : MathUtil.round(((AbstractClientPlayer) target).getHealth(), 1);
        double healthTextWidth = productSansMedium.width(String.valueOf(health));
        double healthBarWidth = Math.max(nameWidth + 35 - healthTextWidth, 65);

        healthAnimation.run((health / ((AbstractClientPlayer) target).getMaxHealth()) * healthBarWidth);
        healthAnimation.setEasing(EASE_OUT_QUINT);
        healthAnimation.setDuration(250);
        double healthRemainingWidth = healthAnimation.getValue();

        double hurtTime = (((AbstractClientPlayer) target).hurtTime == 0 ? 0 :
                ((AbstractClientPlayer) target).hurtTime - mc.timer.renderPartialTicks) * 0.5;
        int faceScale = 32;
        double faceOffset = hurtTime / 2f;
        double width = EDGE_OFFSET + faceScale + EDGE_OFFSET + healthBarWidth + INDENT + healthTextWidth + EDGE_OFFSET;
        double height = faceScale + EDGE_OFFSET * 2;
        this.targetInfoModule.positionValue.setScale(new Vector2d(width, height));

        double scale = openingAnimation.getValue();

        NORMAL_POST_RENDER_RUNNABLES.add(() -> {
            GlStateManager.pushMatrix();
            GlStateManager.translate((x + width / 2) * (1 - scale), (y + height / 2) * (1 - scale), 0);
            GlStateManager.scale(scale, scale, 0);

            // Draw background
            Color background1 = new Color(0, 0, 0, 100);
            Color background2 = new Color(0, 0, 0, 100);
            Color accent1 = getTheme().getFirstColor();
            Color accent2 = getTheme().getSecondColor();

            if (this.backgroundMode.getValue().getName().equals("Tint")) {
                Color theme1 = this.getTheme().getAccentColor(new Vector2d(x, y)), theme2 = this.getTheme().getAccentColor(new Vector2d(x, y + height));
                background1 = new Color(theme1.getRed() / 5, theme1.getGreen() / 5, theme1.getBlue() / 5, 128);
                background2 = new Color(theme2.getRed() / 5, theme2.getGreen() / 5, theme2.getBlue() / 5, 128);
            } else if (this.backgroundMode.getValue().getName().equals("Solid")) {
                Color theme1 = this.getTheme().getFirstColor(), theme2 = this.getTheme().getSecondColor();
                background1 = new Color(theme1.getRed(), theme1.getGreen(), theme1.getBlue(), 128);
                background2 = new Color(theme2.getRed(), theme2.getGreen(), theme2.getBlue(), 128);
                accent1 = new Color(255, 255, 255);
                accent2 = new Color(164, 164, 164);
            }

            RenderUtil.drawRoundedGradientRect(x, y, width, height, 5, background1, background2, true);

            // Render name
            productSansLight.drawString(Localization.get("ui.targethud.name") + " ", x + EDGE_OFFSET + faceScale + PADDING, y + EDGE_OFFSET + INDENT + 2, Color.WHITE.hashCode());
            productSansMedium.drawString(name, x + EDGE_OFFSET + faceScale + PADDING + productSansLight.width(Localization.get("ui.targethud.name")) + 3, y + EDGE_OFFSET + INDENT + 2.5, accent1.hashCode());

            GlStateManager.popMatrix();

            ParticleComponent.render();

            GlStateManager.pushMatrix();
            GlStateManager.translate((x + width / 2) * (1 - scale), (y + height / 2) * (1 - scale), 0);
            GlStateManager.scale(scale, scale, 0);

            // Targets face
            RenderUtil.color(ColorUtil.mixColors(Color.RED, Color.WHITE, hurtTime / 9));
            RenderUtil.dropShadow(3, x + EDGE_OFFSET + faceOffset, y + EDGE_OFFSET + faceOffset,
                    faceScale - hurtTime, faceScale - hurtTime, 20, this.getTheme().getRound() * 2);
            renderTargetHead((AbstractClientPlayer) target, x + EDGE_OFFSET + faceOffset, y + EDGE_OFFSET + faceOffset,
                    faceScale - hurtTime);

            // Health background
            RenderUtil.roundedRectangle(x + EDGE_OFFSET + faceScale + PADDING, y + EDGE_OFFSET + faceScale - INDENT - 7,
                    healthBarWidth, 6, 3, getTheme().getBackgroundShade());

            // Health
            RenderUtil.drawRoundedGradientRect(x + EDGE_OFFSET + faceScale + PADDING, y + EDGE_OFFSET + faceScale - INDENT - 7,
                    healthRemainingWidth, 6, 3, accent2, accent1, true);

            productSansMedium.drawString(String.valueOf(health),
                    x + EDGE_OFFSET + faceScale + PADDING + healthBarWidth + INDENT,
                    y + EDGE_OFFSET + faceScale - INDENT - 8, accent1.hashCode());
            GlStateManager.popMatrix();
        });

        NORMAL_PRE_RENDER_RUNNABLES.add(() -> {
            GlStateManager.pushMatrix();
            GlStateManager.translate((x + width / 2) * (1 - scale), (y + height / 2) * (1 - scale), 0);
            GlStateManager.scale(scale, scale, 0);

            // Health background
            RenderUtil.roundedRectangle(x + EDGE_OFFSET + faceOffset, y + EDGE_OFFSET + faceOffset,
                    faceScale - hurtTime, faceScale - hurtTime, this.getTheme().getRound() * 2,
                    ColorUtil.withAlpha(Color.RED, (int) (hurtTime / 9 * 255)));

            // Health
            RenderUtil.drawRoundedGradientRect(x + EDGE_OFFSET + faceScale + PADDING, y + EDGE_OFFSET + faceScale - INDENT - 5,
                    healthRemainingWidth, 6, 3, getTheme().getFirstColor(), getTheme().getSecondColor(), true);

            GlStateManager.popMatrix();
        });

        NORMAL_BLUR_RUNNABLES.add(() -> {
            GlStateManager.pushMatrix();
            GlStateManager.translate((x + width / 2) * (1 - scale), (y + height / 2) * (1 - scale), 0);
            GlStateManager.scale(scale, scale, 0);
            RenderUtil.roundedRectangle(x, y, width, height, 5, Color.BLACK);
            GlStateManager.popMatrix();
        });

        NORMAL_POST_BLOOM_RUNNABLES.add(() -> {
            GlStateManager.pushMatrix();
            GlStateManager.translate((x + width / 2) * (1 - scale), (y + height / 2) * (1 - scale), 0);
            GlStateManager.scale(scale, scale, 0);

//            final boolean glow = this.backgroundMode.getValue().getName().equals("Solid");
            final boolean glow = false;
            final Color outlineColor1 = glow ? this.getTheme().getFirstColor() : new Color(0, 0, 0, 128);
            final Color outlineColor2 = glow ? this.getTheme().getSecondColor() : new Color(0, 0, 0, 128);
            RenderUtil.drawRoundedGradientRect(x, y, width, height, 6, outlineColor1, outlineColor2, true);

            GlStateManager.popMatrix();
        });
    };

    private void renderTargetHead(final AbstractClientPlayer abstractClientPlayer, final double x, final double y, final double size) {
        StencilUtil.initStencil();
        StencilUtil.bindWriteStencilBuffer();
        RenderUtil.roundedRectangle(x, y, size, size, this.getTheme().getRound() * 2, this.getTheme().getBackgroundShade());
        StencilUtil.bindReadStencilBuffer(1);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
        GlStateManager.enableTexture2D();

        final ResourceLocation resourceLocation = targetInfoModule.inWorld && abstractClientPlayer.getHealth() > 0
                ? abstractClientPlayer.getLocationSkin() : RenderSkeleton.getEntityTexture();

        mc.getTextureManager().bindTexture(resourceLocation);

        Gui.drawScaledCustomSizeModalRect(x, y, 4, 4, 4, 4, size, size, 32, 32);
        GlStateManager.disableBlend();
        StencilUtil.uninitStencilBuffer();
    }

    @EventLink()
    public final Listener<TickEvent> onTick = event -> {

        if (this.targetInfoModule == null) return;
        Entity target = this.targetInfoModule.target;

        if (target == null || openingAnimation.getValue() <= 0 || !this.particles.getValue()) return;

        double hurtTime = (((AbstractClientPlayer) target).hurtTime == 0 ? 0 :
                ((AbstractClientPlayer) target).hurtTime - mc.timer.renderPartialTicks) * 0.5;

        if (hurtTime > 0) {
            for (int i = 0; i < hurtTime * Math.random() / 2; i++) {
                ParticleComponent.add(new Particle(new Vector2f((float) (targetInfoModule.position.x + 20), (float) (targetInfoModule.position.y + 20)),
                        new Vector2f((float) (Math.random() - 0.5) * 1.7f, (float) (Math.random() - 0.5) * 1.7f)));
            }
        }
    };
}
