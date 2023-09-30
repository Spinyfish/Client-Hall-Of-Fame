package markgg.alts;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import markgg.Client;
import markgg.util.RandomUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;

public class GuiAltManager extends GuiScreen{

	private GuiButton login;
	private GuiButton remove;
	private GuiButton genAlt;
	private GuiButton randAlt1;
	private AltLoginThread loginThread;
	private int offset;
	public Alt selectedAlt = null;
	
	private String status = EnumChatFormatting.GRAY + "No alts selected"; public void actionPerformed(GuiButton button) throws IOException {
		RandomUtil altGen = new RandomUtil();
		Random random = new Random();
		String user;
		AltManager altManager;
		String pass;
		switch (button.id) {
		case 0:
			if (loginThread == null) {
				mc.displayGuiScreen(null);
				break;
			} 
			if (!loginThread.getStatus().equals(EnumChatFormatting.YELLOW + "Attempting to log in") && !loginThread.getStatus().equals(EnumChatFormatting.RED + "Do not hit back!" + EnumChatFormatting.YELLOW + " Logging in...")) {
				mc.displayGuiScreen(null);
				break;
			} 
			loginThread.setStatus(EnumChatFormatting.RED + "Failed to login! Please try again!" + EnumChatFormatting.YELLOW + " Logging in...");
			break;
		case 1:
			user = selectedAlt.getUsername();
			loginThread = new AltLoginThread(user);
			loginThread.start();
			break;
		case 2:
			if (loginThread != null) {
				loginThread = null;
			}
			altManager = Client.altManager;
			AltManager.registry.remove(selectedAlt);
			status = "Removed";
			selectedAlt = null;
			break;
		case 3:
			mc.displayGuiScreen(new GuiAddAlt(this));
			break;
		case 4:
			mc.displayGuiScreen(new GuiAltLogin(this));
			break;
		case 5:
			altManager = Client.altManager;
			int regSize = altManager.registry.size();
			int randIndex = random.nextInt(regSize);
			Alt randAlt = altManager.registry.get(randIndex);
			String randAltString = randAlt.getUsername();
			mc.session = new Session(randAltString, "", "", "legacy");
			status = "§7Selected random alt from list!";
			selectedAlt = randAlt;
			break;
		case 6:
			String generatedAlt = altGen.genAlt();
			mc.session = new Session(generatedAlt, "", "", "legacy");
			status = "§7Generated random alt!";
			AltManager.registry.add(new Alt(generatedAlt));
			break;
		} 
	}

