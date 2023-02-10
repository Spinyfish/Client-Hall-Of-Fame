/**
 * @project Myth
 * @author CodeMan
 * @at 06.08.22, 23:06
 */
package dev.myth.features.display;

import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;
import dev.myth.api.draggable.DraggableComponent;
import dev.myth.api.feature.Feature;
import dev.myth.api.utils.font.CFontRenderer;
import dev.myth.api.utils.font.FontLoaders;
import dev.myth.api.utils.render.ColorUtil;
import dev.myth.api.utils.render.RenderUtil;
import dev.myth.api.utils.render.shader.list.DropShadowUtil;
import dev.myth.events.Render2DEvent;
import dev.myth.features.combat.KillAuraFeature;
import dev.myth.features.visual.BlurFeature;
import dev.myth.features.visual.GlowFeature;
import dev.myth.features.visual.ShadowFeature;
import dev.myth.main.ClientMain;
import dev.myth.managers.DraggableManager;
import dev.myth.managers.FeatureManager;
import dev.myth.managers.ShaderManager;
import dev.myth.settings.ColorSetting;
import dev.myth.settings.EnumSetting;
import dev.myth.settings.NumberSetting;
import dev.myth.ui.clickgui.dropdowngui.ClickGui;
import dev.myth.ui.clickgui.skeetgui.SkeetGui;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

@Feature.Info(
        name = "TargetHUD",
        description = "Displays information about the target",
        category = Feature.Category.DISPLAY
)
public class TargetHUDFeature extends Feature {

    public final EnumSetting<Mode> mode = new EnumSetting<>("Mode", Mode.EXHIBITION);
    public final EnumSetting<NovolineType> novolineType = new EnumSetting<>("NovolineType", NovolineType.NEW).setDisplayName("Type").addDependency(() -> mode.is(Mode.NOVOLINE));
    public final EnumSetting<NovolineWtfType> novolineWtfType = new EnumSetting<>("NovolineWtfType", NovolineWtfType.FIRST).setDisplayName("Type").addDependency(() -> mode.is(Mode.NOVOLINEWTF));
    public final NumberSetting xPos = new NumberSetting("X", 200, 0, 1000, 10);
    public final NumberSetting yPos = new NumberSetting("Y", 200, 0, 1000, 10);
    public final ColorSetting aquaColor1 = new ColorSetting("Color 1", Color.CYAN).addDependency(() -> mode.is(Mode.NOVOLINE));
    public final ColorSetting aquaColor2 = new ColorSetting("Color 2", Color.CYAN.darker()).addDependency(() -> mode.is(Mode.NOVOLINE) );

    public double lastTime;

    /* animations */
    public float healthPercentage = 0.0f;
    public float lastHealthPercentage = 0.0f;

    public float armorPercentage = 0.0f;
    public float lastArmorPercentage = 0.0f;

    private DraggableComponent draggableComponent;

    @Override
    public void onEnable() {
        super.onEnable();

        ScaledResolution sr = new ScaledResolution(MC);

        if(draggableComponent == null) {
            draggableComponent = new DraggableComponent("TargetHUD", xPos.getValue(), yPos.getValue(), 100, 100, sr);
        }

        ClientMain.INSTANCE.manager.getManager(DraggableManager.class).registerDraggable(draggableComponent);

        draggableComponent.setX(xPos.getValue() / sr.getScaledWidth());
        draggableComponent.setY(yPos.getValue() / sr.getScaledHeight());

        doLog(draggableComponent.getX(sr) + " " + draggableComponent.getY(sr));
    }

    @Override
    public void onDisable() {
        super.onDisable();
        ClientMain.INSTANCE.manager.getManager(DraggableManager.class).unregisterDraggable(draggableComponent);
    }

