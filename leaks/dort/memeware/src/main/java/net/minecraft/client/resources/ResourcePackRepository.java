package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
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

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ResourcePackRepository {
    private static final Logger logger = LogManager.getLogger();
    private static final FileFilter resourcePackFilter = new FileFilter() {
        // private static final String __OBFID = "CL_00001088";
        public boolean accept(File p_accept_1_) {
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
    private ListenableFuture listenableFutureDownload;
    private List repositoryEntriesAll = Lists.newArrayList();
    private List repositoryEntries = Lists.newArrayList();
    // private static final String __OBFID = "CL_00001087";

    public ResourcePackRepository(File p_i45101_1_, File p_i45101_2_, IResourcePack p_i45101_3_, IMetadataSerializer p_i45101_4_, GameSettings p_i45101_5_) {
        this.dirResourcepacks = p_i45101_1_;
        this.dirServerResourcepacks = p_i45101_2_;
        this.rprDefaultResourcePack = p_i45101_3_;
        this.rprMetadataSerializer = p_i45101_4_;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();
        Iterator var6 = p_i45101_5_.resourcePacks.iterator();

        while (var6.hasNext()) {
            String var7 = (String) var6.next();
            Iterator var8 = this.repositoryEntriesAll.iterator();

            while (var8.hasNext()) {
                ResourcePackRepository.Entry var9 = (ResourcePackRepository.Entry) var8.next();

                if (var9.getResourcePackName().equals(var7)) {
                    this.repositoryEntries.add(var9);
                    break;
                }
            }
        }
    }

    private void fixDirResourcepacks() {
        if (!this.dirResourcepacks.isDirectory() && (!this.dirResourcepacks.delete() || !this.dirResourcepacks.mkdirs())) {
            logger.debug("Unable to create resourcepack folder: " + this.dirResourcepacks);
        }
    }

    private List getResourcePackFiles() {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
    }

    public void updateRepositoryEntriesAll() {
        ArrayList var1 = Lists.newArrayList();
        Iterator var2 = this.getResourcePackFiles().iterator();

        while (var2.hasNext()) {
            File var3 = (File) var2.next();
            ResourcePackRepository.Entry var4 = new ResourcePackRepository.Entry(var3, null);

            if (!this.repositoryEntriesAll.contains(var4)) {
                try {
                    var4.updateResourcePack();
                    var1.add(var4);
                } catch (Exception var6) {
                    var1.remove(var4);
                }
            } else {
                int var5 = this.repositoryEntriesAll.indexOf(var4);

                if (var5 > -1 && var5 < this.repositoryEntriesAll.size()) {
                    var1.add(this.repositoryEntriesAll.get(var5));
                }
            }
        }

        this.repositoryEntriesAll.removeAll(var1);
        var2 = this.repositoryEntriesAll.iterator();

        while (var2.hasNext()) {
            ResourcePackRepository.Entry var7 = (ResourcePackRepository.Entry) var2.next();
            var7.closeResourcePack();
        }

        this.repositoryEntriesAll = var1;
    }

    public List getRepositoryEntriesAll() {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List getRepositoryEntries() {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public void func_148527_a(List p_148527_1_) {
        this.repositoryEntries.clear();
        this.repositoryEntries.addAll(p_148527_1_);
    }

    public File getDirResourcepacks() {
        return this.dirResourcepacks;
    }

    public ListenableFuture downloadResourcePack(String url, String hash) {
        String packName;

        if (hash.matches("^[a-f0-9]{40}$")) {
            packName = hash;
        } else {
            packName = url.substring(url.lastIndexOf("/") + 1);

            if (packName.contains("?")) {
                packName = packName.substring(0, packName.indexOf("?"));
            }

            if (!packName.endsWith(".zip")) {
                return Futures.immediateFailedFuture(new IllegalArgumentException("Invalid filename; must end in .zip"));
            }

            packName = "legacy_" + packName.replaceAll("\\W", "");
        }

        final File saveFile = new File(this.dirServerResourcepacks, packName);
        this.lock.lock();

        try {
            this.clearResourcePack();

            if (saveFile.exists() && hash.length() == 40) {
                try {
                    String fileHash = Hashing.sha1().hashBytes(Files.toByteArray(saveFile)).toString();

                    if (fileHash.equals(hash)) {
                        ListenableFuture var16 = this.setResourcePackInstance(saveFile);
                        return var16;
                    }

                    logger.warn("File " + saveFile + " had wrong hash (expected " + hash + ", found " + fileHash + "). Deleting it.");
                    FileUtils.deleteQuietly(saveFile);
                } catch (IOException var13) {
                    logger.warn("File " + saveFile + " couldn\'t be hashed. Deleting it.", var13);
                    FileUtils.deleteQuietly(saveFile);
                }
            }

            final GuiScreenWorking workingScreen = new GuiScreenWorking();
            Map sessionInfo = Minecraft.getSessionInfo();
            final Minecraft mc = Minecraft.getMinecraft();
            Futures.getUnchecked(mc.addScheduledTask(new Runnable() {
                public void run() {
                    mc.displayGuiScreen(workingScreen);
                }
            }));
            final SettableFuture var8 = SettableFuture.create();
            this.listenableFutureDownload = HttpUtil.downloadResourcePack(saveFile, url, sessionInfo, 52428800, workingScreen, mc.getProxy());
            Futures.addCallback(this.listenableFutureDownload, new FutureCallback() {
                // private static final String __OBFID = "CL_00002394";
                public void onSuccess(Object p_onSuccess_1_) {
                    ResourcePackRepository.this.setResourcePackInstance(saveFile);
                    var8.set(null);
                }

                public void onFailure(Throwable p_onFailure_1_) {
                    var8.setException(p_onFailure_1_);
                }
            });
            ListenableFuture var9 = this.listenableFutureDownload;
            return var9;
        } finally {
            this.lock.unlock();
        }
    }

    public ListenableFuture setResourcePackInstance(File p_177319_1_) {
        this.resourcePackInstance = new FileResourcePack(p_177319_1_);
        return Minecraft.getMinecraft().scheduleResourceRefresh();
    }

    /**
     * Getter for the IResourcePack instance associated with this ResourcePackRepository
     */
    public IResourcePack getResourcePackInstance() {
        return this.resourcePackInstance;
    }

    public void clearResourcePack() {
        this.lock.lock();

        try {
            if (this.listenableFutureDownload != null) {
                this.listenableFutureDownload.cancel(true);
            }

            this.listenableFutureDownload = null;
            this.resourcePackInstance = null;
        } finally {
            this.lock.unlock();
        }
    }

    public class Entry {
        private final File resourcePackFile;
        private IResourcePack reResourcePack;
        private PackMetadataSection rePackMetadataSection;
        private BufferedImage texturePackIcon;
        private ResourceLocation locationTexturePackIcon;
        // private static final String __OBFID = "CL_00001090";

        private Entry(File p_i1295_2_) {
            this.resourcePackFile = p_i1295_2_;
        }

        public void updateResourcePack() throws IOException {
            this.reResourcePack = (IResourcePack) (this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile));
            this.rePackMetadataSection = (PackMetadataSection) this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

            try {
                this.texturePackIcon = this.reResourcePack.getPackImage();
            } catch (IOException var2) {
                ;
            }

            if (this.texturePackIcon == null) {
                this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
            }

            this.closeResourcePack();
        }

        public void bindTexturePackIcon(TextureManager p_110518_1_) {
            if (this.locationTexturePackIcon == null) {
                this.locationTexturePackIcon = p_110518_1_.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
            }

            p_110518_1_.bindTexture(this.locationTexturePackIcon);
        }

        public void closeResourcePack() {
            if (this.reResourcePack instanceof Closeable) {
                IOUtils.closeQuietly((Closeable) this.reResourcePack);
            }
        }

        public IResourcePack getResourcePack() {
            return this.reResourcePack;
        }

        public String getResourcePackName() {
            return this.reResourcePack.getPackName();
        }

        public String getTexturePackDescription() {
            return this.rePackMetadataSection == null ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing \'pack\' section)" : this.rePackMetadataSection.func_152805_a().getFormattedText();
        }

        public boolean equals(Object p_equals_1_) {
            return this == p_equals_1_ ? true : (p_equals_1_ instanceof ResourcePackRepository.Entry ? this.toString().equals(p_equals_1_.toString()) : false);
        }

        public int hashCode() {
            return this.toString().hashCode();
        }

        public String toString() {
            return String.format("%s:%s:%d", new Object[]{this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", Long.valueOf(this.resourcePackFile.lastModified())});
        }

        Entry(File p_i1296_2_, Object p_i1296_3_) {
            this(p_i1296_2_);
        }
    }
}
