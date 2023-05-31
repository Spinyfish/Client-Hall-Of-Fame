package me.felix.clickgui.renderables;

import de.lirium.Client;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import me.felix.clickgui.abstracts.ClickGUIHandler;
import me.felix.util.dropshadow.JHLabsShaderRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderableCategory extends ClickGUIHandler {

    public int x, y, dragX, dragY, mouseX, mouseY;

    public ArrayList<RenderableModule> renderableModules = new ArrayList<>();

    private boolean dragging, isHovering;
    public boolean expanded = true;

    public final ModuleFeature.Category category;

    /***
     * @param startX the first "X" position when the clickGUI is called.
     * @param startY the first "Y" position when the clickGUI is called.
     * @param category the given module category
     */

    public RenderableCategory(int startX, int startY, ModuleFeature.Category category) {
        this.category = category;
        this.x = startX;
        this.y = startY;

        for (ModuleFeature moduleFeature : Client.INSTANCE.getModuleManager().getFeatures()) {
            if (moduleFeature.getCategory() != this.category)
                continue;
            renderableModules.add(new RenderableModule(moduleFeature));
        }

    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY) {
        if (expanded)
            for (final RenderableModule renderableModule : renderableModules) {
                renderableModule.onMouseReleased(mouseX, mouseY);
            }

        this.dragging = false;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseKey) {
        onMouseClicked(mouseX, mouseY, mouseKey, true);
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseKey, boolean dragging) {
        if (expanded)
            for (final RenderableModule renderableModule : renderableModules)
                renderableModule.onMouseClicked(mouseX, mouseY, mouseKey);
        if (!isHovering) return;
        if (mouseKey == 0 && dragging) {
            this.dragX = x - mouseX;
            this.dragY = y - mouseY;
            this.dragging = true;
        } else if (mouseKey == 1) {
            this.expanded = !expanded;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, int x, int y) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        if (this.dragging) {
            this.x = calculateMousePosition(mouseX, dragX);
            this.y = calculateMousePosition(mouseY, dragY);
        }

        this.isHovering = isHovered(mouseX, mouseY, x, y, width, height);

        final FontRenderer fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 20);

        //Panel rectangle code Block
        {
            JHLabsShaderRenderer.renderShadow(x, y, width, height, 20, new Color(20, 20, 21));
            Gui.drawRect(x, y, x + width, (y + height), new Color(20, 20, 20).getRGB());
            StencilUtil.init();
            Gui.drawRect(x, y, x + width, y + 2, -1);
            StencilUtil.readBuffer(1);
            RenderUtil.renderEnchantment();
            StencilUtil.uninit();

            GlStateManager.disableBlend();
            GlStateManager.disableLighting();
            GlStateManager.resetColor();
        }

        fontRenderer.drawString(category.getDisplay(), calculateMiddle(category.getDisplay(), fontRenderer, x, width), y + 2, -1);

        //Module rendering codeBlock
        if (expanded) {
            final AtomicInteger yAdditional = new AtomicInteger(height);

            for (final RenderableModule renderableModule : renderableModules) {
                renderableModule.drawScreen(mouseX, mouseY, x, y + yAdditional.getAndAdd(renderableModule.height));

                if (renderableModule.extended)
                    yAdditional.addAndGet(renderableModule.yAxisAdditional);

            }
        }

    }
}
