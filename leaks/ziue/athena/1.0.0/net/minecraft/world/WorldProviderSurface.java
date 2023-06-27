package net.minecraft.world;

public class WorldProviderSurface extends WorldProvider
{
    @Override
    public String getDimensionName() {
        return "Overworld";
    }
    
    @Override
    public String getInternalNameSuffix() {
        return "";
    }
}
