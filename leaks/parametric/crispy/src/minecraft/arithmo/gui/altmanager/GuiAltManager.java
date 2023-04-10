/*
 * Decompiled with CFR 0_122.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.lwjgl.input.Keyboard
 *  org.lwjgl.input.Mouse
 *  org.lwjgl.opengl.GL11
 */
package arithmo.gui.altmanager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import crispy.Crispy;

import crispy.fonts.decentfont.FontUtil;
import crispy.fonts.decentfont.MinecraftFontRenderer;
import crispy.util.render.gui.Draw;
import crispy.util.render.gui.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class GuiAltManager extends GuiScreen {
	private GuiButton login;

	private GuiButton remove;
	private GuiButton rename;
	private AltLoginThread loginThread;
	private int offset;
	public Alt selectedAlt = null;
	private String status =  "\2473Idle...";

	public GuiAltManager() {
//      
	}

	@Override
	public void actionPerformed(GuiButton button) {
		switch (button.id) {
		case 0: {
			if (this.loginThread == null) {
				this.mc.displayGuiScreen(new GuiMainMenu());
				break;
			}
			
			break;
		}
		case 1: {
			String user = this.selectedAlt.getUsername();
			String pass = this.selectedAlt.getPassword();
			this.loginThread = new AltLoginThread(user, pass);
			this.loginThread.start();
			break;
		}
		case 2: {
			if (this.loginThread != null) {
				this.loginThread = null;
			}
			AltManager.registry.remove(this.selectedAlt);
			this.status = "\u00a7aRemoved.";
			try {
				Crispy.INSTANCE.getAltFile().getFile(Alts.class).saveFile();
			} catch (Exception user) {
				// empty catch block
			}
			this.selectedAlt = null;
			break;
		}
		case 3: {
			this.mc.displayGuiScreen(new GuiAddAlt(this));
			break;
		}
		case 4: {
			this.mc.displayGuiScreen(new GuiAltLogin(this));
			break;
		}
		case 5: {
			ArrayList<Alt> registry = AltManager.registry;
			Random random = new Random();
			Alt randomAlt = registry.get(random.nextInt(AltManager.registry.size()));
			String user2 = randomAlt.getUsername();
			String pass2 = randomAlt.getPassword();
			this.loginThread = new AltLoginThread(user2, pass2);
			this.loginThread.start();
			break;
		}
		case 6: {
			this.mc.displayGuiScreen(new GuiRenameAlt(this));
			break;
		}
		case 7: {
			this.mc.displayGuiScreen(new GuiMultiplayer(null));
			break;
		}
		case 8: {
			for (int i = 0; i < 3; i++) {
				AltManager.registry.clear();
				try {
					Crispy.INSTANCE.getAltFile().getFile(Alts.class).loadFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.status = "\u00a7bReloaded!";
			}
			break;
		} case 9: {
			this.mc.displayGuiScreen(new GuiTheAltening(this));
			break;
		} case 10: {
			
		}
	
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		
		if (Mouse.hasWheel()) {
			int wheel = Mouse.getDWheel();
			if (wheel < 0) {
				this.offset += 26;
				if (this.offset < 0) {
					this.offset = 0;
				}
			} else if (wheel > 0) {
				this.offset -= 26;
				if (this.offset < 0) {
					this.offset = 0;
				}
			}
		}
		ScaledResolution res = new ScaledResolution(this.mc, mc.displayWidth, mc.displayHeight);
		MinecraftFontRenderer fontRendererObj = FontUtil.cleanmedium;
		Gui.drawRect(0.0, 0.0, res.getScaledWidth(), res.getScaledHeight(), Colors.getColor(0));
		fontRendererObj.drawString(this.mc.getSession().getUsername(), 10, 10, 14540253);

		StringBuilder sb = new StringBuilder("Account Manager - ");
		this.drawCenteredString(fontRendererObj, sb.append(AltManager.registry.size()).append(" alts").toString(),
				this.width / 2, 10, -1);
		this.drawCenteredString(fontRendererObj,
				this.loginThread == null ? this.status : this.loginThread.getStatus(), this.width / 2, 20, -1);

		RenderUtil.drawRoundedRect(0, 33.0, this.width, this.height - 50, 0, Colors.getColor(25, 25, 25, 255));
		//Gui.drawRect(50.0, 33.0, this.width - 50, this.height - 50, Colors.getColor(25, 25, 25, 255));
		RenderUtil.drawRoundedRect(500, 33.0, this.width / 1.05, this.height - 50, 5, Colors.getColor(45, 45, 45, 255));

		GL11.glPushMatrix();
		this.prepareScissorBox(0.0f, 33.0f, this.width, this.height - 50);
		GL11.glEnable((int) 3089);
		int y = 38;
		for (Alt alt : AltManager.registry) {
			if (!this.isAltInArea(y))
				continue;
			int pos = y - this.offset;
			boolean hovered = this.isMouseOverAlt(par1, par2, pos);
			String name = alt.getMask().equals("") ? alt.getUsername() : alt.getMask();
			String pass = alt.getPassword().equals("") ? "\u00a7cCracked" : alt.getPassword().replaceAll(".", "*");

			if(selectedAlt != alt) {
				RenderUtil.drawRoundedRect(52.0, y - this.offset - 7, this.width / 3, y - this.offset + 20, 5, hovered ? Colors.getColor(55, 55, 55, 255) : Colors.getColor(45, 45, 45, 255));
			}  else {
				RenderUtil.drawRoundedRect(52.0, y - this.offset - 7, this.width / 3, y - this.offset + 20, 5, hovered ? Colors.getColor(105, 105, 105, 255) : Colors.getColor(85, 85, 85, 255));

			}
			this.drawCenteredString(fontRendererObj, "Username: "+name, this.width / 5, y - this.offset, -1);
			this.drawCenteredString(fontRendererObj, "Password: " + pass, this.width / 5, y - this.offset + 10, 5592405);
			y += 36;
		}
		if(mc.session.getUsername() != null) {
			FontUtil.cleanmedium.drawCenteredString("Alt info: ", width / 2f, 43, -1);
			FontUtil.cleanmedium.drawCenteredString("Username: " + mc.getSession().getUsername(), width / 1.7f, 43, -1);

		}
		GL11.glDisable((int) 3089);
		GL11.glPopMatrix();
		super.drawScreen(par1, par2, par3);
		if (this.selectedAlt == null) {
			this.login.enabled = false;
			this.remove.enabled = false;
			this.rename.enabled = false;
		} else {
			this.login.enabled = true;
			this.remove.enabled = true;
			this.rename.enabled = true;
		}
		if (Keyboard.isKeyDown((int) 200)) {
			this.offset -= 26;
			if (this.offset < 0) {
				this.offset = 0;
			}
		} else if (Keyboard.isKeyDown((int) 208)) {
			this.offset += 26;
			if (this.offset < 0) {
				this.offset = 0;
			}
		}
	}

	@Override
	public void initGui() {
	
		this.buttonList.add(new GuiButton(0, this.width / 2 + 116, this.height - 24, 75, 20, "Cancel"));
		this.login = new GuiButton(1, this.width / 2 - 122, this.height - 48, 100, 20, "Login");
		this.buttonList.add(this.login);
		this.remove = new GuiButton(2, this.width / 2 - 40, this.height - 24, 70, 20, "Remove");
		this.buttonList.add(this.remove);
		this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 86, this.height - 48, 100, 20, "Add"));
		this.buttonList.add(new GuiButton(4, this.width / 2 - 16, this.height - 48, 100, 20, "Direct Login"));
		this.buttonList.add(new GuiButton(5, this.width / 2 - 122, this.height - 24, 78, 20, "Random"));
		this.rename = new GuiButton(6, this.width / 2 + 38, this.height - 24, 70, 20, "Edit");
		this.buttonList.add(this.rename);
		this.buttonList.add(new GuiButton(8, this.width / 2 - 190, this.height - 48, 60, 20, "Reload"));
		this.buttonList.add(new GuiButton(9, this.width /2 - 190, this.height - 24, 60,20 , "Altening"));

		this.login.enabled = false;
		this.remove.enabled = false;
		this.rename.enabled = false;
	}

	private boolean isAltInArea(int y) {
		return y - this.offset <= this.height - 50;
	}

	private boolean isMouseOverAlt(int x, int y, int y1) {
		return x >= 52 && y >= y1 - 4 && x <= this.width / 3 && y <= y1 + 20 && x >= 0 && y >= 33 && x <= this.width
				&& y <= this.height - 50;
	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		if (this.offset < 0) {
			this.offset = 0;
		}
		int y = 38;
		for (Alt alt : AltManager.registry) {
			int pos = y - this.offset;
			if (this.isMouseOverAlt(par1, par2, pos)) {
				if (alt == this.selectedAlt) {
					this.actionPerformed((GuiButton) this.buttonList.get(1));
					return;
				}
				this.selectedAlt = alt;
			}
			y += 36;
		}
		try {
			super.mouseClicked(par1, par2, par3);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void prepareScissorBox(float x, float y, float x2, float y2) {
		ScaledResolution scale = new ScaledResolution(this.mc, mc.displayWidth, mc.displayHeight);
		int factor = scale.getScaleFactor();
		GL11.glScissor((int) ((int) (x * (float) factor)),
				(int) ((int) (((float) scale.getScaledHeight() - y2) * (float) factor)),
				(int) ((int) ((x2 - x) * (float) factor)), (int) ((int) ((y2 - y) * (float) factor)));
	}
}
