package me.jinthium.straight.api.clickgui.dropdown;

import best.azura.irc.utils.Wrapper;
import me.jinthium.straight.api.clickgui.dropdown.components.ModuleRect;
import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.modules.visual.Hud;
import me.jinthium.straight.impl.settings.NewModeSetting;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import me.jinthium.straight.impl.utils.animation.Animation;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.misc.HoveringUtil;
import me.jinthium.straight.impl.utils.render.ColorUtil;
import me.jinthium.straight.impl.utils.render.RenderUtil;
import me.jinthium.straight.impl.utils.render.RoundedUtil;
import me.jinthium.straight.impl.utils.render.StencilUtil;
import me.jinthium.straight.impl.utils.tuples.Pair;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.src.Config;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.jinthium.straight.impl.utils.misc.HoveringUtil.isHovering;


public class CategoryPanel implements Screen {

    private final Module.Category category;

    private final float rectWidth = 105;
    private final float categoryRectHeight = 15;
    private boolean typing;

    public final Pair<Animation, Animation> openingAnimations;
    private List<ModuleRect> moduleRects;

    public boolean isTyping() {
        return typing;
    }

    public CategoryPanel(Module.Category category, Pair<Animation, Animation> openingAnimations) {
        this.category = category;
        this.openingAnimations = openingAnimations;
    }

    @Override
    public void initGui() {
        if (moduleRects == null) {
            moduleRects = new ArrayList<>();
            if(category == Module.Category.CONFIG) moduleRects.add(new ModuleRect(new Module("Open Config Panel", Module.Category.CONFIG)));
            for (Module module : Client.INSTANCE.getModuleManager().getModulesInCategory(category).stream().sorted(Comparator.comparing(Module::getName)).toList()) {
                for(Setting setting : module.getSettingsList()){
                    if(setting == null)
                        continue;

                    if(setting instanceof NewModeSetting nms){
                        for(ModuleMode<?> mode : nms.getValues()){
                            if(mode == module.getCurrentMode() && !mode.getSettings().isEmpty()){
                                for(Setting settingss : mode.getSettings().keySet()){
                                    if(!module.getSettingsList().contains(settingss)) {
                                        settingss.addParent(nms, r -> (mode == module.getCurrentMode()));
                                        module.addSettings(settingss);
                                    }
                                }
                            }
                        }
                    }
                }
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
    ModuleRect configRect;

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if(moduleRects == null) {
            return;
        }

        if (openingAnimations == null) return;


        float alpha = Math.min(1, openingAnimations.getFirst().getOutput().floatValue());

        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        Pair<Color, Color> clientColors = Pair.of(hud.getHudColor((float) System.currentTimeMillis() / 600), hud.getHudColor((float) System.currentTimeMillis() / 600).darker());

        //Multiply it by the alpha again so that it eases faster
        float alphaValue = alpha * alpha;
        Color clientFirst = ColorUtil.applyOpacity(clientColors.getFirst(), alphaValue);
        Color clientSecond = ColorUtil.applyOpacity(clientColors.getSecond(), alphaValue);
        int textColor = ColorUtil.applyOpacity(-1, alpha);


        float x = category.getDrag().getX(), y = category.getDrag().getY();

        ScaledResolution sr = ScaledResolution.fetchResolution(mc);
        Module.allowedClickGuiHeight = 2 * sr.getScaledHeight() / 3f;

        float allowedHeight = Module.allowedClickGuiHeight;


        boolean hoveringMods = isHovering(x, y + categoryRectHeight, rectWidth, allowedHeight, mouseX, mouseY);

        RenderUtil.resetColor();
        float realHeight = Math.min(actualHeight, Module.allowedClickGuiHeight);


        if (DropdownClickGUI.gradient) {
            RoundedUtil.drawGradientVertical(x - .75f, y - .5f, rectWidth + 1.5f, realHeight + categoryRectHeight + 1.5f, 5, clientFirst, clientSecond);
        } else {
            RoundedUtil.drawRound(x - .75f, y - .5f, rectWidth + 1.5f, realHeight + categoryRectHeight + 2f, 7, new Color(25, 25, 25));
        }



        StencilUtil.initStencilToWrite();
        RoundedUtil.drawRound(x + 1, y + categoryRectHeight + 5, rectWidth - 2, realHeight - 6, 5, Color.BLACK);
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


        float yMovement = switch (category.getCategoryName()) {
            case "Movement", "Player", "Misc" -> .5f;
            case "Visuals" -> 1f;
            case "Exploit", "Scripts" -> 1;
            default -> 0;
        };


        RenderUtil.resetColor();
        float textWidth = normalFont22.getStringWidth(category.getCategoryName() + " ") / 2f;
        iconFont20.drawCenteredString(category.getIcon(), x + rectWidth / 2f + textWidth,
                y + iconFont20.getMiddleOfBox(categoryRectHeight) + yMovement, textColor);

        RenderUtil.resetColor();
        normalFont22.drawString(category.getCategoryName(), x + ((rectWidth / 2f - textWidth) - (iconFont20.getStringWidth(category.getIcon()) / 2f)),
                y + normalFont22.getMiddleOfBox(categoryRectHeight) + 2, textColor);
    }

    public void renderEffects() {
        float x = category.getDrag().getX(), y = category.getDrag().getY();

        float alpha = Math.min(1, openingAnimations.getFirst().getOutput().floatValue());
        alpha *= alpha;


        Hud hud = Client.INSTANCE.getModuleManager().getModule(Hud.class);
        Pair<Color, Color> clientColors = Pair.of(hud.getHudColor((float) System.currentTimeMillis() / 600), hud.getHudColor((float) System.currentTimeMillis() / 600).darker());
        Color clientFirst = ColorUtil.applyOpacity(clientColors.getFirst(), alpha);
        Color clientSecond = ColorUtil.applyOpacity(clientColors.getSecond(), alpha);

        float allowedHeight = Math.min(actualHeight, Module.allowedClickGuiHeight);
        boolean glow = true;

        if (DropdownClickGUI.gradient && glow) {
            RoundedUtil.drawGradientVertical(x - .75f, y - .5f, rectWidth + 1.5f, allowedHeight + categoryRectHeight + 1.5f, 5,
                    clientFirst, clientSecond);

        } else {
            RoundedUtil.drawRound(x - .75f, y - .5f, rectWidth + 1.5f, allowedHeight + categoryRectHeight + 1.5f, 5, (glow) ? clientFirst :
                    ColorUtil.applyOpacity(Color.BLACK, alpha));
        }
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

    private final List<ModuleRect> moduleRectFilter = new ArrayList<>();

    public List<ModuleRect> getModuleRects() {
        moduleRectFilter.clear();
        moduleRectFilter.addAll(moduleRects.stream().filter(moduleRect -> moduleRect.getSearchScore() > 60)
                .sorted(Comparator.comparingInt(ModuleRect::getSearchScore).reversed()).toList());

        return moduleRectFilter;
    }

}
