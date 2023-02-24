package me.kansio.client.gui.clickgui.frame.components.impl;

import me.kansio.client.gui.clickgui.frame.Values;
import me.kansio.client.gui.clickgui.frame.components.Component;
import me.kansio.client.gui.clickgui.frame.components.FrameModule;
import me.kansio.client.gui.clickgui.utils.render.animation.easings.Animate;
import me.kansio.client.gui.clickgui.utils.render.animation.easings.Easing;
import me.kansio.client.value.Value;
import me.kansio.client.value.value.BooleanValue;
import me.kansio.client.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class BoolSetting extends Component implements Values {
    private final Animate animation;

    public BoolSetting(int x, int y, FrameModule owner, Value setting)
    {
        super(x, y, owner, setting);
        this.animation = new Animate().setMin(0).setMax(5).setSpeed(15).setEase(Easing.LINEAR).setReversed(!((BooleanValue) setting).getValue());
    }

    @Override
    public void initGui()
    {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        animation.update();
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        fontRenderer.drawString("§7" + getSetting().getName(), x + 5, y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)), -1, true);
        //Gui.drawRect(x + defaultWidth - 15, y, x + defaultWidth - 5, y + 10, darkerMainColor);
        RenderUtils.drawFilledCircle(x + defaultWidth - 10, (int) (y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)) + 6.75f), 5, new Color(darkerMainColor));

        if (((BooleanValue) getSetting()).getValue() || animation.getValue() != 0) {
            RenderUtils.drawFilledCircle(x + defaultWidth - 10, (int) (y + (getOffset() / 2F - (fontRenderer.FONT_HEIGHT / 2F)) + 6.75f), animation.getValue(), new Color(enabledColor));
            GlStateManager.resetColor();
            GL11.glColor4f(1, 1, 1, 1);
        }
    }


    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if(RenderUtils.hover(x, y, mouseX, mouseY, defaultWidth, getOffset())) {
            BooleanValue set = (BooleanValue) getSetting();
            set.setValue(!set.getValue());
            animation.setReversed(!set.getValue());
            return true;
        }
        return false;
    }

    @Override
    public void onGuiClosed(int mouseX, int mouseY, int mouseButton)
    {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public int getOffset()
    {
        return 15;
    }
}
