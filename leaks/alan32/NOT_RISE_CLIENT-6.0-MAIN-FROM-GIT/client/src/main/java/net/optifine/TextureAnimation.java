package net.optifine;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.TextureUtils;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;
import java.util.Properties;

public class TextureAnimation {
    private String srcTex = null;
    private String dstTex = null;
    ResourceLocation dstTexLoc = null;
    private int dstTextId = -1;
    private int dstX = 0;
    private int dstY = 0;
    private int frameWidth = 0;
    private int frameHeight = 0;
    private TextureAnimationFrame[] frames = null;
    private int currentFrameIndex = 0;
    private boolean interpolate = false;
    private int interpolateSkip = 0;
    private ByteBuffer interpolateData = null;
    byte[] srcData = null;
    private ByteBuffer imageData = null;
    private boolean active = true;
    private boolean valid = true;

    public TextureAnimation(final String texFrom, final byte[] srcData, final String texTo, final ResourceLocation locTexTo, final int dstX, final int dstY, final int frameWidth, final int frameHeight, final Properties props) {
        this.srcTex = texFrom;
        this.dstTex = texTo;
        this.dstTexLoc = locTexTo;
        this.dstX = dstX;
        this.dstY = dstY;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        final int i = frameWidth * frameHeight * 4;

        if (srcData.length % i != 0) {
            Config.warn("Invalid animated texture length: " + srcData.length + ", frameWidth: " + frameWidth + ", frameHeight: " + frameHeight);
        }

        this.srcData = srcData;
        int j = srcData.length / i;

        if (props.get("tile.0") != null) {
            for (int k = 0; props.get("tile." + k) != null; ++k) {
                j = k + 1;
            }
        }

        final String s2 = (String) props.get("duration");
        final int l = Math.max(Config.parseInt(s2, 1), 1);
        this.frames = new TextureAnimationFrame[j];

        for (int i1 = 0; i1 < this.frames.length; ++i1) {
            final String s = (String) props.get("tile." + i1);
            final int j1 = Config.parseInt(s, i1);
            final String s1 = (String) props.get("duration." + i1);
            final int k1 = Math.max(Config.parseInt(s1, l), 1);
            final TextureAnimationFrame textureanimationframe = new TextureAnimationFrame(j1, k1);
            this.frames[i1] = textureanimationframe;
        }

        this.interpolate = Config.parseBoolean(props.getProperty("interpolate"), false);
        this.interpolateSkip = Config.parseInt(props.getProperty("skip"), 0);

        if (this.interpolate) {
            this.interpolateData = GLAllocation.createDirectByteBuffer(i);
        }
    }

    public boolean nextFrame() {
        final TextureAnimationFrame textureanimationframe = this.getCurrentFrame();

        if (textureanimationframe == null) {
            return false;
        } else {
            ++textureanimationframe.counter;

            if (textureanimationframe.counter < textureanimationframe.duration) {
                return this.interpolate;
            } else {
                textureanimationframe.counter = 0;
                ++this.currentFrameIndex;

                if (this.currentFrameIndex >= this.frames.length) {
                    this.currentFrameIndex = 0;
                }

                return true;
            }
        }
    }

    public TextureAnimationFrame getCurrentFrame() {
        return this.getFrame(this.currentFrameIndex);
    }

    public TextureAnimationFrame getFrame(int index) {
        if (this.frames.length <= 0) {
            return null;
        } else {
            if (index < 0 || index >= this.frames.length) {
                index = 0;
            }

            final TextureAnimationFrame textureanimationframe = this.frames[index];
            return textureanimationframe;
        }
    }

    public int getFrameCount() {
        return this.frames.length;
    }

    public void updateTexture() {
        if (this.valid) {
            if (this.dstTextId < 0) {
                final ITextureObject itextureobject = TextureUtils.getTexture(this.dstTexLoc);

                if (itextureobject == null) {
                    this.valid = false;
                    return;
                }

                this.dstTextId = itextureobject.getGlTextureId();
            }

            if (this.imageData == null) {
                this.imageData = GLAllocation.createDirectByteBuffer(this.srcData.length);
                this.imageData.put(this.srcData);
                this.imageData.flip();
                this.srcData = null;
            }

            this.active = !SmartAnimations.isActive() || SmartAnimations.isTextureRendered(this.dstTextId);

            if (this.nextFrame()) {
                if (this.active) {
                    final int j = this.frameWidth * this.frameHeight * 4;
                    final TextureAnimationFrame textureanimationframe = this.getCurrentFrame();

                    if (textureanimationframe != null) {
                        final int i = j * textureanimationframe.index;

                        if (i + j <= this.imageData.limit()) {
                            if (this.interpolate && textureanimationframe.counter > 0) {
                                if (this.interpolateSkip <= 1 || textureanimationframe.counter % this.interpolateSkip == 0) {
                                    final TextureAnimationFrame textureanimationframe1 = this.getFrame(this.currentFrameIndex + 1);
                                    final double d0 = 1.0D * (double) textureanimationframe.counter / (double) textureanimationframe.duration;
                                    this.updateTextureInerpolate(textureanimationframe, textureanimationframe1, d0);
                                }
                            } else {
                                this.imageData.position(i);
                                GlStateManager.bindTexture(this.dstTextId);
                                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.imageData);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateTextureInerpolate(final TextureAnimationFrame frame1, final TextureAnimationFrame frame2, final double dd) {
        final int i = this.frameWidth * this.frameHeight * 4;
        final int j = i * frame1.index;

        if (j + i <= this.imageData.limit()) {
            final int k = i * frame2.index;

            if (k + i <= this.imageData.limit()) {
                this.interpolateData.clear();

                for (int l = 0; l < i; ++l) {
                    final int i1 = this.imageData.get(j + l) & 255;
                    final int j1 = this.imageData.get(k + l) & 255;
                    final int k1 = this.mix(i1, j1, dd);
                    final byte b0 = (byte) k1;
                    this.interpolateData.put(b0);
                }

                this.interpolateData.flip();
                GlStateManager.bindTexture(this.dstTextId);
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, this.dstX, this.dstY, this.frameWidth, this.frameHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, this.interpolateData);
            }
        }
    }

    private int mix(final int col1, final int col2, final double k) {
        return (int) ((double) col1 * (1.0D - k) + (double) col2 * k);
    }

    public String getSrcTex() {
        return this.srcTex;
    }

    public String getDstTex() {
        return this.dstTex;
    }

    public ResourceLocation getDstTexLoc() {
        return this.dstTexLoc;
    }

    public boolean isActive() {
        return this.active;
    }
}
