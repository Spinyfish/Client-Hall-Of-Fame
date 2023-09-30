package dev.tenacity.ui.clickguis.dropdown;


import dev.tenacity.utils.tuples.Pair;
import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.ModuleCollection;
import dev.tenacity.module.impl.render.ClickGUIMod;
import dev.tenacity.module.impl.render.HUDMod;
import dev.tenacity.module.impl.render.PostProcessing;
import dev.tenacity.module.settings.Setting;
import dev.tenacity.ui.Screen;
import dev.tenacity.ui.clickguis.dropdown.components.ModuleRect;
import dev.tenacity.utils.animations.Animation;
import dev.tenacity.utils.misc.HoveringUtil;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.*;
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

    private final float rectWidth = 105;
    private final float categoryRectHeight = 17;
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
        if(moduleRects == null) {
            return;
        }

        if (category.equals(Category.SCRIPTS) && ModuleCollection.reloadModules) {
            moduleRects.clear();
            for (Module module : Tenacity.INSTANCE.getModuleCollection().getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
                moduleRects.add(new ModuleRect(module));
            }
            ModuleCollection.reloadModules = false;
            return;
        }

        if (openingAnimations == null) return;


        float alpha = Math.min(1, openingAnimations.getFirst().getOutput().floatValue());

        Theme theme = Theme.getCurrentTheme();
        Pair<Color, Color> clientColors = HUDMod.getClientColors();

        //Multiply it by the alpha again so that it eases faster
        float alphaValue = alpha * alpha;
        if (ClickGUIMod.transparent.isEnabled()) {
            alphaValue *= .75f;
        }
        Color clientFirst = ColorUtil.applyOpacity(clientColors.getFirst(), alphaValue);
        Color clientSecond = ColorUtil.applyOpacity(clientColors.getSecond(), alphaValue);
        int textColor = ColorUtil.applyOpacity(-1, alpha);


        float x = category.getDrag().getX(), y = category.getDrag().getY();

        if (ClickGUIMod.scrollMode.getMode().equals("Value")) {
            Module.allowedClickGuiHeight = ClickGUIMod.clickHeight.getValue().floatValue();
        } else {
            ScaledResolution sr = new ScaledResolution(mc);
            Module.allowedClickGuiHeight = 2 * sr.getScaledHeight() / 3f;
        }

        float allowedHeight = Module.allowedClickGuiHeight;


        boolean hoveringMods = isHovering(x, y + categoryRectHeight, rectWidth, allowedHeight, mouseX, mouseY);

        RenderUtil.resetColor();
        float realHeight = Math.min(actualHeight, Module.allowedClickGuiHeight);

        if (theme.equals(Theme.RED_COFFEE)) {
            Color temp = clientFirst;
            clientFirst = clientSecond;
            clientSecond = temp;
        }

        if (DropdownClickGUI.gradient) {
            Gui.drawGradientRect(x - .75f, y - .5f, x + rectWidth + 1.5f, y + realHeight + categoryRectHeight + 1.5f, clientFirst.getRGB(), clientSecond.getRGB());
        } else {
            Gui.drawRect(x - .75f, y - .5f, x + rectWidth + 1.5f, y + realHeight + categoryRectHeight + 1.5f, clientFirst.getRGB());
        }

        StencilUtil.initStencilToWrite();
        Gui.drawRect2(x, y + categoryRectHeight, rectWidth, realHeight, Color.BLACK.getRGB());
        StencilUtil.readStencilBuffer(1);


        double scroll = category.getScroll().getScroll();
        double count = 0;

        float rectHeight = 17F;


        for (ModuleRect moduleRect : getModuleRects()) {
            moduleRect.alpha = alpha;
            moduleRect.x = x - .5f;
            moduleRect.height = rectHeight;
            moduleRect.panelLimitY = y + categoryRectHeight - 2;
            moduleRect.y = (float) (y + categoryRectHeight + (count * rectHeight) + MathUtils.roundToHalf(scroll));
            moduleRect.width = rectWidth + 1;
            moduleRect.drawScreen(mouseX, mouseY);

            // count ups by one but then accounts for setting animation opening
            count += 1 + (moduleRect.getSettingSize() * (16 / rectHeight));
        }

        typing = getModuleRects().stream().anyMatch(ModuleRect::isTyping);


        actualHeight = (float) (count * rectHeight);

        if (hoveringMods) {
            category.getScroll().onScroll(25);
            float hiddenHeight = (float) ((count * rectHeight) - allowedHeight);
            category.getScroll().setMaxScroll(Math.max(0, hiddenHeight));
        }

        StencilUtil.uninitStencilBuffer();
        RenderUtil.resetColor();

        float yMovement;
        switch (category.name) {
            case "Movement":
            case "Player":
            case "Misc":
                yMovement = .5f;
                break;
            case "Render":
                yMovement = 1f;
                break;
            case "Exploit":
            case "Scripts":
                yMovement = 1;
                break;
            default:
                yMovement = 0;
                break;

        }

        RenderUtil.resetColor();
        float textWidth = lithiumBoldFont22.getStringWidth(category.name);

        iconFont20.drawCenteredString(
                category.icon,
                x + iconFont20.getMiddleOfBox(categoryRectHeight) + yMovement + 3,
                y + iconFont20.getMiddleOfBox(categoryRectHeight) + yMovement,
                textColor
        );

        RenderUtil.resetColor();

        lithiumBoldFont22.drawString(
                category.name,
                x + rectWidth - textWidth - 3,
                y + lithiumBoldFont22.getMiddleOfBox(categoryRectHeight),
                textColor
        );

    }

    public void renderEffects() {
        float x = category.getDrag().getX(), y = category.getDrag().getY();

        float alpha = Math.min(1, openingAnimations.getFirst().getOutput().floatValue());
        alpha *= alpha;


        Theme theme = Theme.getCurrentTheme();
        Pair<Color, Color> clientColors = theme.getColors();
        Color clientFirst = ColorUtil.applyOpacity(clientColors.getFirst(), alpha);
        Color clientSecond = ColorUtil.applyOpacity(clientColors.getSecond(), alpha);

        float allowedHeight = Math.min(actualHeight, Module.allowedClickGuiHeight);
        boolean glow = PostProcessing.glowOptions.getSetting("ClickGui").isEnabled();

        if (DropdownClickGUI.gradient && glow && ClickGUIMod.outlineAccent.isEnabled()) {
            if (theme.equals(Theme.RED_COFFEE)) {
                Color temp = clientFirst;
                clientFirst = clientSecond;
                clientSecond = temp;
            }

            RoundedUtil.drawGradientVertical(x - .75f, y - .5f, rectWidth + 1.5f, allowedHeight + categoryRectHeight + 1.5f, 5,
                    clientFirst, clientSecond);

        } else {
            Gui.drawRect2(x - .75f, y - .5f, rectWidth + 1.5f, allowedHeight + categoryRectHeight + 1.5f, ColorUtil.applyOpacity(Color.BLACK, alpha).getRGB());
        }
    }

    public void drawToolTips(int mouseX, int mouseY) {
        getModuleRects().forEach(moduleRect -> moduleRect.tooltipObject.drawScreen(mouseX, mouseY));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean canDrag = HoveringUtil.isHovering(category.getDrag().getX(), category.getDrag().getY(), rectWidth, categoryRectHeight, mouseX, mouseY);
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
