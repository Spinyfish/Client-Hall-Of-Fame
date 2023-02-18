/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiYesNo
 *  net.minecraft.util.EnumChatFormatting
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.clickgui.config.SavePresetScreen
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.Component
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.layout.dropdown.utils.Rect
 *  vip.astroline.client.service.config.preset.PresetManager
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.storage.utils.other.MathUtils
 *  vip.astroline.client.storage.utils.render.ColorUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 */
package vip.astroline.client.layout.dropdown.components.impl.config;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.util.EnumChatFormatting;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.clickgui.config.SavePresetScreen;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.Component;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.layout.dropdown.utils.Rect;
import vip.astroline.client.service.config.preset.PresetManager;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.storage.utils.other.MathUtils;
import vip.astroline.client.storage.utils.render.ColorUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;

public class PresetList
extends Component {
    private int index;
    private float scrollValue;
    private Rect list_rect = new Rect();
    private Rect load_rect = new Rect();
    private Rect save_rect = new Rect();
    private Rect del_rect = new Rect();
    private boolean isHoveredList;
    private boolean isHoveredLoad;
    private boolean isHoveredSave;
    private boolean isHoveredDelete;

    public PresetList(Panel panel, int offX, int offY) {
        super(panel, offX, offY, "PresetList");
        this.width = ClickGUI.settingsWidth;
        this.height = 96;
        this.type = "PresetList";
    }

    public void render(int mouseX, int mouseY) {
        String preset;
        float x = this.x + 2;
        float y = this.y + 2;
        float margin = 2.0f;
        this.list_rect = new Rect((float)this.x + margin, y, (float)this.width - margin * 2.0f, (float)(this.height - 18));
        GuiRenderUtils.beginCrop((float)((float)this.x + margin), (float)(y + (float)(this.height - 18) - 0.5f), (float)((float)this.width - margin * 2.0f), (float)((float)(this.height - 18) - 1.0f), (float)2.0f);
        for (int i = 0; i < PresetManager.presets.size(); y += FontManager.tiny.getHeight(preset) + 2.0f, ++i) {
            preset = (String)PresetManager.presets.get(i);
            if (i == this.index) {
                GuiRenderUtils.drawRect((float)(x + 0.5f), (float)(y + 0.5f + this.scrollValue), (float)((float)this.width - margin * 2.0f - 1.0f), (float)(FontManager.tiny.getHeight(preset) + 2.0f), (int)Hud.hudColor1.getColorInt());
            }
            FontManager.tiny.drawString(preset, x + 2.0f, y + 2.0f + this.scrollValue, Hud.isLightMode.getValue() != false ? ColorUtils.GREY.c : new Color(0xD5D5D5).getRGB());
        }
        GuiRenderUtils.endCrop();
        y = this.y + this.height - 14;
        GuiRenderUtils.drawBorderedRect((float)x, (float)y, (float)24.0f, (float)12.0f, (float)0.5f, (int)(Hud.isLightMode.getValue().booleanValue() ? (this.isHoveredLoad ? -10044417 : -10049281) : (this.isHoveredLoad ? GuiRenderUtils.darker((int)-14145496, (int)-30) : -14145496)), (int)(Hud.isLightMode.getValue() != false ? new Color(ColorUtils.BLUE.c).getRGB() : Hud.hudColor1.getColorInt()));
        FontManager.tiny.drawString("Load", x + 4.0f, y + 2.0f, new Color(0xF8F8F8).getRGB());
        this.load_rect = new Rect(x, y, 24.0f, 12.0f);
        x = this.x + 76;
        GuiRenderUtils.drawBorderedRect((float)x, (float)y, (float)12.0f, (float)12.0f, (float)0.5f, (int)(Hud.isLightMode.getValue().booleanValue() ? (this.isHoveredSave ? -10044417 : -10049281) : (this.isHoveredSave ? GuiRenderUtils.darker((int)-14145496, (int)-30) : -14145496)), (int)(Hud.isLightMode.getValue() != false ? new Color(ColorUtils.BLUE.c).getRGB() : Hud.hudColor1.getColorInt()));
        FontManager.tiny.drawString("+", x + 4.0f, y + 2.0f, new Color(0xF8F8F8).getRGB());
        this.save_rect = new Rect(x, y, 12.0f, 12.0f);
        x = this.x + 62;
        if (!this.canberemoved()) return;
        GuiRenderUtils.drawBorderedRect((float)x, (float)y, (float)12.0f, (float)12.0f, (float)0.5f, (int)(Hud.isLightMode.getValue().booleanValue() ? (this.isHoveredDelete ? -10044417 : -10049281) : (this.isHoveredDelete ? GuiRenderUtils.darker((int)-14145496, (int)-30) : -14145496)), (int)(Hud.isLightMode.getValue() != false ? new Color(ColorUtils.BLUE.c).getRGB() : Hud.hudColor1.getColorInt()));
        FontManager.tiny.drawString("-", x + 5.0f, y + 2.0f, new Color(0xF8F8F8).getRGB());
        this.del_rect = new Rect(x, y, 12.0f, 12.0f);
    }

    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
        this.isHovered = this.contains(mouseX, mouseY) && this.parent.mouseOver(mouseX, mouseY);
        this.isHoveredList = MathUtils.contains((float)mouseX, (float)mouseY, (Rect)this.list_rect) && this.isHovered;
        this.isHoveredLoad = MathUtils.contains((float)mouseX, (float)mouseY, (Rect)this.load_rect) && this.isHovered;
        this.isHoveredSave = MathUtils.contains((float)mouseX, (float)mouseY, (Rect)this.save_rect) && this.isHovered;
        boolean bl = this.isHoveredDelete = MathUtils.contains((float)mouseX, (float)mouseY, (Rect)this.del_rect) && this.isHovered && this.canberemoved();
        if (isPressed && !this.wasMousePressed) {
            this.pressed(mouseY);
        }
        this.wasMousePressed = isPressed;
    }

    public void noMouseUpdates() {
        this.isHovered = false;
        this.isHoveredList = false;
        this.isHoveredLoad = false;
        this.isHoveredSave = false;
        this.isHoveredDelete = false;
    }

    public void mouseWheelUpdate(int mouseX, int mouseY, int amount) {
        if (amount == 0) return;
        if (!MathUtils.contains((float)mouseX, (float)mouseY, (Rect)this.list_rect)) {
            return;
        }
        amount = amount > 0 ? (int)(FontManager.tiny.getHeight() + 2.0f) : -((int)(FontManager.tiny.getHeight() + 2.0f));
        this.scrollValue += (float)amount;
        this.scrollValue = Math.max(this.scrollValue, (float)((int)((float)PresetManager.presets.size() * -(FontManager.tiny.getHeight() + 2.0f)) + (this.height - 19)));
        this.scrollValue = Math.min(0.0f, this.scrollValue);
    }

    private void pressed(int mouseY) {
        String preset;
        if (this.isHoveredList) {
            this.index = (int)((float)mouseY - this.scrollValue - this.list_rect.getY()) / (int)(FontManager.tiny.getHeight() + 2.0f);
            this.index = Math.max(0, this.index);
            this.index = Math.min(PresetManager.presets.size() - 1, this.index);
        }
        if (this.isHoveredLoad) {
            if (PresetManager.presets.size() - 1 < this.index) {
                return;
            }
            preset = (String)PresetManager.presets.get(this.index);
            PresetManager.loadPreset((String)preset);
            Astroline.INSTANCE.tellPlayer("Loaded preset " + EnumChatFormatting.YELLOW + preset);
        }
        if (this.isHoveredSave) {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new SavePresetScreen((GuiScreen)Astroline.INSTANCE.getDropdown()));
        }
        if (!this.isHoveredDelete) return;
        if (!this.canberemoved()) return;
        if (PresetManager.presets.size() - 1 < this.index) {
            return;
        }
        preset = (String)PresetManager.presets.get(this.index);
        Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiYesNo((result, id) -> {
            if (result) {
                PresetManager.deletePreset((String)preset);
            }
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)Astroline.INSTANCE.getDropdown());
        }, "Are you sure you want to remove the preset? " + EnumChatFormatting.YELLOW + preset, "", 0));
    }

    private boolean canberemoved() {
        try {
            if (PresetManager.presets.size() - 1 >= this.index) String preset;
            return !(preset = (String)PresetManager.presets.get(this.index)).startsWith("#");
            return false;
        }
        catch (Exception ex) {
            return false;
        }
    }
}
