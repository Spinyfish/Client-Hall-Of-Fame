package felix.gui.click;

import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;

import felix.Client;
import felix.gui.click.components.Frame;
import felix.management.FontManager;
import felix.util.font.FontRenderer;
import net.minecraft.client.gui.VanillaFontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class ClickGui extends GuiScreen {

	public static int compID = 0;

	private ArrayList<Frame> frames = new ArrayList<Frame>();

	private final FontRenderer fr = Client.INSTANCE.getFontManager().getFont("Display 16", true);

	public ClickGui() {
		compID = 0;

	}

	protected void addFrame(Frame frame) {
		if (!frames.contains(frame)) {
			frames.add(frame);
		}
	}

	protected ArrayList<Frame> getFrames() {
		return frames;
	}

	@Override
	public void initGui() {
		for (Frame frame : frames) {
			frame.initialize();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		for (Frame frame : frames) {
			frame.mouseClicked(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		for (Frame frame : frames) {
			frame.keyTyped(keyCode, typedChar);
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		ScaledResolution sR = new ScaledResolution(mc);

		for (Frame frame : frames) {
			frame.render(mouseX, mouseY);
		}
	}
}

