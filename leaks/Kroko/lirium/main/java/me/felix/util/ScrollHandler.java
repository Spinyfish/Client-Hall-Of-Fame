/*
 * Copyright Felix Hans from ScrollHandler coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.util;

import de.lirium.base.animation.Animation;
import de.lirium.base.animation.Easings;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.input.Mouse;

@Getter
@Setter
public class ScrollHandler {

    public float maxScroll = Float.MAX_VALUE, minScroll = 0, rawScroll;
    private float scroll;
    private Animation scrollAnimation = new Animation();

    public void onScroll() {
        scroll = Math.abs((float) (rawScroll));
        rawScroll += Mouse.getDWheel() / 4f;
        rawScroll = Math.max(Math.min(minScroll, rawScroll), -maxScroll);
    }

    public float getScroll() {
        scrollAnimation.update();
        scrollAnimation.animate(rawScroll, .05, Easings.BOUNCE_IN);
        scroll = scrollAnimation.getValue();
        return scroll;
    }

}