package dev.echo.ui.clickguis.dropdown;

import dev.echo.Echo;
import dev.echo.utils.tuples.Pair;
import dev.echo.module.Category;
import dev.echo.module.Module;
import dev.echo.module.ModuleCollection;
import dev.echo.module.impl.render.ClickGUIMod;
import dev.echo.module.impl.render.HUDMod;
import dev.echo.module.impl.render.PostProcessing;
import dev.echo.module.settings.Setting;
import dev.echo.ui.Screen;
import dev.echo.ui.clickguis.dropdown.components.ModuleRect;
import dev.echo.utils.animations.Animation;
import dev.echo.utils.misc.HoveringUtil;
import dev.echo.utils.misc.MathUtils;
import dev.echo.utils.render.*;
import lombok.Getter;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static dev.echo.utils.misc.HoveringUtil.isHovering;

public class CategoryPanel implements Screen {

    private final Category category;

    private final float rectWidth = 105;
    private final float categoryRectHeight = 15;
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
            for (Module module : Echo.INSTANCE.getModuleCollection().getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
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
            for (Module module : Echo.INSTANCE.getModuleCollection().getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).collect(Collectors.toList())) {
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


        if (ClickGUIMod.outlineAccent.isEnabled()) {
            if (theme.equals(Theme.RED_COFFEE)) {
                Color temp = clientFirst;
                clientFirst = clientSecond;
                clientSecond = temp;
            }
            if (DropdownClickGUI.gradient) {
                RoundedUtil.drawGradientVertical(x - .75f, y - .5f, rectWidth + 1.5f, realHeight + categoryRectHeight + 1.5f, 5, clientFirst, clientSecond);
            } else {
                RoundedUtil.drawRound(x - .75f, y - .5f, rectWidth + 1.5f, realHeight + categoryRectHeight + 1.5f, 5, clientFirst);
            }
        } else {
            RoundedUtil.drawRound(x - .75f, y - .5f, rectWidth + 1.5f, realHeight + categoryRectHeight + 1.5f, 5,
                    ColorUtil.tripleColor(20, alphaValue));
            if (!ClickGUIMod.transparent.isEnabled()) {
                Gui.drawRect2(x, y + categoryRectHeight, rectWidth, 3, clientFirst.getRGB());
            }
            if (DropdownClickGUI.gradient) {
                RoundedUtil.drawGradientVertical(x + 1, y + categoryRectHeight + 1, rectWidth - 2, realHeight - 2, 4, clientFirst, clientSecond);
            } else {
                RoundedUtil.drawRound(x + .8f, y + categoryRectHeight + .8f, rectWidth - 1.6f, realHeight - 1.6f, 3.5f, clientFirst);
            }

        }



        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(x + 1, y + categoryRectHeight + 5, rectWidth - 2, realHeight - 6, 3, Color.BLACK);
        Gui.drawRect2(x, y + categoryRectHeight, rectWidth, 10, Color.BLACK.getRGB());
        StencilUtil.readStencilBuffer(1);


        double scroll = category.getScroll().getScroll();
        double count = 0;

        float rectHeight = 14;


        for (ModuleRect moduleRect : getModuleRects()) {
            moduleRect.alpha = alpha;
            moduleRect.x = x - .5f;
            moduleRect.height = rectHeight;
            moduleRect.panelLimitY = y + categoryRectHeight - 2;
            moduleRect.y = (float) (y + categoryRectHeight + (count * rectHeight) + MathUtils.roundToHalf(scroll));
            moduleRect.width = rectWidth + 1;
            moduleRect.drawScreen(mouseX, mouseY);

            // count ups by one but then accounts for setting animation opening
            count += 1 + (moduleRect.getSettingSize() * (16 / 14f));
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
        float textWidth = echoBoldFont22.getStringWidth(category.name + " ") / 2f;
        iconFont20.drawCenteredString(category.icon, x + rectWidth / 2f + textWidth,
                y + iconFont20.getMiddleOfBox(categoryRectHeight) + yMovement, textColor);

        RenderUtil.resetColor();
        echoBoldFont22.drawString(category.name, x + ((rectWidth / 2f - textWidth) - (iconFont20.getStringWidth(category.icon) / 2f)),
                y + echoBoldFont22.getMiddleOfBox(categoryRectHeight), textColor);

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

        if(!ClickGUIMod.outlineAccent.isEnabled()){
            RoundedUtil.drawRound(x - .75f, y - .5f, rectWidth + 1.5f, allowedHeight + categoryRectHeight + 1.5f, 5, Color.BLACK);
            return;
        }


        if (DropdownClickGUI.gradient && glow && ClickGUIMod.outlineAccent.isEnabled()) {
            if (theme.equals(Theme.RED_COFFEE)) {
                Color temp = clientFirst;
                clientFirst = clientSecond;
                clientSecond = temp;
            }

            RoundedUtil.drawGradientVertical(x - .75f, y - .5f, rectWidth + 1.5f, allowedHeight + categoryRectHeight + 1.5f, 5,
                    clientFirst, clientSecond);

        } else {
            RoundedUtil.drawRound(x - .75f, y - .5f, rectWidth + 1.5f, allowedHeight + categoryRectHeight + 1.5f, 5, (glow && ClickGUIMod.outlineAccent.isEnabled()) ? clientFirst :
                    ColorUtil.applyOpacity(Color.BLACK, alpha));
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
        if (!Echo.INSTANCE.getSearchBar().isFocused()) {
            return moduleRects;
        }

        String search = Echo.INSTANCE.getSearchBar().getSearchField().getText();

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
