package net.minecraft.client.shader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;

public class Shader
{
    private final ShaderManager manager;
    public final Framebuffer framebufferIn;
    public final Framebuffer framebufferOut;
    private final List<Object> listAuxFramebuffers = Lists.<Object>newArrayList();
    private final List<String> listAuxNames = Lists.<String>newArrayList();
    private final List<Integer> listAuxWidths = Lists.<Integer>newArrayList();
    private final List<Integer> listAuxHeights = Lists.<Integer>newArrayList();
    private Matrix4f projectionMatrix;

    public Shader(IResourceManager p_i45089_1_, String p_i45089_2_, Framebuffer p_i45089_3_, Framebuffer p_i45089_4_) throws JsonException, IOException
    {
        this.manager = new ShaderManager(p_i45089_1_, p_i45089_2_);
        this.framebufferIn = p_i45089_3_;
        this.framebufferOut = p_i45089_4_;
    }

    public void deleteShader()
    {
        this.manager.deleteShader();
    }

    public void addAuxFramebuffer(String p_148041_1_, Object p_148041_2_, int p_148041_3_, int p_148041_4_)
    {
        this.listAuxNames.add(this.listAuxNames.size(), p_148041_1_);
        this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), p_148041_2_);
        this.listAuxWidths.add(this.listAuxWidths.size(), Integer.valueOf(p_148041_3_));
        this.listAuxHeights.add(this.listAuxHeights.size(), Integer.valueOf(p_148041_4_));
    }

    private void preLoadShader()
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.disableFog();
        GlStateManager.disableLighting();
        GlStateManager.disableColorMaterial();
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(0);
    }

    public void setProjectionMatrix(Matrix4f p_148045_1_)
    {
        this.projectionMatrix = p_148045_1_;
    } 
    public void loadShaderTransparent(float p_148042_1_)
    {
        //  this.preLoadShader();
          // GlStateManager.disableAlpha();
      	GL11.glEnable(GL11.GL_BLEND);
         // GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
         // GlStateManager.blend
          this.framebufferIn.unbindFramebuffer();
          float var2 = (float)this.framebufferOut.framebufferTextureWidth;
          float var3 = (float)this.framebufferOut.framebufferTextureHeight;
          GlStateManager.viewport(0, 0, (int)var2, (int)var3);
          this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

          for (int var4 = 0; var4 < this.listAuxFramebuffers.size(); ++var4)
          {
              this.manager.addSamplerTexture((String)this.listAuxNames.get(var4), this.listAuxFramebuffers.get(var4));
              this.manager.getShaderUniformOrDefault("AuxSize" + var4).set((float)((Integer)this.listAuxWidths.get(var4)).intValue(), (float)((Integer)this.listAuxHeights.get(var4)).intValue());
          }

          this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
          this.manager.getShaderUniformOrDefault("InSize").set((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
          this.manager.getShaderUniformOrDefault("OutSize").set(var2, var3);
          this.manager.getShaderUniformOrDefault("Time").set(p_148042_1_);
          Minecraft var9 = Minecraft.getMinecraft();
          this.manager.getShaderUniformOrDefault("ScreenSize").set((float)var9.displayWidth, (float)var9.displayHeight);
          this.manager.useShader();
          //TODO alpha shader
          //this.framebufferOut.framebufferClear();
          this.framebufferOut.bindFramebuffer(false);
          //GlStateManager.depthMask(false);
        // GlStateManager.colorMask(true, true, true, false);
          Tessellator var5 = Tessellator.getInstance();
          WorldRenderer var6 = var5.getWorldRenderer();
          //var6.draw();
          var6.func_178991_c(-1);
          var6.pos(0.0D, (double)var3, 500.0D);
          var6.pos((double)var2, (double)var3, 500.0D);
          var6.pos((double)var2, 0.0D, 500.0D);
          var6.pos(0.0D, 0.0D, 500.0D);
          var5.draw();
         // GlStateManager.depthMask(true);
         // GlStateManager.colorMask(true, true, true, true);
          this.manager.endShader();
          this.framebufferOut.unbindFramebuffer();
          this.framebufferIn.unbindFramebufferTexture();
          Iterator var7 = this.listAuxFramebuffers.iterator();

          while (var7.hasNext())
          {
              Object var8 = var7.next();

              if (var8 instanceof Framebuffer)
              {
                  ((Framebuffer)var8).unbindFramebufferTexture();
              }
          }
      }

    public void loadShader(float p_148042_1_)
    {
        this.preLoadShader();
        this.framebufferIn.unbindFramebuffer();
        float f = (float)this.framebufferOut.framebufferTextureWidth;
        float f1 = (float)this.framebufferOut.framebufferTextureHeight;
        GlStateManager.viewport(0, 0, (int)f, (int)f1);
        this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

        for (int i = 0; i < this.listAuxFramebuffers.size(); ++i)
        {
            this.manager.addSamplerTexture((String)this.listAuxNames.get(i), this.listAuxFramebuffers.get(i));
            this.manager.getShaderUniformOrDefault("AuxSize" + i).set((float)((Integer)this.listAuxWidths.get(i)).intValue(), (float)((Integer)this.listAuxHeights.get(i)).intValue());
        }

        this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
        this.manager.getShaderUniformOrDefault("InSize").set((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
        this.manager.getShaderUniformOrDefault("OutSize").set(f, f1);
        this.manager.getShaderUniformOrDefault("Time").set(p_148042_1_);
        Minecraft minecraft = Minecraft.getMinecraft();
        this.manager.getShaderUniformOrDefault("ScreenSize").set((float)minecraft.displayWidth, (float)minecraft.displayHeight);
        this.manager.useShader();
        this.framebufferOut.framebufferClear();
        this.framebufferOut.bindFramebuffer(false);
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(true, true, true, true);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(0.0D, (double)f1, 500.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)f, (double)f1, 500.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos((double)f, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
        worldrenderer.pos(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(true, true, true, true);
        this.manager.endShader();
        this.framebufferOut.unbindFramebuffer();
        this.framebufferIn.unbindFramebufferTexture();

        for (Object object : this.listAuxFramebuffers)
        {
            if (object instanceof Framebuffer)
            {
                ((Framebuffer)object).unbindFramebufferTexture();
            }
        }
    }

    public ShaderManager getShaderManager()
    {
        return this.manager;
    }
}
