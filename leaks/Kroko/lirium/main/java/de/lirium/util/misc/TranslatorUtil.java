/**
 * @project LiriumV4
 * @author Skush/Duzey
 * @at 13.12.2022
 */
package de.lirium.util.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import god.buddy.aot.BCompiler;
import lombok.experimental.UtilityClass;
import okhttp3.*;

import java.util.StringJoiner;

@UtilityClass
public class TranslatorUtil {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public String getTranslatedText(final String text, final String targetLanguage) {
        try {
            final OkHttpClient client = new OkHttpClient();
            final RequestBody body = new FormBody.Builder()
                    .add("client", "gtx")
                    .add("sl", "auto")
                    .add("tl", targetLanguage)
                    .add("dt", "t")
                    .add("q", text)
                    .build();
            final Request request = new Request.Builder()
                    .url("https://translate.googleapis.com/translate_a/single")
                    .post(body)
                    .build();
            final Response response = client.newCall(request).execute();
            final String responseString = response.body().string();

            final JsonArray jsonArray = gson.fromJson(responseString, JsonArray.class);
            final JsonArray jsonArray1 = jsonArray.get(0).getAsJsonArray();
            final StringJoiner stringJoiner = new StringJoiner("");
            for (final JsonElement jsonElement : jsonArray1)
                stringJoiner.add(jsonElement.getAsJsonArray().get(0).getAsString());
            return stringJoiner.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to detected language!";
        }
    }
}