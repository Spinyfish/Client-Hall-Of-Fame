package club.pulsive.client.intent.yay.account;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IntentAccount {

	@Expose
	@SerializedName("username")
	public String username;
	
	@Expose
	@SerializedName("email")
	public String email;
	
	@Expose
	@SerializedName("intent_uid")
	public int intent_uid;
	
	@Expose
	@SerializedName("client_uid")
	public int client_uid;
	
	@Expose
	@SerializedName("discord_tag")
	public String discord_tag;
	
	@Expose
	@SerializedName("discord_id")
	public String discord_id;

	@Expose
	@SerializedName("twoFactor")
	public boolean twoFactor;
	
	@Expose
	@SerializedName("api_key")
	public String api_key;

	@Expose
	@SerializedName("loggedIn")
	public boolean loggedIn;
	
}
