package com.alan.clients.util.font.impl.rise;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

@Getter
@AllArgsConstructor
public class FontCharacter {

    private final int texture;
    private final float width;
    private final float height;

    public void render(final float x, final float y) {
        GlStateManager.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y);
        GL11.glEnd();
    }
}