package net.minecraft.client.audio;

import net.minecraft.util.*;

public interface ITickableSound extends ISound, ITickable
{
    boolean isDonePlaying();
}
