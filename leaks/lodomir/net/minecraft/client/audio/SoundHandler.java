/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundList;
import net.minecraft.client.audio.SoundListSerializer;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.audio.SoundPoolEntry;
import net.minecraft.client.audio.SoundRegistry;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundHandler
implements IResourceManagerReloadListener,
ITickable {
    private static final Logger logger = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(SoundList.class, (Object)new SoundListSerializer()).create();
    private static final ParameterizedType TYPE = new ParameterizedType(){

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{String.class, SoundList.class};
        }

        @Override
        public Type getRawType() {
            return Map.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    };
    public static final SoundPoolEntry missing_sound = new SoundPoolEntry(new ResourceLocation("meta:missing_sound"), 0.0, 0.0, false);
    private final SoundRegistry sndRegistry = new SoundRegistry();
    private final SoundManager sndManager;
    private final IResourceManager mcResourceManager;

    public SoundHandler(IResourceManager manager, GameSettings gameSettingsIn) {
        this.mcResourceManager = manager;
        this.sndManager = new SoundManager(this, gameSettingsIn);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        this.sndManager.reloadSoundSystem();
        this.sndRegistry.clearMap();
        for (String s : resourceManager.getResourceDomains()) {
            try {
                for (IResource iresource : resourceManager.getAllResources(new ResourceLocation(s, "sounds.json"))) {
                    try {
                        Map<String, SoundList> map = this.getSoundMap(iresource.getInputStream());
                        for (Map.Entry<String, SoundList> entry : map.entrySet()) {
                            this.loadSoundResource(new ResourceLocation(s, entry.getKey()), entry.getValue());
                        }
                    }
                    catch (RuntimeException runtimeexception) {
                        logger.warn("Invalid sounds.json", (Throwable)runtimeexception);
                    }
                }
            }
            catch (IOException iOException) {
            }
        }
    }

    protected Map<String, SoundList> getSoundMap(InputStream stream) {
        Map map;
        try {
            map = (Map)GSON.fromJson((Reader)new InputStreamReader(stream), (Type)TYPE);
        }
        finally {
            IOUtils.closeQuietly((InputStream)stream);
        }
        return map;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private void loadSoundResource(ResourceLocation location, SoundList sounds) {
        SoundEventAccessorComposite soundeventaccessorcomposite;
        boolean flag;
        boolean bl = flag = !this.sndRegistry.containsKey(location);
        if (!flag && !sounds.canReplaceExisting()) {
            soundeventaccessorcomposite = (SoundEventAccessorComposite)this.sndRegistry.getObject(location);
        } else {
            if (!flag) {
                logger.debug("Replaced sound event location {}", new Object[]{location});
            }
            soundeventaccessorcomposite = new SoundEventAccessorComposite(location, 1.0, 1.0, sounds.getSoundCategory());
            this.sndRegistry.registerSound(soundeventaccessorcomposite);
        }
        block10: for (final SoundList.SoundEntry soundlist$soundentry : sounds.getSoundList()) {
            ISoundEventAccessor<SoundPoolEntry> lvt_10_1_;
            String s = soundlist$soundentry.getSoundEntryName();
            ResourceLocation resourcelocation = new ResourceLocation(s);
            final String s1 = s.contains(":") ? resourcelocation.getResourceDomain() : location.getResourceDomain();
            switch (soundlist$soundentry.getSoundEntryType()) {
                case FILE: {
                    ResourceLocation resourcelocation1 = new ResourceLocation(s1, "sounds/" + resourcelocation.getResourcePath() + ".ogg");
                    InputStream inputstream = null;
                    try {
                        inputstream = this.mcResourceManager.getResource(resourcelocation1).getInputStream();
                    }
                    catch (FileNotFoundException var18) {
                        logger.warn("File {} does not exist, cannot add it to event {}", new Object[]{resourcelocation1, location});
                        IOUtils.closeQuietly((InputStream)inputstream);
                        continue block10;
                    }
                    catch (IOException ioexception) {
                        logger.warn("Could not load sound file " + resourcelocation1 + ", cannot add it to event " + location, (Throwable)ioexception);
                        {
                            catch (Throwable throwable) {
                                IOUtils.closeQuietly(inputstream);
                                throw throwable;
                            }
                        }
                        IOUtils.closeQuietly((InputStream)inputstream);
                        continue block10;
                    }
                    IOUtils.closeQuietly((InputStream)inputstream);
                    lvt_10_1_ = new SoundEventAccessor(new SoundPoolEntry(resourcelocation1, soundlist$soundentry.getSoundEntryPitch(), soundlist$soundentry.getSoundEntryVolume(), soundlist$soundentry.isStreaming()), soundlist$soundentry.getSoundEntryWeight());
                    break;
                }
                case SOUND_EVENT: {
                    lvt_10_1_ = new ISoundEventAccessor<SoundPoolEntry>(){
                        final ResourceLocation field_148726_a;
                        {
                            this.field_148726_a = new ResourceLocation(s1, soundlist$soundentry.getSoundEntryName());
                        }

                        @Override
                        public int getWeight() {
                            SoundEventAccessorComposite soundeventaccessorcomposite1 = (SoundEventAccessorComposite)SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return soundeventaccessorcomposite1 == null ? 0 : soundeventaccessorcomposite1.getWeight();
                        }

                        @Override
                        public SoundPoolEntry cloneEntry() {
                            SoundEventAccessorComposite soundeventaccessorcomposite1 = (SoundEventAccessorComposite)SoundHandler.this.sndRegistry.getObject(this.field_148726_a);
                            return soundeventaccessorcomposite1 == null ? missing_sound : soundeventaccessorcomposite1.cloneEntry();
                        }
                    };
                    break;
                }
                default: {
                    throw new IllegalStateException("IN YOU FACE");
                }
            }
            soundeventaccessorcomposite.addSoundToEventPool(lvt_10_1_);
        }
    }

    public SoundEventAccessorComposite getSound(ResourceLocation location) {
        return (SoundEventAccessorComposite)this.sndRegistry.getObject(location);
    }

    public void playSound(ISound sound) {
        this.sndManager.playSound(sound);
    }

    public void playDelayedSound(ISound sound, int delay) {
        this.sndManager.playDelayedSound(sound, delay);
    }

    public void setListener(EntityPlayer player, float p_147691_2_) {
        this.sndManager.setListener(player, p_147691_2_);
    }

    public void pauseSounds() {
        this.sndManager.pauseAllSounds();
    }

    public void stopSounds() {
        this.sndManager.stopAllSounds();
    }

    public void unloadSounds() {
        this.sndManager.unloadSoundSystem();
    }

    @Override
    public void update() {
        this.sndManager.updateAllSounds();
    }

    public void resumeSounds() {
        this.sndManager.resumeAllSounds();
    }

    public void setSoundLevel(SoundCategory category, float volume) {
        if (category == SoundCategory.MASTER && volume <= 0.0f) {
            this.stopSounds();
        }
        this.sndManager.setSoundCategoryVolume(category, volume);
    }

    public void stopSound(ISound p_147683_1_) {
        this.sndManager.stopSound(p_147683_1_);
    }

    public SoundEventAccessorComposite getRandomSoundFromCategories(SoundCategory ... categories) {
        ArrayList list = Lists.newArrayList();
        for (ResourceLocation resourcelocation : this.sndRegistry.getKeys()) {
            SoundEventAccessorComposite soundeventaccessorcomposite = (SoundEventAccessorComposite)this.sndRegistry.getObject(resourcelocation);
            if (!ArrayUtils.contains((Object[])categories, (Object)((Object)soundeventaccessorcomposite.getSoundCategory()))) continue;
            list.add(soundeventaccessorcomposite);
        }
        if (list.isEmpty()) {
            return null;
        }
        return (SoundEventAccessorComposite)list.get(new Random().nextInt(list.size()));
    }

    public boolean isSoundPlaying(ISound sound) {
        return this.sndManager.isSoundPlaying(sound);
    }
}

