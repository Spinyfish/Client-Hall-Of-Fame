package me.kansio.client.gui.clickgui.frame.components.impl;

import me.kansio.client.gui.clickgui.frame.Values;
import me.kansio.client.gui.clickgui.frame.components.Component;
import me.kansio.client.gui.clickgui.frame.components.FrameModule;
import me.kansio.client.value.Value;
import me.kansio.client.value.value.StringValue;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

public class StringSetting extends Component implements Values {

    public StringSetting(int x, int y, FrameModule owner, Value setting) {
        super(x, y, owner, setting);
    }

    private boolean typing;

    @Override
    public void initGui() {
        typing = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        StringValue slide = (StringValue) getSetting();

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        fontRenderer.drawString("§7" + slide.getName() + ": §f" + slide.getValue(), x + 5, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)), stringColor, true);

        if (typing) {
            Gui.drawRect(x + 125, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)) + 9, defaultWidth - 8, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)) + 7 + 4, -1);
        }
    }


    private double roundToPlace(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && mouseButton == 0) {
            typing = !typing;
        }
        return RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset()) && mouseButton == 0;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton) {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        StringValue slide = (StringValue) getSetting();
        if (keyCode == Keyboard.KEY_RSHIFT
                || keyCode == Keyboard.KEY_LSHIFT
                || keyCode == Keyboard.KEY_CAPITAL)
            return;

        //enter
        if (keyCode == 28) {
            typing = false;
            return;
        }


        //backspace
        if (keyCode == 14 && typing) {
            if (slide.getValue().toCharArray().length == 0) {
                return;
            }
            slide.setValue(removeLastChar(slide.getValue()));
            return;
        }

        List<Character> whitelistedChars = Arrays.asList(
                '&', ' ', '#', '[', ']', '(', ')',
                '.', ',', '<', '>', '-', '$',
                '!', '"', '\'', '\\', '/', '=',
                '+', ',', '|', '^', '?', '`', ';', ':',
                '@', '£', '%', '{', '}', '_', '*', '»'
        );

        for (char whitelistedChar : whitelistedChars) {
            if (typedChar == whitelistedChar && typing) {
                slide.setValue(slide.getValue() + typedChar);
                return;
            }
        }

        if (!Character.isLetterOrDigit(typedChar)) {
            return;
        }


        if (typing) {
            slide.setValue(slide.getValue() + typedChar);
        }
    }

    private String removeLastChar(String s) {
        return s.substring(0, s.length() - 1);
    }

    @Override
    public int getOffset() {
        return 15;
    }

}
