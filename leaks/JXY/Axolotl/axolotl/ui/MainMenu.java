package axolotl.ui;

import axolotl.Axolotl;
import font.CFontRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import viamcp.gui.GuiProtocolSelector;

import java.util.ArrayList;

public class MainMenu extends GuiScreen {

	public MainMenu() {
		if(Axolotl.INSTANCE != null && !Axolotl.INSTANCE.clientOn){
			mc.displayGuiScreen(new GuiMainMenu());
		}
	}

	public class Button {

		public String string;
		public GuiScreen clickScreen;

		public Button(String string, GuiScreen clickScreen) {
			this.string = string;
			this.clickScreen = clickScreen;
		}


	}

	public Button[] buttons;

	public CFontRenderer font;

	public int mainColor = 0xFFC2CB;

	@Override
	public void initGui() {
		font = mc.customFont;
		buttons = new Button[]{
				new Button("Singleplayer", new GuiSelectWorld(this)),
				new Button("Multiplayer", new GuiMultiplayer(this)),
				new Button("AltManager", new AltManager(this)),
				new Button("Version", new GuiProtocolSelector(this)),
				new Button("Settings", new GuiOptions(this, mc.gameSettings)),
				new Button("Quit", new GuiIngameMenu())
		};
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {

		this.drawDefaultBackground();

		mc.getTextureManager().bindTexture(new ResourceLocation("Axolotl/axolotlbg.png"));
		this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);

		this.drawButtons(mouseX, mouseY);
		this.drawTitle();

	}

	private void drawTitle() {
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.width / 2.0F, this.height / 2.0F - font.getHeight() / 2.0F + 100.0F, 0.0F);
		GlStateManager.scale(2.0F, 2.0F, 1.0F);
		GlStateManager.translate(-(this.width / 2.0F), -(this.height / 2.0F - font.getHeight() / 2.0F + 100.0F), 0.0F);
		drawCenteredStringCF(font, Axolotl.INSTANCE.name, (this.width / 2.0F), this.height / 2.0F - font.getHeight() / 2.0F, mainColor);
		GlStateManager.popMatrix();
		drawCenteredStringCF(font, Axolotl.INSTANCE.version, this.width / 2.0F, this.height / 2.0F - font.getHeight() / 2.0F, mainColor);
	}

	private void drawButtons(int mouseX, int mouseY) {

		int count = 0;
		byte b;
		int i;

		ArrayList<String> strings = new ArrayList<>();
		for(Button but : buttons){
			strings.add(but.string);
		}

		Object[] buttons = strings.toArray();

		for (i = buttons.length, b = 0; b < i; ) {
			String name = (String)buttons[b];
			float x = (this.width / buttons.length * count) + (this.width / buttons.length) / 2.0F - font.getStringWidth(name) / 2.0F;
			float y = (this.height - 20);
			boolean hovered = (mouseX >= x && mouseY >= y && mouseX < x + font.getStringWidth(name) && mouseY < y + font.getHeight());
			drawCenteredStringCF(font, name, ((this.width / buttons.length * count) + (this.width / buttons.length) / 2.0F), (this.height - 20), hovered ? mainColor : -1);
			count++;
			b++;
		}

	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {

		int count = 0;
		for (int i=0;i<buttons.length;i++) {
			String name = buttons[i].string;
			float x = (this.width / buttons.length * count) + (this.width / buttons.length) / 2.0F - font.getStringWidth(name) / 2.0F;
			float y = (this.height - 20);
			boolean hovered = (mouseX >= x && mouseY >= y && mouseX < x + font.getStringWidth(name) && mouseY < y + font.getHeight());

			if (hovered) {

				Button but = this.buttons[i];

				if(but.clickScreen instanceof GuiIngameMenu)mc.shutdown();
				else mc.displayGuiScreen(but.clickScreen);

			}
			count++;
		}
	}

}
