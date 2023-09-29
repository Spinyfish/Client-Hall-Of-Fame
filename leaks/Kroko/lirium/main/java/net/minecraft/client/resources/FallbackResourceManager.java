package net.minecraft.client.resources;

import com.google.common.collect.Lists;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FallbackResourceManager implements IResourceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final List<IResourcePack> resourcePacks = Lists.<IResourcePack>newArrayList();
    private final MetadataSerializer frmMetadataSerializer;

    public FallbackResourceManager(MetadataSerializer frmMetadataSerializerIn) {
        this.frmMetadataSerializer = frmMetadataSerializerIn;
    }

    public void addResourcePack(IResourcePack resourcePack) {
        this.resourcePacks.add(resourcePack);
    }

    public Set<String> getResourceDomains() {
        return Collections.<String>emptySet();
    }

    public IResource getResource(ResourceLocation location) throws IOException {
        this.checkResourcePath(location);
        IResourcePack iresourcepack = null;
        ResourceLocation resourcelocation = getLocationMcmeta(location);

        for (int i = this.resourcePacks.size() - 1; i >= 0; --i) {
            final IResourcePack resource = this.resourcePacks.get(i);

            if (location.getResourcePath().contains("gui/"))
                if (resource instanceof FileResourcePack) {
                    final FileResourcePack resourcePack = (FileResourcePack) resource;
                    if (resourcePack.legacy) continue;
                }
            if (iresourcepack == null && resource.resourceExists(resourcelocation)) {
                iresourcepack = resource;
            }

            if (resource.resourceExists(location)) {
                InputStream inputstream = null;

                if (iresourcepack != null) {
                    inputstream = this.getInputStream(resourcelocation, iresourcepack);
                }

                return new SimpleResource(resource.getPackName(), location, this.getInputStream(location, resource), inputstream, this.frmMetadataSerializer);
            }
        }

        throw new FileNotFoundException(location.toString());
    }

    protected InputStream getInputStream(ResourceLocation location, IResourcePack resourcePack) throws IOException {
        InputStream inputstream = resourcePack.getInputStream(location);
        return (InputStream) (LOGGER.isDebugEnabled() ? new FallbackResourceManager.InputStreamLeakedResourceLogger(inputstream, location, resourcePack.getPackName()) : inputstream);
    }

    private void checkResourcePath(ResourceLocation p_188552_1_) throws IOException {
        if (p_188552_1_.getResourcePath().contains("..")) {
            throw new IOException("Invalid relative path to resource: " + p_188552_1_);
        }
    }

    public List<IResource> getAllResources(ResourceLocation location) throws IOException {
        this.checkResourcePath(location);
        List<IResource> list = Lists.<IResource>newArrayList();
        ResourceLocation resourcelocation = getLocationMcmeta(location);

        for (IResourcePack iresourcepack : this.resourcePacks) {
            if (iresourcepack.resourceExists(location)) {
                InputStream inputstream = iresourcepack.resourceExists(resourcelocation) ? this.getInputStream(resourcelocation, iresourcepack) : null;
                list.add(new SimpleResource(iresourcepack.getPackName(), location, this.getInputStream(location, iresourcepack), inputstream, this.frmMetadataSerializer));
            }
        }

        if (list.isEmpty()) {
            throw new FileNotFoundException(location.toString());
        } else {
            return list;
        }
    }

    static ResourceLocation getLocationMcmeta(ResourceLocation location) {
        return new ResourceLocation(location.getResourceDomain(), location.getResourcePath() + ".mcmeta");
    }

    static class InputStreamLeakedResourceLogger extends InputStream {
        private final InputStream inputStream;
        private final String message;
        private boolean isClosed;

        public InputStreamLeakedResourceLogger(InputStream p_i46093_1_, ResourceLocation location, String resourcePack) {
            this.inputStream = p_i46093_1_;
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();

            this.message = "Leaked resource: '" + location + "' loaded from pack: '" + resourcePack + "'\n" + bytearrayoutputstream;
        }

        public void close() throws IOException {
            this.inputStream.close();
            this.isClosed = true;
        }

        protected void finalize() throws Throwable {
            if (!this.isClosed) {
                FallbackResourceManager.LOGGER.warn(this.message);
            }

            super.finalize();
        }

        public int read() throws IOException {
            return this.inputStream.read();
        }
    }
}
