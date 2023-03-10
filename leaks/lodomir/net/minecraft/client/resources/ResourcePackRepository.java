/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  com.google.common.util.concurrent.FutureCallback
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.SettableFuture
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.comparator.LastModifiedFileComparator
 *  org.apache.commons.io.filefilter.IOFileFilter
 *  org.apache.commons.io.filefilter.TrueFileFilter
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
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
import net.minecraft.client.resources.FileResourcePack;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ResourcePackRepository {
    private static final Logger logger = LogManager.getLogger();
    private static final FileFilter resourcePackFilter = new FileFilter(){

        @Override
        public boolean accept(File p_accept_1_) {
            boolean flag = p_accept_1_.isFile() && p_accept_1_.getName().endsWith(".zip");
            boolean flag1 = p_accept_1_.isDirectory() && new File(p_accept_1_, "pack.mcmeta").isFile();
            return flag || flag1;
        }
    };
    private final File dirResourcepacks;
    public final IResourcePack rprDefaultResourcePack;
    private final File dirServerResourcepacks;
    public final IMetadataSerializer rprMetadataSerializer;
    private IResourcePack resourcePackInstance;
    private final ReentrantLock lock = new ReentrantLock();
    private ListenableFuture<Object> field_177322_i;
    private List<Entry> repositoryEntriesAll = Lists.newArrayList();
    private List<Entry> repositoryEntries = Lists.newArrayList();

    public ResourcePackRepository(File dirResourcepacksIn, File dirServerResourcepacksIn, IResourcePack rprDefaultResourcePackIn, IMetadataSerializer rprMetadataSerializerIn, GameSettings settings) {
        this.dirResourcepacks = dirResourcepacksIn;
        this.dirServerResourcepacks = dirServerResourcepacksIn;
        this.rprDefaultResourcePack = rprDefaultResourcePackIn;
        this.rprMetadataSerializer = rprMetadataSerializerIn;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();
        Iterator iterator = settings.resourcePacks.iterator();
        block0: while (iterator.hasNext()) {
            String s = (String)iterator.next();
            for (Entry resourcepackrepository$entry : this.repositoryEntriesAll) {
                if (!resourcepackrepository$entry.getResourcePackName().equals(s)) continue;
                if (resourcepackrepository$entry.func_183027_f() == 1 || settings.field_183018_l.contains(resourcepackrepository$entry.getResourcePackName())) {
                    this.repositoryEntries.add(resourcepackrepository$entry);
                    continue block0;
                }
                iterator.remove();
                logger.warn("Removed selected resource pack {} because it's no longer compatible", new Object[]{resourcepackrepository$entry.getResourcePackName()});
            }
        }
    }

    private void fixDirResourcepacks() {
        if (this.dirResourcepacks.exists()) {
            if (!(this.dirResourcepacks.isDirectory() || this.dirResourcepacks.delete() && this.dirResourcepacks.mkdirs())) {
                logger.warn("Unable to recreate resourcepack folder, it exists but is not a directory: " + this.dirResourcepacks);
            }
        } else if (!this.dirResourcepacks.mkdirs()) {
            logger.warn("Unable to create resourcepack folder: " + this.dirResourcepacks);
        }
    }

    private List<File> getResourcePackFiles() {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
    }

    public void updateRepositoryEntriesAll() {
        ArrayList list = Lists.newArrayList();
        for (File file1 : this.getResourcePackFiles()) {
            Entry resourcepackrepository$entry = new Entry(file1);
            if (!this.repositoryEntriesAll.contains(resourcepackrepository$entry)) {
                try {
                    resourcepackrepository$entry.updateResourcePack();
                    list.add(resourcepackrepository$entry);
                }
                catch (Exception var6) {
                    list.remove(resourcepackrepository$entry);
                }
                continue;
            }
            int i = this.repositoryEntriesAll.indexOf(resourcepackrepository$entry);
            if (i <= -1 || i >= this.repositoryEntriesAll.size()) continue;
            list.add(this.repositoryEntriesAll.get(i));
        }
        this.repositoryEntriesAll.removeAll(list);
        for (Entry resourcepackrepository$entry1 : this.repositoryEntriesAll) {
            resourcepackrepository$entry1.closeResourcePack();
        }
        this.repositoryEntriesAll = list;
    }

    public List<Entry> getRepositoryEntriesAll() {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List<Entry> getRepositoryEntries() {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public void setRepositories(List<Entry> p_148527_1_) {
        this.repositoryEntries.clear();
        this.repositoryEntries.addAll(p_148527_1_);
    }

    public File getDirResourcepacks() {
        return this.dirResourcepacks;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ListenableFuture<Object> downloadResourcePack(String url, String hash) {
        String s = hash.matches("^[a-f0-9]{40}$") ? hash : "legacy";
        final File file1 = new File(this.dirServerResourcepacks, s);
        this.lock.lock();
        try {
            ListenableFuture<Object> listenablefuture;
            this.func_148529_f();
            if (file1.exists() && hash.length() == 40) {
                String s1;
                block8: {
                    ListenableFuture<Object> listenablefuture1;
                    s1 = Hashing.sha1().hashBytes(Files.toByteArray((File)file1)).toString();
                    if (!s1.equals(hash)) break block8;
                    ListenableFuture<Object> listenableFuture = listenablefuture1 = this.setResourcePackInstance(file1);
                    return listenableFuture;
                }
                try {
                    logger.warn("File " + file1 + " had wrong hash (expected " + hash + ", found " + s1 + "). Deleting it.");
                    FileUtils.deleteQuietly((File)file1);
                }
                catch (IOException ioexception) {
                    logger.warn("File " + file1 + " couldn't be hashed. Deleting it.", (Throwable)ioexception);
                    FileUtils.deleteQuietly((File)file1);
                }
            }
            this.func_183028_i();
            final GuiScreenWorking guiscreenworking = new GuiScreenWorking();
            Map<String, String> map = Minecraft.getSessionInfo();
            final Minecraft minecraft = Minecraft.getMinecraft();
            Futures.getUnchecked(minecraft.addScheduledTask(new Runnable(){

                @Override
                public void run() {
                    minecraft.displayGuiScreen(guiscreenworking);
                }
            }));
            final SettableFuture settablefuture = SettableFuture.create();
            this.field_177322_i = HttpUtil.downloadResourcePack(file1, url, map, 0x3200000, guiscreenworking, minecraft.getProxy());
            Futures.addCallback(this.field_177322_i, (FutureCallback)new FutureCallback<Object>(){

                public void onSuccess(Object p_onSuccess_1_) {
                    ResourcePackRepository.this.setResourcePackInstance(file1);
                    settablefuture.set(null);
                }

                public void onFailure(Throwable p_onFailure_1_) {
                    settablefuture.setException(p_onFailure_1_);
                }
            });
            ListenableFuture<Object> listenableFuture = listenablefuture = this.field_177322_i;
            return listenableFuture;
        }
        finally {
            this.lock.unlock();
        }
    }

    private void func_183028_i() {
        ArrayList list = Lists.newArrayList((Iterable)FileUtils.listFiles((File)this.dirServerResourcepacks, (IOFileFilter)TrueFileFilter.TRUE, (IOFileFilter)null));
        Collections.sort(list, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        int i = 0;
        for (File file1 : list) {
            if (i++ < 10) continue;
            logger.info("Deleting old server resource pack " + file1.getName());
            FileUtils.deleteQuietly((File)file1);
        }
    }

    public ListenableFuture<Object> setResourcePackInstance(File p_177319_1_) {
        this.resourcePackInstance = new FileResourcePack(p_177319_1_);
        return Minecraft.getMinecraft().scheduleResourcesRefresh();
    }

    public IResourcePack getResourcePackInstance() {
        return this.resourcePackInstance;
    }

    public void func_148529_f() {
        this.lock.lock();
        try {
            if (this.field_177322_i != null) {
                this.field_177322_i.cancel(true);
            }
            this.field_177322_i = null;
            if (this.resourcePackInstance != null) {
                this.resourcePackInstance = null;
                Minecraft.getMinecraft().scheduleResourcesRefresh();
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public class Entry {
        private final File resourcePackFile;
        private IResourcePack reResourcePack;
        private PackMetadataSection rePackMetadataSection;
        private BufferedImage texturePackIcon;
        private ResourceLocation locationTexturePackIcon;

        private Entry(File resourcePackFileIn) {
            this.resourcePackFile = resourcePackFileIn;
        }

        public void updateResourcePack() throws IOException {
            this.reResourcePack = this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile);
            this.rePackMetadataSection = (PackMetadataSection)this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");
            try {
                this.texturePackIcon = this.reResourcePack.getPackImage();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            if (this.texturePackIcon == null) {
                this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
            }
            this.closeResourcePack();
        }

        public void bindTexturePackIcon(TextureManager textureManagerIn) {
            if (this.locationTexturePackIcon == null) {
                this.locationTexturePackIcon = textureManagerIn.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
            }
            textureManagerIn.bindTexture(this.locationTexturePackIcon);
        }

        public void closeResourcePack() {
            if (this.reResourcePack instanceof Closeable) {
                IOUtils.closeQuietly((Closeable)((Closeable)((Object)this.reResourcePack)));
            }
        }

        public IResourcePack getResourcePack() {
            return this.reResourcePack;
        }

        public String getResourcePackName() {
            return this.reResourcePack.getPackName();
        }

        public String getTexturePackDescription() {
            return this.rePackMetadataSection == null ? (Object)((Object)EnumChatFormatting.RED) + "Invalid pack.mcmeta (or missing 'pack' section)" : this.rePackMetadataSection.getPackDescription().getFormattedText();
        }

        public int func_183027_f() {
            return this.rePackMetadataSection.getPackFormat();
        }

        public boolean equals(Object p_equals_1_) {
            return this == p_equals_1_ ? true : (p_equals_1_ instanceof Entry ? this.toString().equals(p_equals_1_.toString()) : false);
        }

        public int hashCode() {
            return this.toString().hashCode();
        }

        public String toString() {
            return String.format("%s:%s:%d", this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", this.resourcePackFile.lastModified());
        }
    }
}

