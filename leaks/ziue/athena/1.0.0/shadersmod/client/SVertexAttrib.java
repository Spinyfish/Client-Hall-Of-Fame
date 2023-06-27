package shadersmod.client;

import net.minecraft.client.renderer.vertex.*;

public class SVertexAttrib
{
    public int index;
    public int count;
    public VertexFormatElement.EnumType type;
    public int offset;
    
    public SVertexAttrib(final int index, final int count, final VertexFormatElement.EnumType type) {
        this.index = index;
        this.count = count;
        this.type = type;
    }
}
