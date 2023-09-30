/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package wtf.monsoon.api.manager.alt;

import com.thealtening.api.retriever.BasicDataRetriever;
import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.manager.alt.Alt;

public class AltManager {
    private final List<Alt> alts = new ArrayList<Alt>();
    private final TheAlteningAuthentication alteningAuthentication = TheAlteningAuthentication.mojang();
    private final BasicDataRetriever alteningAltFetcher = new BasicDataRetriever(this.apiKey);
    private String apiKey = "api-0000-0000-0000";

    public AltManager() {
        this.alteningAuthentication.updateService(AlteningServiceType.MOJANG);
    }

    public void addAlt(Alt alt) {
        this.alts.add(alt);
    }

    public void removeAlt(Alt alt) {
        this.alts.remove(alt);
    }

    public JSONObject getJSON() {
        JSONObject json = new JSONObject();
        this.alts.forEach(alt -> {
            JSONObject altJson = new JSONObject();
            try {
                altJson.put("password", (Object)alt.getPassword());
                altJson.put("auth", (Object)alt.getAuthenticator().name());
                altJson.put("username", (Object)alt.getUsername());
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                json.put(alt.getEmail(), (Object)altJson);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        });
        try {
            json.put("altening-api-key", (Object)this.getApiKey());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        this.alteningAltFetcher.updateKey(apiKey);
        Wrapper.getMonsoon().getConfigSystem().saveAlts(this);
    }

    public List<Alt> getAlts() {
        return this.alts;
    }

    public TheAlteningAuthentication getAlteningAuthentication() {
        return this.alteningAuthentication;
    }

    public BasicDataRetriever getAlteningAltFetcher() {
        return this.alteningAltFetcher;
    }

    public String getApiKey() {
        return this.apiKey;
    }
}

