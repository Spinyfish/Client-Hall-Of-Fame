/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.IResourcePack;

public interface IReloadableResourceManager
extends IResourceManager {
    public void reloadResources(List<IResourcePack> var1);

    public void registerReloadListener(IResourceManagerReloadListener var1);
}

