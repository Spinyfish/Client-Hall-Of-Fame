package de.fanta.clickgui.defaultgui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;

import java.io.File;
import java.util.ArrayList;

public class ConfigBox {
    private File dir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/" + "configs" + "/");

    public float x, y;
    public float dragX, dragY;
    public float lastDragX, lastDragY;
    public float scroll;

    public boolean isDragging;

    public ArrayList<ConfigButton> buttons = new ArrayList<>();
    private float buttonsHeight;

    public ConfigBox(float x, float y) {
        this.x = x;
        this.y = y;

        if(dir.exists() && dir.listFiles() != null) {
            float yOff = 16;
            for (File file : dir.listFiles()) {
                buttons.add(new ConfigButton(this, file, x + 2, y + yOff));
                yOff += 12.5F;
            }

            buttonsHeight = yOff;
        } else dir.mkdir();
    }

    public void drawConfigBox(float mouseX, float mouseY) {
        if(this.isDragging) {
            this.dragX = mouseX - lastDragX;
            this.dragY = mouseY - lastDragY;
        }
      //  Client.blurHelper.blur2(x + dragX, y + dragY, x + dragX + 120, y + dragY + 135, (float) 100);
        //RenderUtil.rectangle(x + dragX, y + dragY, x + dragX + 120, y + dragY + 135, Colors.getColor(25, 25, 25, 255));
        RenderUtil.rectangle(x + dragX, y + dragY, x + dragX + 120, y + dragY + 135, Colors.getColor(40, 40, 40, 200));

        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Config Menu", x + dragX + 4.5F, y + dragY + 2.5F, -1);
        Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("__________", x + dragX + 4.5F, y + dragY + 2.5F,
				-1);
     //   RenderUtil.rectangle(x + dragX + 1, y + dragY + 16, x + dragX + 119, y + dragY + 134, Colors.getColor(40, 40, 40, 255));
        RenderUtil.rectangle(x + dragX + 1, y + dragY + 16, x + dragX + 119, y + dragY + 134, Colors.getColor(25, 25, 25, 25));

        if(mouseX >= x + dragX && mouseY >= y + dragY + 16 && mouseX <= x + dragX + 119 && mouseY <= y + dragY + 134) {
            double mouseWheel = Mouse.getDWheel() / 20.0;

            if(Mouse.getEventDWheel() != 0) {
                scroll -= mouseWheel;

                float diff = buttonsHeight - 132.5F;
                scroll = MathHelper.clamp_float(scroll, Math.min(0, -diff), 0);
            }
        }

        GlStateManager.pushMatrix();
        RenderUtil.scissorBox(x + dragX, y + dragY + 17, x + dragX + 119, y + dragY + 134);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        buttons.forEach(button -> button.drawButton(mouseX, mouseY));
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }

    public void configBoxClicked(float mouseX, float mouseY, int mouseButton) {
        boolean hovering = mouseX >= x + dragX && mouseY >= y + dragY && mouseX <= x + dragX + 120 && mouseY <= y + dragY + 15;

        if(mouseButton == 0) {
            if (hovering) {
                isDragging = true;
                this.lastDragX = mouseX - dragX;
                this.lastDragY = mouseY - dragY;
            } else {
                buttons.forEach(button -> button.buttonClicked(mouseX, mouseY, mouseButton));
            }
        }
    }

    public void configBoxReleased(float mouseX, float mouseY, int state) {
        isDragging = false;
        buttons.forEach(button -> button.buttonReleased(mouseX, mouseY, state));
    }
}