	public void drawScreen(int par1, int par2, float par3) {
		if (Mouse.hasWheel()) {
			int wheel = Mouse.getDWheel();
			if (wheel < 0) {
				offset += 26;
				if (offset < 0) {
					offset = 0;
				}
			} else if (wheel > 0) {
				offset -= 26;
				if (offset < 0) {
					offset = 0;
				}
			} 
		} 
		mc.getTextureManager().bindTexture(new ResourceLocation("Raze/background.jpg"));
		this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
		this.drawGradientRect(0, height - width, width, height, 0x00000000, 0xff000000);
		drawString(fontRendererObj, mc.session.getUsername(), 10, 10, -1);
		FontRenderer fontRendererObj = this.fontRendererObj;
		StringBuilder sb2 = new StringBuilder("Raze Alt Menu - ");

		drawCenteredString(fontRendererObj, sb2.append(AltManager.registry.size()).append(" alts").toString(), (width / 2), 10.0F, -1);
		drawCenteredString(fontRendererObj, (loginThread == null) ? status : loginThread.getStatus(), (width / 2), 20.0F, -1);
		Gui.drawRect(50.0D, 33.0D, (width - 50), (height - 50), -1879048192);
		GL11.glPushMatrix();
		prepareScissorBox(0.0F, 33.0F, width, (height - 50));
		GL11.glEnable(3089);
		int y2 = 38;
		AltManager altManager2 = Client.altManager;
		for (Alt alt2 : AltManager.registry) {
			if (!isAltInArea(y2))
				continue;  String name = alt2.getMask().equals("") ? alt2.getUsername() : alt2.getMask();
				if (alt2 == selectedAlt) {
					if (isMouseOverAlt(par1, par2, y2 - offset) && Mouse.isButtonDown(0)) {
						Gui.drawRect(52.0D, (y2 - offset - 4), (width - 52), (y2 - offset + 20), -2142088622);
					} else if (isMouseOverAlt(par1, par2, y2 - offset)) {
						Gui.drawRect(52.0D, (y2 - offset - 4), (width - 52), (y2 - offset + 20), -2142088622);
					} else {
						Gui.drawRect(52.0D, (y2 - offset - 4), (width - 52), (y2 - offset + 20), -2144259791);
					} 
				} else if (isMouseOverAlt(par1, par2, y2 - offset) && Mouse.isButtonDown(0)) {
					Gui.drawRect(52.0D, (y2 - offset - 4), (width - 52), (y2 - offset + 20), -1879048192);
				} else if (isMouseOverAlt(par1, par2, y2 - offset)) {
					Gui.drawRect(52.0D, (y2 - offset - 4), (width - 52), (y2 - offset + 20), -1879048192);
				} 
				drawCenteredString(fontRendererObj, name, (width / 2), (y2 - offset) + 4, -1);
				y2 += 26;
		} 
		GL11.glDisable(3089);
		GL11.glPopMatrix();
		super.drawScreen(par1, par2, par3);
		if (selectedAlt == null) {
			login.enabled = false;
			remove.enabled = false;
		} else {
			login.enabled = true;
			remove.enabled = true;
		}
		if(altManager2.registry.size() > 1) {
			randAlt1.enabled = true;
		}else {
			randAlt1.enabled = false;
		}
		if (Keyboard.isKeyDown(200)) {
			offset -= 26;
			if (offset < 0) {
				offset = 0;
			}
		} else if (Keyboard.isKeyDown(208)) {
			offset += 26;
			if (offset < 0) {
				offset = 0;
			}
		} 
	}

	public void initGui() {
		Client.getDiscordRP().update("Changing Alts", "");
		buttonList.add(new GuiButton(0, width / 2 + 4 + 50, height - 24, 100, 20, "Cancel"));
		login = new GuiButton(1, width / 2 - 154, height - 48, 100, 20, "Login");
		buttonList.add(login);
		remove = new GuiButton(2, width / 2 - 154, height - 24, 100, 20, "Remove");
		buttonList.add(remove);
		buttonList.add(new GuiButton(3, width / 2 + 4 + 50, height - 48, 100, 20, "Add"));
		buttonList.add(new GuiButton(4, width / 2 - 50, height - 48, 100, 20, "Direct Login"));
		genAlt = new GuiButton(6, width / 2 - 50, height - 24, 100, 20, "Generate");
		buttonList.add(genAlt);
		randAlt1 = new GuiButton(5, width - 50, height - 24, 48, 20, "Random");
		buttonList.add(randAlt1);
		randAlt1.enabled = false;
		login.enabled = false;
		remove.enabled = false;
	}

	private boolean isAltInArea(int y2) {
		if (y2 - offset <= height - 50) {
			return true;
		}
		return false;
	}

	private boolean isMouseOverAlt(int x2, int y2, int y1) {
		if (x2 >= 52 && y2 >= y1 - 4 && x2 <= width - 52 && y2 <= y1 + 20 && x2 >= 0 && y2 >= 33 && x2 <= width && y2 <= height - 50) {
			return true;
		}
		return false;
	}

	protected void mouseClicked(int par1, int par2, int par3) throws IOException {
		if (offset < 0) {
			offset = 0;
		}
		int y2 = 38 - offset;
		AltManager altManager = Client.altManager;
		for (Alt alt2 : AltManager.registry) {
			if (isMouseOverAlt(par1, par2, y2)) {
				if (alt2 == selectedAlt) {
					actionPerformed((GuiButton) buttonList.get(1));
					return;
				} 
				selectedAlt = alt2;
			} 
			y2 += 26;
		} 
		try {
			super.mouseClicked(par1, par2, par3);
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
	}

	public void prepareScissorBox(float x2, float y2, float x22, float y22) {
		ScaledResolution scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int factor = scale.getScaleFactor();
		GL11.glScissor((int)(x2 * factor), (int)((scale.getScaledHeight() - y22) * factor), (int)((x22 - x2) * factor), (int)((y22 - y2) * factor));
	}
}
