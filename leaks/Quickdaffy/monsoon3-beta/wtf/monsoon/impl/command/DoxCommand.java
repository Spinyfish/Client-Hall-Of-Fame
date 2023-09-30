/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.ClientProtocolException
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.DefaultHttpClient
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package wtf.monsoon.impl.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.util.entity.PlayerUtil;

public class DoxCommand
extends Command {
    public DoxCommand() {
        super("Dox");
    }

    @Override
    public void execute(String[] args) {
        new Thread(() -> {
            if (args.length != 1) {
                PlayerUtil.sendClientMessage("Usage: .dox <username>");
                return;
            }
            String username = args[0];
            JSONArray array = null;
            try {
                array = this.getJSONFromUrl("http://51.254.127.82:1337/api/search?key=ZmFyYnlpbGVzdGdheQ==&text=" + username);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
            if (array == null) {
                PlayerUtil.sendClientMessage("Failed to dox user.");
                return;
            }
            for (int i = 0; i < array.length(); ++i) {
                JSONObject user = null;
                StringBuilder sb = new StringBuilder();
                try {
                    user = array.getJSONObject(i);
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (user == null) continue;
                try {
                    sb.append("Username: ").append(user.getString("pseudos"));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                try {
                    sb.append(" | IPs: ").append(user.getString("ips"));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                try {
                    sb.append(" | Passwords: ").append(user.getString("passwords"));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                try {
                    sb.append(" | Emails: ").append(user.getString("emails"));
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                sb.append(" | DOXED BY AMOGUSSEARCHER.GA");
                PlayerUtil.sendClientMessage(sb.toString());
            }
        }).start();
    }

    public JSONArray getJSONFromUrl(String url) {
        InputStream is = null;
        Object jObj = null;
        String json = "";
        JSONArray jArray = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpClient.execute((HttpUriRequest)httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.ISO_8859_1), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(json);
            jArray = new JSONArray(json);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return jArray;
    }
}

