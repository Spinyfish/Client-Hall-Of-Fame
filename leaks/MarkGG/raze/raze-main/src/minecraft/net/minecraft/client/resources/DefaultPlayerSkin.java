package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.util.ResourceLocation;

public class DefaultPlayerSkin
{
    private static final ResourceLocation field_177337_a = new ResourceLocation("textures/entity/steve.png");
    private static final ResourceLocation field_177336_b = new ResourceLocation("textures/entity/alex.png");
    private static final String __OBFID = "CL_00002396";

    public static ResourceLocation getDefaultSkinLegacy()
    {
        return field_177337_a;
    }

    public static ResourceLocation getDefaultSkin(UUID p_177334_0_)
    {
        return isSlimSkin(p_177334_0_) ? field_177336_b : field_177337_a;
    }

    public static String getSkinType(UUID p_177332_0_)
    {
        return isSlimSkin(p_177332_0_) ? "slim" : "default";
    }

    private static boolean isSlimSkin(UUID p_177333_0_)
    {
        return (p_177333_0_.hashCode() & 1) == 1;
    }
}
