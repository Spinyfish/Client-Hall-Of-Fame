package dev.tenacity.utils.font;

import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

import java.awt.*;

public interface AbstractFontRenderer {

    @Exclude(Strategy.NAME_REMAPPING)
    float getStringWidth(String text);

    int drawStringWithShadow(String name, float x, float y, int color);

    @Exclude(Strategy.NAME_REMAPPING)
    void drawStringWithShadow(String name, float x, float y, Color color);

    int drawCenteredString(String name, float x, float y, int color);

    @Exclude(Strategy.NAME_REMAPPING)
    void drawCenteredString(String name, float x, float y, Color color);

    String trimStringToWidth(String text, int width);

    String trimStringToWidth(String text, int width, boolean reverse);

    int drawString(String text, float x, float y, int color, boolean shadow);

    @Exclude(Strategy.NAME_REMAPPING)
    void drawString(String name, float x, float y, Color color);

    int drawString(String name, float x, float y, int color);

    @Exclude(Strategy.NAME_REMAPPING)
    float getMiddleOfBox(float height);

    @Exclude(Strategy.NAME_REMAPPING)
    int getHeight();


}
