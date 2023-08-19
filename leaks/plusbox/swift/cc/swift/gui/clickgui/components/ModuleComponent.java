/**
 * @project hakarware
 * @author CodeMan
 * @at 25.07.23, 16:22
 */

package cc.swift.gui.clickgui.components;

import cc.swift.gui.clickgui.GuiComponent;
import cc.swift.module.Module;
import cc.swift.util.ChatUtil;
import cc.swift.util.ColorUtil;
import cc.swift.util.MouseUtil;
import cc.swift.util.render.animation.Animation;
import cc.swift.util.render.animation.Easings;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINE_SMOOTH;

public class ModuleComponent extends GuiComponent {

    @Getter
    private final Module module;

    private boolean expanded, listening;

    private final Animation animation = new Animation();

    public ModuleComponent(Module module, double width, double height) {
        super(0, 0, width, height);
        this.module = module;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.listening) {
            if (keyCode != Keyboard.KEY_ESCAPE)
                this.module.setKey(keyCode);
            else
                this.module.setKey(Keyboard.KEY_NONE);
            this.listening = false;
            ChatUtil.printChatMessage(this.module.getName() + " is now bound to " + Keyboard.getKeyName(this.module.getKey()));
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        animation.updateAnimation();

        Gui.drawRect(x, y, x + width, y + height, ColorUtil.GRAY.getRGB());

        CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 16);
        int fontY = (int) Math.ceil(this.y + (height - vietnamFont.getHeight()) / 2);
        vietnamFont.drawStringWithShadow(listening ? "Listening..." : module.getName(), x + 6, fontY, module.isEnabled() ? Color.RED.getRGB() : -1);

        if (!getChildren().isEmpty()) {
            int middleY = (int) Math.ceil(this.y + height / 2);
            int middleX = (int) (x + width - 8);
            GlStateManager.color(1, 1, 1, 1);
            glPushMatrix();

            int rotation = (int) (180 - 180 * animation.getValue());

            if (rotation != 0) {
                glTranslated(middleX, middleY, 0);
                glRotated(rotation, 0, 0, 1);
                glTranslated(-middleX, -middleY, 0);
            }

            glDisable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glEnable(GL_LINE_SMOOTH);

            glBegin(GL_POLYGON);

            glVertex2d(middleX, middleY - 1);
            glVertex2d(middleX - 2, middleY + 1);
            glVertex2d(middleX + 2, middleY + 1);

            glEnd();

            glDisable(GL_BLEND);
            glDisable(GL_LINE_SMOOTH);
            glEnable(GL_TEXTURE_2D);

            if (rotation != 0) {
                glTranslated(middleX, middleY, 0);
                glRotated(-rotation, 0, 0, 1);
                glTranslated(-middleX, -middleY, 0);
            }

            glPopMatrix();
            GlStateManager.color(1, 1, 1, 1);

            if (expanded || animation.getValue() > 0)
                super.draw(mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (MouseUtil.isHovered(x, y, width, height, mouseX, mouseY)) {
            if (mouseButton == 0) {
                module.toggle();
            }
            if (mouseButton == 1 && !getChildren().isEmpty()) {
                expanded = !expanded;
                animation.animate(expanded ? 1 : 0, 200, Easings.NONE);
            }
            if (mouseButton == 2) {
                listening = !listening;
            }
        }
        if (expanded)
            super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public double getFullHeight() {
        double height = this.height;
        if (expanded || animation.getValue() > 0) {
            for (GuiComponent component : getChildren()) {
                if (!component.isVisible()) continue;
                height += component.getFullHeight();
            }
        }
        return (height - this.height) * animation.getValue() + this.height;
    }
}
