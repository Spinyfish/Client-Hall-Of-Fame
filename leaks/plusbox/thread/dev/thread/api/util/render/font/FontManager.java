package dev.thread.api.util.render.font;

import java.util.concurrent.ConcurrentHashMap;

public class FontManager extends ConcurrentHashMap<String, FontRenderer> {
    public FontManager() {
        put("sf", new FontRenderer("assets/minecraft/thread/fonts/SFProDisplay-Regular.ttf", "SFProDisplay-Regular.ttf"));
        put("poppins", new FontRenderer("assets/minecraft/thread/fonts/Poppins-Light.ttf", "Poppins-Light.ttf"));
        put("poppins-semibold", new FontRenderer("assets/minecraft/thread/fonts/Poppins-SemiBold.ttf", "Poppins-SemiBold.ttf"));
        put("icons", new FontRenderer("assets/minecraft/thread/fonts/ClickGUI-Icons.ttf", "ClickGUI-Icons.ttf"));
    }
}
