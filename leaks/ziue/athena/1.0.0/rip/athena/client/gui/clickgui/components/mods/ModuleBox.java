package rip.athena.client.gui.clickgui.components.mods;

import rip.athena.client.gui.framework.*;
import rip.athena.client.modules.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.gui.framework.draw.*;
import java.awt.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.*;
import rip.athena.client.modules.impl.other.*;
import net.minecraft.client.*;
import java.util.*;
import net.minecraft.util.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.gui.clickgui.*;

public class ModuleBox extends MenuComponent
{
    protected static final int INACTIVE;
    protected static final int ACTIVE;
    protected static final int COG_BORDER;
    private static final int MIN_SPACING = 8;
    protected Module module;
    protected ButtonState lastState;
    protected boolean mouseDown;
    private List<String> lines;
    private int tHeight;
    
    public ModuleBox(final Module module, final int x, final int y, final int width, final int height) {
        super(x, y, width, height);
        this.lastState = ButtonState.NORMAL;
        this.lines = new ArrayList<String>();
        this.module = module;
        final String text = module.getName().toUpperCase();
        final String[] words = text.split(" ");
        final StringBuilder curWord = new StringBuilder();
        for (String toAdd : words) {
            final String word = toAdd;
            if (!curWord.toString().isEmpty()) {
                toAdd = " " + toAdd;
            }
            if (FontManager.getProductSansRegular(30).width(curWord.toString() + toAdd) + 8 > width) {
                this.lines.add(curWord.toString());
                curWord.setLength(0);
                toAdd = word;
            }
            curWord.append(toAdd);
        }
        this.lines.add(curWord.toString());
        this.tHeight = 0;
        for (final String line : this.lines) {
            this.tHeight += (int)rip.athena.client.font.FontManager.baloo17.getHeight(line);
        }
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(0, 0, 0, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(0, 0, 0, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(35, 35, 35, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(36, 36, 38, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(36, 36, 38, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(36, 36, 38, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(36, 36, 38, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(213, 213, 213, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(213, 213, 213, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(213, 213, 213, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(213, 213, 213, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onMouseClick(final int button) {
        if (button == 0) {
            this.mouseDown = true;
        }
    }
    
    @Override
    public boolean passesThrough() {
        if (this.disabled || this.parent == null) {
            return true;
        }
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        if (this.mouseDown) {
            this.mouseDown = false;
            if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height + 1) {
                if (mouseX >= x + 10 && mouseX <= x + this.width - 11 && mouseY >= y + this.height - 10 - 20 && mouseY <= y + this.height - 10) {
                    if (this.module.isToggled()) {
                        this.module.setEnabled(false);
                    }
                    else {
                        this.module.setEnabled(true);
                    }
                    this.onToggle();
                }
                if (!this.module.getEntries().isEmpty() && mouseX >= x + this.width - 14 - 17 - 4 && mouseX <= x + this.width - 14 - 17 - 4 + 24 && mouseY >= y + 14 - 4 && mouseY <= y + 14 - 3 + 23) {
                    this.onOpenSettings();
                }
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void onPreSort() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        ButtonState state = ButtonState.NORMAL;
        if (!this.disabled) {
            if (mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height) {
                state = ButtonState.HOVER;
            }
        }
        else {
            state = ButtonState.DISABLED;
        }
        this.lastState = state;
    }
    
    @Override
    public void onRender() {
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        int drawColor;
        final int defaultColor = drawColor = this.getColor(DrawType.BACKGROUND, ButtonState.NORMAL);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        RoundedUtils.drawRoundedRect((float)x, (float)y, (float)(x + this.width), (float)(y + this.height), 26.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        RoundedUtils.drawRoundedRect((float)(x + 1), (float)(y + 1), (float)(x + this.width - 1), (float)(y + this.height - 1), 26.0f, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
        int yPos = y + this.height / 2 - this.tHeight / 2 + 10;
        for (final String line : this.lines) {
            if (Settings.customGuiFont) {
                FontManager.getNunitoBold(30).drawString(line, x + this.width / 2.0f - FontManager.getNunitoBold(30).width(line) / 2.0, yPos, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
                yPos += (int)rip.athena.client.font.FontManager.baloo30.getHeight(line);
            }
            else {
                Minecraft.getMinecraft().fontRendererObj.drawString(line, (int)(x + this.width / 2.0f - Minecraft.getMinecraft().fontRendererObj.getStringWidth(line) / 2), yPos, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
                yPos += Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
            }
        }
        final int mouseX = this.parent.getMouseX();
        final int mouseY = this.parent.getMouseY();
        if (mouseX >= x + 10 && mouseX <= x + this.width - 11 && mouseY >= y + this.height - 10 - 20 && mouseY <= y + this.height - 10) {
            drawColor = backgroundColor;
        }
        if (!Objects.equals(this.module.getIcon(), "")) {
            DrawUtils.drawImage(new ResourceLocation(this.module.getIcon()), x + this.width / 2 - 25, y + 20, 50, 50);
        }
        RoundedUtils.drawRoundedRect((float)(x + 10), (float)(y + this.height - 10 - 20), (float)(x + this.width - 10), (float)(y + this.height - 10), 12.0f, this.module.isToggled() ? new Color(0, 200, 0, 255).getRGB() : new Color(200, 0, 0, 225).getRGB());
        RoundedUtils.drawRoundedRect((float)(x + 11), (float)(y + this.height - 10 - 19), (float)(x + this.width - 11), (float)(y + this.height - 11), 12.0f, this.module.isToggled() ? new Color(40, 157, 93, 255).getRGB() : new Color(157, 40, 40, 225).getRGB());
        final String text = this.module.isToggled() ? "ENABLED" : "DISABLED";
        if (Settings.customGuiFont) {
            FontManager.getNunitoBold(25).drawString(text, x + this.width / 2.0f - FontManager.getNunitoBold(25).width(text) / 2.0, y + this.height - 10 - 15, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(text, (int)(x + this.width / 2.0f - Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2), y + this.height - 10 - 15, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        if (!this.module.getEntries().isEmpty()) {
            drawColor = defaultColor;
            if (mouseX >= x + this.width - 14 - 17 - 4 && mouseX <= x + this.width - 14 - 17 - 4 + 24 && mouseY >= y + 14 - 4 && mouseY <= y + 14 - 3 + 23) {
                drawColor = Color.black.getRGB();
            }
            RoundedUtils.drawRound((float)(x + this.width - 14 - 17 - 3), (float)(y + 14 - 3), 23.0f, 23.0f, 12.0f, new Color(10, 10, 10, 150));
            RoundedUtils.drawRoundedGradientOutlineCorner((float)(x + this.width - 14 - 17 - 3), (float)(y + 14 - 3), (float)(this.width + x - 11), (float)(34 + y), 1.0f, 12.0f, ColorUtil.getClientColor(0, 255).getRGB(), ColorUtil.getClientColor(90, 255).getRGB(), ColorUtil.getClientColor(180, 255).getRGB(), ColorUtil.getClientColor(270, 255).getRGB());
            this.drawImage(new ResourceLocation("Athena/gui/menu/settings.png"), x + this.width - 14 - 17, y + 14, 17, 17);
        }
    }
    
    public Module getModule() {
        return this.module;
    }
    
    public void onOpenSettings() {
    }
    
    public void onToggle() {
    }
    
    static {
        INACTIVE = new Color(10, 90, 32, IngameMenu.MENU_ALPHA).getRGB();
        ACTIVE = new Color(90, 10, 12, IngameMenu.MENU_ALPHA).getRGB();
        COG_BORDER = new Color(57, 57, 59, IngameMenu.MENU_ALPHA).getRGB();
    }
}
