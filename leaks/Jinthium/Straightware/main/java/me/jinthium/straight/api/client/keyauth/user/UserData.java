package me.jinthium.straight.api.client.keyauth.user;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserData {

	private String username;
	private String subscription;
	private String expiry;

	public UserData(JSONObject json) {
		JSONObject info = json.getJSONObject("info");

		JSONArray subArray = info.getJSONArray("subscriptions");
		JSONObject subObject = subArray.getJSONObject(0);
		
		this.username = info.getString("username");
		this.subscription = subObject.getString("subscription");
		this.expiry = subObject.getString("expiry");
	}

	public String getUsername() {
		return username;
	}

	public String getSubscription() {
		return subscription;
	}

	public String getExpiry() {
		return expiry;
	}

}
