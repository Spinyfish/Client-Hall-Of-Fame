package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository
{
    private static final Logger logger = LogManager.getLogger();
    private static final FileFilter resourcePackFilter = new FileFilter()
    {
        public boolean accept(File p_accept_1_)
        {
            boolean var2 = p_accept_1_.isFile() && p_accept_1_.getName().endsWith(".zip");
            boolean var3 = p_accept_1_.isDirectory() && (new File(p_accept_1_, "pack.mcmeta")).isFile();
            return var2 || var3;
        }
    };
    private final File dirResourcepacks;
    public final IResourcePack rprDefaultResourcePack;
    private final File dirServerResourcepacks;
    public final IMetadataSerializer rprMetadataSerializer;
    private IResourcePack resourcePackInstance;
    private final ReentrantLock lock = new ReentrantLock();
    private ListenableFuture field_177322_i;
    private List repositoryEntriesAll = Lists.newArrayList();
    private List repositoryEntries = Lists.newArrayList();

    public ResourcePackRepository(File dirResourcepacksIn, File dirServerResourcepacksIn, IResourcePack rprDefaultResourcePackIn, IMetadataSerializer rprMetadataSerializerIn, GameSettings settings)
    {
        this.dirResourcepacks = dirResourcepacksIn;
        this.dirServerResourcepacks = dirServerResourcepacksIn;
        this.rprDefaultResourcePack = rprDefaultResourcePackIn;
        this.rprMetadataSerializer = rprMetadataSerializerIn;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();
        Iterator var6 = settings.resourcePacks.iterator();

        while (var6.hasNext())
        {
            String var7 = (String)var6.next();
            Iterator var8 = this.repositoryEntriesAll.iterator();

            while (var8.hasNext())
            {
                ResourcePackRepository.Entry var9 = (ResourcePackRepository.Entry)var8.next();

                if (var9.getResourcePackName().equals(var7))
                {
                    this.repositoryEntries.add(var9);
                    break;
                }
            }
        }
    }

    private void fixDirResourcepacks()
    {
        if (!this.dirResourcepacks.isDirectory() && (!this.dirResourcepacks.delete() || !this.dirResourcepacks.mkdirs()))
        {
            logger.debug("Unable to create resourcepack folder: " + this.dirResourcepacks);
        }
    }

    private List getResourcePackFiles()
    {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
    }

    public void updateRepositoryEntriesAll()
    {
        ArrayList var1 = Lists.newArrayList();
        Iterator var2 = this.getResourcePackFiles().iterator();

        while (var2.hasNext())
        {
            File var3 = (File)var2.next();
            ResourcePackRepository.Entry var4 = new ResourcePackRepository.Entry(var3, null);

            if (!this.repositoryEntriesAll.contains(var4))
            {
                try
                {
                    var4.updateResourcePack();
                    var1.add(var4);
                }
                catch (Exception var6)
                {
                    var1.remove(var4);
                }
            }
            else
            {
                int var5 = this.repositoryEntriesAll.indexOf(var4);

                if (var5 > -1 && var5 < this.repositoryEntriesAll.size())
                {
                    var1.add(this.repositoryEntriesAll.get(var5));
                }
            }
        }

        this.repositoryEntriesAll.removeAll(var1);
        var2 = this.repositoryEntriesAll.iterator();

        while (var2.hasNext())
        {
            ResourcePackRepository.Entry var7 = (ResourcePackRepository.Entry)var2.next();
            var7.closeResourcePack();
        }

        this.repositoryEntriesAll = var1;
    }

    public List getRepositoryEntriesAll()
    {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List getRepositoryEntries()
    {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public void setRepositories(List p_148527_1_)
    {
        this.repositoryEntries.clear();
        this.repositoryEntries.addAll(p_148527_1_);
    }

    public File getDirResourcepacks()
    {
        return this.dirResourcepacks;
    }

    public ListenableFuture downloadResourcePack(String url, String hash)
    {
        String var3;

        if (hash.matches("^[a-f0-9]{40}$"))
        {
            var3 = hash;
        }
        else
        {
            var3 = url.substring(url.lastIndexOf("/") + 1);

            if (var3.contains("?"))
            {
                var3 = var3.substring(0, var3.indexOf("?"));
            }

            if (!var3.endsWith(".zip"))
            {
                return Futures.immediateFailedFuture(new IllegalArgumentException("Invalid filename; must end in .zip"));
            }

            var3 = "legacy_" + var3.replaceAll("\\W", "");
        }

        final File var4 = new File(this.dirServerResourcepacks, var3);
        this.lock.lock();
        ListenableFuture var9;

        try
        {
            this.func_148529_f();

            if (var4.exists() && hash.length() == 40)
            {
                try
                {
                    String var5 = Hashing.sha1().hashBytes(Files.toByteArray(var4)).toString();

                    if (var5.equals(hash))
                    {
                        ListenableFuture var16 = this.setResourcePackInstance(var4);
                        return var16;
                    }

                    logger.warn("File " + var4 + " had wrong hash (expected " + hash + ", found " + var5 + "). Deleting it.");
                    FileUtils.deleteQuietly(var4);
                }
                catch (IOException var13)
                {
                    logger.warn("File " + var4 + " couldn\'t be hashed. Deleting it.", var13);
                    FileUtils.deleteQuietly(var4);
                }
            }

            final GuiScreenWorking var15 = new GuiScreenWorking();
            Map var6 = Minecraft.getSessionInfo();
            final Minecraft var7 = Minecraft.getMinecraft();
            Futures.getUnchecked(var7.addScheduledTask(new Runnable()
            {
                public void run()
                {
                    var7.displayGuiScreen(var15);
                }
            }));
            final SettableFuture var8 = SettableFuture.create();
            this.field_177322_i = HttpUtil.downloadResourcePack(var4, url, var6, 52428800, var15, var7.getProxy());
            Futures.addCallback(this.field_177322_i, new FutureCallback()
            {
                public void onSuccess(Object p_onSuccess_1_)
                {
                    ResourcePackRepository.this.setResourcePackInstance(var4);
                    var8.set((Object)null);
                }
                public void onFailure(Throwable p_onFailure_1_)
                {
                    var8.setException(p_onFailure_1_);
                }
            });
            var9 = this.field_177322_i;
        }
        finally
        {
            this.lock.unlock();
        }

        return var9;
    }

    public ListenableFuture setResourcePackInstance(File p_177319_1_)
    {
        this.resourcePackInstance = new FileResourcePack(p_177319_1_);
        return Minecraft.getMinecraft().scheduleResourcesRefresh();
    }

    /**
     * Getter for the IResourcePack instance associated with this ResourcePackRepository
     */
    public IResourcePack getResourcePackInstance()
    {
        return this.resourcePackInstance;
    }

    public void func_148529_f()
    {
        this.lock.lock();

        try
        {
            if (this.field_177322_i != null)
            {
                this.field_177322_i.cancel(true);
            }

            this.field_177322_i = null;
            this.resourcePackInstance = null;
        }
        finally
        {
            this.lock.unlock();
        }
    }

    public class Entry
    {
        private final File resourcePackFile;
        private IResourcePack reResourcePack;
        private PackMetadataSection rePackMetadataSection;
        private BufferedImage texturePackIcon;
        private ResourceLocation locationTexturePackIcon;

        private Entry(File resourcePackFileIn)
        {
            this.resourcePackFile = resourcePackFileIn;
        }

        public void updateResourcePack() throws IOException
        {
            this.reResourcePack = (IResourcePack)(this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile));
            this.rePackMetadataSection = (PackMetadataSection)this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

            try
            {
                this.texturePackIcon = this.reResourcePack.getPackImage();
            }
            catch (IOException var2)
            {
                ;
            }

            if (this.texturePackIcon == null)
            {
                this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
            }

            this.closeResourcePack();
        }

        public void bindTexturePackIcon(TextureManager textureManagerIn)
        {
            if (this.locationTexturePackIcon == null)
            {
                this.locationTexturePackIcon = textureManagerIn.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
            }

            textureManagerIn.bindTexture(this.locationTexturePackIcon);
        }

        public void closeResourcePack()
        {
            if (this.reResourcePack instanceof Closeable)
            {
                IOUtils.closeQuietly((Closeable)this.reResourcePack);
            }
        }

        public IResourcePack getResourcePack()
        {
            return this.reResourcePack;
        }

        public String getResourcePackName()
        {
            return this.reResourcePack.getPackName();
        }

        public String getTexturePackDescription()
        {
            return this.rePackMetadataSection == null ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing \'pack\' section)" : this.rePackMetadataSection.getPackDescription().getFormattedText();
        }

        public boolean equals(Object p_equals_1_)
        {
            return this == p_equals_1_ ? true : (p_equals_1_ instanceof ResourcePackRepository.Entry ? this.toString().equals(p_equals_1_.toString()) : false);
        }

        public int hashCode()
        {
            return this.toString().hashCode();
        }

        public String toString()
        {
            return String.format("%s:%s:%d", new Object[] {this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", Long.valueOf(this.resourcePackFile.lastModified())});
        }

        Entry(File p_i1296_2_, Object p_i1296_3_)
        {
            this(p_i1296_2_);
        }
    }
}
