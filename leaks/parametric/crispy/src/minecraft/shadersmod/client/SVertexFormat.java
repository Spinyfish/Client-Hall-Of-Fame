package shadersmod.client;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class SVertexFormat
{
    public static final int vertexSizeBlock = 14;
    public static final int offsetMidTexCoord = 8;
    public static final int offsetTangent = 10;
    public static final int offsetEntity = 12;
    public static final VertexFormat defVertexFormatTextured = makeDefVertexFormatTextured();

    public static VertexFormat makeDefVertexFormatBlock()
    {
        VertexFormat vf = new VertexFormat();
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.POSITION, 3));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUseage.COLOR, 4));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.UV, 2));
        vf.setElement(new VertexFormatElement(1, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.UV, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.NORMAL, 3));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.PADDING, 1));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.PADDING, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 4));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 4));
        return vf;
    }

    public static VertexFormat makeDefVertexFormatItem()
    {
        VertexFormat vf = new VertexFormat();
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.POSITION, 3));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUseage.COLOR, 4));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.UV, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.NORMAL, 3));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.PADDING, 1));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.PADDING, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 4));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 4));
        return vf;
    }

    public static VertexFormat makeDefVertexFormatTextured()
    {
        VertexFormat vf = new VertexFormat();
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.POSITION, 3));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUseage.PADDING, 4));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.UV, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.NORMAL, 3));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.PADDING, 1));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.PADDING, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 4));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 4));
        return vf;
    }

    public static void setDefBakedFormat(VertexFormat vf)
    {
        vf.clear();
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.POSITION, 3));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUseage.COLOR, 4));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.UV, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.NORMAL, 3));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.PADDING, 1));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.PADDING, 2));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 4));
        vf.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.PADDING, 4));
    }
}
