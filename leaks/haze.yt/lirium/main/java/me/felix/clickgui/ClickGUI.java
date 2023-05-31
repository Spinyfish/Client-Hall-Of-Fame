package me.felix.clickgui;

import com.viaversion.viaversion.util.Triple;
import de.lirium.Client;
import de.lirium.base.helper.OverlappingHelper;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.misc.KeyUtil;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import me.felix.clickgui.renderables.RenderableCategory;
import me.felix.util.dropshadow.JHLabsShaderRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class ClickGUI extends GuiScreen {

    private int alpha = 0;

    public boolean fade;

    public FontRenderer descriptionFont;

    public final ArrayList<RenderableCategory> renderableCategories = new ArrayList<>();

    private final OverlappingHelper<RenderableCategory> overlappingHelper = new OverlappingHelper<>();

    public ClickGUI() {
        final AtomicInteger x = new AtomicInteger(0);

        for (ModuleFeature.Category category : ModuleFeature.Category.values()) {
            renderableCategories.add(new RenderableCategory(x.addAndGet(110), 40, category));
        }
        this.overlappingHelper.init(renderableCategories);
    }

    private final Color categoryColor = new Color(20, 20, 21), categoryColor2 = new Color(20, 20, 20);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        alpha = (int) RenderUtil.getAnimationState(alpha, 215, 900);

        if (fade)
            RenderUtil.drawGradient(0, 0, width, height, Integer.MIN_VALUE, new Color(Client.INSTANCE.clientColor.getRed(), Client.INSTANCE.clientColor.getGreen(), Client.INSTANCE.clientColor.getBlue(), alpha).getRGB());
        else
            alpha = 0;

        for (RenderableCategory renderableCategory : renderableCategories) {
            renderableCategory.drawScreen(mouseX, mouseY, renderableCategory.x, renderableCategory.y);
        }

        renderableCategories.forEach(renderableCategory -> {
            if (renderableCategory.expanded)
                renderableCategory.renderableModules.forEach(renderableModule -> {
                    if (renderableModule.isHovered(mouseX, mouseY, (int) renderableModule.x, (int) renderableModule.y, renderableModule.width, renderableModule.height)) {
                        final String description = renderableModule.moduleFeature.getDescription();
                        if (descriptionFont == null) {
                            descriptionFont = Client.INSTANCE.getFontLoader().get("Arial", 16);
                        }
                        final int x = mouseX + 5, y = mouseY, width = descriptionFont.getStringWidth(description) + 4, height = descriptionFont.FONT_HEIGHT + 4;
                        JHLabsShaderRenderer.renderShadow(x, y, width, height, 20, categoryColor);
                        Gui.drawRect(x, y, x + width, (y + height), categoryColor2.getRGB());
                        StencilUtil.init();
                        Gui.drawRect(x, y, x + width, y + 2, -1);
                        StencilUtil.readBuffer(1);
                        RenderUtil.renderEnchantment();
                        StencilUtil.uninit();

                        GlStateManager.disableBlend();
                        GlStateManager.disableLighting();
                        GlStateManager.resetColor();

                        descriptionFont.drawString(description, x + width / 2F - descriptionFont.getStringWidth(description) / 2F, y + height / 2F - descriptionFont.FONT_HEIGHT / 2F, -1);

                    }
                });
        });
        if (descriptionFont != null)
            descriptionFont.drawString(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? "§a§lFAST INTERACTION MODE ENABLED" : "§fPress §lSHIFT §rfor fast interaction mode", 2, new ScaledResolution(mc).getScaledHeight() - descriptionFont.FONT_HEIGHT - 2, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private final Predicate<Triple<Integer, Integer, RenderableCategory>> categoryPredicate = triple -> {
        final int mouseX = triple.first(), mouseY = triple.second();
        final RenderableCategory category = triple.third();
        if(mouseX >= category.x && mouseX <= category.x + category.width && mouseY >= category.y && mouseY <= category.y + category.height) return true;
        return false;
    };

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        final RenderableCategory category = overlappingHelper.getInFront(mouseX, mouseY, categoryPredicate);

        if(category != null) {
            final List<RenderableCategory> list = overlappingHelper.resort(category);
            renderableCategories.clear();
            renderableCategories.addAll(list);
            category.onMouseClicked(mouseX, mouseY, mouseButton);
        }

        for (RenderableCategory renderableCategory : renderableCategories)
            renderableCategory.onMouseClicked(mouseX, mouseY, mouseButton, false);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (RenderableCategory renderableCategory : renderableCategories)
            renderableCategory.onMouseReleased(mouseX, mouseY);

        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        this.alpha = 0;
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
