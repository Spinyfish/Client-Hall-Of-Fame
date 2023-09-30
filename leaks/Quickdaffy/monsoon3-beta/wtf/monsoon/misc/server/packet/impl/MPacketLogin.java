/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package wtf.monsoon.misc.server.packet.impl;

import net.minecraft.client.Minecraft;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.misc.Identification;
import wtf.monsoon.misc.server.packet.MPacket;

public class MPacketLogin
implements MPacket {
    private String token;

    public MPacketLogin() {
    }

    public MPacketLogin(String token) {
        this.token = token;
    }

    @Override
    public JSONObject getPacketData() {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject loginData = new JSONObject();
            loginData.put("username", (Object)Wrapper.getMonsoonAccount().getUsername());
            loginData.put("hwid", (Object)Identification.getHWID());
            loginData.put("uid", (Object)Wrapper.getMonsoonAccount().getUid());
            loginData.put("ip", (Object)Identification.getIp());
            if (this.token != null) {
                loginData.put("token", (Object)this.token);
            }
            if (this.token != null) {
                loginData.put("minecraftUsername", (Object)Minecraft.getSessionInfo().get("X-Minecraft-Username"));
            }
            if (this.token != null) {
                jsonObject.put("id", (Object)"c1");
            } else {
                jsonObject.put("id", (Object)"c2");
            }
            jsonObject.put("loginData", (Object)loginData);
        }
        catch (JSONException exception) {
            exception.printStackTrace();
        }
        return jsonObject;
    }
}

