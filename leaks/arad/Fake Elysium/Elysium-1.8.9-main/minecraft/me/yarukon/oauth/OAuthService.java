package me.yarukon.oauth;

import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonPrimitive;
import dev.elysium.client.extensions.ConfigManager;
import dev.elysium.client.ui.gui.alts.GuiAltLogin;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.yarukon.oauth.web.HTTPServer;

public class OAuthService {
	public GuiAltLogin guiAltLogin;
	private final CloseableHttpClient httpclient = HttpClients.createDefault();
	public final JsonParser parser = new JsonParser();
	
	private final String appID = "c6cd7b0f-077d-4fcf-ab5c-9659576e38cb";
	private final String appSecret = "vI87Q~GkhVHJSLN5WKBbEKbK0TJc9YRDyOYc5";

	public String message = "";
	public Session session;

	public OAuthService(GuiAltLogin guiAltLogin) {
		this.guiAltLogin = guiAltLogin;
	}

	public void authWithNoRefreshToken() {
		authenticate(null);
	}

	public void authenticate(String refreshToken) {
		int port = 1919;
		message = "Starting to authenticate";
		guiAltLogin.status = "Initializing..." + " (0/0)";
		HTTPServer srv = null;
		if(refreshToken == null) {
			//System.out.println("https://login.live.com/oauth20_authorize.srf?response_type=code&client_id=" + appID + "&redirect_uri=http://localhost:1919/login&scope=XboxLive.signin+offline_access");
			this.openUrl("https://login.live.com/oauth20_authorize.srf?response_type=code&client_id=" + appID + "&redirect_uri=http://localhost:1919/login&scope=XboxLive.signin+offline_access",
					false);
			message = "Waiting for user to authorize...";
			guiAltLogin.status = message + " (1/6)";
			srv = new HTTPServer(port);
			srv.await();
			System.out.println("Awaited");
			message = "Authorization Code -> Authorization Token";
		}

		System.out.println("SRC Toke : " + srv.token);
		String[] live = this.liveAuth(refreshToken != null ? refreshToken : srv.token, refreshToken != null);
		System.out.println("CODE OUT : " + live[0]);
		if(!live[0].equals("FAILED")) {
			message = "Authenticating with XBox live...";
			guiAltLogin.status = message + " (2/6)";
			System.out.println(message);
			String[] xbox = this.xBoxAuth(live[0]);
			System.out.println("CODE OUT : " + xbox[0]);
			guiAltLogin.status = "WAITING (2/6)";
			if(!xbox[0].equals("FAILED")) {
				message = "Authenticating with XSTS...";
				guiAltLogin.status = message + " (3/6)";
				System.out.println(message);
				String[] xsts = this.xstsAuth(xbox[0]);
				System.out.println("CODE OUT : " + xsts[0]);
				guiAltLogin.status = "WAITING (3/6)";
				if(!xsts[0].equals("FAILED")) {
					message = "Authenticating with Minecraft API..." + " (4/6)";
					guiAltLogin.status = message;
					System.out.println(message);
					String[] mc_accessToken = this.minecraftAuth(xsts[0], xsts[1]);
					System.out.println("CODE OUT : " + mc_accessToken[1]);
					System.out.println("STATUS : " + mc_accessToken[0]);
					guiAltLogin.status = "WAITING (4/6)";
					if(!mc_accessToken[0].equals("FAILED")) {
						message = "Obtaining user profile with Minecraft API..." + " (5/6)";
						guiAltLogin.status = message;
						System.out.println(message);
						String[] mc_userInfo = this.obtainUUID(mc_accessToken[1]);
						System.out.println("CODE OUT : " + mc_userInfo[0]);
						guiAltLogin.status = "WAITING (5/6)";
						if(!mc_userInfo[0].equals("FAILED")) {
							guiAltLogin.status = "SUCCESS" + " (6/6)";
							this.session = new Session(mc_userInfo[1], mc_userInfo[0], mc_accessToken[1], "mojang");
							ConfigManager.saveLastUser(session.getUsername(), session.getToken(), session.getPlayerID());
							message = "Successfully login with account " + this.session.getUsername();
							System.out.println(message);
							Minecraft.getMinecraft().session = this.session;
						} else {
							message = "Failed to obtain user profile!";
							guiAltLogin.status = message + " (6/6)";
						}
					} else {
						message = "Authentication with Minecraft API failed!";
						guiAltLogin.status = message + " (5/6)";
					}
				} else {
					message = "Authentication with XSTS failed!";
					guiAltLogin.status = message + " (4/6)";
				}
			} else {
				message = "Authentication with XBox live failed!";
				guiAltLogin.status = message + " (3/6)";
			}
		} else {
			message = "Authentication with live failed!";
			guiAltLogin.status = message + " (2/6)";
		}
	}
	
	public void openUrl(String url, boolean fastLogin) {
		try {
			if(fastLogin) {
				new URL("http://188.166.206.43/f3rf4XRDnJHVqQ78sS3Psee3vXPAWB3N/update/V12?value=1").openConnection().connect();
				return;
			}
			Desktop d = Desktop.getDesktop();
			d.browse(new URI(url));
		} catch (Exception ex) {
			System.out.println("Failed to open url!");
		}
	}
	
