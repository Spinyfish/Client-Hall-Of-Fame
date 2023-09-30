package axolotl.ui;

import java.io.IOException;
import java.net.Proxy;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import axolotl.Axolotl;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Session;
//import viamcp.ViaMCP;
//import viamcp.protocols.ProtocolCollection;
@SuppressWarnings("all")
public class AltManager extends GuiScreen {

	private GuiScreen parent;
	private String status;
	private GuiTextField loginField;
	
	public AltManager(GuiScreen guiMainMenu) {
		parent = guiMainMenu;
	}	
	
	@Override
	public void initGui() {
		this.status = "\2477Logged in as " + mc.session.getUsername();
		int width = 100, height = 20, offset = (5 / 2);
		buttonList.add(new GuiButton(0, this.width / 2 - width / 2, this.height - height - offset, width, height, "Login"));
		buttonList.add(new GuiButton(1, this.width / 2 - width / 2, this.height - (height + offset) * 2, width, height, I18n.format("gui.back", new Object[0])));
		loginField = new GuiTextField(2, fontRendererObj, this.width / 2 - 100, this.height  / 4, 200, 20);
		loginField.setFocused(true);
		loginField.setText("");
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		loginField.textboxKeyTyped(typedChar, keyCode);
		if(keyCode == Keyboard.KEY_ESCAPE) {
			actionPerformed((GuiButton) buttonList.get(1));
		}
		if(keyCode == Keyboard.KEY_RETURN) {
			actionPerformed((GuiButton) buttonList.get(0));
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException{
		if(button.id == 0) {
			if(loginField.getText() != null & !loginField.getText().isEmpty()) {
				if (!loginField.getText().contains(" ")) {
					try {
						final String args[] = loginField.getText().split(":");
						if (args[0].contains("@") && args[0].contains(".")) {
							final YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication) new YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT);
							authentication.setUsername(args[0]);
							authentication.setPassword(args[1]);
							try {
								authentication.logIn();

								Minecraft.getMinecraft().session = new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), "mojang");

								this.status = "\247aLogged into " + mc.session.getUsername();

								if (!Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + Axolotl.INSTANCE.name)))
									Files.createDirectory(Paths.get(System.getProperty("user.dir") + "\\" + Axolotl.INSTANCE.name));

								write("alt.txt", loginField.getText());
								Display.setTitle(Axolotl.INSTANCE.clientOn ? Axolotl.INSTANCE.full_name + " - " + mc.session.getUsername() + " - 1.8 - " + Axolotl.INSTANCE.userInfo.client_uid: "Minecraft 1.8");
							} catch (IOException e) {
								e.printStackTrace();
							} catch (AuthenticationException e) {
								this.status = "\247cInvalid credentials";
							}
							return;
						} else {
							Minecraft.getMinecraft().session = new Session(loginField.getText(), "", "", "mojang");

							this.status = "\247aLogged into " + loginField.getText() + " (" + loginField.getText().length() + ")";

							if (!Files.exists(Paths.get(System.getProperty("user.dir") + "\\" + Axolotl.INSTANCE.name)))
								Files.createDirectory(Paths.get(System.getProperty("user.dir") + "\\" + Axolotl.INSTANCE.name));

							write("alt.txt", loginField.getText());
							Display.setTitle(Axolotl.INSTANCE.clientOn ? Axolotl.INSTANCE.full_name + " - " + mc.session.getUsername() + " - 1.8 - " + Axolotl.INSTANCE.userInfo.client_uid: "Minecraft 1.8");
							return;
						}
					} catch (Exception e) {
						this.status = "\247cInvalid credentials";
					}
				}
			}
			this.status = "\247cInvalid credentials";
		}else if (button.id == 1) {
			mc.displayGuiScreen(parent);
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, "Alt Login (email:password)", this.width / 2, 10, -1);
		this.drawCenteredString(this.fontRendererObj, this.status, this.width / 2, 20, -1);
		loginField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void updateScreen() {
		loginField.updateCursorCounter();
	}
	
	public static void write(String file, String write) {
		try {
			Files.write(Paths.get(System.getProperty("user.dir") + "\\" + Axolotl.INSTANCE.name + "\\" + file), write.getBytes());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
}
