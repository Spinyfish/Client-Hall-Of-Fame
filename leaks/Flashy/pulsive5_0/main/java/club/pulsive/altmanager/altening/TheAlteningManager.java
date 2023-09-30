package club.pulsive.altmanager.altening;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class TheAlteningManager {

	public static ArrayList<String> altInformation = new ArrayList<String>();
	public static String paidAPIToken = "";
	public static ServiceSwitcher serviceSwitcher = new ServiceSwitcher();

	public String responseMessage;

	public static boolean checkAPIToken(String token) {
		ArrayList<String> urlResponce = getLinesFromURL("http://api.thealtening.com/v1/license?token=" + token);

		String responceLines = "";
		for (String string : urlResponce) {
			responceLines += string;
		}

		if (responceLines.equals("")) {
			return false;
		}

		JsonObject jsonObject = new JsonParser().parse(responceLines).getAsJsonObject();
		if (jsonObject.has("premium")) {
			TheAlteningManager.paidAPIToken = token;
		} else {
			TheAlteningManager.paidAPIToken = null;
			return false;
		}
		return true;
	}

	public static String generateAlt() {
		ArrayList<String> urlResponce = getLinesFromURL("http://api.thealtening.com/v1/generate?info=true&token=" + TheAlteningManager.paidAPIToken);

		String responceLines = "";
		for (String s : urlResponce) {
			responceLines += s;
		}

		if (responceLines.equals("")) {
			return null;
		}

		JsonObject jsonObject = new JsonParser().parse(responceLines).getAsJsonObject();

		TheAlteningManager.serviceSwitcher.switchToService(AlteningServiceType.THEALTENING);

		return jsonObject.get("token").getAsString();
	}

	public static void checkFreeToken(String token) {

		TheAlteningManager.serviceSwitcher.switchToService(AlteningServiceType.THEALTENING);
		YggdrasilAuthenticationService yggdrasilAuthenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
		YggdrasilUserAuthentication yggdrasilUserAuthentication = (YggdrasilUserAuthentication) yggdrasilAuthenticationService.createUserAuthentication(Agent.MINECRAFT);
		yggdrasilUserAuthentication.setUsername(token);
		yggdrasilUserAuthentication.setPassword("UsingWWESAltening");
		try {
			yggdrasilUserAuthentication.logIn();
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return;
		}

		Session session = new Session(yggdrasilUserAuthentication.getSelectedProfile().getName(), yggdrasilUserAuthentication.getSelectedProfile().getId().toString(), yggdrasilUserAuthentication.getAuthenticatedToken(), "LEGACY");
		Minecraft.getMinecraft().session = session;

		TheAlteningManager.altInformation.clear();
		TheAlteningManager.altInformation.add("Username : " + session.getUsername());
	}

	public static ArrayList<String> getLinesFromURL(String urlString) {
		ArrayList<String> lines = new ArrayList<String>();

		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
			bufferedReader.close();
		} catch (Exception e) {
			//e.printStackTrace();
		}

		return lines;
	}

}
