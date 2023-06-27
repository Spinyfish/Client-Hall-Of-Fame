package net.minecraft.block.material;

public class MaterialPortal extends Material
{
    public MaterialPortal(final MapColor color) {
        super(color);
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }
    
    @Override
    public boolean blocksLight() {
        return false;
    }
    
    @Override
    public boolean blocksMovement() {
        return false;
    }
}
