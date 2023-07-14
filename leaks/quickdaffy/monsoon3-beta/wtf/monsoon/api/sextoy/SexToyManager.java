/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.util.EntityUtils
 *  org.json.JSONObject
 */
package wtf.monsoon.api.sextoy;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.sextoy.MonsoonLovenseToy;
import wtf.monsoon.impl.event.EventUpdate;

public class SexToyManager {
    private List<MonsoonLovenseToy> toys;
    private int vibrationAmount;
    private int vibrationIncrement;
    @EventLink
    private final Listener<EventUpdate> eventUpdateListener = e -> {
        if (Minecraft.getMinecraft().thePlayer.hurtTime <= 0) {
            this.vibrationAmount = 0;
        }
        if (this.vibrationAmount < this.vibrationIncrement) {
            --this.vibrationIncrement;
        } else if (this.vibrationAmount > this.vibrationIncrement) {
            ++this.vibrationIncrement;
        }
        if (this.vibrationIncrement < 0) {
            this.vibrationIncrement = 0;
        }
        if (this.vibrationAmount < 0) {
            this.vibrationAmount = 0;
        }
        this.getMainToy().vibrate(this.vibrationIncrement);
    };

    public void init() {
        Wrapper.getLogger().info("Initializing the Monsoon Sex Toy Integration");
        try {
            this.toys = new ArrayList<MonsoonLovenseToy>();
            this.findToys();
            this.vibrationAmount = 0;
            this.vibrationIncrement = 0;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findToys() throws Exception {
        String url = "https://api.lovense.com/api/lan/getToys";
        HashMap requestParameter = new HashMap();
        CloseableHttpClient httpClient = HttpClients.custom().build();
        HttpPost httpPost = new HttpPost(url);
        HttpResponse response = httpClient.execute((HttpUriRequest)httpPost);
        String responseBody = EntityUtils.toString((HttpEntity)response.getEntity());
        JSONObject jsonObject = new JSONObject(responseBody);
        System.out.println(responseBody);
    }

    public MonsoonLovenseToy getMainToy() {
        if (this.toys.isEmpty()) {
            return new MonsoonLovenseToy();
        }
        return this.toys.get(0);
    }

    public void vibrate(int amount) {
        this.setVibrationAmount(amount);
    }

    public List<MonsoonLovenseToy> getToys() {
        return this.toys;
    }

    public int getVibrationAmount() {
        return this.vibrationAmount;
    }

    public int getVibrationIncrement() {
        return this.vibrationIncrement;
    }

    public void setVibrationAmount(int vibrationAmount) {
        this.vibrationAmount = vibrationAmount;
    }

    public void setVibrationIncrement(int vibrationIncrement) {
        this.vibrationIncrement = vibrationIncrement;
    }
}

