package cc.swift.module.impl.render.hud;

import cc.swift.util.IMinecraft;
import cc.swift.util.render.font.CFontRenderer;
import cc.swift.util.render.font.FontRenderer;
import net.minecraft.client.Minecraft;

public enum HudFont implements IMinecraft {
    MINECRAFT {
        @Override
        public void drawStringWithShadow(String text, float x, float y, int color) {
            mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
        }
        @Override
        public double getFontHeight() {
            return mc.fontRendererObj.FONT_HEIGHT;
        }
        @Override
        public double getStringWidth(String s){
            return mc.fontRendererObj.getStringWidth(s);
        }
    },
    VIETNAM {
        CFontRenderer font = FontRenderer.getFont("vietnam.ttf", 18);

        @Override
        public void drawStringWithShadow(String text, float x, float y, int color) {
            CFontRenderer vietnamFont = FontRenderer.getFont("vietnam.ttf", 18);
            vietnamFont.drawStringWithShadow(text, x, y, color);
        }
        @Override
        public double getFontHeight() {
            return font.getHeight();
        }

        @Override
        public double getStringWidth(String s){
            return font.getStringWidth(s);
        }
    },
    INTER {
        CFontRenderer font = FontRenderer.getFont("Inter.ttf", 18);

        @Override
        public void drawStringWithShadow(String text, float x, float y, int color) {
            font.drawStringWithShadow(text, x, y, color);
        }
        @Override
        public double getFontHeight() {
            return font.getHeight();
        }

        @Override
        public double getStringWidth(String s){
            return font.getStringWidth(s);
        }
    },
    NEVERLOSE {
        CFontRenderer font = FontRenderer.getFont("neverlose.ttf", 19);
        @Override
        public void drawStringWithShadow(String text, float x, float y, int color) {
            font.drawStringWithShadow(text, x, y, color);
        }
        @Override
        public double getFontHeight() {
            return font.getHeight();
        }

        @Override
        public double getStringWidth(String s){
            return font.getStringWidth(s);
        }
    },
    PRODUCT {
        CFontRenderer font = FontRenderer.getFont("productsans.ttf", 18);
        @Override
        public void drawStringWithShadow(String text, float x, float y, int color) {
            font.drawStringWithShadow(text, x, y, color);
        }
        @Override
        public double getFontHeight() {
            return font.getHeight();
        }

        @Override
        public double getStringWidth(String s){
            return font.getStringWidth(s);
        }
    },
    TENACITY {
        CFontRenderer font = FontRenderer.getFont("tenacity.ttf", 18);

        @Override
        public void drawStringWithShadow(String text, float x, float y, int color) {
            font.drawStringWithShadow(text, x, y, color);
        }
        @Override
        public double getFontHeight() {
            return font.getHeight();
        }

        @Override
        public double getStringWidth(String s){
            return font.getStringWidth(s);
        }
    },
    WEBDINGS {
        CFontRenderer font = FontRenderer.getFont("webdings.ttf", 18);

        @Override
        public void drawStringWithShadow(String text, float x, float y, int color) {
            font.drawStringWithShadow(text, x, y, color);
        }
        @Override
        public double getFontHeight() {
            return font.getHeight();
        }

        @Override
        public double getStringWidth(String s){
            return font.getStringWidth(s);
        }
    },
    WINGDINGS {
        CFontRenderer font = FontRenderer.getFont("wingding.ttf", 18);

        @Override
        public void drawStringWithShadow(String text, float x, float y, int color) {
            font.drawStringWithShadow(text, x, y, color);
        }
        @Override
        public double getFontHeight() {
            return font.getHeight();
        }

        @Override
        public double getStringWidth(String s){
            return font.getStringWidth(s);
        }
    };

    public abstract void drawStringWithShadow(String text, float x, float y, int color);

    public abstract double getFontHeight();
    public abstract double getStringWidth(String s);
}
