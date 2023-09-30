/*
 * Copyright Felix Hans from Friend coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.friends;

public class Friend {

    public final String name;
    public String nickname;

    public Friend(String name, String... nickname) {
        this.name = name;
        this.nickname = nickname[0];
    }

}
