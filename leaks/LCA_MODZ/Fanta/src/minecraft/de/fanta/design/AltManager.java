package de.fanta.design;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;

import org.json.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import de.fanta.Client;
import de.fanta.msauth.MicrosoftAuthentication;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;

public class AltManager extends GuiScreen {

	private static final String ALTENING_AUTH_SERVER = "http://authserver.thealtening.com/";
	private static final String ALTENING_SESSION_SERVER = "http://sessionserver.thealtening.com/";
	public GuiScreen parentScreen;
	private final String status = "";
	public GuiTextField email;
	public GuiTextField password;
	public static String emailName = "";
	public static String passwordName = "";
	public static int i = 0;
	// private static TheAlteningAuthentication mojang =
	// TheAlteningAuthentication.mojang();
	// private static TheAlteningAuthentication theAltening =
	// TheAlteningAuthentication.theAltening();

	public AltManager(GuiScreen event) {
		parentScreen = event;
	}

	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttonList.add(new GuiButton(10, 10, height / 4 + 120 + 130, 68, 20, "Microsoft"));
		this.buttonList.add(new GuiButton(11, 10, height / 4 + 144 + 130, 68, 20, "Token"));
		this.buttonList.add(new GuiButton(9, 10, height / 4 + 96 + 130, 68, 20, "Add"));
		this.buttonList.add(new GuiButton(3, 10, height / 4 + 96 + 96, 68, 20, "Remove"));
		this.buttonList.add(new GuiButton(1, 10, height / 4 + 96 + 50, 68, 20, "Login"));
		this.buttonList.add(new GuiButton(5, 10, height / 4 + 96 + 73, 68, 20, "Back"));
		this.buttonList.add(new GuiButton(2, 10, height / 4 + 96 + 15, 68, 20, "Clipboard"));

