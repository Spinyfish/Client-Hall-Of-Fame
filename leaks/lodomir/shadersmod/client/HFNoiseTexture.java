/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.BufferUtils
 *  org.lwjgl.opengl.GL11
 */
package shadersmod.client;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class HFNoiseTexture {
    public int texID = GL11.glGenTextures();
    public int textureUnit = 15;

    public HFNoiseTexture(int width, int height) {
        byte[] abyte = this.genHFNoiseImage(width, height);
        ByteBuffer bytebuffer = BufferUtils.createByteBuffer((int)abyte.length);
        bytebuffer.put(abyte);
        bytebuffer.flip();
        GlStateManager.bindTexture(this.texID);
        GL11.glTexImage2D((int)3553, (int)0, (int)6407, (int)width, (int)height, (int)0, (int)6407, (int)5121, (ByteBuffer)bytebuffer);
        GL11.glTexParameteri((int)3553, (int)10242, (int)10497);
        GL11.glTexParameteri((int)3553, (int)10243, (int)10497);
        GL11.glTexParameteri((int)3553, (int)10240, (int)9729);
        GL11.glTexParameteri((int)3553, (int)10241, (int)9729);
        GlStateManager.bindTexture(0);
    }

    public int getID() {
        return this.texID;
    }

    public void destroy() {
        GlStateManager.deleteTexture(this.texID);
        this.texID = 0;
    }

    private int random(int seed) {
        seed ^= seed << 13;
        seed ^= seed >> 17;
        seed ^= seed << 5;
        return seed;
    }

    private byte random(int x, int y, int z) {
        int i = (this.random(x) + this.random(y * 19)) * this.random(z * 23) - z;
        return (byte)(this.random(i) % 128);
    }

    private byte[] genHFNoiseImage(int width, int height) {
        byte[] abyte = new byte[width * height * 3];
        int i = 0;
        for (int j = 0; j < height; ++j) {
            for (int k = 0; k < width; ++k) {
                for (int l = 1; l < 4; ++l) {
                    abyte[i++] = this.random(k, j, l);
                }
            }
        }
        return abyte;
    }
}

