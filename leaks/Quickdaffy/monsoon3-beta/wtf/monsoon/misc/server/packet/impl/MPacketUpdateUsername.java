/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package wtf.monsoon.misc.server.packet.impl;

import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.misc.server.packet.MPacket;

public class MPacketUpdateUsername
implements MPacket {
    private String newUsername;

    public MPacketUpdateUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    @Override
    public JSONObject getPacketData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", (Object)"c3");
            jsonObject.put("newUsername", (Object)this.newUsername);
        }
        catch (JSONException exception) {
            exception.printStackTrace();
        }
        return jsonObject;
    }

    public String getNewUsername() {
        return this.newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}

