package dev.tenacity.module.impl.render.targethud;

import dev.tenacity.module.impl.render.TargetHUDMod;
import dev.tenacity.utils.animations.ContinualAnimation;
import dev.tenacity.utils.font.CustomFont;
import dev.tenacity.utils.objects.PlayerDox;
import dev.tenacity.utils.render.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class AutoDoxTargetHUD extends TargetHUD {

    public AutoDoxTargetHUD() {
        super("Auto-Dox");
    }

    public Map<EntityLivingBase, PlayerDox> doxMap = new HashMap<>();
    public final PlayerDox thePlayerDox = new PlayerDox(mc.thePlayer);

    private final ContinualAnimation animatedHealthBar = new ContinualAnimation();

    @Override
    public void render(float x, float y, float alpha, EntityLivingBase target) {
        PlayerDox dox = doxMap.get(target);
        if (dox == null) return;
        String state = dox.getState();
        CustomFont tahomaBoldFont27 = tahomaFont.boldSize(27),
                tahomaBoldFont16 = tahomaFont.boldSize(16),
                tahomaFont12 = tahomaFont.size(12),
                tahomaBoldFont10 = tahomaFont.boldSize(10),
                tahomaBoldFont14 = tahomaFont.boldSize(14);


        setWidth(Math.max(160, tahomaBoldFont27.getStringWidth(state) + 40 + tahomaBoldFont16.getStringWidth("DRIVER LICENSE")));
        setHeight(80);


        Color BLUE = new Color(40, 40, 160, (int) (alpha * 255));
        Color RED = new Color(220, 75, 60, (int) (alpha * 255));
        Color BLACK = ColorUtil.applyOpacity(new Color(40, 40, 80), alpha);


        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 3, ColorUtil.applyOpacity(new Color(212, 203, 178), alpha));

        Color goldColor = new Color(255, 221, 0, (int) (alpha * 255));
        Gui.drawRect2(x + 10, y + 3 + tahomaBoldFont27.getHeight(), getWidth() - 25, .5, goldColor.getRGB());
        Gui.drawRect2(x + 60, y + 6 + tahomaBoldFont27.getHeight(), getWidth() - (25 + 50), .5, goldColor.getRGB());


        RenderUtil.resetColor();
        tahomaBoldFont27.drawString(state, x + 5, y + 3, BLUE);
        RenderUtil.resetColor();
        tahomaFont12.drawString("USA", x + 5 + tahomaBoldFont27.getStringWidth(state), y + 4, BLUE);
        RenderUtil.resetColor();
        tahomaBoldFont16.drawString("DRIVER LICENSE", x + getWidth() - (15 + tahomaBoldFont16.getStringWidth("DRIVER LICENSE")), y + 5, BLUE);

        Gui.drawRect2(x + 5, y + 5 + tahomaBoldFont27.getHeight(), 45, getHeight() - (20 + tahomaBoldFont27.getHeight()), new Color(152, 210, 224, (int) (alpha * 255)).getRGB());

        float textX = x + 53;
        float textY = y + 13 + tahomaBoldFont27.getHeight();

        tahomaBoldFont10.drawString("DL", textX, textY, BLACK);
        tahomaBoldFont16.drawString(dox.getLiscenseNumber(), textX + 9, textY - 3, RED);

        tahomaBoldFont10.drawString("EXP", textX, textY + 9, BLACK);
        RenderUtil.resetColor();
        tahomaBoldFont14.drawString(dox.getExpirationDate(), textX + 11, textY + 7, RED);

        tahomaBoldFont10.drawString("FN", textX, textY + 17.5f, BLACK);
        RenderUtil.resetColor();
        tahomaBoldFont14.drawString(target.getName(), textX + 10, textY + 15.5f, BLACK);

        RenderUtil.resetColor();
        tahomaBoldFont10.drawString(dox.getTopAddress(), textX, textY + 23, BLACK);
        RenderUtil.resetColor();
        tahomaBoldFont10.drawString(dox.getBottomAddress(), textX, textY + 28, BLACK);

        RenderUtil.resetColor();
        tahomaBoldFont10.drawString("DOB", textX, textY + 36, BLUE);
        RenderUtil.resetColor();
        tahomaBoldFont14.drawString(dox.getDOB(), textX + 12, textY + 34, RED);


        tahomaBoldFont10.drawString("CLASS C", x + getWidth() - (15 + tahomaBoldFont10.getStringWidth("CLASS C")), y + 28, BLACK);
        RenderUtil.resetColor();
        tahomaBoldFont10.drawString("SEX " + (dox.isMale() ? "M" : "F"), x + getWidth() - (15 + tahomaBoldFont10.getStringWidth("CLASS C")), y + 35, BLACK);
        RenderUtil.resetColor();
        tahomaBoldFont10.drawString("WGT 300 lb", x + getWidth() - (15 + tahomaBoldFont10.getStringWidth("CLASS C")), y + 42, BLACK);


        RenderUtil.resetColor();


        float size = 45;
        StencilUtil.initStencilToWrite();
        Gui.drawRect2((x + getWidth() - 45), y + getHeight() - 30, 22, 18, -1);
        Gui.drawRect2(x + 5, y + 5 + tahomaBoldFont27.getHeight(), 45, getHeight() - (20 + tahomaBoldFont27.getHeight()), new Color(152, 210, 224, (int) (alpha * 255)).getRGB());
        StencilUtil.readStencilBuffer(1);

        RenderUtil.resetColor();
        TargetHUDMod.renderLayers = false;
        RenderUtil.color(-1, alpha);
        GLUtil.startBlend();
        GuiInventory.drawEntityOnScreen((int) (x + 5 + 45 / 2f), (int) (y + 5 + tahomaBoldFont27.getHeight() + 50 + size), (int) size, 0, 0, target);

        //RenderUtil.color(-1, alpha);
        Gui.drawRect2((x + getWidth() - 45), y + getHeight() - 30, 20, 18,
                new Color(150, 150, 150, ((int) alpha * 120)).getRGB());
        GLUtil.startBlend();
        RenderUtil.color(Color.GRAY.getRGB(), alpha * .5f);
        GuiInventory.drawEntityOnScreen((int) (x + getWidth() - 35), (int) (y + getHeight() + 8), 18, 0, 0, target);
        Gui.drawRect2((x + getWidth() - 45), y + getHeight() - 30, 20, 18, ColorUtil.tripleColor(150, alpha).getRGB());
        TargetHUDMod.renderLayers = true;
        StencilUtil.uninitStencilBuffer();


        float healthbarWidth = getWidth() - 10;
        float healthPercent = (target.getHealth() + target.getAbsorptionAmount()) / (target.getMaxHealth() + target.getAbsorptionAmount());
        float var = healthbarWidth * healthPercent;
        animatedHealthBar.animate(var, 18);


        Gui.drawRect2(x + 5, y + getHeight() - 9, healthbarWidth, 5, ColorUtil.applyOpacity(BLACK, .5f).getRGB());
        Gui.drawRect2(x + 5, y + getHeight() - 9, animatedHealthBar.getOutput(), 5, BLUE.getRGB());


    }

    @Override
    public void renderEffects(float x, float y, float alpha, boolean glow) {
        Color color = glow ? new Color(212, 203, 178) : Color.BLACK;
        RoundedUtil.drawRound(x, y, getWidth(), getHeight(), 3, ColorUtil.applyOpacity(color, alpha));
    }
}