    @Handler
    public final Listener<Render2DEvent> updateEventListener = updateEvent -> {

        GlStateManager.disableBlend();
        KillAuraFeature killAuraFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(KillAuraFeature.class);
        EntityLivingBase target = killAuraFeature.target;

        if(MC.currentScreen instanceof ClickGui || MC.currentScreen instanceof GuiChat) {
            target = MC.thePlayer;
        } else if(target == null || !killAuraFeature.isEnabled()) {
            return;
        }

        ScaledResolution sr = updateEvent.getScaledResolution();

        double x = draggableComponent.getX(sr), y = draggableComponent.getY(sr);

        switch (mode.getValue()) {
            case EXHIBITION:
                drawExhibition(x, y, target);
                break;
            case FLUX:
                drawFlux(x, y, target);
                break;
            case NOVOLINE:
                switch (novolineType.getValue()) {
                    case NEW:
                        drawNovolineNew(x, y, target);
                        break;
                    case STELLA:
                        drawNovolineStella(x, y, target);
                        break;
                    case STYLES:
                        drawNovolineStyles(x, y, target);
                        break;
                    case BASIC:
                        drawNovolineBasic(x, y, target);
                        break;
                }
                break;
            case NOVOLINEWTF:
                switch (novolineWtfType.getValue()) {
                    case FIRST:
                        drawNovolinewtfFirst(x, y, target);
                        break;
                    case SECOND:
                        drawNovolinewtfSecond(x, y, target);
                        break;
                    case THIRD:
                        drawNovolinewtfThird(x, y, target);
                        break;
                }
                break;
            case ASTOLFO:
                drawAstolfo(x, y, target);
                break;
            case MYTH:
                drawMyth(x, y, target);
                break;
        }
    };

    // TargetHuds

    private void drawMyth(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        final HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);

