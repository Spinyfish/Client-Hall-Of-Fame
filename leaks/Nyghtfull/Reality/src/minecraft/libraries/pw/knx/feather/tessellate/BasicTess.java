package libraries.pw.knx.feather.tessellate;

import org.lwjgl.opengl.GL11;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import java.nio.ByteBuffer;

public class BasicTess implements Tessellation
{
    int index;
    int[] raw;
    ByteBuffer buffer;
    FloatBuffer fBuffer;
    IntBuffer iBuffer;
    private int colors;
    private float texU;
    private float texV;
    private boolean color;
    private boolean texture;
    
    BasicTess(int capacity) {
        super();
        capacity *= 6;
        this.raw = new int[capacity];
        this.buffer = ByteBuffer.allocateDirect(capacity * 4).order(ByteOrder.nativeOrder());
        this.fBuffer = this.buffer.asFloatBuffer();
        this.iBuffer = this.buffer.asIntBuffer();
    }
    
    @Override
    public Tessellation setColor(final int color) {
        this.color = true;
        this.colors = color;
        return this;
    }
    
    @Override
    public Tessellation setTexture(final float u, final float v) {
        this.texture = true;
        this.texU = u;
        this.texV = v;
        return this;
    }
    
    @Override
    public Tessellation addVertex(final float x, final float y, final float z) {
        final int dex = this.index * 6;
        this.raw[dex] = Float.floatToRawIntBits(x);
        this.raw[dex + 1] = Float.floatToRawIntBits(y);
        this.raw[dex + 2] = Float.floatToRawIntBits(z);
        this.raw[dex + 3] = this.colors;
        this.raw[dex + 4] = Float.floatToRawIntBits(this.texU);
        this.raw[dex + 5] = Float.floatToRawIntBits(this.texV);
        ++this.index;
        return this;
    }
    
    @Override
    public Tessellation bind() {
        final int dex = this.index * 6;
        this.iBuffer.put(this.raw, 0, dex);
        this.buffer.position(0);
        this.buffer.limit(dex * 4);
        if (this.color) {
            this.buffer.position(12);
            GL11.glColorPointer(4, true, 24, this.buffer);
        }
        if (this.texture) {
            this.fBuffer.position(4);
            GL11.glTexCoordPointer(2, 24, this.fBuffer);
        }
        this.fBuffer.position(0);
        GL11.glVertexPointer(3, 24, this.fBuffer);
        return this;
    }
    
    @Override
    public Tessellation pass(final int mode) {
        GL11.glDrawArrays(mode, 0, this.index);
        return this;
    }
    
    @Override
    public Tessellation unbind() {
        this.iBuffer.position(0);
        return this;
    }
    
    @Override
    public Tessellation reset() {
        this.iBuffer.clear();
        this.index = 0;
        this.color = false;
        this.texture = false;
        return this;
    }
}
