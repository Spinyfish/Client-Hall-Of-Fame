package me.felix.clickgui.renderables.settings;

import de.lirium.Client;
import de.lirium.base.setting.impl.ComboBox;
import de.lirium.util.render.FontRenderer;
import me.felix.clickgui.abstracts.ClickGUIHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderableComboBox extends ClickGUIHandler {

    public final ComboBox<String> comboBox;

    private double x, y;

    private boolean hover, extended;

    public RenderableComboBox(ComboBox<String> comboBox) {
        this.comboBox = comboBox;
    }

    @Override
    public void onMouseReleased(int mouseX, int mouseY) {

    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseKey) {
        if (hover) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || extended) {
                extended = !extended;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.5F));
            } else {
                int index = comboBox.modes.indexOf(comboBox.getValue());
                if (mouseKey == 0) index = index + 1 >= comboBox.modes.size() ? 0 : index + 1;
                if (mouseKey == 1) index = index - 1 < 0 ? comboBox.modes.size() - 1 : index - 1;
                comboBox.setValue(comboBox.modes.get(index));
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 2F));
            }
        } else {
            if(extended) {
                final double height = (comboBox.modes.size() - 1) * (this.height + 3);
                final boolean hover = isHovered(mouseX, mouseY, (int) x, (int) (y + this.height), width, (int) height);
                if (hover && mouseKey == 0) {
                    final AtomicInteger addition = new AtomicInteger();
                    comboBox.modes.forEach(mode -> {
                        if (!mode.equalsIgnoreCase(comboBox.getValue())) {
                            if (isHovered(mouseX, mouseY, (int) x, (int) (y + this.height + addition.get()), width, this.height)) {
                                comboBox.setValue(mode);
                                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 2F));
                            }
                            addition.addAndGet(this.height - 3);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, int x, int y) {
    }

    public int draw(int mouseX, int mouseY, int x, int y) {

        final FontRenderer fontRenderer = Client.INSTANCE.getFontLoader().get("arial", 17);

        this.hover = isHovered(mouseX, mouseY, x, y, width, height);
        this.x = x;
        this.y = y;

        Gui.drawRect(x, y, x + width, y + height, new Color(30, 30, 30).getRGB());

        int extendY = height;
        if (extended) {
            for (String mode : comboBox.modes) {
                if (mode.equalsIgnoreCase(comboBox.getValue())) continue;
                Gui.drawRect(x, y + extendY, x + width, y + height + extendY + 3, new Color(30, 30, 30).getRGB());
                fontRenderer.drawString("- " + mode, x + 3, y + 1 + extendY, -1);
                extendY += fontRenderer.FONT_HEIGHT + 3;
            }
        }
        extendY -= height;

        fontRenderer.drawString(comboBox.getDisplay() + " : " + comboBox.getValue(), x + 3, y + 2, -1);
        return extendY;
    }
}
