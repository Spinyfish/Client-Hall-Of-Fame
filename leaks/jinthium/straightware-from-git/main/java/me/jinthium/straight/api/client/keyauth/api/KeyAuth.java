package me.jinthium.straight.api.client.keyauth.api;

import best.azura.irc.utils.HWIDUtil;
import com.google.gson.*;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import me.jinthium.straight.api.client.keyauth.user.UserData;
import me.jinthium.straight.api.client.keyauth.util.HWID;
import obfuscation.NativeLib;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.lwjglx.Sys;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.concurrent.CopyOnWriteArrayList;

@NativeLib
public class KeyAuth {

	public final String appname;
	public final String ownerid;
	private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
	public final String version;
	public final String url;

	protected String sessionid;
	public String postCheck;
	protected boolean initialized;

	public boolean isInitialized() {
		return initialized;
	}

	protected UserData userData;
	public final CopyOnWriteArrayList<String> users = new CopyOnWriteArrayList<>();

	public KeyAuth(String appname, String ownerid, String version, String url) {
		this.appname = appname;
		this.ownerid = ownerid;
		this.version = version;
		this.url = url;
	}

	public UserData getUserData() {
		return userData;
	}

	static {
		try {
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}

			} };

			SSLContext sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
			CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
			Unirest.setHttpClient(httpclient);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		HttpResponse<String> response;
		try {
			response = Unirest.post(url).field("type", "init").field("ver", version).field("name", appname)
					.field("ownerid", ownerid).asString();

			//System.out.println(response.getBody());
			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (response.getBody().equalsIgnoreCase("KeyAuth_Invalid")) {
					// Calling the method with a disabled connection
					// System.exit(0);
					System.out.println("invalid");
				}

				if (responseJSON.getBoolean("success")) {
//					System.out.println(responseJSON);
					sessionid = responseJSON.getString("sessionid");
//					System.out.println(respo);
					initialized = true;
					//System.out.println("Session ID: " + responseJSON.getString("sessionid"));

				} else if (responseJSON.getString("message").equalsIgnoreCase("invalidver")) {
					// Calling the method with a disabled version
					// System.out.println(reponseJSON.getString("download"));

				} else {
					System.out.println(responseJSON.getString("message"));
					// System.exit(0);
				}

			} catch (Exception e) {

			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void login(String username, String password) {
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			return;
		}

		HttpResponse<String> response;
		try {
			String hwid = HWID.getHWID();

			response = Unirest.post(url).field("type", "login").field("username", username).field("pass", password)
					.field("hwid", hwid).field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid)
					.asString();

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (!responseJSON.getBoolean("success")) {
					System.out.println("Error haha");
					// System.exit(0);
				} else {
					HttpResponse<JsonNode> hwidResponse;
					try {
						hwidResponse = Unirest.get("http://jinthium.com/funnytheconnorthebrettban?username=" + username).asJson();
						String hwidNode = hwidResponse.getBody().getObject().get("hwid").toString();
						if(hwidNode.equals(hwid)){
							postCheck = hwidNode;
							if(username.equals(hwidResponse.getBody().getObject().get("username"))) {
								userData = new UserData(responseJSON);
								System.out.println("Successfully Authenticated");
							}else{
								System.out.println("Username doesnt match");
							}
						}else{
							System.out.println("Hwid doesnt match");
						}
					}catch (UnirestException ex){
						ex.printStackTrace();
					}
					// optional success msg
				}

			} catch (Exception e) {

			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void upgrade(String username, String key) {
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			return;
		}

		HttpResponse<String> response;
		try {
			String hwid = HWID.getHWID();

			response = Unirest.post(url).field("type", "upgrade").field("username", username).field("key", key)
					.field("hwid", hwid).field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid)
					.asString();

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (!responseJSON.getBoolean("success")) {
					System.out.println("Error");
					// System.exit(0);
				} else {

					// optional success msg
				}

			} catch (Exception e) {

			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void license(String key) {
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			return;
		}

		HttpResponse<String> response;
		try {
			String hwid = HWID.getHWID();

			response = Unirest.post(url).field("type", "license").field("key", key).field("hwid", hwid)
					.field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid).asString();

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (!responseJSON.getBoolean("success")) {
					System.out.println("the license does not exist");
					// System.exit(0);
				} else {
					userData = new UserData(responseJSON);

					// optional success msg
				}

			} catch (Exception e) {

			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void getSessions(){
//		HttpResponse<String> response;
//		try{
//			response = Unirest.get("https://keyauth.win/api/seller/?sellerkey=6ef0d48cdaee5a0f451380b266825136&type=fetchallsessions").header("accept", "application/json").asString();
//			JsonObject jsonElement = gson.fromJson(response.getBody(), JsonObject.class);
//			JSONObject jsonResponse = new JSONObject(response.getBody());
//			System.out.println(jsonElement.getAsString());
////			System.out.println(jsonResponse.get("sessions"));
//		}catch(UnirestException ex){
//			ex.printStackTrace();
//		}
		OkHttpClient client = new OkHttpClient();

		try {
			Request request = new Request.Builder()
					.url("https://keyauth.win/api/seller/?sellerkey=6ef0d48cdaee5a0f451380b266825136&type=fetchallsessions")
					.get()
					.addHeader("Accept", "application/json")
					.build();

			Response response = client.newCall(request).execute();
			JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class);

			if(jsonObject.has("sessions")) {
				JsonArray user = jsonObject.getAsJsonArray("sessions");
				for(JsonElement userData : user){
					String username = userData.getAsJsonObject().get("credential").getAsString();
					if(!users.contains(username)){
						users.add(username);
						System.out.println(username);
					}
				}
			}

		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	public void endSession(){
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			return;
		}

		HttpResponse<String> response;
		try{
			response = Unirest.get(String.format("https://keyauth.win/api/seller/?sellerkey=6ef0d48cdaee5a0f451380b266825136&type=kill&sessid=%s", sessionid)).asString();
			JSONObject jsonResponse = new JSONObject(response.getBody());

			if(jsonResponse.getBoolean("success")){
				System.out.println("Killed Session: " + userData.getUsername());
			}

		}catch (UnirestException ex){
			ex.printStackTrace();
		}
	}

	public void ban() {
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			return;
		}

		HttpResponse<String> response;
		try {
			String hwid = HWID.getHWID();

			response = Unirest.post(url).field("type", "ban").field("sessionid", sessionid).field("name", appname)
					.field("ownerid", ownerid).asString();

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (!responseJSON.getBoolean("success")) {
					System.out.println("Error");
					// System.exit(0);
				} else {

					// optional success msg
				}

			} catch (Exception e) {

			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}

	public void webhook(String webid, String param) {
		if (!initialized) {
			System.out.println("\n\n Please initzalize first");
			return;
		}

		HttpResponse<String> response;
		try {
			String hwid = HWID.getHWID();

			response = Unirest.post(url).field("type", "webhook").field("webid", webid).field("params", param)
					.field("sessionid", sessionid).field("name", appname).field("ownerid", ownerid).asString();

			try {
				JSONObject responseJSON = new JSONObject(response.getBody());

				if (!responseJSON.getBoolean("success")) {
					System.out.println("Error");
					// System.exit(0);
				} else {

					// optional success msg
				}

			} catch (Exception e) {

			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
	}
}
