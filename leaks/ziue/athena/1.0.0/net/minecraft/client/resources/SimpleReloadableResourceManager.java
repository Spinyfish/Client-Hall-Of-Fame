package net.minecraft.client.resources;

import net.minecraft.client.resources.data.*;
import java.util.*;
import net.minecraft.util.*;
import java.io.*;
import com.google.common.base.*;
import org.apache.logging.log4j.*;
import com.google.common.collect.*;

public class SimpleReloadableResourceManager implements IReloadableResourceManager
{
    private static final Logger logger;
    private static final Joiner joinerResourcePacks;
    public static final Map<String, FallbackResourceManager> domainResourceManagers;
    private final List<IResourceManagerReloadListener> reloadListeners;
    private final Set<String> setResourceDomains;
    private final IMetadataSerializer rmMetadataSerializer;
    
    public SimpleReloadableResourceManager(final IMetadataSerializer rmMetadataSerializerIn) {
        this.reloadListeners = (List<IResourceManagerReloadListener>)Lists.newArrayList();
        this.setResourceDomains = (Set<String>)Sets.newLinkedHashSet();
        this.rmMetadataSerializer = rmMetadataSerializerIn;
    }
    
    public void reloadResourcePack(final IResourcePack resourcePack) {
        for (final String s : resourcePack.getResourceDomains()) {
            this.setResourceDomains.add(s);
            FallbackResourceManager fallbackresourcemanager = SimpleReloadableResourceManager.domainResourceManagers.get(s);
            if (fallbackresourcemanager == null) {
                fallbackresourcemanager = new FallbackResourceManager(this.rmMetadataSerializer);
                SimpleReloadableResourceManager.domainResourceManagers.put(s, fallbackresourcemanager);
            }
            fallbackresourcemanager.addResourcePack(resourcePack);
        }
    }
    
    @Override
    public Set<String> getResourceDomains() {
        return this.setResourceDomains;
    }
    
    @Override
    public IResource getResource(final ResourceLocation location) throws IOException {
        final IResourceManager iresourcemanager = SimpleReloadableResourceManager.domainResourceManagers.get(location.getResourceDomain());
        if (iresourcemanager != null) {
            return iresourcemanager.getResource(location);
        }
        throw new FileNotFoundException(location.toString());
    }
    
    @Override
    public List<IResource> getAllResources(final ResourceLocation location) throws IOException {
        final IResourceManager iresourcemanager = SimpleReloadableResourceManager.domainResourceManagers.get(location.getResourceDomain());
        if (iresourcemanager != null) {
            return iresourcemanager.getAllResources(location);
        }
        throw new FileNotFoundException(location.toString());
    }
    
    private void clearResources() {
        SimpleReloadableResourceManager.domainResourceManagers.clear();
        this.setResourceDomains.clear();
    }
    
    @Override
    public void reloadResources(final List<IResourcePack> p_110541_1_) {
        this.clearResources();
        SimpleReloadableResourceManager.logger.info("Reloading ResourceManager: " + SimpleReloadableResourceManager.joinerResourcePacks.join(Iterables.transform((Iterable)p_110541_1_, (Function)new Function<IResourcePack, String>() {
            public String apply(final IResourcePack p_apply_1_) {
                return p_apply_1_.getPackName();
            }
        })));
        for (final IResourcePack iresourcepack : p_110541_1_) {
            this.reloadResourcePack(iresourcepack);
        }
        this.notifyReloadListeners();
    }
    
    @Override
    public void registerReloadListener(final IResourceManagerReloadListener reloadListener) {
        this.reloadListeners.add(reloadListener);
        reloadListener.onResourceManagerReload(this);
    }
    
    private void notifyReloadListeners() {
        for (final IResourceManagerReloadListener iresourcemanagerreloadlistener : this.reloadListeners) {
            iresourcemanagerreloadlistener.onResourceManagerReload(this);
        }
    }
    
    static {
        logger = LogManager.getLogger();
        joinerResourcePacks = Joiner.on(", ");
        domainResourceManagers = Maps.newHashMap();
    }
}