		email = new GuiTextField(3, fontRendererObj, 10, 50, 200, 20);
		password = new GuiTextField(4, fontRendererObj, 10, 100, 200, 20);

	}

	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		email.textboxKeyTyped(typedChar, keyCode);
		password.textboxKeyTyped(typedChar, keyCode);
		if (keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(parentScreen);
		}
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	public void updateScreen() {
		email.updateCursorCounter();
		password.updateCursorCounter();
	}

	public void mouseClicked(int x, int y, int m) {
		email.mouseClicked(x, y, m);
		password.mouseClicked(x, y, m);

		try {
			super.mouseClicked(x, y, m);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		ScaledResolution s = new ScaledResolution(this.mc);
		this.drawDefaultBackground();
		int i1 = 0;

		Client.getBackgrundAPI3().renderShader();
		// Gui.drawRect(width - 100, 0, 300, height, 0xFF333333);

		Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Email:", 10, 36, 0xffffffff);
		Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("Password:", 10, 86, 0xffffffff);
		Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow("§3Logged in with §a" + mc.session.getUsername(),
				width / 2, 20, 0xffffffff);

		email.drawTextBox();
		password.drawTextBox();

		final int boxY = 26;
		final int boxX = 50;

		for (final AltTypes slot : AltsSaver.AltTypeList) {
			if (slot != null) {
				Gui.drawRect(300, (int) (boxY * 1.4 + i1), width - boxX - 50, boxY + i1 + 40,
						new Color(30, 30, 30, 120).getRGB());
				Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(slot.EmailName, width / 3,
						boxY + i1 + 20, 0xffbeff11);
//				Client.INSTANCE.unicodeBasicFontRenderer.drawStringWithShadow(slot.PWName,
//						width / 2 + Client.INSTANCE.unicodeBasicFontRenderer.getStringWidth(slot.EmailName) + 2,
//						boxY + i1 + 20, 0xffbeff11);

				final boolean isHovered = mouseX <= width - boxX - 50 && mouseX >= 300
						&& mouseY >= (int) (boxY * 1.4 + i1) && mouseY <= boxY + i1 + 40;

				if (isHovered) {
					Gui.drawRect(300, (int) (boxY * 1.4 + i1), width - boxX - 50, boxY + i1 + 40, 0x4Dc0c0c0);

					emailName = slot.getEmail().trim();
					passwordName = slot.getPassword().trim();

					if (mc.currentScreen == this && Mouse.isButtonDown(0)) {
						login(emailName, passwordName);
					}
				}

				i1 += 40;
			}
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public boolean login1(String email, String password) {
		try {
			YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
			YggdrasilUserAuthentication auth = new YggdrasilUserAuthentication(service, Agent.MINECRAFT);
			auth.setUsername(email);
			auth.setPassword(password);

			if (auth.canLogIn())
				try {
					auth.logIn();
					(Minecraft.getMinecraft()).session = new Session(auth.getSelectedProfile().getName(),
							auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
							Client.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
					return true;
				} catch (AuthenticationException authenticationException) {
					return false;
				}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	public void login(final String Email, final String password) {
		final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY,
				"");
		final YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication) authenticationService
				.createUserAuthentication(Agent.MINECRAFT);
		// api-dndx-huea-9zmq

		authentication.setUsername(Email);
		authentication.setPassword(password);
		try {
			authentication.logIn();
			Minecraft.getMinecraft().session = new Session(authentication.getSelectedProfile().getName(),
					authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(),
					"mojang");
			Client.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
		} catch (Exception e) {
			Minecraft.getMinecraft().session = new Session(Email, "", "", "mojang");
		}
	}

	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 11) {
			loginToken(getClipboardString());
		}
		if (button.id == 10) {
			MicrosoftAuthentication.getInstance().loginWithPopUpWindow();
			Client.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());
		}
		if (button.id == 9) {
			if (!email.getText().isEmpty() && !password.getText().isEmpty()) {
				emailName = email.getText();
				passwordName = password.getText();
				AltsSaver.AltTypeList.add(new AltTypes(emailName, passwordName));
			}
			AltsSaver.saveAltsToFile();
		}

		if (button.id == 3) {

		}

		if (button.id == 1) {
			if (!email.getText().isEmpty() && !password.getText().isEmpty()) {
				login(email.getText().trim(), password.getText().trim());
				email.setText("");
				password.setText("");
			}else {
				if (email.getText().isEmpty() && password.getText().isEmpty()) {
					password.setText("a");
					
					StringBuilder randomName = new StringBuilder();
		            String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789_";
		            int random = ThreadLocalRandom.current().nextInt(8, 16);
		            for (int i = 0; i < random; i++) {
		                randomName.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(1, alphabet.length())));
		            }
		            email.setText(""+randomName);
				}
			}
		}
		if (button.id == 2) {

			final String Copy = getClipboardString();
			final String[] WomboCombo = Copy.split(":");
			final String Email1 = WomboCombo[0];
			final String Passwort = WomboCombo[1];

			email.writeText(Email1);
			password.writeText(Passwort);
		}

		if (button.id == 26) {
			mc.displayGuiScreen(parentScreen);
		}

		if (button.id == 5) {

			this.mc.displayGuiScreen(new GuiMainMenu());

		}

	}

	
	
	
	public static boolean loginToken(String token) {
     //   mojang.updateService(AlteningServiceType.MOJANG);

        URL url = null;
        try {
            url = new URL("https://api.minecraftservices.com/minecraft/profile/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.setRequestProperty("Authorization", "Bearer " + token);

        conn.setRequestProperty("Content-Type", "application/json");
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String output;

        StringBuilder response = new StringBuilder();
        while (true) {
            try {
                if ((output = in.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            response.append(output);
        }

        String jsonString = response.toString();
        JSONObject obj = new JSONObject(jsonString);

        String username = obj.getString("name");
        String uuid = obj.getString("id");

        Minecraft.getMinecraft().session = new Session(username, uuid, token, "null");
        Client.INSTANCE.ircClient.setIngameName(Minecraft.getMinecraft().session.getUsername());

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
	
}
