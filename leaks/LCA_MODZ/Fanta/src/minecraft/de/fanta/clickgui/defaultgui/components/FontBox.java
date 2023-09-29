package de.fanta.clickgui.defaultgui.components;

import java.awt.GraphicsEnvironment;
import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import de.fanta.Client;
import de.fanta.utils.Colors;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class FontBox {
//    private File dir = new File(Minecraft.getMinecraft().mcDataDir + "/" + Client.INSTANCE.name + "/" + "configs" + "/");

	public float x, y;
	public float dragX, dragY;
	public float lastDragX, lastDragY;
	public float scroll;

	public boolean isDragging;

	public ArrayList<FontButton> buttons = new ArrayList<>();
	private float buttonsHeight;

	public FontBox(float x, float y) {
		this.x = x;
		this.y = y;
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		float yOff = 16;
		for (String font : fontNames) {
			buttons.add(new FontButton(this, font, x + 2, y + yOff));
			yOff += 12.5F;
		}
		buttonsHeight = yOff;
	}

	public void drawConfigBox(float mouseX, float mouseY) {
		if (this.isDragging) {
			this.dragX = mouseX - lastDragX;
			this.dragY = mouseY - lastDragY;
		}
		Client.blurHelper.blur2(x + dragX, y + dragY, x + dragX + 200, y + dragY + 135, (float) 100);
		RenderUtil.rectangle(x + dragX, y + dragY, x + dragX + 200, y + dragY + 135, Colors.getColor(40, 40, 40, 200));
		Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Font Menu", x + dragX + 4.5F, y + dragY + 2.5F,
				-1);
		Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("________", x + dragX + 4.5F, y + dragY + 2.5F,
				-1);
		
		RenderUtil.rectangle(x + dragX + 1, y + dragY + 16, x + dragX + 199, y + dragY + 134,
				Colors.getColor(25, 25, 25, 25));

		if (mouseX >= x + dragX && mouseY >= y + dragY + 16 && mouseX <= x + dragX + 199 && mouseY <= y + dragY + 134) {
			double mouseWheel = -(Mouse.getDWheel() / 20.0);

			if (Mouse.getEventDWheel() != 0) {
				scroll -= mouseWheel;

				float diff = buttonsHeight - 132.5F;
				scroll = MathHelper.clamp_float(scroll, Math.min(0, -diff), 0);
			}
		}

		GlStateManager.pushMatrix();
		RenderUtil.scissorBox(x + dragX, y + dragY + 17, x + dragX + 199, y + dragY + 134);
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		buttons.forEach(button -> button.drawButton(mouseX, mouseY));
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.popMatrix();
	}

	public void configBoxClicked(float mouseX, float mouseY, int mouseButton) {
		boolean hovering = mouseX >= x + dragX && mouseY >= y + dragY && mouseX <= x + dragX + 200
				&& mouseY <= y + dragY + 15;

		if (mouseButton == 0) {
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
