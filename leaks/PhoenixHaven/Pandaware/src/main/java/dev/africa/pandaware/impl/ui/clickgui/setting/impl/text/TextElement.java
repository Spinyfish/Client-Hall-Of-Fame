package dev.africa.pandaware.impl.ui.clickgui.setting.impl.text;

import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.font.Fonts;
import dev.africa.pandaware.impl.setting.TextBoxSetting;
import dev.africa.pandaware.impl.ui.clickgui.setting.api.Element;
import dev.africa.pandaware.utils.math.vector.Vec2i;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class TextElement extends Element<TextBoxSetting> {
    public TextElement(Module module, ModuleMode<?> moduleMode, TextBoxSetting setting) {
        super(module, moduleMode, setting);

        this.textBox = new TextBox(1, 0, 0, 70, 15);
        this.textBox.setFocused(false);
        this.textBox.setText(setting.getValue());
        Keyboard.enableRepeatEvents(true);
    }

    private final TextBox textBox;

    @Override
    public void handleRender(Vec2i mousePosition, float pTicks) {
        Fonts.getInstance().getComfortaMedium().drawString(
                this.getSetting().getName(),
                this.getPosition().getX(),
                this.getPosition().getY(),
                -1
        );

        GlStateManager.pushAttribAndMatrix();

        this.textBox.backgroundColor = (textBox.isFocused() ?
                new Color(75, 75, 75, 255) : new Color(42, 42, 42, 255));

        this.textBox.yPosition = this.getPosition().getY() - 4;
        this.textBox.xPosition = this.getPosition().getX() + this.getSize().getX() - 70;
        this.textBox.width = 63;
        this.textBox.height = 15;

        this.textBox.drawTextBox();
        this.getSetting().setValue(this.textBox.getText());

        GlStateManager.popAttribAndMatrix();
    }

    @Override
    public void handleClick(Vec2i mousePosition, int button) {
        this.textBox.mouseClicked(mousePosition.getX(), mousePosition.getY(), button);
    }

    @Override
    public void handleScreenUpdate() {
        this.textBox.updateCursorCounter();
    }

    @Override
    public void handleKeyboard(char typedChar, int keyCode) {
        this.textBox.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void handleGuiClose() {
        this.textBox.setFocused(false);
    }
}
