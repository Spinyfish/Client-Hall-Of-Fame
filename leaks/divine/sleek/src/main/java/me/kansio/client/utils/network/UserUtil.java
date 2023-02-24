package me.kansio.client.utils.network;

import lombok.Getter;
import lombok.Setter;

public class UserUtil {

    public static String getBuildType(int uid) {
        return uid <= 4 ? "Developer" : "Release";
    }

}
