package dev.tenacity.ui.clickguis.lithium;

import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.impl.render.ClickGUIMod;
import dev.tenacity.module.settings.Setting;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.clickguis.lithium.components.ModuleRect;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.*;
import dev.tenacity.utils.tuples.Pair;
import lombok.Getter;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dev.tenacity.utils.misc.HoveringUtil.isHovering;

public class CategoryPanel implements Screen {

    private final Category category;

    private final float categoryRectWidth = 130;
    private final float categoryRectHeight = 24;
    @Getter
    private boolean typing;

    public final Pair<Animation, Animation> openingAnimations;
    private List<ModuleRect> moduleRects;

    public CategoryPanel(Category category, Pair<Animation, Animation> openingAnimations) {
        this.category = category;
        this.openingAnimations = openingAnimations;
    }

    @Override
    public void initGui() {
        if (moduleRects == null) {
            moduleRects = new ArrayList<>();
            for (Module module : Tenacity.INSTANCE.getModuleCollection().getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
                moduleRects.add(new ModuleRect(module));
            }
        }

        if (moduleRects != null) {
            moduleRects.forEach(ModuleRect::initGui);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (moduleRects != null) {
            moduleRects.forEach(moduleRect -> moduleRect.keyTyped(typedChar, keyCode));
        }
    }

    public void onDrag(int mouseX, int mouseY) {
        category.getDrag().onDraw(mouseX, mouseY);
    }


    float actualHeight = 0;

    @Override
    public void drawScreen(int mouseX, int mouseY) {

        if (category == Category.SCRIPTS)
            return;

        if (moduleRects == null) {
            return;
        }

        if (openingAnimations == null) return;


        float alpha = Math.min(1, openingAnimations.getFirst().getOutput().floatValue());

        int textColor = ColorUtil.applyOpacity(-1, alpha);

        float x = category.getDrag().getX(), y = category.getDrag().getY();

        if (ClickGUIMod.scrollMode.getMode().equals("Value")) {
            Module.allowedClickGuiHeight = ClickGUIMod.clickHeight.getValue().floatValue();
        } else {
            ScaledResolution sr = new ScaledResolution(mc);
            Module.allowedClickGuiHeight = 2 * sr.getScaledHeight() / 3f;
        }

        float allowedHeight = Module.allowedClickGuiHeight;


        boolean hoveringMods = isHovering(x, y + categoryRectHeight, categoryRectWidth, allowedHeight, mouseX, mouseY);

        RenderUtil.resetColor();
        float realHeight = Math.min(actualHeight, Module.allowedClickGuiHeight);

        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(x + 0.75F, y + 0.75F, categoryRectWidth - 1.5F, categoryRectHeight + realHeight - 1.5F, 8, Color.BLACK);
        StencilUtil.readStencilBuffer(1);

        double scroll = category.getScroll().getScroll();
        double count = 0;

        float rectHeight = this.categoryRectHeight;

        for (ModuleRect moduleRect : getModuleRects()) {
            moduleRect.alpha = alpha;
            moduleRect.x = x;
            moduleRect.height = rectHeight;
            moduleRect.panelLimitY = y + categoryRectHeight - 2;
            moduleRect.y = (float) (y + categoryRectHeight + (count * rectHeight) + MathUtils.roundToHalf(scroll));
            moduleRect.width = categoryRectWidth;
            moduleRect.drawScreen(mouseX, mouseY);

            // count ups by one but then accounts for setting animation opening
            count += 1 + (moduleRect.getSettingSize() * (20 / rectHeight));
        }

        typing = getModuleRects().stream().anyMatch(ModuleRect::isTyping);

        actualHeight = (float) (count * rectHeight);

        if (hoveringMods) {
            category.getScroll().onScroll(25);
            float hiddenHeight = (float) ((count * rectHeight) - allowedHeight);
            category.getScroll().setMaxScroll(Math.max(0, hiddenHeight));
        }

        Gui.drawRect(
                x,
                y,
                x + categoryRectWidth,
                y + categoryRectHeight,
                0xCC000000
        );

        lithiumBoldFont26.drawString(
                category.name,
                x + categoryRectWidth / 2.0F - lithiumBoldFont26.getStringWidth(category.name) / 2.0F,
                y + lithiumBoldFont26.getMiddleOfBox(categoryRectHeight),
                textColor
        );

        StencilUtil.uninitStencilBuffer();
        RenderUtil.resetColor();
    }

    public void renderEffects(boolean bloom) {
        float x = category.getDrag().getX(), y = category.getDrag().getY();
        float realHeight = Math.min(actualHeight, Module.allowedClickGuiHeight);

        if (bloom) {
            RoundedUtil.drawRound(
                    x + 0.75F, y + 0.75F,
                    categoryRectWidth - 1.5F,
                    realHeight + categoryRectHeight - 1.5F,
                    8,
                    Color.BLACK
            );
        } else {
            RoundedUtil.drawRound(
                    x + 0.75F, y + 0.75F,
                    categoryRectWidth - 1.5F,
                    realHeight + categoryRectHeight - 1.5F,
                    8,
                    new Color(0xBB000000)
            );
        }
    }

    public void drawToolTips(int mouseX, int mouseY) {
        getModuleRects().forEach(moduleRect -> moduleRect.tooltipObject.drawScreen(mouseX, mouseY));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean canDrag = HoveringUtil.isHovering(category.getDrag().getX(), category.getDrag().getY(), categoryRectWidth, categoryRectHeight, mouseX, mouseY);
        category.getDrag().onClick(mouseX, mouseY, button, canDrag);
        getModuleRects().forEach(moduleRect -> moduleRect.mouseClicked(mouseX, mouseY, button));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        category.getDrag().onRelease(state);
        getModuleRects().forEach(moduleRect -> moduleRect.mouseReleased(mouseX, mouseY, state));
    }

    private final List<String> searchTerms = new ArrayList<>();
    private String searchText;
    private final List<ModuleRect> moduleRectFilter = new ArrayList<>();

    public List<ModuleRect> getModuleRects() {
        if (!Tenacity.INSTANCE.getSearchBar().isFocused()) {
            return moduleRects;
        }

        String search = Tenacity.INSTANCE.getSearchBar().getSearchField().getText();

        if (search.equals(searchText)) {
            return moduleRectFilter;
        } else {
            searchText = search;
        }

        moduleRectFilter.clear();
        for (ModuleRect moduleRect : moduleRects) {
            searchTerms.clear();
            Module module = moduleRect.module;

            searchTerms.add(module.getName());
            searchTerms.add(module.getCategory().name);
            if (!module.getAuthor().isEmpty()) {
                searchTerms.add(module.getAuthor());
            }
            for (Setting setting : module.getSettingsList()) {
                searchTerms.add(setting.name);
            }

            moduleRect.setSearchScore(FuzzySearch.extractOne(search, searchTerms).getScore());
        }

        moduleRectFilter.addAll(moduleRects.stream().filter(moduleRect -> moduleRect.getSearchScore() > 60)
                .sorted(Comparator.comparingInt(ModuleRect::getSearchScore).reversed()).collect(Collectors.toList()));

        return moduleRectFilter;
    }
}
