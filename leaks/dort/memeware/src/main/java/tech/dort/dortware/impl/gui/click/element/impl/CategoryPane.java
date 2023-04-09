package tech.dort.dortware.impl.gui.click.element.impl;

import net.minecraft.client.gui.Gui;
import tech.dort.dortware.Client;
import tech.dort.dortware.api.font.CustomFontRenderer;
import tech.dort.dortware.api.module.enums.ModuleCategory;
import tech.dort.dortware.impl.gui.click.GuiUtils;
import tech.dort.dortware.impl.gui.click.element.Element;
import tech.dort.dortware.impl.modules.render.ClickGUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class CategoryPane extends Element {

    private final ModuleCategory category;
    public final int headerHeight = 16;

    public List<ModulePane> getModPanes() {
        return modPanes;
    }

    private final List<ModulePane> modPanes = new ArrayList<>();
    private boolean expanded = true;
    private boolean selected;

    public CategoryPane(ModuleCategory category, int startX, int startY) {
        this.category = category;
        posX = startX;
        posY = startY;
        width = 100;
        initModPanes();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    private void initModPanes() {
        Client.INSTANCE.getModuleManager().getAllInCategory(category).forEach(module -> modPanes.add(new ModulePane(module, width)));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (ModulePane modPane : modPanes) {
            if (this.isExpanded()) {
                modPane.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void onDraw(int mouseX, int mouseY, float partialTicks) {
//        posX = 256;
//        posY = 256;
        GuiUtils.glStartFontRenderer();
        ClickGUI clickGUI = Client.INSTANCE.getModuleManager().get(ClickGUI.class);
        int color = clickGUI.getGuiColor();
        if (expanded) {

            height = headerHeight;
            for (ModulePane modPane : modPanes) {
                height += modPane.getHeight();
            }
            //draw category background
            Gui.drawRect(posX, posY, posX + width, posY + height, new Color(0, 0, 0, 0.6F).getRGB());

            int height2 = headerHeight;
            for (ModulePane modPane : modPanes) {
                modPane.setPos(posX, posY + height2);
                modPane.onDraw(mouseX, mouseY, partialTicks);
                height2 += modPane.getHeight();
            }

        }
        //HEADER
        final CustomFontRenderer font = Client.INSTANCE.getFontManager().getFont("Large").getRenderer();
        Gui.drawRect(posX, posY, posX + width, posY + headerHeight, color);
        float widthPadding = ((width - (font.getWidth(category.getName()))) / 2);
        float heightPadding = ((headerHeight - (font.getHeight(category.getName()))) / 2);
        font.drawString(category.getName(), posX + widthPadding, posY + heightPadding, -1);
        //2m + textWidth = 80
    }


    public void toggleExpansion() {
        expanded = !expanded;
    }

    public void movePane(int x, int y) {
        posX += x;
        posY += y;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
