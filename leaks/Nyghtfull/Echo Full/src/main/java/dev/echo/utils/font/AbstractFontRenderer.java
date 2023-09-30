package dev.echo.utils.font;



import java.awt.*;

public interface AbstractFontRenderer {

   
    float getStringWidth(String text);

    int drawStringWithShadow(String name, float x, float y, int color);

   
    void drawStringWithShadow(String name, float x, float y, Color color);

    int drawCenteredString(String name, float x, float y, int color);

   
    void drawCenteredString(String name, float x, float y, Color color);

    String trimStringToWidth(String text, int width);

    String trimStringToWidth(String text, int width, boolean reverse);

    int drawString(String text, float x, float y, int color, boolean shadow);

   
    void drawString(String name, float x, float y, Color color);

    int drawString(String name, float x, float y, int color);

   
    float getMiddleOfBox(float height);

   
    int getHeight();


}
