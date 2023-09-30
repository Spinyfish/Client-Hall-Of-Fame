package shadersmod.client;

import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexFormatElement extends VertexFormatElement
{
    int sUsage;

    public SVertexFormatElement(int sUsage, EnumType type, int count)
    {
        super(0, type, EnumUsage.PADDING, count);
        this.sUsage = sUsage;
    }
}
