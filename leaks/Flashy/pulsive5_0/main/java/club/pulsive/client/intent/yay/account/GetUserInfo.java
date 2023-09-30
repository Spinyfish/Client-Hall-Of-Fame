package club.pulsive.client.intent.yay.account;

import club.pulsive.client.intent.yay.EnvironmentConstants;
import club.pulsive.client.intent.yay.util.ConstructableEntry;
import club.pulsive.client.intent.yay.web.Browser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GetUserInfo implements EnvironmentConstants {

	static final String COMBO_LOGIN_ENDPOINT = "login?email=%s&password=%s", API_KEY_LOGIN_ENDPOINT = "login?key=%s";

	/**
	 * Reference object for exceptions
	 */
	public static final IntentAccount loginFailure = new IntentAccount(), parseFailure = new IntentAccount();

	/**
	 * @param api_key API Key for secure Intent account retrieval
	 * @return Intent user account
	 */
	public static IntentAccount getIntentAccount(String api_key) {
		try {
			api_key = URLEncoder.encode(api_key, StandardCharsets.UTF_8.toString());
			String response = Browser.getResponse(String.format(API_KEY_LOGIN_ENDPOINT, api_key));
			
			if(response.equals("Invalid Login Credentials"))
				return loginFailure;
			
			try {
				return new Gson().fromJson(response, IntentAccount.class);
			}catch(JsonSyntaxException e) {
				e.printStackTrace();
				return parseFailure;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return loginFailure;
		}
	}
	
	/**
	 * @param email Email address of Intent account
	 * @param password Password for Intent account
	 * @return Intent user account
	 * @implNote Should not be used in production due to security risks.
	 * Violations may result in penalty.
	 */
	public IntentAccount getIntentAccount(String email, String password) {
		try {
			//email = URLEncoder.encode(email, StandardCharsets.UTF_8.toString());
			//password = URLEncoder.encode(password, StandardCharsets.UTF_8.toString());
			String response = Browser.postResponse("login", new ConstructableEntry<>("email", email), new ConstructableEntry<>("password", password));
			
			if(response.equals("Invalid Login Credentials"))
				return loginFailure;
			
			try {
				return new Gson().fromJson(response, IntentAccount.class);
			}catch(JsonSyntaxException e) {
				e.printStackTrace();
				return parseFailure;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return loginFailure;
		}
	}
}
