package rip.athena.client.utils.font.impl.athena;

import net.minecraft.client.renderer.*;
import org.lwjgl.opengl.*;

public class FontCharacter
{
    private final int texture;
    private final float width;
    private final float height;
    
    public void render(final float x, final float y) {
        GlStateManager.bindTexture(this.texture);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(x, y + this.height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(x + this.width, y + this.height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(x + this.width, y);
        GL11.glEnd();
    }
    
    public int getTexture() {
        return this.texture;
    }
    
    public float getWidth() {
        return this.width;
    }
    
    public float getHeight() {
        return this.height;
    }
    
    public FontCharacter(final int texture, final float width, final float height) {
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
}