        final double nameWidth = FontLoaders.NOVO_FONT_22.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 140), height = 48;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);


        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));

        final float calculatedArmor = target.getTotalArmorValue() / 20f;
        final float animatedArmorBar = calculatedArmor + armorPercentage;

        if (lastArmorPercentage != calculatedArmor) {
            armorPercentage += lastArmorPercentage - calculatedArmor;
        }
        lastArmorPercentage = calculatedArmor;
        armorPercentage = Math.max(0.0f, (armorPercentage - armorPercentage / 20.0f));


        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);

        if(target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 3), (int) (y + 4), 32, 32);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 3), (int) (y + 4), 32, 32, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 3), (int) (y + 4), 32, 32, new ResourceLocation("myth/icons/mark.png"));
        }

        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.NOVO_FONT_20.drawString(target.getName(), (float) (x + 36), (float) (y + 7), -1);
        RenderUtil.drawRect(x + 3, y + 38, 3 + animatedHealthBar * 60 * 2.1, 1.5f, new Color(135,247,108).getRGB());
        RenderUtil.drawRect(x + 3, y + 42, 3 + animatedArmorBar * 60 * 2.1, 1.5f, new Color(65,105,225).getRGB());
    }

    private void drawAstolfo(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        final HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);

        final double nameWidth =  MC.fontRendererObj.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 140), height = 48;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);


        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));


        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);

        GuiInventory.drawEntityOnScreen((int) (x + 16), (int) (y + 43), 20, 18, getYaw(),  target);
        MC.fontRendererObj.drawStringWithShadow(target.getName().toLowerCase(Locale.ROOT), (float) (x + 33), (float) (y + 5), -1);
        GL11.glPushMatrix();
        GlStateManager.translate(x, y, 1.0F);
        GL11.glScalef(1.3F, 1.3F, 1.3F);
        GlStateManager.translate(-x, -y, 1.0F);
        MC.fontRendererObj.drawStringWithShadow(Math.round(target.getHealth()) + ".0 ❤", (float) (x + 25), (float) (y + 14), hudFeature.arrayListColor.getColor());
        GL11.glPopMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        RenderUtil.drawRect(x + 33, y + 35, animatedHealthBar * 40 * 2.5, 6, hudFeature.arrayListColor.getColor());
    }


    private void drawNovolineBasic(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        final HUDFeature hudFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(HUDFeature.class);

        final double nameWidth = FontLoaders.SFUI_20.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 144), height = 50;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);

        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));


        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);

        if (target.hurtTime != 0)
            RenderUtil.drawRect((int) (x + 3), (int) (y + 4), 32, 32, new Color(100, 0, 0, 50).getRGB());

        if(target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 3), (int) (y + 4), 32, 32);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 3), (int) (y + 4), 32, 32, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 3), (int) (y + 4), 32, 32, new ResourceLocation("myth/icons/mark.png"));
        }
        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.SFUI_19.drawString("Name: " + target.getName(), (float) (x + 38), (float) (y + 9), -1);
        FontLoaders.SFUI_19.drawString("Distance: " + Math.round(getPlayer().getDistanceToEntity(target)) + ".0", (float) (x + 38), (float) (y + 22), -1);
        Gui.drawRect((int) (x + 3), (int) (y + 40), x + 5 + animatedHealthBar * 60 * 2, y + 46, hudFeature.arrayListColor.getColor());
        FontLoaders.SFUI_15.drawString(Math.round(target.getHealth()) + ".0", (float) (x + 5.5 + animatedHealthBar * 60 * 2), (float) (y + 41), -1);
    }

    private void drawNovolinewtfThird(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        
        final double nameWidth = FontLoaders.SFUI_20.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 140), height = 39;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);


        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));


        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);

        if (target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 3), (int) (y + 3), 32, 32);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 3), (int) (y + 3), 32, 32, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 3), (int) (y + 3), 32, 32, new ResourceLocation("myth/icons/mark.png"));
        }

        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.NOVO_FONT_18.drawString(target.getName(), (float) (x + 37), (float) (y + 6), -1);
        FontLoaders.SFUI_15.drawString(Math.round(target.getHealth()) + " HP - " + Math.round(getPlayer().getDistanceToEntity(target)) + "m",  (float) (x + 37), (float) (y + 16), -1);

        //Glow effect
        DropShadowUtil.start();
        RenderUtil.drawRect((float) (x + 37), (float) (y + 26),  animatedHealthBar * 40 * 2.5, 4, new Color(41, 197, 255).getRGB());
        DropShadowUtil.stop(5, new Color(41, 197, 255));
        RenderUtil.drawRect((float) (x + 37), (float) (y + 26),  animatedHealthBar * 40 * 2.5, 4, new Color(41, 197, 255).getRGB());


    }

    private void drawNovolinewtfSecond(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        
        final double nameWidth = FontLoaders.SFUI_20.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 120), height = 35;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);


        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));


        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);

        if (target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 3), (int) (y + 3), 28, 28);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 3), (int) (y + 3), 28, 28, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 3), (int) (y + 3), 28, 28, new ResourceLocation("myth/icons/mark.png"));
        }

        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.NOVO_FONT_20.drawString(target.getName(), (float) (x + 33), (float) (y + 5), -1);
        FontLoaders.SFUI_16.drawString(Math.round(target.getHealth()) + " HP - " + Math.round(getPlayer().getDistanceToEntity(target)) + "m",  (float) (x + 33), (float) (y + 15), -1);
        RenderUtil.drawRect((int) (x + 33.5), (int) (y + 24), (int) (80.5), (int) (7.9), new Color(0, 0, 0, 80).getRGB());
        Gui.drawGradientRectSideways((int) (x + 33.5), (int) (y + 24), (int) (x + 33.5 + animatedHealthBar * 40 * 2), (int) (y + 23.5 + 7.9), new Color(255,20,147).getRGB(), new Color(255,182,193).getRGB());


    }

    private void drawNovolinewtfFirst(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        
        final double nameWidth = FontLoaders.SFUI_20.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 110), height = 45;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);

        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));

        final float calculatedArmor = target.getTotalArmorValue() / 20f;
        final float animatedArmorBar = calculatedArmor + armorPercentage;

        if (lastArmorPercentage != calculatedArmor) {
            armorPercentage += lastArmorPercentage - calculatedArmor;
        }
        lastArmorPercentage = calculatedArmor;
        armorPercentage = Math.max(0.0f, (armorPercentage - armorPercentage / 20.0f));

        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);


        if (target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 3), (int) (y + 3), 28, 28);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 3), (int) (y + 3), 28, 28, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 3), (int) (y + 3), 28, 28, new ResourceLocation("myth/icons/mark.png"));
        }

        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.SFUI_20.drawString(target.getName(), (float) (x + 32), (float) (y + 5), -1);
        FontLoaders.SFUI_16.drawString("Health: " + Math.round(target.getHealth()) + ".0", (float) (x + 33), (float) (y + 15), -1);
        FontLoaders.SFUI_16.drawString("Distance: " + Math.round(getPlayer().getDistanceToEntity(target)) + "m", (float) (x + 33), (float) (y + 23), -1);
        Gui.drawGradientRectSideways(x + 3, y + 36, x + 3 + animatedHealthBar * 50 * 2, y + 38, new Color(10, 100, 10).getRGB(), new Color(135,247,108).getRGB());
        Gui.drawGradientRectSideways(x + 3, y + 40, x + 3 + animatedArmorBar * 50 * 2, y + 42, new Color(65,105,225).getRGB(), new Color(0,191,255).getRGB());

    }

    private void drawNovolineStyles(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        
        final double nameWidth = FontLoaders.SFUI_20.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 110), height = 45;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);

        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));

        final float calculatedArmor = target.getTotalArmorValue() / 20f;
        final float animatedArmorBar = calculatedArmor + armorPercentage;

        if (lastArmorPercentage != calculatedArmor) {
            armorPercentage += lastArmorPercentage - calculatedArmor;
        }
        lastArmorPercentage = calculatedArmor;
        armorPercentage = Math.max(0.0f, (armorPercentage - armorPercentage / 20.0f));

        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {

            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);


        if (target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 3), (int) (y + 3), 28, 28);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 3), (int) (y + 3), 28, 28, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 3), (int) (y + 3), 28, 28, new ResourceLocation("myth/icons/mark.png"));
        }

        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.SFUI_20.drawString(target.getName(), (float) (x + 32), (float) (y + 5), -1);
        FontLoaders.SFUI_16.drawString("Health: " + Math.round(target.getHealth()) + ".00", (float) (x + 33), (float) (y + 15), -1);
        FontLoaders.SFUI_16.drawString("Distance: " + Math.round(getPlayer().getDistanceToEntity(target)) + ".00", (float) (x + 33), (float) (y + 23), -1);
        Gui.drawGradientRectSideways(x + 3, y + 36, x + 3 + animatedHealthBar * 49 * 2, y + 38, new Color(50,205,50).getRGB(),  new Color(118,255,122).brighter().getRGB());
        Gui.drawGradientRectSideways(x + 3, y + 40, x + 3 + animatedArmorBar * 49 * 2, y + 42, new Color(28,28,240).getRGB(), new Color(70,130,180).getRGB());

    }

    private void drawNovolineStella(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        
        final double nameWidth = FontLoaders.SFUI_20.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 125), height = 43;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);

        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));

        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);

        if(target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 3), (int) (y + 4), 35, 35);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 3), (int) (y + 4), 35, 35, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 3), (int) (y + 4), 35, 35, new ResourceLocation("myth/icons/mark.png"));
        }

        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.NOVO_FONT.drawStringWithShadow(target.getName(), (float) (x + 42), (float) (y + 5), -1);
        FontLoaders.NOVO_FONT.drawStringWithShadow("Distance: " + Math.round(getPlayer().getDistanceToEntity(target)) + ".00", (float) (x + 42), (float) (y + 15), -1);
        RenderUtil.drawRect((float) (x + 42), (float) (y + 26),  animatedHealthBar * 40 * 2, 13, aquaColor1.getColor());
        FontLoaders.NOVO_FONT.drawString(Math.round(target.getHealth()) + ".00", (float) (x + 70), (float) (y + 30), -1);
    }

    private void drawNovolineNew(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        
        final double nameWidth = FontLoaders.SFUI_20.getStringWidth(target.getName());
        final double width = Math.max((int) (nameWidth * 1.5), 144), height = 50;
        final ArrayList<ItemStack> items = new ArrayList<>();

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);

        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));


        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 2, new Color(0, 0, 0, 120).getRGB(), 0, 0);
        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);

        if (target.hurtTime != 0)
            RenderUtil.drawRect((int) (x + 3), (int) (y + 4), 32, 32, new Color(100, 0, 0, 50).getRGB());

        if(target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 3), (int) (y + 4), 32, 32);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 3), (int) (y + 4), 32, 32, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 3), (int) (y + 4), 32, 32, new ResourceLocation("myth/icons/mark.png"));
        }
        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.SFUI_20.drawString(target.getName(), (float) (x + 38), (float) (y + 6), -1);
        Gui.drawGradientRectSideways((int) (x + 3), (int) (y + 40), x + 5 + animatedHealthBar * 60 * 2, y + 46, aquaColor1.getColor(), aquaColor2.getColor());
        FontLoaders.SFUI_15.drawString(Math.round(target.getHealth()) + ".0", (float) (x + 5.5 + animatedHealthBar * 60 * 2), (float) (y + 41), -1);
        for(int i = 0; i < 5; i++)
            RenderUtil.drawRect(x + 37 + i * 16, (int) (y + 16), 15, 16, new Color(0, 0, 0, 100).getRGB());


        if(target.getHeldItem() != null) {
            items.add(target.getHeldItem());
        }

        for(int i = 3; i >= 0; i--) {
            ItemStack stack = target.getCurrentArmor(i);
            if(stack != null) {
                items.add(stack);
            }
        }

        int i = 0;
        for(ItemStack stack : items) {

            MC.getRenderItem().renderItemAndEffectIntoGUI(stack, (int) (x + 37 + i * 16), (int) (y + 16));
            i++;
        }
    }

    private void drawFlux(final double x, final double y, final EntityLivingBase target) {
        final BlurFeature blurFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(BlurFeature.class);
        final GlowFeature glowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(GlowFeature.class);
        final ShadowFeature shadowFeature = ClientMain.INSTANCE.manager.getManager(FeatureManager.class).getFeature(ShadowFeature.class);
        
        double nameWidth = FontLoaders.SFUI_16.getStringWidth(target.getName());
        double width = Math.max((int) (60 + nameWidth), 140), height = 36;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);

        final float calcualtedHealth = target.getHealth() / target.getMaxHealth();
        final float animatedHealthBar = calcualtedHealth + healthPercentage;

        if (lastHealthPercentage != calcualtedHealth) {
            healthPercentage += lastHealthPercentage - calcualtedHealth;
        }
        lastHealthPercentage = calcualtedHealth;
        healthPercentage = Math.max(0.0f, (healthPercentage - healthPercentage / 20.0f));

        final float calculatedArmor = target.getTotalArmorValue() / 20f;
        final float animatedArmorBar = calculatedArmor + armorPercentage;

        if (lastArmorPercentage != calculatedArmor) {
            armorPercentage += lastArmorPercentage - calculatedArmor;
        }
        lastArmorPercentage = calculatedArmor;
        armorPercentage = Math.max(0.0f, (armorPercentage - armorPercentage / 20.0f));

        if (glowFeature.isEnabled() && glowFeature.modules.isEnabled("TargetHUD") || shadowFeature.isEnabled() && shadowFeature.modules.isEnabled("TargetHUD")) {
            DropShadowUtil.start();
            RenderUtil.drawRoundedRect(x, y, width, height, 1, new Color(0, 0, 0, 100).getRGB(), -1, 0);
            int radius = shadowFeature.isEnabled() ? 13 : glowFeature.glowRadius.getValue().intValue();
            Color glowColor = shadowFeature.isEnabled() ? Color.BLACK : glowFeature.glowColor.getValue();
            DropShadowUtil.stop(radius, glowColor);
        }

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.startBlur();

        RenderUtil.drawRoundedRect(x, y, width, height, 1, new Color(0, 0, 0, 100).getRGB(), -1, 0);

        if (blurFeature.isEnabled() && blurFeature.modules.isEnabled("TargetHUD"))
            ClientMain.INSTANCE.manager.getManager(ShaderManager.class).BLUR_SHADER.stopBlur(blurFeature.blurSigma.getValue().intValue(), blurFeature.blurRadius.getValue().intValue(), 1);

        if (target instanceof EntityPlayer)
            RenderUtil.drawHead((AbstractClientPlayer) target, (int) (x + 1), (int) (y + 1), 34, 34);
        else {
            GlStateManager.disableBlend();
            RenderUtil.drawRect((int) (x + 1), (int) (y + 1), 34, 34, new Color(0, 0, 0, 70).getRGB());
            RenderUtil.drawImage((int) (x + 1), (int) (y + 1), 34, 34, new ResourceLocation("myth/icons/mark.png"));
        }

        FontLoaders.SFUI_12.drawString(" ", 1, 0, -1); // Glow Bug Fix
        FontLoaders.SFUI_16.drawString(target.getName(), (float) (x + 37), (float) (y + 7), -1);
        FontLoaders.SFUI_15.drawString(Math.round(target.getHealth()) + "hp", (float) (x + width - 20), (float) (y + 7), -1); // Hardcode
        RenderUtil.drawImage((int) (x + 37), (int) (y + 15), 8, 8, new ResourceLocation("myth/icons/heart.png"), Color.WHITE);
        RenderUtil.drawImage((int) (x + 37), (int) (y + 24), 8, 8, new ResourceLocation("myth/icons/shield.png"), Color.WHITE);
        RenderUtil.drawRect((int) (x + 46), (int) (y + 17), 1 + animatedHealthBar * 35 * 2, 4, new Color(135,247,108).getRGB());
        RenderUtil.drawRect((int) (x + 46), (int) (y + 26), 1 + animatedArmorBar * 35 * 2, 4, new Color(65,127,232).getRGB());
    }

    private void drawExhibition(final double x, final double y, final EntityLivingBase target) {
        CFontRenderer font = FontLoaders.SFUI_20;
        CFontRenderer fontSmall = FontLoaders.TAHOMA_11;


        double width = Math.max(130, font.getStringWidth(target.getName()) + 45), height = 50;

        draggableComponent.setWidth(width);
        draggableComponent.setHeight(height);

//        Gui.drawRect(x, y, x + width, y + height, Color.BLACK.getRGB());
//        Gui.drawRect(x + 1, y + 1, x + width - 1, y + height - 1, new Color(37, 37, 37).getRGB());
//        Gui.drawRect(x + 4, y + 4, x + width - 4, y + height - 4, new Color(22, 22, 22).getRGB());
//        Gui.drawRect(x + 4, y + 4, x + width - 4, y + height - 4, new Color(16, 16, 16).getRGB());
//        Gui.drawRect(x + 5, y + 5, x + width - 5, y + height - 5, new Color(22, 22, 22).getRGB());

        RenderUtil.drawSkeetRect(x, y, width, height, false, false);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if(!target.isInvisible()){
            RendererLivingEntity.NAME_TAG_RANGE = 0;
            RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 0;
            GuiInventory.drawEntityOnScreen((int) (x + 20), (int) (y + 42), 18, target.rotationYaw, -target.rotationPitch, target);
            RendererLivingEntity.NAME_TAG_RANGE = 64f;
            RendererLivingEntity.NAME_TAG_RANGE_SNEAK = 32f;
        }

        font.drawString(target.getName(), (float) (x + 40), (float) (y + 7), -1);

        fontSmall.drawString("HP: " + ((int) target.getHealth()) + " | Dist: " + ((int) MC.thePlayer.getDistanceToEntity(target)), (float) (x + 42), (float) (y + 25), -1);

        double barWidth = width - 50, barHeight = 6;

        double barOffset = 16;

        Color healthColor = new Color(ColorUtil.getHealthColor(target));

        Gui.drawRect(x + 40, y + barOffset, x + 40 + barWidth, y + barOffset + barHeight, Color.BLACK.getRGB());
        Gui.drawRect(x + 40 + 1, y + barOffset + 1, x + 40 + barWidth - 1, y + barOffset + barHeight - 1, healthColor.darker().darker().getRGB());
        Gui.drawRect(x + 40 + 1, y + barOffset + 1, x + 40 + ((Math.min(target.getHealth(), target.getMaxHealth()) / target.getMaxHealth()) * barWidth) - 1, y + barOffset + barHeight - 1, healthColor.getRGB());

        int amount = (int) (barWidth / 10);
        int length = (int) (barWidth / amount);

        for(int i = 1; i <= amount; i++) {
            Gui.drawRect(x + 40 + i * length - 0.5, y + barOffset, x + 40 + i * length, y + barOffset + barHeight, Color.BLACK.getRGB());
        }

        ArrayList<ItemStack> items = new ArrayList<>();

        for(int i = 3; i >= 0; i--) {
            ItemStack stack = target.getCurrentArmor(i);
            if(stack != null) {
                items.add(stack);
            }
        }

        if(target.getHeldItem() != null) {
            items.add(target.getHeldItem());
        }

        int i = 0;
        for(ItemStack stack : items) {
            MC.getRenderItem().renderItemAndEffectIntoGUI(stack, (int) (x + 40 + i * 15), (int) (y + 29));
            i++;
        }
    }

    public enum Mode {
        EXHIBITION("Exhibition"),
        FLUX("Flux"),
        NOVOLINE("Novoline"),
        NOVOLINEWTF("Novoline Wtf"),
        ASTOLFO("Astolfo"),
        MYTH("Myth");

        private final String name;

        Mode(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum NovolineType {
        NEW("New"),
        STELLA("Stella"),
        STYLES("Styles"),
        BASIC("Basic");

        private final String name;

        NovolineType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public enum NovolineWtfType {
        FIRST("1"),
        SECOND("2"),
        THIRD("3");

        private final String name;

        NovolineWtfType(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
