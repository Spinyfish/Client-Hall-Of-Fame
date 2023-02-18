/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.renderer.GlStateManager
 *  org.lwjgl.input.Mouse
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.layout.dropdown.ClickGUI
 *  vip.astroline.client.layout.dropdown.components.impl.Button
 *  vip.astroline.client.layout.dropdown.components.impl.bind.BindScreen
 *  vip.astroline.client.layout.dropdown.panel.Panel
 *  vip.astroline.client.layout.dropdown.utils.AnimationTimer
 *  vip.astroline.client.service.font.FontManager
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.render.Hud
 *  vip.astroline.client.service.module.value.ValueManager
 *  vip.astroline.client.storage.utils.other.MathUtils
 *  vip.astroline.client.storage.utils.render.render.GuiRenderUtils
 */
package vip.astroline.client.layout.dropdown.components.impl.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import vip.astroline.client.Astroline;
import vip.astroline.client.layout.dropdown.ClickGUI;
import vip.astroline.client.layout.dropdown.components.impl.Button;
import vip.astroline.client.layout.dropdown.components.impl.bind.BindScreen;
import vip.astroline.client.layout.dropdown.panel.Panel;
import vip.astroline.client.layout.dropdown.utils.AnimationTimer;
import vip.astroline.client.service.font.FontManager;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.render.Hud;
import vip.astroline.client.service.module.value.ValueManager;
import vip.astroline.client.storage.utils.other.MathUtils;
import vip.astroline.client.storage.utils.render.render.GuiRenderUtils;

public class ModuleButton
extends Button {
    public Module module;
    private boolean hasSettings;
    private boolean isHoveredMod;
    private boolean isHoveredSetting;
    private AnimationTimer expandAnimation = new AnimationTimer(15);

    public ModuleButton(Panel panel, int offX, int offY, String title, String tooltip, Module module) {
        super(panel, offX, offY, title);
        this.hasSettings = !ValueManager.getValueByModNameForRender((String)module.getName()).isEmpty();
        this.width = Math.max(ClickGUI.defaultWidth, panel.width);
        this.height = ClickGUI.buttonHeight;
        this.module = module;
        this.type = "ModuleButton";
    }

    public void render(int mouseX, int mouseY) {
        this.expandAnimation.update(Astroline.INSTANCE.getDropdown().isVisibleComponetsByGroup(this.module.getName() + "_setting"));
        int colorEnabled = this.module.isToggled() ? Hud.hudColor1.getColorInt() : -14145496;
        GuiRenderUtils.drawRect((float)this.x, (float)this.y, (float)this.width, (float)(FontManager.tiny.getHeight(this.title) + 4.0f), (int)colorEnabled);
        if (this.hasSettings) {
            float x = (float)this.x + 85.0f;
            float y = (float)this.y + (float)this.height / 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x, (float)y, (float)0.0f);
            GlStateManager.rotate((float)((float)this.expandAnimation.getValue() * 90.0f), (float)0.0f, (float)0.0f, (float)1.0f);
            GuiRenderUtils.drawLine2D((double)-1.0, (double)-2.0, (double)1.0, (double)0.0, (float)1.0f, (int)-1);
            GuiRenderUtils.drawLine2D((double)-1.0, (double)2.0, (double)1.0, (double)0.0, (float)1.0f, (int)-1);
            GlStateManager.translate((float)(-x), (float)(-y), (float)0.0f);
            GlStateManager.popMatrix();
        }
        int color = -1;
        GuiRenderUtils.setColor((int)color);
        FontManager.small.drawString(this.title, (float)(this.x + 5), (float)(this.y + this.height / 2) - FontManager.small.getHeight(this.title) / 2.0f, color);
    }

    public void update(int mouseX, int mouseY) {
        super.update(mouseX, mouseY);
        this.isToggled = this.module != null ? this.module.isToggled() : false;
    }

    protected void pressed() {
        if (this.isHoveredMod && this.module != null) {
            if (Mouse.isButtonDown((int)1)) {
                this.isHoveredSetting = true;
            } else if (Mouse.isButtonDown((int)0)) {
                this.isHoveredSetting = false;
                this.module.enableModule();
            }
        }
        if (!this.isHoveredSetting) return;
        if (!this.hasSettings) return;
        if (!Astroline.INSTANCE.getDropdown().isVisibleComponetsByGroup(this.module.getName() + "_setting")) {
            Astroline.INSTANCE.getDropdown().toggleBox(this.module.getName() + "_setting", true);
        } else {
            Astroline.INSTANCE.getDropdown().toggleBox(this.module.getName() + "_setting", false);
        }
        this.parent.repositionComponents();
    }

    public void mouseUpdates(int mouseX, int mouseY, boolean isPressed) {
        this.isHoveredMod = this.containsMod(mouseX, mouseY) && this.parent.mouseOver(mouseX, mouseY) && this.isHovered;
        boolean bl = this.isHoveredSetting = this.containsSettings(mouseX, mouseY) && this.parent.mouseOver(mouseX, mouseY) && this.isHovered;
        if (Mouse.isButtonDown((int)2)) {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new BindScreen(this.module, (GuiScreen)Astroline.INSTANCE.getDropdown()));
        }
        super.mouseUpdates(mouseX, mouseY, isPressed);
    }

    private boolean containsMod(int mouseX, int mouseY) {
        return MathUtils.contains((float)mouseX, (float)mouseY, (float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + this.height));
    }

    private boolean containsSettings(int mouseX, int mouseY) {
        return MathUtils.contains((float)mouseX, (float)mouseY, (float)this.x, (float)this.y, (float)(this.x + this.width), (float)(this.y + this.height));
    }
}
