/*
 * Copyright Felix Hans from Notification coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.notifications;

import de.lirium.base.animation.Animation;
import de.lirium.base.animation.Easings;
import de.lirium.base.helper.TimeHelper;
import de.lirium.util.render.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.awt.*;

public class Notification {

    private final FontRenderer titleFontRenderer, descriptionFontRenderer;

    private final TimeHelper timeHelper;
    private final String title, description;
    private final long stayTime;

    private final Animation rectangleAnimation = new Animation();

    public Notification(String title, String description, long... time) {
        this.title = title;
        this.description = description;
        final long defaultStayTime = 3000;
        if (time[0] == 0)
            stayTime = defaultStayTime;
        else
            stayTime = time[0];

        titleFontRenderer = new FontRenderer(new Font("Arial", Font.PLAIN, 30));
        descriptionFontRenderer = new FontRenderer(new Font("Arial", Font.PLAIN, 23));

        (timeHelper = new TimeHelper()).reset();

    }

    public boolean hasTimeReached() {
        return timeHelper.hasReached(stayTime) && rectangleAnimation.isDone();
    }

    public void doRenderNotification(final int x, final int y) {

        final int rectangleWidth = Math.max(titleFontRenderer.getStringWidth(title), descriptionFontRenderer.getStringWidth(description)) + 10, rectangleHeight = 60;

        {
            rectangleAnimation.update();
            rectangleAnimation.animate(rectangleWidth, .2, Easings.BACK_IN4);
        }

        Gui.drawRect(x, y, (int) (x + rectangleAnimation.getValue()), y + rectangleHeight, new Color(23, 23, 23).getRGB());

    }

}
