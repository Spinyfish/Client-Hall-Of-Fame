package markgg.alts;

import java.net.Proxy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import markgg.Client;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

public final class AltLoginThread extends Thread{
	
	private String status;
	private final String username;
	private Minecraft mc = Minecraft.getMinecraft();

	public AltLoginThread(String username) {
		super("Alt Login Thread");
		this.username = username;
		this.status = EnumChatFormatting.GRAY + "Waiting...";
	}

	public String getStatus() {
		return status;
	}


	public void run() {
		mc.session = new Session(username, "", "", "legacy");
		status = EnumChatFormatting.GREEN + "Logged in. (" + username + " - offline alt)";
		return;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
