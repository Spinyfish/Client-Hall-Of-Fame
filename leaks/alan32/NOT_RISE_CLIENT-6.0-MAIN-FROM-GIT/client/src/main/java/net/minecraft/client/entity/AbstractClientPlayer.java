package net.minecraft.client.entity;

import com.alan.clients.Client;
import com.alan.clients.newevent.impl.render.LookEvent;
import com.alan.clients.util.vector.Vector2f;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.optifine.player.CapeUtils;
import net.optifine.player.PlayerConfigurations;
import net.optifine.reflect.Reflector;

public abstract class AbstractClientPlayer extends EntityPlayer {
    private NetworkPlayerInfo playerInfo;
    private ResourceLocation locationOfCape = null;
    private long reloadCapeTimeMs = 0L;
    private boolean elytraOfCape = false;
    private String nameClear = null;
    private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");

    public AbstractClientPlayer(final World worldIn, final GameProfile playerProfile) {
        super(worldIn, playerProfile);
        this.nameClear = playerProfile.getName();

        if (this.nameClear != null && !this.nameClear.isEmpty()) {
            this.nameClear = StringUtils.stripControlCodes(this.nameClear);
        }

        CapeUtils.downloadCape(this);
        PlayerConfigurations.getPlayerConfiguration(this);
    }

    /**
     * Returns true if the player is in spectator mode.
     */
    public boolean isSpectator() {
        final NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
        return networkplayerinfo != null && networkplayerinfo.getGameType() == WorldSettings.GameType.SPECTATOR;
    }

    /**
     * Checks if this instance of AbstractClientPlayer has any associated player data.
     */
    public boolean hasPlayerInfo() {
        return this.getPlayerInfo() != null;
    }

    protected NetworkPlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
        }

        return this.playerInfo;
    }

    /**
     * Returns true if the player has an associated skin.
     */
    public boolean hasSkin() {
        final NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
    }

    /**
     * Returns true if the player instance has an associated skin.
     */
    public ResourceLocation getLocationSkin() {
        final NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkplayerinfo.getLocationSkin();
    }

    public ResourceLocation getLocationCape() {
        if (!Config.isShowCapes()) {
            return null;
        } else {
            if (this.reloadCapeTimeMs != 0L && System.currentTimeMillis() > this.reloadCapeTimeMs) {
                CapeUtils.reloadCape(this);
                this.reloadCapeTimeMs = 0L;
            }

            if (this.locationOfCape != null) {
                return this.locationOfCape;
            } else {
                final NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
                return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
            }
        }
    }

    public static ThreadDownloadImageData getDownloadImageSkin(final ResourceLocation resourceLocationIn, final String username) {
        final TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject itextureobject = texturemanager.getTexture(resourceLocationIn);

        if (itextureobject == null) {
            itextureobject = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)), DefaultPlayerSkin.getDefaultSkin(getOfflineUUID(username)), new ImageBufferDownload());
            texturemanager.loadTexture(resourceLocationIn, itextureobject);
        }

        return (ThreadDownloadImageData) itextureobject;
    }

    /**
     * Returns true if the username has an associated skin.
     *
     * @param username The username of the player being checked.
     */
    public static ResourceLocation getLocationSkin(final String username) {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
    }

    public String getSkinType() {
        final NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
        return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkplayerinfo.getSkinType();
    }

    public float getFovModifier() {
        float f = 1.0F;

        if (this.capabilities.isFlying) {
            f *= 1.1F;
        }

        final IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        double attributeValue = iattributeinstance.getAttributeValue();

        if (isPotionActive(Potion.moveSpeed)) {
            final int modifier = getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
            attributeValue /= 1.0D + (modifier * 0.20000000298023224D);
        }

        f = (float) ((double) f * ((attributeValue / (double) this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

        if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0F;
        }

        if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
            final int i = this.getItemInUseDuration();
            float f1 = (float) i / 20.0F;

            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 = f1 * f1;
            }

            f *= 1.0F - f1 * 0.15F;
        }

        return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, Float.valueOf(f)) : f;
    }

    public String getNameClear() {
        return this.nameClear;
    }

    public ResourceLocation getLocationOfCape() {
        return this.locationOfCape;
    }

    public void setLocationOfCape(final ResourceLocation p_setLocationOfCape_1_) {
        this.locationOfCape = p_setLocationOfCape_1_;
    }

    public boolean hasElytraCape() {
        final ResourceLocation resourcelocation = this.getLocationCape();
        return resourcelocation != null && (resourcelocation != this.locationOfCape || this.elytraOfCape);
    }

    public void setElytraOfCape(final boolean p_setElytraOfCape_1_) {
        this.elytraOfCape = p_setElytraOfCape_1_;
    }

    public boolean isElytraOfCape() {
        return this.elytraOfCape;
    }

    public long getReloadCapeTimeMs() {
        return this.reloadCapeTimeMs;
    }

    public void setReloadCapeTimeMs(final long p_setReloadCapeTimeMs_1_) {
        this.reloadCapeTimeMs = p_setReloadCapeTimeMs_1_;
    }

    /**
     * interpolated look vector
     */
    public Vec3 getLook(final float partialTicks) {
        float yaw = this.rotationYaw;
        float pitch = this.rotationPitch;

        LookEvent lookEvent = new LookEvent(new Vector2f(yaw, pitch));
        Client.INSTANCE.getEventBus().handle(lookEvent);
        yaw = lookEvent.getRotation().x;
        pitch = lookEvent.getRotation().y;

        return this.getVectorForRotation(pitch, yaw);
    }
}
