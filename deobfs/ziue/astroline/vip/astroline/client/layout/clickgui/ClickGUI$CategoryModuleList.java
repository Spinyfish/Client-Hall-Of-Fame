/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.clickgui.ClickGUI
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.ColorValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.service.module.value.ModeValue
 *  vip.astroline.client.service.module.value.Value
 *  vip.astroline.client.service.module.value.ValueManager
 *  vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils
 *  vip.astroline.client.storage.utils.gui.clickgui.SmoothAnimationTimer
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.layout.clickgui;

import java.awt.Color;
import java.lang.invoke.LambdaMetafactory;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.clickgui.ClickGUI;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.ColorValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.service.module.value.ModeValue;
import vip.astroline.client.service.module.value.Value;
import vip.astroline.client.service.module.value.ValueManager;
import vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils;
import vip.astroline.client.storage.utils.gui.clickgui.SmoothAnimationTimer;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class ClickGUI.CategoryModuleList {
    public boolean isReleased = true;
    FloatValue draggingFloat = null;
    public Category cat;
    public float x;
    public float y;
    public float minY = -100.0f;
    float hue;
    float saturation = 0.0f;
    float brightness = 0.0f;
    float alpha = 0.0f;
    boolean colorSelectorDragging;
    boolean hueSelectorDragging;
    boolean alphaSelectorDragging;
    boolean expanded = false;
    public float scrollY = 0.0f;
    public SmoothAnimationTimer scrollAnimation = new SmoothAnimationTimer(0.0f);

    public ClickGUI.CategoryModuleList(Category category) {
        this.cat = category;
    }

    /*
     * Unable to fully structure code
     */
    public void draw(float x, float y, float mouseX, float mouseY) {
        if (!Mouse.isButtonDown((int)0)) {
            this.draggingFloat = null;
            this.isReleased = true;
        }
        font = FontManager.baloo16;
        this.x = x;
        this.y = y;
        if (RenderUtil.isHovering((float)mouseX, (float)mouseY, (float)x, (float)y, (float)(x + (ClickGUI.this.windowWidth - 100.0f)), (float)(y + ClickGUI.this.windowHeight))) {
            wheel = Mouse.getDWheel() / 2;
            this.scrollY += (float)wheel;
            if (this.scrollY <= this.minY) {
                this.scrollY = this.minY;
            }
            if (this.scrollY >= 0.0f) {
                this.scrollY = 0.0f;
            }
            this.minY = ClickGUI.this.windowHeight - 10.0f;
            this.scrollAnimation.setTarget(this.scrollY);
            var7_8 = this.scrollAnimation.update(true) == false;
        } else {
            Mouse.getDWheel();
        }
        x += 30.0f;
        y += 25.0f + this.scrollAnimation.getValue();
        var6_7 = Astroline.INSTANCE.moduleManager.getModules().stream().sorted(Comparator.comparing((Function<Module, String>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, toString(), (Lvip/astroline/client/service/module/Module;)Ljava/lang/String;)())).collect(Collectors.<T>toList()).iterator();
        block0: while (true) {
            block59: {
                if (var6_7.hasNext() == false) return;
                module = (Module)var6_7.next();
                if (module.getCategory() != this.cat && this.cat != Category.Global) continue;
                if (ClickGUI.this.isHovering(mouseX, mouseY, x - 10.0f, y - 12.5f, x + ClickGUI.this.windowWidth - 150.0f, y + 12.5f) && !ClickGUI.this.isHovering(mouseX, mouseY, x + ClickGUI.this.windowWidth - 400.0f + 216.0f, y - 5.0f, x + ClickGUI.this.windowWidth - 400.0f + 232.0f, y + 5.0f) && Mouse.isButtonDown((int)0) && this.isReleased && ClickGUI.this.currentModule != module && this.cat != Category.Global) {
                    this.isReleased = false;
                    ClickGUI.this.currentModule = module;
                    if (ClickGUI.this.currentModule.getMode() != null) {
                        ClickGUI.this.currentModule.getMode().getAlphaTimer().setValue(0.0f);
                    }
                    for (Value value : ValueManager.getValues()) {
                        if (!(value instanceof FloatValue)) continue;
                        ((FloatValue)value).getAnimationTimer().setValue(0.0f);
                    }
                }
                module.toggleButtonAnimation = AnimationUtils.getAnimationState((float)module.toggleButtonAnimation, (float)(module.isToggled() != false ? 222.0f : 218.0f), (float)((float)((double)Math.max(10.0f, Math.abs(module.toggleButtonAnimation - (module.isToggled() != false ? 222.0f : 218.0f)) * 35.0f) * 0.3)));
                if (module == ClickGUI.this.currentModule || this.cat == Category.Global) break block59;
                RenderUtil.drawRoundedRect((float)(x - 10.0f), (float)(y - 12.5f), (float)(x + ClickGUI.this.windowWidth - 150.0f), (float)(y + 12.5f + module.ySmooth.getValue()), (int)(Hud.isLightMode.getValue() != false ? -1 : -12565429));
                font.drawString(module.getName(), (float)((int)(x + 10.0f)), (float)((int)(y - 6.0f)), -6447715);
                if (module.isToggable) {
                    RenderUtil.drawRoundedRect((float)(x + ClickGUI.this.windowWidth - 400.0f + 218.0f), (float)(y - 3.0f), (float)(x + ClickGUI.this.windowWidth - 400.0f + 230.0f), (float)(y + 3.0f), (float)2.5f, (int)-1710619);
                    GuiRenderUtils.drawRoundedRect((float)(x + ClickGUI.this.windowWidth - 400.0f + module.toggleButtonAnimation), (float)(y - 4.0f), (float)8.0f, (float)8.0f, (float)360.0f, (int)(module.isToggled() != false ? Hud.hudColor1.getColorInt() : -1), (float)1.0f, (int)(module.isToggled() != false ? Hud.hudColor1.getColorInt() : -6316129));
                    if (ClickGUI.this.isHovering(mouseX, mouseY, x + ClickGUI.this.windowWidth - 400.0f + 216.0f, y - 5.0f, x + ClickGUI.this.windowWidth - 400.0f + 232.0f, y + 5.0f) && Mouse.isButtonDown((int)0) && this.isReleased) {
                        this.isReleased = false;
                        module.setEnabled(module.isToggled() == false);
                    }
                }
                module.ySmooth.update(false);
                y += module.ySmooth.getValue();
                this.minY -= 40.0f;
                ** GOTO lbl268
            }
            moduleYShouldBe = 0.0f;
            if (module != ClickGUI.this.lastModule) {
                ClickGUI.this.modeButtonAnimation = 0.0f;
                ClickGUI.this.lastModule = module;
            }
            RenderUtil.drawRoundedRect((float)(x - 10.5f), (float)(y - 13.0f), (float)(x + ClickGUI.this.windowWidth - 149.5f), (float)(y + 13.0f + module.ySmooth.getValue()), (int)Hud.hudColor1.getColorInt());
            RenderUtil.drawRoundedRect((float)(x - 10.0f), (float)(y - 12.5f), (float)(x + ClickGUI.this.windowWidth - 150.0f), (float)(y + 12.5f + module.ySmooth.getValue()), (int)(Hud.isLightMode.getValue() != false ? -1 : -12565429));
            FontManager.icon10.drawString("n", x, y - 3.0f, Hud.hudColor1.getColorInt());
            font.drawString(this.cat == Category.Global ? "Global" : module.getName(), x + 10.0f, y - 6.0f, -6447715);
            FontManager.icon15.drawString("h", x + ClickGUI.this.windowWidth - 150.0f - 12.0f, y - 4.0f, Hud.hudColor1.getColorInt());
            if (this.cat != Category.Global && module.isToggable()) {
                RenderUtil.drawRoundedRect((float)(x + ClickGUI.this.windowWidth - 400.0f + 218.0f), (float)(y - 3.0f), (float)(x + ClickGUI.this.windowWidth - 400.0f + 230.0f), (float)(y + 3.0f), (float)2.5f, (int)-1710619);
                GuiRenderUtils.drawRoundedRect((float)(x + ClickGUI.this.windowWidth - 400.0f + module.toggleButtonAnimation), (float)(y - 4.0f), (float)8.0f, (float)8.0f, (float)360.0f, (int)(module.isToggled() != false ? Hud.hudColor1.getColorInt() : -1), (float)1.0f, (int)(module.isToggled() != false ? Hud.hudColor1.getColorInt() : -6316129));
                if (ClickGUI.this.isHovering(mouseX, mouseY, x + ClickGUI.this.windowWidth - 400.0f + 216.0f, y - 5.0f, x + ClickGUI.this.windowWidth - 400.0f + 232.0f, y + 5.0f) && Mouse.isButtonDown((int)0) && this.isReleased) {
                    this.isReleased = false;
                    module.setEnabled(module.isToggled() == false);
                }
            }
            sliderCount = 0;
            sliderX = 0;
            booleanCount = 0;
            booleanX = 5;
            colorCount = 0;
            colorX = 0;
            for (Value value : ValueManager.getValueByModName((String)(this.cat == Category.Global ? "Global" : module.getName()))) {
                modeX = 5;
                modeCount = 0;
                if (!(value instanceof ModeValue)) continue;
                modeValue = (ModeValue)value;
                font.drawString(value.getKey(), (float)((int)(x - 1.0f)), (float)((int)(y + (moduleYShouldBe += 10.0f))), -6447715);
                var20_32 = modeValue.getModes();
                var21_35 = var20_32.length;
                for (var22_38 = 0; var22_38 < var21_35; ++modeCount, ++var22_38) {
                    mode = var20_32[var22_38];
                    isCurrentMode = modeValue.isCurrentMode(mode);
                    if ((float)modeX + font.getStringWidth(mode) + 20.0f >= ClickGUI.this.windowWidth - 140.0f) {
                        modeX = 5;
                        moduleYShouldBe += 15.0f;
                        modeCount = 0;
                    }
                    if (ClickGUI.this.isHovering(mouseX, mouseY, x + (float)modeX - 3.0f, y + moduleYShouldBe + 8.0f, x + (float)modeX + font.getStringWidth(mode) + 5.0f, y + moduleYShouldBe + 23.0f) && Mouse.isButtonDown((int)0) && this.isReleased && !isCurrentMode) {
                        this.isReleased = false;
                        modeValue.setValue(mode);
                        modeValue.getAlphaTimer().setValue(0.0f);
                    }
                    modeValue.getAlphaTimer().update(true);
                    curX = x + (float)modeX;
                    curY = y + moduleYShouldBe;
                    FontManager.icon15.drawString("n", curX - 6.0f, curY + 12.0f, -6447715);
                    if (isCurrentMode) {
                        ClickGUI.this.modeButtonAnimation = AnimationUtils.getAnimationState((float)ClickGUI.this.modeButtonAnimation, (float)2.1f, (float)((float)((double)Math.max(1.0f, Math.abs(ClickGUI.this.modeButtonAnimation - 2.1f) * 25.0f) * 0.3)));
                        RenderUtil.drawCircle((float)(curX - 2.2f), (float)(curY + 16.3f), (int)0, (int)360, (float)ClickGUI.this.modeButtonAnimation, (int)Hud.hudColor1.getColorInt());
                        FontManager.icon10.drawString("k", curX - 4.8f, curY + 13.4f, Hud.hudColor1.getColorInt());
                    }
                    font.drawString(mode, (float)((int)(x + (float)modeX + 4.0f)), (float)((int)(y + moduleYShouldBe + 10.0f)), -6447715);
                    modeX = (int)((float)modeX + (font.getStringWidth(mode) + 20.0f));
                }
                if (modeCount < true) continue;
                moduleYShouldBe += 15.0f;
            }
            for (Value value : ValueManager.getValueByModName((String)(this.cat == Category.Global ? "Global" : module.getName()))) {
                if (!(value instanceof FloatValue)) continue;
                floatValue = (FloatValue)value;
                x1 = x + (float)sliderX;
                dymX = 110.0f;
                percentShould = (floatValue.getValueState() - floatValue.getMin()) / (floatValue.getMax() - floatValue.getMin());
                floatValue.getAnimationTimer().setTarget(percentShould * 10.0f);
                floatValue.getAnimationTimer().update(true);
                percent = floatValue.getAnimationTimer().getValue() / 10.0f;
                y1 = y + moduleYShouldBe + 23.0f;
                y2 = y + moduleYShouldBe + 24.0f;
                font.drawString(value.getKey(), (float)((int)(x + (float)sliderX)), (float)((int)(y + 9.0f + moduleYShouldBe)), -6447715);
                round = (float)Math.round(floatValue.getValueState() * 100.0f) / 100.0f + "";
                font.drawString(round + (floatValue.getUnit() == null ? "" : floatValue.getUnit()), (float)((int)(x + (float)sliderX + dymX - font.getStringWidth(round + (floatValue.getUnit() == null ? "" : floatValue.getUnit())))), (float)((int)(y + 9.0f + moduleYShouldBe)), -6447715);
                RenderUtil.drawRect((float)x1, (float)y1, (float)(x1 + dymX), (float)y2, (int)-1842205);
                RenderUtil.drawRect((float)x1, (float)y1, (float)(x1 + dymX * percent), (float)y2, (int)Hud.hudColor1.getColorInt());
                RenderUtil.circle((float)(x1 + dymX * percent), (float)((y1 + y2) / 2.0f), (float)2.0f, (int)Hud.hudColor1.getColorInt());
                if (ClickGUI.this.isHovering(mouseX, mouseY, x1, y1 - 4.0f, x1 + dymX, y2 + 4.0f) && Mouse.isButtonDown((int)0) && this.isReleased) {
                    this.isReleased = false;
                    this.draggingFloat = floatValue;
                }
                if (this.draggingFloat == floatValue) {
                    draggingValue = mouseX - x1;
                    if (draggingValue > 110.0f) {
                        draggingValue = 110.0f;
                    }
                    if (draggingValue < 0.0f) {
                        draggingValue = 0.0f;
                    }
                    curValue = (float)Math.round((draggingValue / 110.0f * (floatValue.getMax() - floatValue.getMin()) + floatValue.getMin()) / floatValue.getIncrement()) * floatValue.getIncrement();
                    floatValue.setValue(curValue);
                }
                sliderX += 125;
                ++sliderCount;
                if (!((float)sliderX + dymX >= ClickGUI.this.windowWidth - 150.0f)) continue;
                moduleYShouldBe += 20.0f;
                sliderCount = 0;
                sliderX = 0;
            }
            if (sliderCount > 0) {
                moduleYShouldBe += 25.0f;
            }
            for (Value value : ValueManager.getValueByModName((String)(this.cat == Category.Global ? "Global" : module.getName()))) {
                if (!(value instanceof BooleanValue)) continue;
                booleanValue = (BooleanValue)value;
                if ((float)booleanX + font.getStringWidth(booleanValue.getKey()) + 20.0f >= ClickGUI.this.windowWidth - 140.0f) {
                    booleanX = 5;
                    moduleYShouldBe += 15.0f;
                    booleanCount = 0;
                }
                isChecked = booleanValue.getValueState();
                if (ClickGUI.this.isHovering(mouseX, mouseY, x + (float)booleanX - 3.0f, y + moduleYShouldBe + 8.0f, x + (float)booleanX + font.getStringWidth(booleanValue.getKey()) + 5.0f, y + moduleYShouldBe + 23.0f) && Mouse.isButtonDown((int)0) && this.isReleased) {
                    this.isReleased = false;
                    booleanValue.setValue((Object)(booleanValue.getValueState() == false));
                }
                FontManager.icon15.drawString("j", x + (float)booleanX - 5.0f, y + moduleYShouldBe + 11.5f, isChecked != false ? Hud.hudColor1.getColorInt() : -3289651);
                font.drawString(booleanValue.getKey(), (float)((int)(x + (float)booleanX + 4.0f)), (float)((int)(y + 10.0f + moduleYShouldBe)), -6447715);
                ++booleanCount;
                booleanX = (int)((float)booleanX + (font.getStringWidth(booleanValue.getKey()) + 20.0f));
            }
            var15_19 = ValueManager.getValueByModName((String)(this.cat == Category.Global ? "Global" : module.getName())).iterator();
            while (true) {
                if (var15_19.hasNext()) {
                    value = (Value)var15_19.next();
                    if (!(value instanceof ColorValue)) continue;
                    colorV = (ColorValue)value;
                    if ((float)colorX + font.getStringWidth(colorV.getKey()) + 40.0f >= ClickGUI.this.windowWidth - 140.0f) {
                        colorX = 0;
                        moduleYShouldBe += 95.0f;
                        colorCount = 0;
                    }
                    w = colorX + 75;
                    h = moduleYShouldBe + 33.0f;
                    x2 = this.getExpandedX(x, w);
                    y2 = this.getExpandedY(y, h);
                    width = this.getExpandedWidth(x, w);
                    height = this.getExpandedHeight(x, w);
                    black = -16777216;
                    font.drawString(colorV.getKey(), (float)((int)(x + (float)colorX + 4.0f)), (float)((int)(y + 21.0f + moduleYShouldBe)), -6447715);
                    guiAlpha = 40;
                    Gui.drawRect((double)(x2 - 0.5f), (double)(y2 - 0.5f), (double)(x2 + width + 0.5f), (double)(y2 + height + 0.5f), (int)-1);
                    color = colorV.getColorInt();
                    colorAlpha = color >> 24 & 255;
                    minAlpha = Math.min(guiAlpha, colorAlpha);
                    if (colorAlpha < 255) {
                        this.drawCheckeredBackground(x2, y2, x2 + width, y2 + height);
                    }
                    newColor = new Color(color >> 16 & 255, color >> 8 & 255, color & 255, minAlpha).getRGB();
                    ClickGUI.this.drawGradientRect(x2, y2, x2 + width - 50.0f, y2 + height - 70.0f, newColor, ClickGUI.darker((int)newColor, (float)0.6f));
                    GL11.glTranslated((double)0.0, (double)0.0, (double)3.0);
                    expandedX = this.getExpandedX(x, w);
                    expandedY = this.getExpandedY(y, h);
                    expandedWidth = this.getExpandedWidth(x, w);
                    expandedHeight = this.getExpandedHeight(x, w);
                    Gui.drawRect((double)expandedX, (double)expandedY, (double)(expandedX + expandedWidth), (double)(expandedY + expandedHeight), (int)black);
                    Gui.drawRect((double)(expandedX + 0.5f), (double)(expandedY + 0.5f), (double)(expandedX + expandedWidth - 0.5f), (double)(expandedY + expandedHeight - 0.5f), (int)new Color(0x39393B).getRGB());
                    Gui.drawRect((double)(expandedX + 1.0f), (double)(expandedY + 1.0f), (double)(expandedX + expandedWidth - 1.0f), (double)(expandedY + expandedHeight - 1.0f), (int)new Color(0x232323).getRGB());
                    colorPickerSize = expandedWidth - 9.0f - 8.0f;
                    colorPickerLeft = expandedX + 3.0f;
                    colorPickerTop = expandedY + 3.0f;
                    colorPickerRight = colorPickerLeft + colorPickerSize;
                    colorPickerBottom = colorPickerTop + colorPickerSize;
                    selectorWhiteOverlayColor = new Color(255, 255, 255, Math.min(40, 180)).getRGB();
                    Gui.drawRect((double)(colorPickerLeft - 0.5f), (double)(colorPickerTop - 0.5f), (double)(colorPickerRight + 0.5f), (double)(colorPickerBottom + 0.5f), (int)black);
                    this.drawColorPickerRect(colorPickerLeft, colorPickerTop, colorPickerRight, colorPickerBottom);
                    hueSliderLeft = this.saturation * (colorPickerRight - colorPickerLeft);
                    alphaSliderTop = (1.0f - this.brightness) * (colorPickerBottom - colorPickerTop);
                    if (this.colorSelectorDragging) {
                        hueSliderRight = colorPickerRight - colorPickerLeft;
                        alphaSliderBottom = mouseX - colorPickerLeft;
                        this.saturation = alphaSliderBottom / hueSliderRight;
                        hueSliderLeft = alphaSliderBottom;
                        hueSliderYDif = colorPickerBottom - colorPickerTop;
                        hueSelectorY = mouseY - colorPickerTop;
                        this.brightness = 1.0f - hueSelectorY / hueSliderYDif;
                        alphaSliderTop = hueSelectorY;
                        this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness), false, colorV);
                    }
                    hueSliderRight = colorPickerLeft + hueSliderLeft - 0.5f;
                    alphaSliderBottom = colorPickerTop + alphaSliderTop - 0.5f;
                    hueSliderYDif = colorPickerLeft + hueSliderLeft + 0.5f;
                    hueSelectorY = colorPickerTop + alphaSliderTop + 0.5f;
                    Gui.drawRect((double)(hueSliderRight - 0.5f), (double)(alphaSliderBottom - 0.5f), (double)hueSliderRight, (double)(hueSelectorY + 0.5f), (int)black);
                    Gui.drawRect((double)hueSliderYDif, (double)(alphaSliderBottom - 0.5f), (double)(hueSliderYDif + 0.5f), (double)(hueSelectorY + 0.5f), (int)black);
                    Gui.drawRect((double)hueSliderRight, (double)(alphaSliderBottom - 0.5f), (double)hueSliderYDif, (double)alphaSliderBottom, (int)black);
                    Gui.drawRect((double)hueSliderRight, (double)hueSelectorY, (double)hueSliderYDif, (double)(hueSelectorY + 0.5f), (int)black);
                    Gui.drawRect((double)hueSliderRight, (double)alphaSliderBottom, (double)hueSliderYDif, (double)hueSelectorY, (int)selectorWhiteOverlayColor);
                    hueSliderLeft = colorPickerRight + 3.0f;
                    hueSliderRight = hueSliderLeft + 8.0f;
                    hueSliderYDif = colorPickerBottom - colorPickerTop;
                    hueSelectorY = (1.0f - this.hue) * hueSliderYDif;
                    if (this.hueSelectorDragging) {
                        inc = mouseY - colorPickerTop;
                        this.hue = 1.0f - inc / hueSliderYDif;
                        hueSelectorY = inc;
                        this.updateColor(Color.HSBtoRGB(this.hue, this.saturation, this.brightness), false, colorV);
                    }
                    Gui.drawRect((double)(hueSliderLeft - 0.5f), (double)(colorPickerTop - 0.5f), (double)(hueSliderRight + 0.5f), (double)(colorPickerBottom + 0.5f), (int)black);
                    hsHeight = colorPickerBottom - colorPickerTop;
                    alphaSelectorX = hsHeight / 5.0f;
                    asLeft = colorPickerTop;
                    i2 = 0;
                } else {
                    if (colorCount >= 1) {
                        moduleYShouldBe += 100.0f;
                    }
                    if (booleanCount >= 1) {
                        moduleYShouldBe += 15.0f;
                    }
                    if (moduleYShouldBe == 0.0f) {
                        moduleYShouldBe = 15.0f;
                        font.drawString("No Settings.", (float)((int)x), (float)((int)(y + 12.0f)), -6447715);
                    }
                    module.ySmooth.setTarget(moduleYShouldBe);
                    module.ySmooth.update(true);
                    y += module.ySmooth.getValue();
                    this.minY -= moduleYShouldBe + 40.0f;
                    if (this.cat == Category.Global) {
                        return;
                    }
lbl268:
                    // 3 sources

                    y += 40.0f;
                    continue block0;
                }
                while ((float)i2 < 5.0f) {
                    last = (float)i2 == 4.0f;
                    ClickGUI.this.drawGradientRect(hueSliderLeft, asLeft, hueSliderRight, asLeft + alphaSelectorX, new Color(Color.HSBtoRGB(1.0f - 0.2f * (float)i2, 1.0f, 1.0f)).getRGB(), new Color(Color.HSBtoRGB(1.0f - 0.2f * (float)(i2 + 1), 1.0f, 1.0f)).getRGB());
                    if (!last) {
                        asLeft += alphaSelectorX;
                    }
                    ++i2;
                }
                hsTop = colorPickerTop + hueSelectorY - 0.5f;
                asRight = colorPickerTop + hueSelectorY + 0.5f;
                Gui.drawRect((double)(hueSliderLeft - 0.5f), (double)(hsTop - 0.5f), (double)hueSliderLeft, (double)(asRight + 0.5f), (int)black);
                Gui.drawRect((double)hueSliderRight, (double)(hsTop - 0.5f), (double)(hueSliderRight + 0.5f), (double)(asRight + 0.5f), (int)black);
                Gui.drawRect((double)hueSliderLeft, (double)(hsTop - 0.5f), (double)hueSliderRight, (double)hsTop, (int)black);
                Gui.drawRect((double)hueSliderLeft, (double)asRight, (double)hueSliderRight, (double)(asRight + 0.5f), (int)black);
                Gui.drawRect((double)hueSliderLeft, (double)hsTop, (double)hueSliderRight, (double)asRight, (int)selectorWhiteOverlayColor);
                alphaSliderTop = colorPickerBottom + 3.0f;
                alphaSliderBottom = alphaSliderTop + 8.0f;
                if (mouseX <= colorPickerLeft || mouseY <= alphaSliderTop || mouseX >= colorPickerRight || mouseY >= alphaSliderBottom) {
                    this.alphaSelectorDragging = false;
                }
                z2 = Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
                r2 = z2 >> 16 & 255;
                g2 = z2 >> 8 & 255;
                b2 = z2 & 255;
                hsHeight = colorPickerRight - colorPickerLeft;
                alphaSelectorX = this.alpha * hsHeight;
                if (this.alphaSelectorDragging) {
                    asLeft = mouseX - colorPickerLeft;
                    this.alpha = asLeft / hsHeight;
                    alphaSelectorX = asLeft;
                    this.updateColor(new Color(r2, g2, b2, (int)(this.alpha * 255.0f)).getRGB(), true, colorV);
                }
                Gui.drawRect((double)(colorPickerLeft - 0.5f), (double)(alphaSliderTop - 0.5f), (double)(colorPickerRight + 0.5f), (double)(alphaSliderBottom + 0.5f), (int)black);
                this.drawCheckeredBackground(colorPickerLeft, alphaSliderTop, colorPickerRight, alphaSliderBottom);
                ClickGUI.this.drawGradientRect((double)colorPickerLeft, (double)alphaSliderTop, (double)colorPickerRight, (double)alphaSliderBottom, true, new Color(r2, g2, b2, 0).getRGB(), new Color(r2, g2, b2, guiAlpha).getRGB());
                asLeft = colorPickerLeft + alphaSelectorX - 0.5f;
                asRight = colorPickerLeft + alphaSelectorX + 0.5f;
                Gui.drawRect((double)(asLeft - 0.5f), (double)alphaSliderTop, (double)(asRight + 0.5f), (double)alphaSliderBottom, (int)black);
                Gui.drawRect((double)asLeft, (double)alphaSliderTop, (double)asRight, (double)alphaSliderBottom, (int)selectorWhiteOverlayColor);
                GL11.glTranslated((double)0.0, (double)0.0, (double)-3.0);
                if (ClickGUI.this.isHovering(mouseX, mouseY, x2, y2, x2 + 80.0f, y2 + 80.0f)) {
                    if (mouseX > colorPickerLeft && mouseY > colorPickerTop && mouseX < colorPickerRight && mouseY < colorPickerBottom) {
                        if (Mouse.isButtonDown((int)0)) {
                            this.colorSelectorDragging = true;
                            this.isReleased = false;
                        } else {
                            this.colorSelectorDragging = false;
                            this.isReleased = true;
                        }
                    } else if (this.colorSelectorDragging) {
                        this.colorSelectorDragging = false;
                    }
                    if (mouseX > hueSliderLeft && mouseY > colorPickerTop && mouseX < hueSliderRight && mouseY < colorPickerBottom) {
                        if (Mouse.isButtonDown((int)0)) {
                            this.hueSelectorDragging = true;
                            this.isReleased = false;
                        } else {
                            this.hueSelectorDragging = false;
                            this.isReleased = true;
                        }
                    } else if (this.hueSelectorDragging) {
                        this.hueSelectorDragging = false;
                    }
                    if (mouseX > colorPickerLeft && mouseY > alphaSliderTop && mouseX < colorPickerRight && mouseY < alphaSliderBottom) {
                        if (Mouse.isButtonDown((int)0)) {
                            this.alphaSelectorDragging = true;
                            this.isReleased = false;
                        } else {
                            this.alphaSelectorDragging = false;
                            this.isReleased = true;
                        }
                    } else if (this.alphaSelectorDragging) {
                        this.alphaSelectorDragging = false;
                    }
                }
                ++colorCount;
                colorX += 85;
            }
            break;
        }
    }

    public float getExpandedX(float x, float w) {
        return x + w - 80.333336f;
    }

    public float getExpandedY(float y, float h) {
        return y + h;
    }

    public float getExpandedWidth(float x, float w) {
        float right = x + w;
        return right - this.getExpandedX(x, w);
    }

    public float getExpandedHeight(float x, float w) {
        return this.getExpandedWidth(x, w);
    }

    public void updateColor(int hex, boolean hasAlpha, ColorValue colorValue) {
        if (hasAlpha) {
            colorValue.setValueInt(hex);
        } else {
            colorValue.setValueInt(new Color(hex >> 16 & 0xFF, hex >> 8 & 0xFF, hex & 0xFF, (int)(this.alpha * 255.0f)).getRGB());
        }
    }

    public void setColor(int color, ColorValue colorValue) {
        colorValue.setValueInt(color);
    }

    private void drawColorPickerRect(float left, float top, float right, float bottom) {
        int hueBasedColor = Color.HSBtoRGB(this.hue, 1.0f, 1.0f);
        ClickGUI.this.drawGradientRect((double)left, (double)top, (double)right, (double)bottom, true, new Color(0xFFFFFF).getRGB(), hueBasedColor);
        ClickGUI.this.drawGradientRect(left, top, right, bottom, 0, Color.black.getRGB());
    }

    private void drawCheckeredBackground(float x, float y, float x2, float y2) {
        Gui.drawRect((double)x, (double)y, (double)x2, (double)y2, (int)new Color(0xFFFFFF).getRGB());
        boolean offset = false;
        while (y < y2) {
            for (float x1 = x + 0.0f; x1 < x2; x1 += 2.0f) {
                if (!(x1 <= x2 - 1.0f)) continue;
                Gui.drawRect((double)x1, (double)y, (double)(x1 + 1.0f), (double)(y + 1.0f), (int)new Color(0x808080).getRGB());
            }
            y += 1.0f;
        }
    }
}
