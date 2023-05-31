package de.lirium.util.altening.data;

import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class AltInfo {
    public final String token, username;
    public final boolean limitReached;
    public final String skin;

    public final Map<String, String> info = new HashMap<>();

    public AltInfo(String token, String username, boolean limitReached, String skin, Tuple<String, String>... info) {
        this.token = token;
        this.username = username;
        this.limitReached = limitReached;
        this.skin = skin;

        for (Tuple<String, String> tuple : info) {
            this.info.put(tuple.getFirst(), tuple.getSecond());
        }
    }

    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(";");
        for (Map.Entry<String, String> entry : info.entrySet()) {
            joiner.add(entry.getKey() + "=" + entry.getValue());
        }
        return token + "(" + username + ";" + limitReached + ";" + skin + ";" + joiner + ")";
    }
}