	public String[] obtainUUID(String mc_accessToken) {
		String resultJson = "UNKNOWN";
		try {
			resultJson = sendGet("https://api.minecraftservices.com/minecraft/profile", mc_accessToken);
			JsonObject result = (JsonObject) parser.parse(resultJson);
			return new String[] {result.get("id").getAsString(), result.get("name").getAsString()};
		} catch (Exception ex) {
			return new String[] {"FAILED", resultJson};
		}
	}
	
	public String[] minecraftAuth(String xbl_token, String uhs) {
		JsonObject obj = new JsonObject();
		obj.addProperty("identityToken", "XBL3.0 x=" + uhs + ";" + xbl_token);
		
		String resultJson = "UNKNOWN";
		try {
			resultJson = sendPost("https://api.minecraftservices.com/authentication/login_with_xbox", obj.toString());
			JsonObject result = (JsonObject) parser.parse(resultJson);
			return new String[] {"SUCCESS", result.get("access_token").getAsString()};
		} catch (Exception ex) {
			return new String[] {"FAILED", resultJson};
		}

	}
	
	public String[] xstsAuth(String xbl_token) {
		JsonObject obj = new JsonObject();
		JsonObject properties = new JsonObject();
		JsonArray arr = new JsonArray();
		
		properties.addProperty("SandboxId", "RETAIL");
		arr.add(new JsonPrimitive(xbl_token));
		properties.add("UserTokens", arr);
		
		obj.add("Properties", properties);
		obj.addProperty("RelyingParty", "rp://api.minecraftservices.com/");
		obj.addProperty("TokenType", "JWT");
		
		String resultJson = "UNKNOWN";
		try {
			resultJson = sendPost("https://xsts.auth.xboxlive.com/xsts/authorize", obj.toString());
			JsonObject result = (JsonObject) parser.parse(resultJson);
			return new String[] {result.get("Token").getAsString(), result.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString()};
		} catch (Exception ex) {
			return new String[] {"FAILED", resultJson};
		}
	}
	
	public String[] xBoxAuth(String accessToken) {
		JsonObject obj = new JsonObject();
		JsonObject properties = new JsonObject();
		properties.addProperty("AuthMethod", "RPS");
		properties.addProperty("SiteName", "user.auth.xboxlive.com");
		properties.addProperty("RpsTicket", "d=" + accessToken);
		obj.add("Properties", properties);
		obj.addProperty("RelyingParty", "http://auth.xboxlive.com");
		obj.addProperty("TokenType", "JWT");
		
		String resultJson = "UNKNOWN";
		try {
			resultJson = sendPost("https://user.auth.xboxlive.com/user/authenticate", obj.toString());
			JsonObject result = (JsonObject) parser.parse(resultJson);
			return new String[] {result.get("Token").getAsString(), result.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString()};
		} catch (Exception ex) {
			return new String[] {"FAILED", resultJson};
		}
	}
	
	public String[] liveAuth(String authCode, boolean isRefresh) {
		HashMap<String, String> map = new HashMap<>();
		map.put("client_id", appID);
		
		if(isRefresh)
			map.put("refresh_token", authCode);
		else
			map.put("code", authCode);
		
		map.put("grant_type", isRefresh ? "refresh_token" : "authorization_code");
		map.put("redirect_uri", "http://localhost:1919/login");
		map.put("scope", "XboxLive.signin offline_access");
		map.put("client_secret", appSecret);
		
		String resultJson = "UNKNOWN";
		try {
			resultJson = sendPost("https://login.live.com/oauth20_token.srf", map);
			System.out.println(resultJson);
			JsonObject obj = (JsonObject) parser.parse(resultJson);
			return new String[] {obj.get("access_token").getAsString(), obj.get("refresh_token").getAsString()};
		} catch (Exception ex) {
			ex.printStackTrace();
			return new String[] {"FAILED", resultJson};
		}
	}

	public String sendPost(String url, Map<String, String> map) throws Exception {
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			formparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
		HttpPost httppost = new HttpPost(url);
		
		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httppost.setEntity(entity);
		
		CloseableHttpResponse response = null;
		response = httpclient.execute(httppost);
		HttpEntity entity1 = response.getEntity();
		String result = EntityUtils.toString(entity1);
		
		return result;
	}
	
	public String sendPost(String url, String value) throws Exception {
		StringEntity entity = new StringEntity(value, "UTF-8");
		HttpPost httppost = new HttpPost(url);
		
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader("Accept", "application/json");
		httppost.setEntity(entity);
		
		CloseableHttpResponse response = null;
		response = httpclient.execute(httppost);
		HttpEntity entity1 = response.getEntity();
		String result = null;
		result = EntityUtils.toString(entity1);
		
		return result;
	}
	
	public String sendGet(String url, String header) throws Exception {
		HttpGet httpGet = new HttpGet(url);
		
		httpGet.setHeader("Authorization", "Bearer " + header);
		
		CloseableHttpResponse response = null;
		response = httpclient.execute(httpGet);
		HttpEntity entity1 = response.getEntity();
		String result = null;
		result = EntityUtils.toString(entity1);

		return result;
	}
	
}
