/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils
 *  vip.astroline.client.storage.utils.other.MathUtils
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 */
package vip.astroline.client.layout.dropdown.components.impl.slider;

import java.awt.Color;
import java.math.BigDecimal;
import org.lwjgl.input.Mouse;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.storage.utils.gui.clickgui.AnimationUtils;
import vip.astroline.client.storage.utils.other.MathUtils;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;

public class BasicSlider
extends Component {
    public float min;
    public float max;
    public float value;
    public float increment;
    protected boolean isDragging;
    public float animation = 0.0f;
    public FloatValue INSTANCE;
    public String unit;

    public BasicSlider(Panel parent, FloatValue val, int offX, int offY, String title, float min, float max, float increment) {
        super(parent, offX, offY, title);
        this.INSTANCE = val;
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.width = ClickGUI.settingsWidth;
        this.height = 19;
        this.type = "BasicSlider";
    }

    public void render(int mouseX, int mouseY) {
        if (this.INSTANCE.anotherShit) {
            float slideRange = this.width - 10;
            float multiplier = (this.value - this.min) / (this.max - this.min);
            if (this.animation == 0.0f) {
                this.animation = multiplier;
            }
            this.animation = AnimationUtils.smoothAnimation((float)this.animation, (float)(multiplier * 100.0f), (float)50.0f, (float)0.3f);
            FontManager.tiny.drawString(this.title, (float)(this.x + 9), (float)(this.y + 2), Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : new Color(0xD5D5D5).getRGB());
            GuiRenderUtils.drawLine2D((double)(this.x + 10), (double)(this.y + 14), (double)(this.x + this.width), (double)(this.y + 14), (float)2.0f, (int)(Hud.isLightMode.getValue() != false ? new Color(-1710875).darker().getRGB() : new Color(0xBFBFBF).getRGB()));
            GuiRenderUtils.drawLine2D((double)(this.x + 10), (double)(this.y + 14), (double)((float)(this.x + 10) + slideRange * (this.animation / 100.0f)), (double)(this.y + 14), (float)2.0f, (int)(Hud.isLightMode.getValue() != false ? ClickGUI.lightMainColor : ClickGUI.mainColor));
            String valueText = this.value % 1.0f != 0.0f ? new BigDecimal(this.value).setScale(1, 4).toString() : new BigDecimal(this.value).setScale(0, 4).toString();
            if (this.unit != null) {
                valueText = valueText + this.unit;
            }
            FontManager.tiny.drawString(valueText, (float)(this.x + this.width) - FontManager.tiny.getStringWidth(valueText) + 1.0f, (float)(this.y + 2), Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : new Color(0xD5D5D5).getRGB());
            GuiRenderUtils.drawRoundedRect((float)((float)(this.x + 8) + slideRange * (this.animation / 100.0f)), (float)((float)this.y + 11.0f), (float)6.0f, (float)6.0f, (float)10.0f, (int)(Hud.isLightMode.getValue() != false ? -14835457 : -6596170), (float)1.0f, (int)(Hud.isLightMode.getValue() != false ? -14835457 : -6596170));
        } else {
            float slideRange = this.width - 22;
            float multiplier = (this.value - this.min) / (this.max - this.min);
            if (this.animation == 0.0f) {
                this.animation = multiplier;
            }
            this.animation = AnimationUtils.smoothAnimation((float)this.animation, (float)(multiplier * 100.0f), (float)50.0f, (float)0.3f);
            GuiRenderUtils.drawRect((float)this.x, (float)(this.y + 5), (float)this.width, (float)(FontManager.tiny.getHeight(this.title) + 1.0f), (int)-14671840);
            GuiRenderUtils.drawRect((float)this.x, (float)((float)this.y + 5.0f), (float)(10.0f + slideRange * (this.animation / 100.0f)), (float)(FontManager.tiny.getHeight(this.title) + 1.0f), (int)Hud.hudColor1.getColorInt());
            FontManager.tiny.drawString(this.title, (float)(this.x + 9), (float)this.y + 5.5f, Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : -1);
            String valueText = (float)Math.round(this.value * 100.0f) / 100.0f + "";
            if (this.unit != null) {
                valueText = valueText + this.unit;
            }
            FontManager.tiny.drawString(valueText, (float)(this.x + this.width) - FontManager.tiny.getStringWidth(valueText) - 9.0f, (float)this.y + 5.5f, Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : -1);
        }
    }

    public static BigDecimal round(float f, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(f));
        bd = bd.setScale(decimalPlace, 4);
        return bd;
    }

    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        if (!this.isDragging) return;
        this.value = MathUtils.map((float)(mouseX - (this.x + 10)), (float)0.0f, (float)(this.width - (this.INSTANCE.anotherShit ? 10 : 22)), (float)this.min, (float)this.max);
        this.value -= this.value % this.increment;
        if (this.value > this.max) {
            this.value = this.max;
        }
        if (!(this.value < this.min)) return;
        this.value = this.min;
    }

    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
        boolean bl = this.isHovered = this.contains(mouseX, mouseY) && this.parent.mouseOver(mouseX, mouseY);
        if (isPressed && !this.wasMousePressed && this.isHovered) {
            this.isDragging = true;
        }
        if (!isPressed) {
            this.isDragging = false;
        }
        this.wasMousePressed = isPressed;
    }

    public void noMouseUpdates() {
        super.noMouseUpdates();
        if (Mouse.isButtonDown((int)0)) return;
        this.isDragging = false;
    }
}
