package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.culling.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.renderer.*;
import optifine.*;
import shadersmod.client.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.gui.*;
import rip.athena.client.socket.*;
import java.util.concurrent.*;

public abstract class Render<T extends Entity>
{
    private static final ResourceLocation shadowTextures;
    protected final RenderManager renderManager;
    protected float shadowSize;
    protected float shadowOpaque;
    private static final String __OBFID = "CL_00000992";
    
    protected Render(final RenderManager renderManager) {
        this.shadowOpaque = 1.0f;
        this.renderManager = renderManager;
    }
    
    public boolean shouldRender(final T livingEntity, final ICamera camera, final double camX, final double camY, final double camZ) {
        AxisAlignedBB axisalignedbb = livingEntity.getEntityBoundingBox();
        if (axisalignedbb.func_181656_b() || axisalignedbb.getAverageEdgeLength() == 0.0) {
            axisalignedbb = new AxisAlignedBB(livingEntity.posX - 2.0, livingEntity.posY - 2.0, livingEntity.posZ - 2.0, livingEntity.posX + 2.0, livingEntity.posY + 2.0, livingEntity.posZ + 2.0);
        }
        return livingEntity.isInRangeToRender3d(camX, camY, camZ) && (livingEntity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisalignedbb));
    }
    
    public void doRender(final T entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (entity.dontRenderNameTag) {
            return;
        }
        this.renderName(entity, x, y, z);
    }
    
    protected void renderName(final T entity, final double x, final double y, final double z) {
        if (this.canRenderName(entity)) {
            this.renderLivingLabel(entity, entity.getDisplayName().getFormattedText(), x, y, z, 64);
        }
    }
    
    protected boolean canRenderName(final T entity) {
        return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
    }
    
    protected void renderOffsetLivingLabel(final T entityIn, final double x, final double y, final double z, final String str, final float p_177069_9_, final double p_177069_10_) {
        this.renderLivingLabel(entityIn, str, x, y, z, 64);
    }
    
    protected abstract ResourceLocation getEntityTexture(final T p0);
    
    protected boolean bindEntityTexture(final T entity) {
        final ResourceLocation resourcelocation = this.getEntityTexture(entity);
        if (resourcelocation == null) {
            return false;
        }
        this.bindTexture(resourcelocation);
        return true;
    }
    
    public void bindTexture(final ResourceLocation location) {
        this.renderManager.renderEngine.bindTexture(location);
    }
    
    private void renderEntityOnFire(final Entity entity, final double x, final double y, final double z, final float partialTicks) {
        GlStateManager.disableLighting();
        final TextureMap texturemap = Minecraft.getMinecraft().getTextureMapBlocks();
        final TextureAtlasSprite textureatlassprite = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_0");
        final TextureAtlasSprite textureatlassprite2 = texturemap.getAtlasSprite("minecraft:blocks/fire_layer_1");
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        final float f = entity.width * 1.4f;
        GlStateManager.scale(f, f, f);
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        float f2 = 0.5f;
        final float f3 = 0.0f;
        float f4 = entity.height / f;
        float f5 = (float)(entity.posY - entity.getEntityBoundingBox().minY);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, -0.3f + (int)f4 * 0.02f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f6 = 0.0f;
        int i = 0;
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        while (f4 > 0.0f) {
            final TextureAtlasSprite textureatlassprite3 = (i % 2 == 0) ? textureatlassprite : textureatlassprite2;
            this.bindTexture(TextureMap.locationBlocksTexture);
            float f7 = textureatlassprite3.getMinU();
            final float f8 = textureatlassprite3.getMinV();
            float f9 = textureatlassprite3.getMaxU();
            final float f10 = textureatlassprite3.getMaxV();
            if (i / 2 % 2 == 0) {
                final float f11 = f9;
                f9 = f7;
                f7 = f11;
            }
            worldrenderer.pos(f2 - f3, 0.0f - f5, f6).tex(f9, f10).endVertex();
            worldrenderer.pos(-f2 - f3, 0.0f - f5, f6).tex(f7, f10).endVertex();
            worldrenderer.pos(-f2 - f3, 1.4f - f5, f6).tex(f7, f8).endVertex();
            worldrenderer.pos(f2 - f3, 1.4f - f5, f6).tex(f9, f8).endVertex();
            f4 -= 0.45f;
            f5 -= 0.45f;
            f2 *= 0.9f;
            f6 += 0.03f;
            ++i;
        }
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }
    
    private void renderShadow(final Entity entityIn, final double x, final double y, final double z, final float shadowAlpha, final float partialTicks) {
        if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            this.renderManager.renderEngine.bindTexture(Render.shadowTextures);
            final World world = this.getWorldFromRenderManager();
            GlStateManager.depthMask(false);
            float f = this.shadowSize;
            if (entityIn instanceof EntityLiving) {
                final EntityLiving entityliving = (EntityLiving)entityIn;
                f *= entityliving.getRenderSizeModifier();
                if (entityliving.isChild()) {
                    f *= 0.5f;
                }
            }
            final double d5 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
            final double d6 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
            final double d7 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
            final int i = MathHelper.floor_double(d5 - f);
            final int j = MathHelper.floor_double(d5 + f);
            final int k = MathHelper.floor_double(d6 - f);
            final int l = MathHelper.floor_double(d6);
            final int i2 = MathHelper.floor_double(d7 - f);
            final int j2 = MathHelper.floor_double(d7 + f);
            final double d8 = x - d5;
            final double d9 = y - d6;
            final double d10 = z - d7;
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            for (final BlockPos blockpos : BlockPos.getAllInBoxMutable(new BlockPos(i, k, i2), new BlockPos(j, l, j2))) {
                final Block block = world.getBlockState(blockpos.down()).getBlock();
                if (block.getRenderType() != -1 && world.getLightFromNeighbors(blockpos) > 3) {
                    this.func_180549_a(block, x, y, z, blockpos, shadowAlpha, f, d8, d9, d10);
                }
            }
            tessellator.draw();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.depthMask(true);
        }
    }
    
    private World getWorldFromRenderManager() {
        return this.renderManager.worldObj;
    }
    
    private void func_180549_a(final Block blockIn, final double p_180549_2_, final double p_180549_4_, final double p_180549_6_, final BlockPos pos, final float p_180549_9_, final float p_180549_10_, final double p_180549_11_, final double p_180549_13_, final double p_180549_15_) {
        if (blockIn.isFullCube()) {
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            double d0 = (p_180549_9_ - (p_180549_4_ - (pos.getY() + p_180549_13_)) / 2.0) * 0.5 * this.getWorldFromRenderManager().getLightBrightness(pos);
            if (d0 >= 0.0) {
                if (d0 > 1.0) {
                    d0 = 1.0;
                }
                final double d2 = pos.getX() + blockIn.getBlockBoundsMinX() + p_180549_11_;
                final double d3 = pos.getX() + blockIn.getBlockBoundsMaxX() + p_180549_11_;
                final double d4 = pos.getY() + blockIn.getBlockBoundsMinY() + p_180549_13_ + 0.015625;
                final double d5 = pos.getZ() + blockIn.getBlockBoundsMinZ() + p_180549_15_;
                final double d6 = pos.getZ() + blockIn.getBlockBoundsMaxZ() + p_180549_15_;
                final float f = (float)((p_180549_2_ - d2) / 2.0 / p_180549_10_ + 0.5);
                final float f2 = (float)((p_180549_2_ - d3) / 2.0 / p_180549_10_ + 0.5);
                final float f3 = (float)((p_180549_6_ - d5) / 2.0 / p_180549_10_ + 0.5);
                final float f4 = (float)((p_180549_6_ - d6) / 2.0 / p_180549_10_ + 0.5);
                worldrenderer.pos(d2, d4, d5).tex(f, f3).color(1.0f, 1.0f, 1.0f, (float)d0).endVertex();
                worldrenderer.pos(d2, d4, d6).tex(f, f4).color(1.0f, 1.0f, 1.0f, (float)d0).endVertex();
                worldrenderer.pos(d3, d4, d6).tex(f2, f4).color(1.0f, 1.0f, 1.0f, (float)d0).endVertex();
                worldrenderer.pos(d3, d4, d5).tex(f2, f3).color(1.0f, 1.0f, 1.0f, (float)d0).endVertex();
            }
        }
    }
    
    public static void renderOffsetAABB(final AxisAlignedBB boundingBox, final double x, final double y, final double z) {
        GlStateManager.disableTexture2D();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldrenderer.setTranslation(x, y, z);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_NORMAL);
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0f, 0.0f, -1.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0f, -1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0f, -1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0f, -1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0f, -1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(-1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(-1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(-1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(-1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(1.0f, 0.0f, 0.0f).endVertex();
        worldrenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(1.0f, 0.0f, 0.0f).endVertex();
        tessellator.draw();
        worldrenderer.setTranslation(0.0, 0.0, 0.0);
        GlStateManager.enableTexture2D();
    }
    
    public void doRenderShadowAndFire(final Entity entityIn, final double x, final double y, final double z, final float yaw, final float partialTicks) {
        if (this.renderManager.options != null) {
            if (this.renderManager.options.field_181151_V && this.shadowSize > 0.0f && !entityIn.isInvisible() && this.renderManager.isRenderShadow()) {
                final double d0 = this.renderManager.getDistanceToCamera(entityIn.posX, entityIn.posY, entityIn.posZ);
                final float f = (float)((1.0 - d0 / 256.0) * this.shadowOpaque);
                if (f > 0.0f) {
                    this.renderShadow(entityIn, x, y, z, f, partialTicks);
                }
            }
            if (entityIn.canRenderOnFire() && (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).isSpectator())) {
                this.renderEntityOnFire(entityIn, x, y, z, partialTicks);
            }
        }
    }
    
    public FontRenderer getFontRendererFromRenderManager() {
        return this.renderManager.getFontRenderer();
    }
    
    protected void renderLivingLabel(final T entityIn, final String str, final double x, final double y, final double z, final int maxDistance) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     1: aload_0         /* this */
        //     2: getfield        net/minecraft/client/renderer/entity/Render.renderManager:Lnet/minecraft/client/renderer/entity/RenderManager;
        //     5: getfield        net/minecraft/client/renderer/entity/RenderManager.livingPlayer:Lnet/minecraft/entity/Entity;
        //     8: invokevirtual   net/minecraft/entity/Entity.getDistanceSqToEntity:(Lnet/minecraft/entity/Entity;)D
        //    11: dstore          d0
        //    13: dload           d0
        //    15: iload           maxDistance
        //    17: iload           maxDistance
        //    19: imul           
        //    20: i2d            
        //    21: dcmpg          
        //    22: ifgt            657
        //    25: aload_0         /* this */
        //    26: invokevirtual   net/minecraft/client/renderer/entity/Render.getFontRendererFromRenderManager:()Lnet/minecraft/client/gui/FontRenderer;
        //    29: astore          fontrenderer
        //    31: ldc             1.6
        //    33: fstore          f
        //    35: ldc             0.016666668
        //    37: fload           f
        //    39: fmul           
        //    40: fstore          f1
        //    42: invokestatic    net/minecraft/client/renderer/GlStateManager.pushMatrix:()V
        //    45: dload_3         /* x */
        //    46: d2f            
        //    47: fconst_0       
        //    48: fadd           
        //    49: dload           y
        //    51: d2f            
        //    52: aload_1         /* entityIn */
        //    53: getfield        net/minecraft/entity/Entity.height:F
        //    56: fadd           
        //    57: ldc             0.5
        //    59: fadd           
        //    60: dload           z
        //    62: d2f            
        //    63: invokestatic    net/minecraft/client/renderer/GlStateManager.translate:(FFF)V
        //    66: fconst_0       
        //    67: fconst_1       
        //    68: fconst_0       
        //    69: invokestatic    org/lwjgl/opengl/GL11.glNormal3f:(FFF)V
        //    72: aload_0         /* this */
        //    73: getfield        net/minecraft/client/renderer/entity/Render.renderManager:Lnet/minecraft/client/renderer/entity/RenderManager;
        //    76: getfield        net/minecraft/client/renderer/entity/RenderManager.playerViewY:F
        //    79: fneg           
        //    80: fconst_0       
        //    81: fconst_1       
        //    82: fconst_0       
        //    83: invokestatic    net/minecraft/client/renderer/GlStateManager.rotate:(FFFF)V
        //    86: aload_0         /* this */
        //    87: getfield        net/minecraft/client/renderer/entity/Render.renderManager:Lnet/minecraft/client/renderer/entity/RenderManager;
        //    90: getfield        net/minecraft/client/renderer/entity/RenderManager.playerViewX:F
        //    93: fconst_1       
        //    94: fconst_0       
        //    95: fconst_0       
        //    96: invokestatic    net/minecraft/client/renderer/GlStateManager.rotate:(FFFF)V
        //    99: fload           f1
        //   101: fneg           
        //   102: fload           f1
        //   104: fneg           
        //   105: fload           f1
        //   107: invokestatic    net/minecraft/client/renderer/GlStateManager.scale:(FFF)V
        //   110: invokestatic    net/minecraft/client/renderer/GlStateManager.disableLighting:()V
        //   113: iconst_0       
        //   114: invokestatic    net/minecraft/client/renderer/GlStateManager.depthMask:(Z)V
        //   117: invokestatic    net/minecraft/client/renderer/GlStateManager.disableDepth:()V
        //   120: invokestatic    net/minecraft/client/renderer/GlStateManager.enableBlend:()V
        //   123: sipush          770
        //   126: sipush          771
        //   129: iconst_1       
        //   130: iconst_0       
        //   131: invokestatic    net/minecraft/client/renderer/GlStateManager.tryBlendFuncSeparate:(IIII)V
        //   134: invokestatic    net/minecraft/client/renderer/Tessellator.getInstance:()Lnet/minecraft/client/renderer/Tessellator;
        //   137: astore          tessellator
        //   139: aload           tessellator
        //   141: invokevirtual   net/minecraft/client/renderer/Tessellator.getWorldRenderer:()Lnet/minecraft/client/renderer/WorldRenderer;
        //   144: astore          worldrenderer
        //   146: iconst_0       
        //   147: istore          b0
        //   149: aload_1         /* entityIn */
        //   150: instanceof      Lnet/minecraft/client/entity/AbstractClientPlayer;
        //   153: ifeq            209
        //   156: aload_1         /* entityIn */
        //   157: checkcast       Lnet/minecraft/client/entity/AbstractClientPlayer;
        //   160: invokevirtual   net/minecraft/client/entity/AbstractClientPlayer.getGameProfile:()Lcom/mojang/authlib/GameProfile;
        //   163: invokevirtual   com/mojang/authlib/GameProfile.getId:()Ljava/util/UUID;
        //   166: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
        //   169: astore          username
        //   171: getstatic       rip/athena/client/modules/impl/other/Settings.socketLogo:Z
        //   174: ifeq            209
        //   177: aload           username
        //   179: invokestatic    rip/athena/client/socket/SocketClient.isUserAsync:(Ljava/lang/String;)Ljava/util/concurrent/CompletableFuture;
        //   182: astore          isUserFuture
        //   184: aload           isUserFuture
        //   186: aload_1         /* entityIn */
        //   187: aload           username
        //   189: invokedynamic   BootstrapMethod #0, apply:(Lnet/minecraft/entity/Entity;Ljava/lang/String;)Ljava/util/function/Function;
        //   194: invokevirtual   java/util/concurrent/CompletableFuture.thenCompose:(Ljava/util/function/Function;)Ljava/util/concurrent/CompletableFuture;
        //   197: aload           fontrenderer
        //   199: aload_1         /* entityIn */
        //   200: invokedynamic   BootstrapMethod #1, accept:(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/entity/Entity;)Ljava/util/function/Consumer;
        //   205: invokevirtual   java/util/concurrent/CompletableFuture.thenAccept:(Ljava/util/function/Consumer;)Ljava/util/concurrent/CompletableFuture;
        //   208: pop            
        //   209: aload_2         /* str */
        //   210: ldc             "deadmau5"
        //   212: invokevirtual   java/lang/String.equals:(Ljava/lang/Object;)Z
        //   215: ifeq            222
        //   218: bipush          -10
        //   220: istore          b0
        //   222: aload_1         /* entityIn */
        //   223: instanceof      Lnet/minecraft/client/entity/AbstractClientPlayer;
        //   226: ifeq            452
        //   229: aload_1         /* entityIn */
        //   230: checkcast       Lnet/minecraft/client/entity/AbstractClientPlayer;
        //   233: invokevirtual   net/minecraft/client/entity/AbstractClientPlayer.getGameProfile:()Lcom/mojang/authlib/GameProfile;
        //   236: invokevirtual   com/mojang/authlib/GameProfile.getId:()Ljava/util/UUID;
        //   239: invokevirtual   java/util/UUID.toString:()Ljava/lang/String;
        //   242: astore          username
        //   244: aload           fontrenderer
        //   246: aload_2         /* str */
        //   247: invokevirtual   net/minecraft/client/gui/FontRenderer.getStringWidth:(Ljava/lang/String;)I
        //   250: iconst_2       
        //   251: idiv           
        //   252: istore          i
        //   254: invokestatic    net/minecraft/client/renderer/GlStateManager.disableTexture2D:()V
        //   257: aload           worldrenderer
        //   259: bipush          7
        //   261: getstatic       net/minecraft/client/renderer/vertex/DefaultVertexFormats.POSITION_COLOR:Lnet/minecraft/client/renderer/vertex/VertexFormat;
        //   264: invokevirtual   net/minecraft/client/renderer/WorldRenderer.begin:(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V
        //   267: aload           worldrenderer
        //   269: iload           i
        //   271: ineg           
        //   272: iconst_1       
        //   273: isub           
        //   274: i2d            
        //   275: iconst_m1      
        //   276: iload           b0
        //   278: iadd           
        //   279: i2d            
        //   280: dconst_0       
        //   281: invokevirtual   net/minecraft/client/renderer/WorldRenderer.pos:(DDD)Lnet/minecraft/client/renderer/WorldRenderer;
        //   284: fconst_0       
        //   285: fconst_0       
        //   286: fconst_0       
        //   287: ldc             0.25
        //   289: invokevirtual   net/minecraft/client/renderer/WorldRenderer.color:(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;
        //   292: invokevirtual   net/minecraft/client/renderer/WorldRenderer.endVertex:()V
        //   295: aload           worldrenderer
        //   297: iload           i
        //   299: ineg           
        //   300: iconst_1       
        //   301: isub           
        //   302: i2d            
        //   303: bipush          8
        //   305: iload           b0
        //   307: iadd           
        //   308: i2d            
        //   309: dconst_0       
        //   310: invokevirtual   net/minecraft/client/renderer/WorldRenderer.pos:(DDD)Lnet/minecraft/client/renderer/WorldRenderer;
        //   313: fconst_0       
        //   314: fconst_0       
        //   315: fconst_0       
        //   316: ldc             0.25
        //   318: invokevirtual   net/minecraft/client/renderer/WorldRenderer.color:(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;
        //   321: invokevirtual   net/minecraft/client/renderer/WorldRenderer.endVertex:()V
        //   324: aload           worldrenderer
        //   326: iload           i
        //   328: iconst_1       
        //   329: iadd           
        //   330: i2d            
        //   331: bipush          8
        //   333: iload           b0
        //   335: iadd           
        //   336: i2d            
        //   337: dconst_0       
        //   338: invokevirtual   net/minecraft/client/renderer/WorldRenderer.pos:(DDD)Lnet/minecraft/client/renderer/WorldRenderer;
        //   341: fconst_0       
        //   342: fconst_0       
        //   343: fconst_0       
        //   344: ldc             0.25
        //   346: invokevirtual   net/minecraft/client/renderer/WorldRenderer.color:(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;
        //   349: invokevirtual   net/minecraft/client/renderer/WorldRenderer.endVertex:()V
        //   352: aload           worldrenderer
        //   354: iload           i
        //   356: iconst_1       
        //   357: iadd           
        //   358: i2d            
        //   359: iconst_m1      
        //   360: iload           b0
        //   362: iadd           
        //   363: i2d            
        //   364: dconst_0       
        //   365: invokevirtual   net/minecraft/client/renderer/WorldRenderer.pos:(DDD)Lnet/minecraft/client/renderer/WorldRenderer;
        //   368: fconst_0       
        //   369: fconst_0       
        //   370: fconst_0       
        //   371: ldc             0.25
        //   373: invokevirtual   net/minecraft/client/renderer/WorldRenderer.color:(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;
        //   376: invokevirtual   net/minecraft/client/renderer/WorldRenderer.endVertex:()V
        //   379: aload           tessellator
        //   381: invokevirtual   net/minecraft/client/renderer/Tessellator.draw:()V
        //   384: invokestatic    net/minecraft/client/renderer/GlStateManager.enableTexture2D:()V
        //   387: aload           fontrenderer
        //   389: aload_2         /* str */
        //   390: aload           fontrenderer
        //   392: aload_2         /* str */
        //   393: invokevirtual   net/minecraft/client/gui/FontRenderer.getStringWidth:(Ljava/lang/String;)I
        //   396: ineg           
        //   397: iconst_2       
        //   398: idiv           
        //   399: iload           b0
        //   401: ldc             553648127
        //   403: invokevirtual   net/minecraft/client/gui/FontRenderer.drawString:(Ljava/lang/String;III)I
        //   406: pop            
        //   407: invokestatic    net/minecraft/client/renderer/GlStateManager.enableDepth:()V
        //   410: iconst_1       
        //   411: invokestatic    net/minecraft/client/renderer/GlStateManager.depthMask:(Z)V
        //   414: aload           fontrenderer
        //   416: aload_2         /* str */
        //   417: aload           fontrenderer
        //   419: aload_2         /* str */
        //   420: invokevirtual   net/minecraft/client/gui/FontRenderer.getStringWidth:(Ljava/lang/String;)I
        //   423: ineg           
        //   424: iconst_2       
        //   425: idiv           
        //   426: iload           b0
        //   428: iconst_m1      
        //   429: invokevirtual   net/minecraft/client/gui/FontRenderer.drawString:(Ljava/lang/String;III)I
        //   432: pop            
        //   433: invokestatic    net/minecraft/client/renderer/GlStateManager.enableLighting:()V
        //   436: invokestatic    net/minecraft/client/renderer/GlStateManager.disableBlend:()V
        //   439: fconst_1       
        //   440: fconst_1       
        //   441: fconst_1       
        //   442: fconst_1       
        //   443: invokestatic    net/minecraft/client/renderer/GlStateManager.color:(FFFF)V
        //   446: invokestatic    net/minecraft/client/renderer/GlStateManager.popMatrix:()V
        //   449: goto            657
        //   452: aload           fontrenderer
        //   454: aload_2         /* str */
        //   455: invokevirtual   net/minecraft/client/gui/FontRenderer.getStringWidth:(Ljava/lang/String;)I
        //   458: iconst_2       
        //   459: idiv           
        //   460: istore          i
        //   462: invokestatic    net/minecraft/client/renderer/GlStateManager.disableTexture2D:()V
        //   465: aload           worldrenderer
        //   467: bipush          7
        //   469: getstatic       net/minecraft/client/renderer/vertex/DefaultVertexFormats.POSITION_COLOR:Lnet/minecraft/client/renderer/vertex/VertexFormat;
        //   472: invokevirtual   net/minecraft/client/renderer/WorldRenderer.begin:(ILnet/minecraft/client/renderer/vertex/VertexFormat;)V
        //   475: aload           worldrenderer
        //   477: iload           i
        //   479: ineg           
        //   480: iconst_1       
        //   481: isub           
        //   482: i2d            
        //   483: iconst_m1      
        //   484: iload           b0
        //   486: iadd           
        //   487: i2d            
        //   488: dconst_0       
        //   489: invokevirtual   net/minecraft/client/renderer/WorldRenderer.pos:(DDD)Lnet/minecraft/client/renderer/WorldRenderer;
        //   492: fconst_0       
        //   493: fconst_0       
        //   494: fconst_0       
        //   495: ldc             0.25
        //   497: invokevirtual   net/minecraft/client/renderer/WorldRenderer.color:(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;
        //   500: invokevirtual   net/minecraft/client/renderer/WorldRenderer.endVertex:()V
        //   503: aload           worldrenderer
        //   505: iload           i
        //   507: ineg           
        //   508: iconst_1       
        //   509: isub           
        //   510: i2d            
        //   511: bipush          8
        //   513: iload           b0
        //   515: iadd           
        //   516: i2d            
        //   517: dconst_0       
        //   518: invokevirtual   net/minecraft/client/renderer/WorldRenderer.pos:(DDD)Lnet/minecraft/client/renderer/WorldRenderer;
        //   521: fconst_0       
        //   522: fconst_0       
        //   523: fconst_0       
        //   524: ldc             0.25
        //   526: invokevirtual   net/minecraft/client/renderer/WorldRenderer.color:(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;
        //   529: invokevirtual   net/minecraft/client/renderer/WorldRenderer.endVertex:()V
        //   532: aload           worldrenderer
        //   534: iload           i
        //   536: iconst_1       
        //   537: iadd           
        //   538: i2d            
        //   539: bipush          8
        //   541: iload           b0
        //   543: iadd           
        //   544: i2d            
        //   545: dconst_0       
        //   546: invokevirtual   net/minecraft/client/renderer/WorldRenderer.pos:(DDD)Lnet/minecraft/client/renderer/WorldRenderer;
        //   549: fconst_0       
        //   550: fconst_0       
        //   551: fconst_0       
        //   552: ldc             0.25
        //   554: invokevirtual   net/minecraft/client/renderer/WorldRenderer.color:(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;
        //   557: invokevirtual   net/minecraft/client/renderer/WorldRenderer.endVertex:()V
        //   560: aload           worldrenderer
        //   562: iload           i
        //   564: iconst_1       
        //   565: iadd           
        //   566: i2d            
        //   567: iconst_m1      
        //   568: iload           b0
        //   570: iadd           
        //   571: i2d            
        //   572: dconst_0       
        //   573: invokevirtual   net/minecraft/client/renderer/WorldRenderer.pos:(DDD)Lnet/minecraft/client/renderer/WorldRenderer;
        //   576: fconst_0       
        //   577: fconst_0       
        //   578: fconst_0       
        //   579: ldc             0.25
        //   581: invokevirtual   net/minecraft/client/renderer/WorldRenderer.color:(FFFF)Lnet/minecraft/client/renderer/WorldRenderer;
        //   584: invokevirtual   net/minecraft/client/renderer/WorldRenderer.endVertex:()V
        //   587: aload           tessellator
        //   589: invokevirtual   net/minecraft/client/renderer/Tessellator.draw:()V
        //   592: invokestatic    net/minecraft/client/renderer/GlStateManager.enableTexture2D:()V
        //   595: aload           fontrenderer
        //   597: aload_2         /* str */
        //   598: aload           fontrenderer
        //   600: aload_2         /* str */
        //   601: invokevirtual   net/minecraft/client/gui/FontRenderer.getStringWidth:(Ljava/lang/String;)I
        //   604: ineg           
        //   605: iconst_2       
        //   606: idiv           
        //   607: iload           b0
        //   609: ldc             553648127
        //   611: invokevirtual   net/minecraft/client/gui/FontRenderer.drawString:(Ljava/lang/String;III)I
        //   614: pop            
        //   615: invokestatic    net/minecraft/client/renderer/GlStateManager.enableDepth:()V
        //   618: iconst_1       
        //   619: invokestatic    net/minecraft/client/renderer/GlStateManager.depthMask:(Z)V
        //   622: aload           fontrenderer
        //   624: aload_2         /* str */
        //   625: aload           fontrenderer
        //   627: aload_2         /* str */
        //   628: invokevirtual   net/minecraft/client/gui/FontRenderer.getStringWidth:(Ljava/lang/String;)I
        //   631: ineg           
        //   632: iconst_2       
        //   633: idiv           
        //   634: iload           b0
        //   636: iconst_m1      
        //   637: invokevirtual   net/minecraft/client/gui/FontRenderer.drawString:(Ljava/lang/String;III)I
        //   640: pop            
        //   641: invokestatic    net/minecraft/client/renderer/GlStateManager.enableLighting:()V
        //   644: invokestatic    net/minecraft/client/renderer/GlStateManager.disableBlend:()V
        //   647: fconst_1       
        //   648: fconst_1       
        //   649: fconst_1       
        //   650: fconst_1       
        //   651: invokestatic    net/minecraft/client/renderer/GlStateManager.color:(FFFF)V
        //   654: invokestatic    net/minecraft/client/renderer/GlStateManager.popMatrix:()V
        //   657: return         
        //    Signature:
        //  (TT;Ljava/lang/String;DDDI)V
        //    StackMapTable: 00 04 FF 00 D1 00 0E 07 01 1D 07 01 1E 07 01 5A 03 03 03 01 03 07 01 5B 02 02 07 01 21 07 01 22 01 00 00 0C FB 00 E5 FF 00 CC 00 08 07 01 1D 07 01 1E 07 01 5A 03 03 03 01 03 00 00
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at us.deathmarine.luyten.FileSaver.doSaveJarDecompiled(FileSaver.java:192)
        //     at us.deathmarine.luyten.FileSaver.access$300(FileSaver.java:45)
        //     at us.deathmarine.luyten.FileSaver$4.run(FileSaver.java:112)
        //     at java.lang.Thread.run(Unknown Source)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public RenderManager getRenderManager() {
        return this.renderManager;
    }
    
    static {
        shadowTextures = new ResourceLocation("textures/misc/shadow.png");
    }
}
