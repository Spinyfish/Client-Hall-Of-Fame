package rip.athena.client.gui.clickgui.pages;

import rip.athena.client.gui.clickgui.components.mods.*;
import net.minecraft.client.*;
import rip.athena.client.gui.clickgui.*;
import java.awt.*;
import rip.athena.client.*;
import rip.athena.client.macros.*;
import rip.athena.client.gui.framework.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.gui.clickgui.components.macros.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.gui.framework.draw.*;

public class MacrosPage extends Page
{
    public final int MENU_SIDE_HEADER_BG_COLOR;
    public static final int MENU_SIDE_BG_COLOR;
    private MacroTextfield name;
    private MacroTextfield commandLine;
    private MacroButton button;
    private MacroButton delete;
    private MenuModKeybind bind;
    private ModScrollPane scrollPane;
    
    public MacrosPage(final Minecraft mc, final Menu menu, final IngameMenu parent) {
        super(mc, menu, parent);
        this.MENU_SIDE_HEADER_BG_COLOR = new Color(35, 35, 35, IngameMenu.MENU_ALPHA).getRGB();
    }
    
    @Override
    public void onInit() {
        final int width = 300;
        final int x = this.menu.getWidth() - width + 20;
        int y = 59;
        final int compWidth = width - 6 - 40;
        this.name = new MacroTextfield(TextPattern.NONE, x, y + 85, compWidth, 22, "...");
        this.commandLine = new MacroTextfield(TextPattern.NONE, x, y + 155, compWidth, 22, "...");
        this.bind = new MenuModKeybind(x, y + 225, compWidth, 22);
        final int acceptWidth = compWidth - 40;
        this.button = new MacroButton("ADD", x - 21 + width / 2 - acceptWidth / 2, y + 265, acceptWidth, 22, true) {
            @Override
            public void onAction() {
                this.setActive(false);
                if (MacrosPage.this.name.getText().isEmpty()) {
                    return;
                }
                if (MacrosPage.this.commandLine.getText().isEmpty()) {
                    return;
                }
                if (!MacrosPage.this.bind.isBound()) {
                    return;
                }
                Athena.INSTANCE.getMacroManager().getMacros().add(new Macro(MacrosPage.this.name.getText(), MacrosPage.this.commandLine.getText(), MacrosPage.this.bind.getBind()));
                MacrosPage.this.name.setText("");
                MacrosPage.this.commandLine.setText("");
                MacrosPage.this.bind.setBind(0);
                MacrosPage.this.populateScrollPane();
            }
        };
        this.delete = new MacroButton("CLEAR ALL MACROS", x - 21 + width / 2 - compWidth / 2, y = this.menu.getHeight() - 22 - 20, compWidth, 22, false) {
            @Override
            public void onAction() {
                this.setActive(false);
                Athena.INSTANCE.getMacroManager().getMacros().clear();
                MacrosPage.this.populateScrollPane();
            }
        };
        this.scrollPane = new ModScrollPane(220, 140, this.menu.getWidth() - width - 131, this.menu.getHeight() - 141, false);
        this.populateScrollPane();
    }
    
    private void populateScrollPane() {
        this.scrollPane.getComponents().clear();
        final int spacing = 15;
        final int height = 30;
        int y = spacing;
        final int x = spacing;
        final int width = this.scrollPane.getWidth() - spacing * 2;
        for (final Macro macro : Athena.INSTANCE.getMacroManager().getMacros()) {
            this.scrollPane.addComponent(new MacroBase(macro.getName(), x, y, width - 90, height));
            final MacroSlimTextField field = new MacroSlimTextField(TextPattern.NONE, x + 80 + spacing + FontManager.getNunito(20).width(macro.getName()), y, width - 320 - spacing * 4 - 90, height - 5) {
                @Override
                public void onAction() {
                    macro.setCommand(this.getText());
                }
            };
            field.setText(macro.getCommand());
            this.scrollPane.addComponent(field);
            final int keybindWidth = (width - 189 - spacing * 4 - 90) / 2;
            final MenuModKeybind keybindElement = new MenuModKeybind(x + spacing + keybindWidth + spacing + 100, y + 5, keybindWidth - 20, height - 10) {
                @Override
                public void onAction() {
                    macro.setKey(this.getBind());
                }
            };
            keybindElement.setBind(macro.getKey());
            this.scrollPane.addComponent(keybindElement);
            final FlipButton flipButton = new FlipButton(width - spacing - 180, y, 80, height) {
                @Override
                public void onAction() {
                    macro.setEnabled(this.isActive());
                }
            };
            flipButton.setActive(macro.isEnabled());
            this.scrollPane.addComponent(flipButton);
            this.scrollPane.addComponent(new SimpleTextButton("X", width - spacing - 90, y + 5, 20, 20, true) {
                @Override
                public void onAction() {
                    Athena.INSTANCE.getMacroManager().getMacros().remove(macro);
                    MacrosPage.this.populateScrollPane();
                }
            });
            y += height + spacing;
        }
    }
    
    @Override
    public void onRender() {
        final int width = 300;
        final int x = this.menu.getX() + this.menu.getWidth() - width + 20;
        int y = this.menu.getY() + 59;
        final int height = 32;
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        this.drawVerticalLine(this.menu.getX() + 215, y + height - 30, height + 432, 3, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getSecondColor());
        if (Settings.customGuiFont) {
            FontManager.getNunitoBold(30).drawString("MACROS", this.menu.getX() + 235, this.menu.getY() + 80, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString("MACROS", this.menu.getX() + 235, this.menu.getY() + 80, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, this.menu.getY() + 58, width, this.menu.getHeight() - 58, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
        DrawImpl.drawRect(this.menu.getX() + this.menu.getWidth() - width, this.menu.getY() + 58, width, height + 1, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getFirstColor());
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y + height, width);
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString("ADD NEW MACRO", this.menu.getX() + this.menu.getWidth() - width / 2 - FontManager.getProductSansRegular(30).width("ADD NEW MACRO") / 2.0, y + height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString("ADD NEW MACRO", this.menu.getX() + this.menu.getWidth() - width / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth("ADD NEW MACRO") / 2, y + height / 2 - Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT / 2, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        this.drawShadowDown(this.menu.getX() + this.menu.getWidth() - width, y - 1, width);
        y += 60;
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString("ENTER NAME", x, y, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString("ENTER NAME", x, y, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        y += 70;
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString("ENTER COMMAND LINE", x, y, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString("ENTER COMMAND LINE", x, y, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        y += 70;
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString("ADD KEYBIND", x, y, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString("ADD KEYBIND", x, y, Athena.INSTANCE.getThemeManager().getPrimaryTheme().getTextColor());
        }
    }
    
    @Override
    public void onLoad() {
        this.menu.addComponent(this.name);
        this.menu.addComponent(this.commandLine);
        this.menu.addComponent(this.bind);
        this.menu.addComponent(this.button);
        this.menu.addComponent(this.delete);
        this.menu.addComponent(this.scrollPane);
    }
    
    @Override
    public void onUnload() {
    }
    
    @Override
    public void onOpen() {
    }
    
    @Override
    public void onClose() {
    }
    
    static {
        MENU_SIDE_BG_COLOR = new Color(35, 35, 35, IngameMenu.MENU_ALPHA).getRGB();
    }
}
