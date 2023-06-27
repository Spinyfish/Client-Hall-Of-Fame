package rip.athena.client.cosmetics.cape;

import net.minecraft.util.*;

public class Cape
{
    private final String name;
    private final ResourceLocation location;
    private final ResourceLocation displayTexture;
    
    public Cape(final String name, final String location, final String displayTexture) {
        this.name = name;
        this.location = new ResourceLocation("Athena/cosmetics/capes/" + location + ".png");
        this.displayTexture = new ResourceLocation("Athena/cosmetics/capes/" + displayTexture + ".png");
    }
    
    public String getName() {
        return this.name;
    }
    
    public ResourceLocation getLocation() {
        return this.location;
    }
    
    public ResourceLocation getDisplayTexture() {
        return this.displayTexture;
    }
}
