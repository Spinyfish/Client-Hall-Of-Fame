package net.minecraft.client.renderer.vertex;

import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import shadersmod.client.SVertexFormat;

public class DefaultVertexFormats
{
    public static VertexFormat BLOCK = new VertexFormat();
    public static VertexFormat ITEM = new VertexFormat();
    private static final VertexFormat BLOCK_VANILLA = BLOCK;
    private static final VertexFormat ITEM_VANILLA = ITEM;

    public static void updateVertexFormats()
    {
        if (Config.isShaders())
        {
            BLOCK = SVertexFormat.makeDefVertexFormatBlock();
            ITEM = SVertexFormat.makeDefVertexFormatItem();
        }
        else
        {
            BLOCK = BLOCK_VANILLA;
            ITEM = ITEM_VANILLA;
        }

        if (Reflector.Attributes_DEFAULT_BAKED_FORMAT.exists())
        {
            VertexFormat vfSrc = ITEM;
            VertexFormat vfDst = (VertexFormat)Reflector.getFieldValue(Reflector.Attributes_DEFAULT_BAKED_FORMAT);
            vfDst.clear();

            for (int i = 0; i < vfSrc.getElementCount(); ++i)
            {
                vfDst.setElement(vfSrc.getElement(i));
            }
        }
    }

    static
    {
        BLOCK.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.POSITION, 3));
        BLOCK.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUseage.COLOR, 4));
        BLOCK.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.UV, 2));
        BLOCK.setElement(new VertexFormatElement(1, VertexFormatElement.EnumType.SHORT, VertexFormatElement.EnumUseage.UV, 2));
        ITEM.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.POSITION, 3));
        ITEM.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.UBYTE, VertexFormatElement.EnumUseage.COLOR, 4));
        ITEM.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUseage.UV, 2));
        ITEM.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.NORMAL, 3));
        ITEM.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.BYTE, VertexFormatElement.EnumUseage.PADDING, 1));
    }
}
