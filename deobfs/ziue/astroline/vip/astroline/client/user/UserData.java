/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package vip.astroline.client.user;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserData {
    private final String username;
    private final String subscription;
    private final String expiry;

    public UserData(JSONObject json) {
        JSONObject info = json.getJSONObject("info");
        JSONArray subArray = info.getJSONArray("subscriptions");
        JSONObject subObject = subArray.getJSONObject(0);
        this.username = info.getString("username");
        this.subscription = subObject.getString("subscription");
        this.expiry = subObject.getString("expiry");
    }

    public String getUsername() {
        return this.username;
    }

    public String getSubscription() {
        return this.subscription;
    }

    public String getExpiry() {
        return this.expiry;
    }
}
