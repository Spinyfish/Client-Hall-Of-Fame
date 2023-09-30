package net.optifine.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.src.Config;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.optifine.reflect.Reflector;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public class ShadersRender {
    private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");

    public static void setFrustrumPosition(final ICamera frustum, final double x, final double y, final double z) {
        frustum.setPosition(x, y, z);
    }

    public static void setupTerrain(final RenderGlobal renderGlobal, final Entity viewEntity, final double partialTicks, final ICamera camera, final int frameCount, final boolean playerSpectator) {
        renderGlobal.setupTerrain(viewEntity, partialTicks, camera, frameCount, playerSpectator);
    }

    public static void beginTerrainSolid() {
        if (Shaders.isRenderingWorld) {
            Shaders.fogEnabled = true;
            Shaders.useProgram(Shaders.ProgramTerrain);
        }
    }

    public static void beginTerrainCutoutMipped() {
        if (Shaders.isRenderingWorld) {
            Shaders.useProgram(Shaders.ProgramTerrain);
        }
    }

    public static void beginTerrainCutout() {
        if (Shaders.isRenderingWorld) {
            Shaders.useProgram(Shaders.ProgramTerrain);
        }
    }

    public static void endTerrain() {
        if (Shaders.isRenderingWorld) {
            Shaders.useProgram(Shaders.ProgramTexturedLit);
        }
    }

    public static void beginTranslucent() {
        if (Shaders.isRenderingWorld) {
            if (Shaders.usedDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33995);
                Shaders.checkGLError("pre copy depth");
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Shaders.renderWidth, Shaders.renderHeight);
                Shaders.checkGLError("copy depth");
                GlStateManager.setActiveTexture(33984);
            }

            Shaders.useProgram(Shaders.ProgramWater);
        }
    }

    public static void endTranslucent() {
        if (Shaders.isRenderingWorld) {
            Shaders.useProgram(Shaders.ProgramTexturedLit);
        }
    }

    public static void renderHand0(final EntityRenderer er, final float par1, final int par2) {
        if (!Shaders.isShadowPass) {
            final boolean flag = Shaders.isItemToRenderMainTranslucent();
            final boolean flag1 = Shaders.isItemToRenderOffTranslucent();

            if (!flag || !flag1) {
                Shaders.readCenterDepth();
                Shaders.beginHand(false);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                Shaders.setSkipRenderHands(flag, flag1);
                er.renderHand(par1, par2, true, false, false);
                Shaders.endHand();
                Shaders.setHandsRendered(!flag, !flag1);
                Shaders.setSkipRenderHands(false, false);
            }
        }
    }

    public static void renderHand1(final EntityRenderer er, final float par1, final int par2) {
        if (!Shaders.isShadowPass && !Shaders.isBothHandsRendered()) {
            Shaders.readCenterDepth();
            GlStateManager.enableBlend();
            Shaders.beginHand(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Shaders.setSkipRenderHands(Shaders.isHandRenderedMain(), Shaders.isHandRenderedOff());
            er.renderHand(par1, par2, true, false, true);
            Shaders.endHand();
            Shaders.setHandsRendered(true, true);
            Shaders.setSkipRenderHands(false, false);
        }
    }

    public static void renderItemFP(final ItemRenderer itemRenderer, final float par1, final boolean renderTranslucent) {
        Shaders.setRenderingFirstPersonHand(true);
        GlStateManager.depthMask(true);

        if (renderTranslucent) {
            GlStateManager.depthFunc(519);
            GL11.glPushMatrix();
            final IntBuffer intbuffer = Shaders.activeDrawBuffers;
            Shaders.setDrawBuffers(Shaders.drawBuffersNone);
            Shaders.renderItemKeepDepthMask = true;
            itemRenderer.renderItemInFirstPerson(par1);
            Shaders.renderItemKeepDepthMask = false;
            Shaders.setDrawBuffers(intbuffer);
            GL11.glPopMatrix();
        }

        GlStateManager.depthFunc(515);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        itemRenderer.renderItemInFirstPerson(par1);
        Shaders.setRenderingFirstPersonHand(false);
    }

    public static void renderFPOverlay(final EntityRenderer er, final float par1, final int par2) {
        if (!Shaders.isShadowPass) {
            Shaders.beginFPOverlay();
            er.renderHand(par1, par2, false, true, false);
            Shaders.endFPOverlay();
        }
    }

    public static void beginBlockDamage() {
        if (Shaders.isRenderingWorld) {
            Shaders.useProgram(Shaders.ProgramDamagedBlock);

            if (Shaders.ProgramDamagedBlock.getId() == Shaders.ProgramTerrain.getId()) {
                Shaders.setDrawBuffers(Shaders.drawBuffersColorAtt0);
                GlStateManager.depthMask(false);
            }
        }
    }

    public static void endBlockDamage() {
        if (Shaders.isRenderingWorld) {
            GlStateManager.depthMask(true);
            Shaders.useProgram(Shaders.ProgramTexturedLit);
        }
    }

    public static void renderShadowMap(final EntityRenderer entityRenderer, final int pass, final float partialTicks, final long finishTimeNano) {
        if (Shaders.usedShadowDepthBuffers > 0 && --Shaders.shadowPassCounter <= 0) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            minecraft.mcProfiler.endStartSection("shadow pass");
            final RenderGlobal renderglobal = minecraft.renderGlobal;
            Shaders.isShadowPass = true;
            Shaders.shadowPassCounter = Shaders.shadowPassInterval;
            Shaders.preShadowPassThirdPersonView = minecraft.gameSettings.thirdPersonView;
            minecraft.gameSettings.thirdPersonView = 1;
            Shaders.checkGLError("pre shadow");
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            minecraft.mcProfiler.endStartSection("shadow clear");
            EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.sfb);
            Shaders.checkGLError("shadow bind sfb");
            minecraft.mcProfiler.endStartSection("shadow camera");
            entityRenderer.setupCameraTransform(partialTicks, 2);
            Shaders.setCameraShadow(partialTicks);
            Shaders.checkGLError("shadow camera");
            Shaders.useProgram(Shaders.ProgramShadow);
            GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
            Shaders.checkGLError("shadow drawbuffers");
            GL11.glReadBuffer(0);
            Shaders.checkGLError("shadow readbuffer");
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, Shaders.sfbDepthTextures.get(0), 0);

            if (Shaders.usedShadowColorBuffers != 0) {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, Shaders.sfbColorTextures.get(0), 0);
            }

            Shaders.checkFramebufferStatus("shadow fb");
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glClear(Shaders.usedShadowColorBuffers != 0 ? GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT : GL11.GL_DEPTH_BUFFER_BIT);
            Shaders.checkGLError("shadow clear");
            minecraft.mcProfiler.endStartSection("shadow frustum");
            final ClippingHelper clippinghelper = ClippingHelperShadow.getInstance();
            minecraft.mcProfiler.endStartSection("shadow culling");
            final Frustum frustum = new Frustum(clippinghelper);
            final Entity entity = minecraft.getRenderViewEntity();
            final double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
            final double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
            final double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
            frustum.setPosition(d0, d1, d2);
            GlStateManager.shadeModel(7425);
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.disableCull();
            minecraft.mcProfiler.endStartSection("shadow prepareterrain");
            minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            minecraft.mcProfiler.endStartSection("shadow setupterrain");
            int i = 0;
            i = entityRenderer.frameCount;
            entityRenderer.frameCount = i + 1;
            renderglobal.setupTerrain(entity, partialTicks, frustum, i, minecraft.thePlayer.isSpectator());
            minecraft.mcProfiler.endStartSection("shadow updatechunks");
            minecraft.mcProfiler.endStartSection("shadow terrain");
            GlStateManager.matrixMode(5888);
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            renderglobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, partialTicks, 2, entity);
            Shaders.checkGLError("shadow terrain solid");
            GlStateManager.enableAlpha();
            renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, partialTicks, 2, entity);
            Shaders.checkGLError("shadow terrain cutoutmipped");
            minecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
            renderglobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, partialTicks, 2, entity);
            Shaders.checkGLError("shadow terrain cutout");
            minecraft.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
            GlStateManager.shadeModel(7424);
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            minecraft.mcProfiler.endStartSection("shadow entities");

            if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
                Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, Integer.valueOf(0));
            }

            renderglobal.renderEntities(entity, frustum, partialTicks);
            Shaders.checkGLError("shadow entities");
            GlStateManager.matrixMode(5888);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.alphaFunc(516, 0.1F);

            if (Shaders.usedShadowDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33989);
                Shaders.checkGLError("pre copy shadow depth");
                GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Shaders.shadowMapWidth, Shaders.shadowMapHeight);
                Shaders.checkGLError("copy shadow depth");
                GlStateManager.setActiveTexture(33984);
            }

            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
            minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            GlStateManager.shadeModel(7425);
            Shaders.checkGLError("shadow pre-translucent");
            GL20.glDrawBuffers(Shaders.sfbDrawBuffers);
            Shaders.checkGLError("shadow drawbuffers pre-translucent");
            Shaders.checkFramebufferStatus("shadow pre-translucent");

            if (Shaders.isRenderShadowTranslucent()) {
                minecraft.mcProfiler.endStartSection("shadow translucent");
                renderglobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, partialTicks, 2, entity);
                Shaders.checkGLError("shadow translucent");
            }

            if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
                RenderHelper.enableStandardItemLighting();
                Reflector.call(Reflector.ForgeHooksClient_setRenderPass, Integer.valueOf(1));
                renderglobal.renderEntities(entity, frustum, partialTicks);
                Reflector.call(Reflector.ForgeHooksClient_setRenderPass, Integer.valueOf(-1));
                RenderHelper.disableStandardItemLighting();
                Shaders.checkGLError("shadow entities 1");
            }

            GlStateManager.shadeModel(7424);
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            GlStateManager.disableBlend();
            GL11.glFlush();
            Shaders.checkGLError("shadow flush");
            Shaders.isShadowPass = false;
            minecraft.gameSettings.thirdPersonView = Shaders.preShadowPassThirdPersonView;
            minecraft.mcProfiler.endStartSection("shadow postprocess");

            if (Shaders.hasGlGenMipmap) {
                if (Shaders.usedShadowDepthBuffers >= 1) {
                    if (Shaders.shadowMipmapEnabled[0]) {
                        GlStateManager.setActiveTexture(33988);
                        GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(0));
                        GL30.glGenerateMipmap(3553);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowFilterNearest[0] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
                    }

                    if (Shaders.usedShadowDepthBuffers >= 2 && Shaders.shadowMipmapEnabled[1]) {
                        GlStateManager.setActiveTexture(33989);
                        GlStateManager.bindTexture(Shaders.sfbDepthTextures.get(1));
                        GL30.glGenerateMipmap(3553);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowFilterNearest[1] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
                    }

                    GlStateManager.setActiveTexture(33984);
                }

                if (Shaders.usedShadowColorBuffers >= 1) {
                    if (Shaders.shadowColorMipmapEnabled[0]) {
                        GlStateManager.setActiveTexture(33997);
                        GlStateManager.bindTexture(Shaders.sfbColorTextures.get(0));
                        GL30.glGenerateMipmap(3553);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowColorFilterNearest[0] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
                    }

                    if (Shaders.usedShadowColorBuffers >= 2 && Shaders.shadowColorMipmapEnabled[1]) {
                        GlStateManager.setActiveTexture(33998);
                        GlStateManager.bindTexture(Shaders.sfbColorTextures.get(1));
                        GL30.glGenerateMipmap(3553);
                        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, Shaders.shadowColorFilterNearest[1] ? GL11.GL_NEAREST_MIPMAP_NEAREST : GL11.GL_LINEAR_MIPMAP_LINEAR);
                    }

                    GlStateManager.setActiveTexture(33984);
                }
            }

            Shaders.checkGLError("shadow postprocess");
            EXTFramebufferObject.glBindFramebufferEXT(36160, Shaders.dfb);
            GL11.glViewport(0, 0, Shaders.renderWidth, Shaders.renderHeight);
            Shaders.activeDrawBuffers = null;
            minecraft.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            Shaders.useProgram(Shaders.ProgramTerrain);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            Shaders.checkGLError("shadow end");
        }
    }

    public static void preRenderChunkLayer(final EnumWorldBlockLayer blockLayerIn) {
        if (Shaders.isRenderBackFace(blockLayerIn)) {
            GlStateManager.disableCull();
        }

        if (OpenGlHelper.useVbo()) {
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL20.glEnableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glEnableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glEnableVertexAttribArray(Shaders.entityAttrib);
        }
    }

    public static void postRenderChunkLayer(final EnumWorldBlockLayer blockLayerIn) {
        if (OpenGlHelper.useVbo()) {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            GL20.glDisableVertexAttribArray(Shaders.midTexCoordAttrib);
            GL20.glDisableVertexAttribArray(Shaders.tangentAttrib);
            GL20.glDisableVertexAttribArray(Shaders.entityAttrib);
        }

        if (Shaders.isRenderBackFace(blockLayerIn)) {
            GlStateManager.enableCull();
        }
    }

    public static void setupArrayPointersVbo() {
        final int i = 14;
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 56, 0L);
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 56, 12L);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 56, 16L);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glTexCoordPointer(2, GL11.GL_SHORT, 56, 24L);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glNormalPointer(GL11.GL_BYTE, 56, 28L);
        GL20.glVertexAttribPointer(Shaders.midTexCoordAttrib, 2, GL11.GL_FLOAT, false, 56, 32L);
        GL20.glVertexAttribPointer(Shaders.tangentAttrib, 4, GL11.GL_SHORT, false, 56, 40L);
        GL20.glVertexAttribPointer(Shaders.entityAttrib, 3, GL11.GL_SHORT, false, 56, 48L);
    }

    public static void beaconBeamBegin() {
        Shaders.useProgram(Shaders.ProgramBeaconBeam);
    }

    public static void beaconBeamStartQuad1() {
    }

    public static void beaconBeamStartQuad2() {
    }

    public static void beaconBeamDraw1() {
    }

    public static void beaconBeamDraw2() {
        GlStateManager.disableBlend();
    }

    public static void renderEnchantedGlintBegin() {
        Shaders.useProgram(Shaders.ProgramArmorGlint);
    }

    public static void renderEnchantedGlintEnd() {
        if (Shaders.isRenderingWorld) {
            if (Shaders.isRenderingFirstPersonHand() && Shaders.isRenderBothHands()) {
                Shaders.useProgram(Shaders.ProgramHand);
            } else {
                Shaders.useProgram(Shaders.ProgramEntities);
            }
        } else {
            Shaders.useProgram(Shaders.ProgramNone);
        }
    }

    public static boolean renderEndPortal(final TileEntityEndPortal te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float offset) {
        if (!Shaders.isShadowPass && Shaders.activeProgram.getId() == 0) {
            return false;
        } else {
            GlStateManager.disableLighting();
            Config.getTextureManager().bindTexture(END_PORTAL_TEXTURE);
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.BLOCK);
            final float f = 0.5F;
            final float f1 = f * 0.15F;
            final float f2 = f * 0.3F;
            final float f3 = f * 0.4F;
            final float f4 = 0.0F;
            final float f5 = 0.2F;
            final float f6 = (float) (System.currentTimeMillis() % 100000L) / 100000.0F;
            final int i = 240;
            worldrenderer.pos(x, y + (double) offset, z + 1.0D).func_181666_a(f1, f2, f3, 1.0F).tex(f4 + f6, f4 + f6).func_181671_a(i, i).endVertex();
            worldrenderer.pos(x + 1.0D, y + (double) offset, z + 1.0D).func_181666_a(f1, f2, f3, 1.0F).tex(f4 + f6, f5 + f6).func_181671_a(i, i).endVertex();
            worldrenderer.pos(x + 1.0D, y + (double) offset, z).func_181666_a(f1, f2, f3, 1.0F).tex(f5 + f6, f5 + f6).func_181671_a(i, i).endVertex();
            worldrenderer.pos(x, y + (double) offset, z).func_181666_a(f1, f2, f3, 1.0F).tex(f5 + f6, f4 + f6).func_181671_a(i, i).endVertex();
            tessellator.draw();
            GlStateManager.enableLighting();
            return true;
        }
    }
}
