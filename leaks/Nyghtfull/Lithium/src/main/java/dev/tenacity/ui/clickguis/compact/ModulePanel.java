package dev.tenacity.ui.clickguis.compact;

import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.ModuleCollection;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.clickguis.compact.impl.ModuleRect;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.objects.Scroll;
import dev.tenacity.utils.render.ColorUtil;
import dev.tenacity.utils.render.RoundedUtil;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class ModulePanel implements Screen {

    public float x, y, rectWidth, rectHeight;
    public Category currentCat;
    public List<ModuleRect> moduleRects;
    private HashMap<Category, Scroll> scrollHashMap;
    private boolean draggingScrollBar;
    public boolean typing;


    @Override
    public void initGui() {
        scrollHashMap = new HashMap<>();
        for (Category category : Category.values()) {
            scrollHashMap.put(category, new Scroll());
        }
        scrollHashMap.put(null, new Scroll());
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        moduleRects.forEach(moduleRect -> moduleRect.keyTyped(typedChar, keyCode));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        typing = false;
        if (ModuleCollection.reloadModules) {
            initGui();
            ModuleCollection.reloadModules = false;
            return;
        }

        int count = 0;
        float leftSideHeight = 0;
        float rightSideHeight = 0;

        float maxScrollbarHeight = rectHeight - 10;
        Scroll scroll = scrollHashMap.get(currentCat);
        if (!Tenacity.INSTANCE.getSideGui().isFocused()) {
            scroll.onScroll(35);
        }

        for (ModuleRect moduleRect : moduleRects) {
            boolean rightSide = count % 2 == 1;
            moduleRect.rectWidth = (rectWidth - (90 + 40)) / 2;
            moduleRect.width = rectWidth;
            moduleRect.height = rectHeight;
            moduleRect.x = x + 100 + (rightSide ? moduleRect.rectWidth + 10 : 0);
            moduleRect.y = (float) (y + 10 + (rightSide ? rightSideHeight : leftSideHeight) + MathUtils.roundToHalf(scroll.getScroll()));
            moduleRect.drawScreen(mouseX, mouseY);
            if (!typing) {
                typing = moduleRect.typing;
            }

            if (rightSide) {
                rightSideHeight += moduleRect.rectHeight + 30;
            } else {
                leftSideHeight += moduleRect.rectHeight + 30;
            }

            count++;
        }
        scroll.setMaxScroll(Math.max(0, Math.max(leftSideHeight, rightSideHeight) - 100));

        float scrollBarHeight = maxScrollbarHeight * (rectHeight / scroll.getMaxScroll());
        scrollBarHeight = Math.min(rectHeight - 10, scrollBarHeight);
        float scrollYMath = ((-scroll.getScroll() / scroll.getMaxScroll()) * (maxScrollbarHeight - scrollBarHeight));

        boolean hoveredScrollBar = HoveringUtil.isHovering(x + rectWidth - 10, y + 5 + scrollYMath, 5, scrollBarHeight, mouseX, mouseY);
        RoundedUtil.drawRound(x + rectWidth - 10, y + 5, 5, maxScrollbarHeight, 2, new Color(32, 32, 32));
        Color scrollColor = new Color(41, 43, 47);
        RoundedUtil.drawRound(x + rectWidth - 10, y + 5 + scrollYMath, 5, scrollBarHeight, 2,
                (hoveredScrollBar || draggingScrollBar) ? ColorUtil.brighter(scrollColor, .8f) : scrollColor);

        Gui.drawRect2(x + rectWidth - 9, y + 5 + scrollYMath + scrollBarHeight / 2f - 2, 3, .5, new Color(64, 68, 75).getRGB());
        Gui.drawRect2(x + rectWidth - 9, y + 5 + scrollYMath + scrollBarHeight / 2f - .5, 3, .5, new Color(64, 68, 75).getRGB());
        Gui.drawRect2(x + rectWidth - 9, y + 5 + scrollYMath + scrollBarHeight / 2f + 1, 3, .5, new Color(64, 68, 75).getRGB());


        if (draggingScrollBar) {
            float percentOfScrollableHeight = ((y + 5) - mouseY) / maxScrollbarHeight;
            scroll.setRawScroll(Math.max(Math.min(0, scroll.getMaxScroll() * percentOfScrollableHeight), -scroll.getMaxScroll()));
        }

    }

    public void drawTooltips(int mouseX, int mouseY) {
        moduleRects.forEach(moduleRect -> moduleRect.tooltipObject.drawScreen(mouseX, mouseY));
    }


    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        float maxScrollbarHeight = rectHeight - 10;
        Scroll scroll = scrollHashMap.get(currentCat);
        float scrollBarHeight = maxScrollbarHeight * (rectHeight / scroll.getMaxScroll());
        float scrollYMath = ((-scroll.getScroll() / scroll.getMaxScroll()) * (maxScrollbarHeight - scrollBarHeight));
        boolean hoveredScrollBar = HoveringUtil.isHovering(x + rectWidth - 10, y + 5 + scrollYMath, 5, scrollBarHeight, mouseX, mouseY);

        if (hoveredScrollBar && button == 0) {
            draggingScrollBar = true;
        }
        moduleRects.forEach(moduleRect -> moduleRect.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (draggingScrollBar) {
            draggingScrollBar = false;
        }
        moduleRects.forEach(moduleRect -> moduleRect.mouseReleased(mouseX, mouseY, state));
    }
}
