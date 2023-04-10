package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class EntityFireworkStarterFX_Factory implements IParticleFactory
{

    public EntityFX getEntityFX(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int ... p_178902_15_)
    {
        EntityFireworkSparkFX var16 = new EntityFireworkSparkFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, Minecraft.getMinecraft().effectRenderer);
        var16.setAlphaF(0.99F);
        return var16;
    }
}
