/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.util.EntityUtils
 */
package com.thealtening.api.retriever;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.thealtening.api.TheAlteningException;
import com.thealtening.api.response.Account;
import com.thealtening.api.response.License;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public interface DataRetriever {
    public static final Logger LOGGER = Logger.getLogger("The Altening");
    public static final Gson gson = new Gson();
    public static final String BASE_URL = "http://api.thealtening.com/v2/";
    public static final String LICENCE_URL = "http://api.thealtening.com/v2/license?key=";
    public static final String GENERATE_URL = "http://api.thealtening.com/v2/generate?info=true&key=";
    public static final String PRIVATE_ACC_URL = "http://api.thealtening.com/v2/private?token=";
    public static final String FAVORITE_ACC_URL = "http://api.thealtening.com/v2/favorite?token=";
    public static final String PRIVATES_URL = "http://api.thealtening.com/v2/privates?key=";
    public static final String FAVORITES_URL = "http://api.thealtening.com/v2/favorites?key=";

    public License getLicense();

    public Account getAccount();

    public boolean isPrivate(String var1) throws TheAlteningException;

    public boolean isFavorite(String var1) throws TheAlteningException;

    public List<Account> getPrivatedAccounts();

    public List<Account> getFavoriteAccounts();

    public void updateKey(String var1);

    default public JsonElement retrieveData(String url) throws TheAlteningException {
        JsonElement jsonElement;
        String response;
        try {
            response = this.connect(url);
            jsonElement = (JsonElement)gson.fromJson(response, JsonElement.class);
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while reading retrieved data from the website");
            e.printStackTrace();
            throw new TheAlteningException("IO", e.getCause());
        }
        if (jsonElement == null) {
            LOGGER.log(Level.SEVERE, "Error while parsing website's response");
            throw new TheAlteningException("JSON", "Parsing error: \n" + response);
        }
        if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("error") && jsonElement.getAsJsonObject().has("errorMessage")) {
            LOGGER.log(Level.SEVERE, "The website returned, type: " + jsonElement.getAsJsonObject().get("error").getAsString() + ". Details:" + jsonElement.getAsJsonObject().get("errorMessage").getAsString());
            throw new TheAlteningException("Connection", "Bad response");
        }
        return jsonElement;
    }

    default public boolean isSuccess(JsonObject jsonObject) {
        return jsonObject.has("success") && jsonObject.get("success").getAsBoolean();
    }

    default public String connect(String link) throws IOException {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet post = new HttpGet(link);
        HttpResponse response = client.execute((HttpUriRequest)post);
        HttpEntity entity = response.getEntity();
        String json = EntityUtils.toString((HttpEntity)entity, (Charset)StandardCharsets.UTF_8);
        return json;
    }
}

