package net.minecraft.client.renderer.entity;

import arithmo.gui.altmanager.Colors;
import arithmo.gui.altmanager.RenderUtil;
import com.google.common.collect.Lists;

;

import java.awt.*;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;

import crispy.Crispy;
import crispy.features.hacks.impl.combat.Aura;
import crispy.features.hacks.impl.movement.Scaffold;
import crispy.features.hacks.impl.render.ESP;
import crispy.features.hacks.impl.render.OutlineESP;
import crispy.util.render.OutlineUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.src.Config;
import net.minecraft.src.Reflector;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public abstract class RendererLivingEntity extends Render
{
    private static final Logger logger = LogManager.getLogger();
    private static final DynamicTexture field_177096_e = new DynamicTexture(16, 16);
    protected ModelBase mainModel;
    protected FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
    protected List layerRenderers = Lists.newArrayList();
    protected boolean renderOutlines = false;
    public static float NAME_TAG_RANGE = 64.0F;
    public static float NAME_TAG_RANGE_SNEAK = 32.0F;

    public RendererLivingEntity(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(renderManagerIn);
        this.mainModel = modelBaseIn;
        this.shadowSize = shadowSizeIn;
    }

    public boolean addLayer(LayerRenderer layer)
    {
        return this.layerRenderers.add(layer);
    }

    protected boolean removeLayer(LayerRenderer layer)
    {
        return this.layerRenderers.remove(layer);
    }

    public ModelBase getMainModel()
    {
        return this.mainModel;
    }

    /**
     * Returns a rotation angle that is inbetween two other rotation angles. par1 and par2 are the angles between which
     * to interpolate, par3 is probably a float between 0.0 and 1.0 that tells us where "between" the two angles we are.
     * Example: par1 = 30, par2 = 50, par3 = 0.5, then return = 40
     */
    protected float interpolateRotation(float par1, float par2, float par3)
    {
        float var4;

        for (var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F)
        {
            ;
        }

        while (var4 >= 180.0F)
        {
            var4 -= 360.0F;
        }

        return par1 + par3 * var4;
    }

    public void transformHeldFull3DItemLayer() {}

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (!Reflector.RenderLivingEvent_Pre_Constructor.exists() || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Pre_Constructor, new Object[] {entity, this, Double.valueOf(x), Double.valueOf(y), Double.valueOf(z)}))
        {
            if (Config.isShaders() && Shaders.useEntityHurtFlash)
            {
                Shaders.setEntityHurtFlash(entity.hurtTime <= 0 && entity.deathTime <= 0 ? 0 : 102, this.getColorMultiplier(entity, entity.getBrightness(partialTicks), partialTicks));
            }

            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
            this.mainModel.isRiding = entity.isRiding();
            this.mainModel.isChild = entity.isChild();

            try
            {



                float var19 = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                float var11 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
                float var12 = var11 - var19;
                float var14;


                if (entity.isRiding() && entity.ridingEntity instanceof EntityLivingBase)
                {




                    EntityLivingBase var20 = (EntityLivingBase)entity.ridingEntity;
                    var19 = this.interpolateRotation(var20.prevRenderYawOffset, var20.renderYawOffset, partialTicks);
                    var12 = var11 - var19;
                    var14 = MathHelper.wrapAngleTo180_float(var12);

                    if (var14 < -85.0F)
                    {
                        var14 = -85.0F;
                    }

                    if (var14 >= 85.0F)
                    {
                        var14 = 85.0F;
                    }

                    var19 = var11 - var14;

                    if (var14 * var14 > 2500.0F)
                    {
                        var19 += var14 * 0.2F;
                    }
                }
                float var201 = 0;



                var201 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                if(entity instanceof EntityPlayerSP && entityYaw != 0) {

                    float YAW = EntityPlayerSP.PostYaw;
                    float PITCH = EntityPlayerSP.PostPitch;
                    float PREVYAW =  EntityPlayerSP.PreYaw;
                    float PREVPITCH =  EntityPlayerSP.PrePitch;
                    if(Aura.aura && Scaffold.scaffoldRot.getYaw() != 999 || Scaffold.scaffoldRot.getPitch() != 999)  {
                        var19 = this.interpolateRotation(PREVYAW, YAW, partialTicks);
                    }
                    if(Crispy.INSTANCE.getHackManager().getModule("Aura", false).isEnabled() && Aura.aura) {
                        var19 = this.interpolateRotation(PREVYAW, YAW, partialTicks);

                    }
                    float renderYaw = this.interpolateRotation(PREVYAW, YAW, partialTicks) - var19;
                    float renderPitch = this.interpolateRotation(PREVPITCH, PITCH, partialTicks);
                    var12 = renderYaw;
                    var201 = renderPitch;
                }
                this.renderLivingAt(entity, x, y, z);
                var14 = this.handleRotationFloat(entity, partialTicks);
                this.rotateCorpse(entity, var14, var19, partialTicks);
                GlStateManager.enableRescaleNormal();
                GlStateManager.scale(-1.0F, -1.0F, 1.0F);
                this.preRenderCallback(entity, partialTicks);
                float var15 = 0.0625F;
                GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
                float var16 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                float var17 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

                if (entity.isChild())
                {
                    var17 *= 3.0F;
                }

                if (var16 > 1.0F)
                {
                    var16 = 1.0F;
                }

                GlStateManager.enableAlpha();
                this.mainModel.setLivingAnimations(entity, var17, var16, partialTicks);
                this.mainModel.setRotationAngles(var17, var16, var14, var12, var201, 0.0625F, entity);
                boolean var18;

                if (this.renderOutlines)
                {
                    var18 = this.setScoreTeamColor(entity);
                    this.renderModel(entity, var17, var16, var14, var12, var201, 0.0625F);

                    if (var18)
                    {
                        this.unsetScoreTeamColor();
                    }
                }
                else
                {
                    var18 = this.setDoRenderBrightness(entity, partialTicks);
                    this.renderModel(entity, var17, var16, var14, var12, var201, 0.0625F);
                    boolean valid = entity instanceof EntityMob || entity instanceof EntityIronGolem ||
                            entity instanceof EntityAnimal || entity instanceof EntityVillager || entity instanceof EntityPlayer;

                    if (var18)
                    {
                        this.unsetBrightness();
                    }



                    if(valid && Crispy.INSTANCE.getHackManager().getModule("ESP", false).isEnabled() && ESP.chams.getObject()) {
                        int renderColor = Colors.getColor(ESP.Red.getObject(), ESP.Green.getObject(), ESP.Blue.getObject());
                        if(entity instanceof EntityPlayer && entity != Minecraft.getMinecraft().thePlayer) {
                            if(ESP.Rainbow.getObject()) {
                                if(entity.hurtTime == 0) {
                                    final Color color = Color.getHSBColor(ESP.h / 255.0f, 0.8f, 1.0f);
                                    final int c = color.getRGB();
                                    renderColor = c;
                                }
                            }

                            Minecraft.getMinecraft().entityRenderer.disableLightmap();
                            RenderUtil.glColor(renderColor);
                            GL11.glPushMatrix();
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                            GL11.glDisable(GL11.GL_TEXTURE_2D);
                            RenderHelper.disableStandardItemLighting();
                            GL11.glEnable(32823);
                            GL11.glPolygonOffset(1.0f, -3900000.0f);
                            this.renderModel(entity, var17, var16, var14, var12, var201, 0.0625F);
                            GL11.glEnable(GL11.GL_TEXTURE_2D);
                            GL11.glEnable(GL11.GL_LIGHTING);
                            GL11.glEnable(GL11.GL_DEPTH_TEST);
                            GlStateManager.enableLighting();
                            GlStateManager.enableBooleanStateAt(0);
                            GlStateManager.enableBooleanStateAt(1);
                            GlStateManager.enableColorMaterial();
                            GL11.glPopMatrix();
                            Minecraft.getMinecraft().entityRenderer.disableLightmap();
                            RenderUtil.glColor(-1);
                        }

                    }
                    if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
                    {

                        this.renderLayers(entity, var17, var16, partialTicks, var14, var12, var201, 0.0625F);
                    }

                    if(valid && Crispy.INSTANCE.getHackManager().getHack(OutlineESP.class).isEnabled()) {
                        this.renderModel(entity, var17, var16, var14, var12, var201, 0.0625F);
                        OutlineUtils.renderOne();
                        this.renderModel(entity, var17, var16, var14, var12, var201, 0.0625F);
                        OutlineUtils.renderTwo();
                        this.renderModel(entity, var17, var16, var14, var12, var201, 0.0625F);
                        OutlineUtils.renderThree();
                        OutlineUtils.renderFour();
                        this.renderModel(entity, var17, var16, var14, var12, var201, 0.0625F);
                        OutlineUtils.renderFive();



                    }
                    GL11.glDisable(32823);
                }



                GlStateManager.disableRescaleNormal();
            }
            catch (Exception var191)
            {
                logger.error("Couldn\'t render entity", var191);
            }

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();

            if (!this.renderOutlines)
            {
                super.doRender(entity, x, y, z, entityYaw, partialTicks);
            }

            if (!Reflector.RenderLivingEvent_Post_Constructor.exists() || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Post_Constructor, new Object[] {entity, this, Double.valueOf(x), Double.valueOf(y), Double.valueOf(z)}))
            {
                ;
            }
        }
    }

    protected boolean setScoreTeamColor(EntityLivingBase entityLivingBaseIn)
    {
        int var2 = 16777215;

        if (entityLivingBaseIn instanceof EntityPlayer)
        {
            ScorePlayerTeam var6 = (ScorePlayerTeam)entityLivingBaseIn.getTeam();

            if (var6 != null)
            {
                String var7 = FontRenderer.getFormatFromString(var6.getColorPrefix());

                if (var7.length() >= 2)
                {
                    var2 = this.getFontRendererFromRenderManager().getColorCode(var7.charAt(1));
                }
            }
        }

        float var61 = (float)(var2 >> 16 & 255) / 255.0F;
        float var71 = (float)(var2 >> 8 & 255) / 255.0F;
        float var5 = (float)(var2 & 255) / 255.0F;
        GlStateManager.disableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(var61, var71, var5, 1.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        return true;
    }

    protected void unsetScoreTeamColor()
    {
        GlStateManager.enableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Renders the model in RenderLiving
     */
    protected void renderModel(EntityLivingBase entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float p_77036_7_)
    {
        boolean var8 = !entitylivingbaseIn.isInvisible();
        boolean var9 = !var8 && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);

        if (var8 || var9)
        {
            if (!this.bindEntityTexture(entitylivingbaseIn))
            {
                return;
            }

            if (var9)
            {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569F);
            }

            this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, p_77036_7_);

            if (var9)
            {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1F);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
    }

    protected boolean setDoRenderBrightness(EntityLivingBase entityLivingBaseIn, float partialTicks)
    {
        return this.setBrightness(entityLivingBaseIn, partialTicks, true);
    }

    protected boolean setBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks, boolean combineTextures)
    {
        float var4 = entitylivingbaseIn.getBrightness(partialTicks);
        int var5 = this.getColorMultiplier(entitylivingbaseIn, var4, partialTicks);
        boolean var6 = (var5 >> 24 & 255) > 0;
        boolean var7 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;

        if (!var6 && !var7)
        {
            return false;
        }
        else if (!var6 && !combineTextures)
        {
            return false;
        }
        else
        {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableTexture2D();
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND2_RGB, GL11.GL_SRC_ALPHA);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
            this.brightnessBuffer.position(0);

            if (var7)
            {
                this.brightnessBuffer.put(1.0F);
                this.brightnessBuffer.put(0.0F);
                this.brightnessBuffer.put(0.0F);
                this.brightnessBuffer.put(0.3F);
            }
            else
            {
                float var8 = (float)(var5 >> 24 & 255) / 255.0F;
                float var9 = (float)(var5 >> 16 & 255) / 255.0F;
                float var10 = (float)(var5 >> 8 & 255) / 255.0F;
                float var11 = (float)(var5 & 255) / 255.0F;
                this.brightnessBuffer.put(var9);
                this.brightnessBuffer.put(var10);
                this.brightnessBuffer.put(var11);
                this.brightnessBuffer.put(1.0F - var8);
            }

            this.brightnessBuffer.flip();
            GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, this.brightnessBuffer);
            GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
            GlStateManager.enableTexture2D();
            GlStateManager.bindTexture(field_177096_e.getGlTextureId());
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_REPLACE);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
            GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            return true;
        }
    }

    protected void unsetBrightness()
    {
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, GL11.GL_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
        GlStateManager.disableTexture2D();
        GlStateManager.bindTexture(0);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, OpenGlHelper.GL_COMBINE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, GL11.GL_TEXTURE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
        GL11.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, GL11.GL_TEXTURE);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(EntityLivingBase entityLivingBaseIn, double x, double y, double z)
    {
        GlStateManager.translate((float)x, (float)y, (float)z);
    }

    protected void rotateCorpse(EntityLivingBase bat, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
        GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);

        if (bat.deathTime > 0)
        {
            float var6 = ((float)bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            var6 = MathHelper.sqrt_float(var6);

            if (var6 > 1.0F)
            {
                var6 = 1.0F;
            }

            GlStateManager.rotate(var6 * this.getDeathMaxRotation(bat), 0.0F, 0.0F, 1.0F);
        }
        else
        {
            String var61 = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getCommandSenderName());

            if (var61 != null && (var61.equals("Dinnerbone") || var61.equals("Grumm")) && (!(bat instanceof EntityPlayer) || ((EntityPlayer)bat).isWearing(EnumPlayerModelParts.CAPE)))
            {
                GlStateManager.translate(0.0F, bat.height + 0.1F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            }
        }
    }

    /**
     * Returns where in the swing animation the living entity is (from 0 to 1).  Args : entity, partialTickTime
     */
    protected float getSwingProgress(EntityLivingBase livingBase, float partialTickTime)
    {
        return livingBase.getSwingProgress(partialTickTime);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(EntityLivingBase livingBase, float partialTicks)
    {
        return (float)livingBase.ticksExisted + partialTicks;
    }

    protected void renderLayers(EntityLivingBase entitylivingbaseIn, float p_177093_2_, float p_177093_3_, float partialTicks, float p_177093_5_, float p_177093_6_, float p_177093_7_, float p_177093_8_)
    {
        Iterator var9 = this.layerRenderers.iterator();

        while (var9.hasNext())
        {
            LayerRenderer var10 = (LayerRenderer)var9.next();
            boolean var11 = this.setBrightness(entitylivingbaseIn, partialTicks, var10.shouldCombineTextures());
            var10.doRenderLayer(entitylivingbaseIn, p_177093_2_, p_177093_3_, partialTicks, p_177093_5_, p_177093_6_, p_177093_7_, p_177093_8_);


            if (var11)
            {
                this.unsetBrightness();
            }
        }
    }

    protected float getDeathMaxRotation(EntityLivingBase entityLivingBaseIn)
    {
        return 90.0F;
    }

    /**
     * Returns an ARGB int color back. Args: entityLiving, lightBrightness, partialTickTime
     */
    protected int getColorMultiplier(EntityLivingBase entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        return 0;
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase entitylivingbaseIn, float partialTickTime) {}

    /**
     * Passes the specialRender and renders it
     */
    public void passSpecialRender(EntityLivingBase entitylivingbaseIn, double x, double y, double z)
    {
        if (!Reflector.RenderLivingEvent_Specials_Pre_Constructor.exists() || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Pre_Constructor, new Object[] {entitylivingbaseIn, this, Double.valueOf(x), Double.valueOf(y), Double.valueOf(z)}))
        {
            if (this.canRenderName(entitylivingbaseIn))
            {
                double var8 = entitylivingbaseIn.getDistanceSqToEntity(this.renderManager.livingPlayer);
                float var10 = entitylivingbaseIn.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;

                if (var8 < (double)(var10 * var10))
                {
                    String var11 = entitylivingbaseIn.getDisplayName().getFormattedText();
                    float var12 = 0.02666667F;
                    GlStateManager.alphaFunc(516, 0.1F);

                    if (entitylivingbaseIn.isSneaking())
                    {
                        FontRenderer var13 = this.getFontRendererFromRenderManager();
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float)x, (float)y + entitylivingbaseIn.height + 0.5F - (entitylivingbaseIn.isChild() ? entitylivingbaseIn.height / 2.0F : 0.0F), (float)z);
                        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                        GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.depthMask(false);
                        GlStateManager.enableBlend();
                        GlStateManager.disableTexture2D();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        Tessellator var14 = Tessellator.getInstance();
                        WorldRenderer var15 = var14.getWorldRenderer();
                        var15.startDrawingQuads();
                        int var16 = var13.getStringWidth(var11) / 2;
                        var15.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
                        var15.addVertex((double)(-var16 - 1), -1.0D, 0.0D);
                        var15.addVertex((double)(-var16 - 1), 8.0D, 0.0D);
                        var15.addVertex((double)(var16 + 1), 8.0D, 0.0D);
                        var15.addVertex((double)(var16 + 1), -1.0D, 0.0D);
                        var14.draw();
                        GlStateManager.enableTexture2D();
                        GlStateManager.depthMask(true);
                        var13.drawString(var11, -var13.getStringWidth(var11) / 2, 0, 553648127);
                        GlStateManager.enableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.popMatrix();
                    }
                    else
                    {
                        this.renderOffsetLivingLabel(entitylivingbaseIn, x, y - (entitylivingbaseIn.isChild() ? (double)(entitylivingbaseIn.height / 2.0F) : 0.0D), z, var11, 0.02666667F, var8);
                    }
                }
            }

            if (!Reflector.RenderLivingEvent_Specials_Post_Constructor.exists() || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Post_Constructor, new Object[] {entitylivingbaseIn, this, Double.valueOf(x), Double.valueOf(y), Double.valueOf(z)}))
            {
                ;
            }
        }
    }

    /**
     * Test if the entity name must be rendered
     */
    protected boolean canRenderName(EntityLivingBase targetEntity)
    {
        EntityPlayerSP var2 = Minecraft.getMinecraft().thePlayer;

        if (targetEntity instanceof EntityPlayer && targetEntity != var2)
        {
            Team var3 = targetEntity.getTeam();
            Team var4 = var2.getTeam();

            if (var3 != null)
            {
                Team.EnumVisible var5 = var3.getNameTagVisibility();

                switch (RendererLivingEntity.SwitchEnumVisible.field_178679_a[var5.ordinal()])
                {
                    case 1:
                        return true;

                    case 2:
                        return false;

                    case 3:
                        return var4 == null || var3.isSameTeam(var4);

                    case 4:
                        return var4 == null || !var3.isSameTeam(var4);

                    default:
                        return true;
                }
            }
        }

        return Minecraft.isGuiEnabled() && targetEntity != this.renderManager.livingPlayer && !targetEntity.isInvisibleToPlayer(var2) && targetEntity.riddenByEntity == null;
    }

    public void setRenderOutlines(boolean renderOutlinesIn)
    {
        this.renderOutlines = renderOutlinesIn;
    }

    protected boolean canRenderName(Entity entity)
    {
        return this.canRenderName((EntityLivingBase)entity);
    }

    public void renderName(Entity entity, double x, double y, double z)
    {
        this.passSpecialRender((EntityLivingBase)entity, x, y, z);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     *
     * @param entityYaw The yaw rotation of the passed entity
     */
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.doRender((EntityLivingBase)entity, x, y, z, entityYaw, partialTicks);
    }

    static
    {
        int[] var0 = field_177096_e.getTextureData();

        for (int var1 = 0; var1 < 256; ++var1)
        {
            var0[var1] = -1;
        }

        field_177096_e.updateDynamicTexture();
    }

    static final class SwitchEnumVisible
    {
        static final int[] field_178679_a = new int[Team.EnumVisible.values().length];

        static
        {
            try
            {
                field_178679_a[Team.EnumVisible.ALWAYS.ordinal()] = 1;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                field_178679_a[Team.EnumVisible.NEVER.ordinal()] = 2;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                field_178679_a[Team.EnumVisible.HIDE_FOR_OTHER_TEAMS.ordinal()] = 3;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                field_178679_a[Team.EnumVisible.HIDE_FOR_OWN_TEAM.ordinal()] = 4;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
