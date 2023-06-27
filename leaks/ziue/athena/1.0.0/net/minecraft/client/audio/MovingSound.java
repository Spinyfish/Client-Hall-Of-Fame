package net.minecraft.client.audio;

import net.minecraft.util.*;

public abstract class MovingSound extends PositionedSound implements ITickableSound
{
    protected boolean donePlaying;
    
    protected MovingSound(final ResourceLocation location) {
        super(location);
        this.donePlaying = false;
    }
    
    @Override
    public boolean isDonePlaying() {
        return this.donePlaying;
    }
}
