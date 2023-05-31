package me.felix.clickgui.renderables.settings;

import de.lirium.Client;
import de.lirium.base.setting.impl.SliderSetting;
import de.lirium.util.render.FontRenderer;
import de.lirium.util.render.RenderUtil;
import de.lirium.util.render.StencilUtil;
import me.felix.clickgui.abstracts.ClickGUIHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Resource;
import java.awt.*;
import java.util.Objects;

public class RenderableSlider extends ClickGUIHandler {
    public SliderSetting<Number> sliderElement;
    private static final ResourceLocation reset = new ResourceLocation("lirium/images/reset.png");

    private boolean dragging = false, hovering;

    private int x, y;

    final int resetSize = 10;

    private double animation;

    public RenderableSlider(SliderSetting<Number> sliderElement) {
        this.sliderElement = sliderElement;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseKey) {
        if(!Objects.equals(sliderElement.getValue(), sliderElement.defaultValue)) {
            if (mouseX >= x + width - resetSize - 1 && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                sliderElement.setValue(sliderElement.defaultValue);
                return;
            }
        }
        if (!hovering) return;

        this.dragging = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, int x, int y) {

        this.x = x;
        this.y = y;

        this.hovering = this.isHovered(mouseX, mouseY, x, y, width, height - 2);

        Gui.drawRect(x, y, x + width, y + height, new Color(30, 30, 30).getRGB());

        this.animation = RenderUtil.getAnimationState(animation, (width * (((sliderElement.getValue().doubleValue() - sliderElement.min.doubleValue()) / (sliderElement.max.doubleValue() - sliderElement.min.doubleValue())))), 172);

        final FontRenderer fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 15);

        Gui.drawRect(x, y + height - 3, (int) (x + animation), y + height, Color.BLUE.getRGB());
        StencilUtil.init();
        Gui.drawRect(x, y + height - 3, (int) (x + animation), y + height, Color.BLUE.getRGB());

        StencilUtil.readBuffer(1);
        RenderUtil.renderEnchantment();
        StencilUtil.uninit();

        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.resetColor();

        if(!Objects.equals(sliderElement.getValue(), sliderElement.defaultValue)) {
            GL11.glColor4f(1,1,1,1);
            RenderUtil.drawPicture(reset, x + width - resetSize - 1, y + 1, resetSize, resetSize);
        }

        fontRenderer.drawString(sliderElement.getDisplay() + " -> " +Math.round(sliderElement.getValue().doubleValue() * 100) / 100., calculateMiddle(sliderElement.name + " -> " +Math.round(sliderElement.getValue().doubleValue() * 100) / 100., fontRenderer, x, width), y + 2, -1);



        if (dragging) {
            try {
                final double percentage = (double) (mouseX - x) / width;
                sliderElement.setValue(Math.max(sliderElement.min.doubleValue(), Math.min(sliderElement.max.doubleValue(), (sliderElement.max.doubleValue() - sliderElement.min.doubleValue()) * percentage + sliderElement.min.doubleValue())));
                if (sliderElement.increment != null)
                    sliderElement.setValue(sliderElement.getValue().doubleValue() - sliderElement.getValue().doubleValue() % sliderElement.increment.doubleValue());
                if (sliderElement.min instanceof Integer)
                    sliderElement.setValue(sliderElement.getValue().intValue());
                else if (sliderElement.min instanceof Float)
                    sliderElement.setValue(sliderElement.getValue().floatValue());
                else if (sliderElement.min instanceof Long)
                    sliderElement.setValue(sliderElement.getValue().longValue());
                else if (sliderElement.min instanceof Byte)
                    sliderElement.setValue(sliderElement.getValue().byteValue());
                else if (sliderElement.min instanceof Short)
                    sliderElement.setValue(sliderElement.getValue().shortValue());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }
}
