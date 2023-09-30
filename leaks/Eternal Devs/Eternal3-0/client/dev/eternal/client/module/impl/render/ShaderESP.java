package dev.eternal.client.module.impl.render;

import static org.lwjgl.opengl.GL20.glGetUniformLocation;

import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventRenderGui;
import dev.eternal.client.event.events.EventRender3D;
import dev.eternal.client.event.events.EventUpdate;
import dev.eternal.client.module.Module;
import dev.eternal.client.module.api.ModuleInfo;
import dev.eternal.client.module.impl.player.Stealer;
import dev.eternal.client.render.engine.program.ShaderProgram;
import dev.eternal.client.util.render.RenderUtil;

import java.util.ArrayList;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
;

@ModuleInfo(
    name = "ShaderESP",
    description = "Your sixth sense. Now in shader edition!",
    category = Module.Category.RENDER)
public class ShaderESP extends Module {
  private final Framebuffer fboA = new Framebuffer(1, 1, false);
  private final Framebuffer fboB = new Framebuffer(1, 1, false);
  private final ArrayList<Object> renderTargets = new ArrayList<>();
  public double offset = 3.0D;
  public double iterations = 3.0D;
  public double gamma = 1.0D;
  public double exposure = 1.0D;
  public boolean stencilEntities = false;
  private ShaderProgram outlineProgram =
      new ShaderProgram("vertex.glsl", "esp/sobel_edge_detection.glsl", "");
  private ShaderProgram postProcessProgram =
      new ShaderProgram("vertex.glsl", "esp/post_process.glsl", "");
  private ShaderProgram effectProgram = new ShaderProgram("vertex.glsl", "esp/effect.glsl", "");
  private Stealer chestStealer;

  private long startTime;

  private void checkSetupFbos() {
    this.fboA.checkSetupFbo(mc.displayWidth, mc.displayHeight);
    this.fboB.checkSetupFbo(mc.displayWidth, mc.displayHeight);
    this.fboA.setFramebufferFilter(GL11.GL_LINEAR);
    this.fboB.setFramebufferFilter(GL11.GL_LINEAR);

    this.fboA.setFramebufferColor(0f, 0f, 0f, 0f);
    this.fboB.setFramebufferColor(0f, 0f, 0f, 0f);
  }

  private void setupShaders() {
    startTime = System.currentTimeMillis();
    this.outlineProgram.delete();
    this.outlineProgram = new ShaderProgram("vertex.glsl", "esp/sobel_edge_detection.glsl", "");

    this.postProcessProgram.delete();
    this.postProcessProgram = new ShaderProgram("vertex.glsl", "esp/post_process.glsl", "");

    this.effectProgram.delete();
    this.effectProgram = new ShaderProgram("vertex.glsl", "esp/effect.glsl", "");
  }

  @Override
  public void onEnable() {
    this.stencilEntities = true;
    this.setupShaders();
    this.chestStealer =
        (Stealer) client.moduleManager().getByClass(Stealer.class);

    assert this.chestStealer != null
        : "ChestStealer module couldn't be attached to ModuleESP (for chest esp)";
  }

  @Subscribe
  public void onEventRender2D(EventRenderGui eventRenderGui) {
    this.onRender2D();
  }

  @Subscribe
  public void onEventRender3D(EventRender3D eventRender2D) {
    this.onRender3D();
  }

  @Subscribe
  public void onEventUpdate(EventUpdate eventUpdate) {
    this.onUpdate();
  }

  private void onRender2D() {
    if (mc.gameSettings.ofFastRender) {
      System.out.println("FAST RENDERERERERER§!$!");
      return;
    }

    gamma = 0.8;
    this.checkSetupFbos();

    GlStateManager.pushMatrix();
    // GlStateManager.translate(0f, 0f, -10f);
    GlStateManager.enableTexture2D();
    GlStateManager.disableLighting();
    GlStateManager.enableAlpha();
    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

    GlStateManager.enableBlend();
    GL14.glBlendFuncSeparate(
        GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

    final ScaledResolution scaledResolution = new ScaledResolution(mc);
    final float width = (float) scaledResolution.getScaledWidth_double();
    final float height = (float) scaledResolution.getScaledHeight_double();

    if (stencilEntities) {
      RenderUtil.initStencil();

      GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 1);
      GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
      GL11.glColorMask(false, false, false, false);
      RenderUtil.drawTexturedQuad(width, height, fboA.framebufferTexture);
      GL11.glColorMask(true, true, true, true);
    }


    // clear fboB and render both outline passes into it
    fboB.clear();
    fboB.bind(false);
    outlineProgram.use();
    this.setOutlineUniforms();
    this.doOutlinePass(0, fboA.framebufferTexture, width, height);

    // render blur
    GL14.glBlendFuncSeparate(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
    ModuleBlur.blur(fboB, fboA, true, (int) 5, (int) 2, width, height);

    /*
     Experimenting
    */
//    effectProgram.use();
//    GL20.glUniform1i(effectProgram.uniform("scene"), 11);
//    GL20.glUniform1i(effectProgram.uniform("entities"), 0);
//    GL20.glUniform1f(effectProgram.uniform("time"), (System.currentTimeMillis() - startTime) / 10000F);
//    GL13.glActiveTexture(GL13.GL_TEXTURE11);
//    GL11.glBindTexture(GL11.GL_TEXTURE_2D, fboA.framebufferTexture);
//    GL13.glActiveTexture(GL13.GL_TEXTURE0);
//    fboB.clear();
//    fboB.bind(false);
//    RenderUtil.drawTexturedQuad(width, height, mc.getFramebuffer().framebufferTexture);
//    effectProgram.unUse();

    // render blur
    if (stencilEntities) {
      RenderUtil.bindReadStencilBuffer(0);
    }
    //     mc.getFramebuffer().bind(false);
    //     RenderUtil.drawTexturedQuad(width, height, fboB.texture);
    // apply post-processing effectsa
    postProcessProgram.use();
    this.setColorMapUniforms();
    this.doColorMapPass(fboA.framebufferTexture, width, height, mc.getFramebuffer(), false);
    postProcessProgram.use();
    GlStateManager.popMatrix();

    if (stencilEntities) {
      RenderUtil.uninitStencilBuffer();
    }

    GlStateManager.alphaFunc(GL11.GL_GREATER, 0.2F);
    GlStateManager.bindTexture(0);
    GlStateManager.color(1f, 1f, 1f, 1f);
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
  }

  private void onRender3D() {
    RenderUtil.renderEntities(
        this.renderTargets,
        fboA,
        obj -> {
          float[] color = new float[]{1f, 1f, 1f};

          if (obj instanceof final Entity ent) {

            if (ent instanceof EntityPlayer) {
              color = new float[]{0.5F, 0.0F, 0.5F};
            } else if (ent instanceof EntityTameable) {
              final EntityTameable entityTameable = (EntityTameable) ent;
              if (entityTameable.isOwner(mc.thePlayer)) color = new float[]{0.1F, 1.0F, 0.1F};
              else {
                if (ent instanceof EntityWolf) {
                  final EntityWolf entityWolf = (EntityWolf) ent;
                  if (entityWolf.isAngry()) color = new float[]{0.75F, 0.0F, 0.0F};
                  else color = new float[]{0.0F, 0.75F, 0.0F};
                } else color = new float[]{0.0F, 0.0F, 0.75F};
              }
            } else if (ent instanceof EntityMob) {
              if (ent instanceof EntityPigZombie) {
                final EntityPigZombie entityPigZombie = (EntityPigZombie) ent;
                if (entityPigZombie.isAngry()) color = new float[]{0.75F, 0.0F, 0.0F};
                else color = new float[]{0.0F, 0.5F, 0.0F};
              } else color = new float[]{0.75F, 0.0F, 0.0F};
            } else if (ent instanceof EntityLiving) {
              color = new float[]{0.0F, 0.5F, 0.0F};
            } else if (ent instanceof EntityItem) {
              color = new float[]{1.0F, 1.0F, 0.0F};
            } else {
              color = new float[]{0.0F, 0.0F, 0.75F};
            }

            if (ent instanceof EntityLivingBase) {
              final EntityLivingBase entLiving = (EntityLivingBase) ent;
              if (entLiving.hurtTime > 0.0F || entLiving.deathTime > 0) {
                float fHurt =
                    Math.max(0.0F, (entLiving.hurtTime - mc.timer.renderPartialTicks) / 10.0F);
                final float aHurt = 0.4F;
                float tHurt =
                    (float)
                        (Math.pow(fHurt, aHurt)
                            * Math.pow(1 - fHurt, aHurt)
                            * Math.pow(2.0, 2.0F * aHurt));

                float tDeath =
                    Math.min(1.0F, (entLiving.deathTime + mc.timer.renderPartialTicks) / 10.0F);

                float red = tHurt + (1 - tHurt) * color[0];
                float green = (1 - tHurt) * color[1];
                float blue = (1 - tHurt) * color[2];

                red = tDeath * 0.0F + (1 - tDeath) * red;
                green = tDeath * 0.0F + (1 - tDeath) * green;
                blue = tDeath * 0.0F + (1 - tDeath) * blue;

                return new float[]{red, green, blue};
              }
            }
          } else if (obj instanceof TileEntityChest) {
            color = new float[]{0.0f, 1.0f, 0.0f};
          } else if (obj instanceof TileEntityEnderChest) {
            color = new float[]{0.47f, 0.03f, 0.74f};
          }
          return color;
        });
    mc.getFramebuffer().bind(false);
    mc.entityRenderer.disableLightmap();
  }

  private void getRenderTargets() {
    this.renderTargets.clear();
    if (mc.gameSettings.thirdPersonView > 0) this.renderTargets.add(mc.thePlayer);
    for (Entity entity : mc.theWorld.loadedEntityList) {
      if (entity instanceof EntityItem
          || (entity instanceof EntityLivingBase
          && entity != mc.thePlayer
          && !entity.isInvisible())) {

        this.renderTargets.add(entity);
      }
    }
    if (this.chestStealer.isEnabled()) {
      for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
        if (tileEntity instanceof TileEntityChest || tileEntity instanceof TileEntityEnderChest) {
          this.renderTargets.add(tileEntity);
        }
      }
    }
  }

  private void onUpdate() {
    this.getRenderTargets();
  }

  private void setOutlineUniforms() {
    GL20.glUniform1i(outlineProgram.uniform("texture"), 0);
    GL20.glUniform2f(
        outlineProgram.uniform("texelSize"),
        1.0F / (float) mc.displayWidth,
        1.0F / (float) mc.displayHeight);
  }

  private void setColorMapUniforms() {
    GL20.glUniform1f(glGetUniformLocation(postProcessProgram.program(), "gamma"), (float) gamma);
    GL20.glUniform1f(
        glGetUniformLocation(postProcessProgram.program(), "exposure"), (float) exposure * 10);
  }

  /**
   * Performs an outline pass on the given texture
   *
   * @param pass    which pass to perform (0 for (1, 0), 1 for (0, 1))
   * @param texture the texture to apply the outline effect on
   * @param width   the width of the outline pass
   * @param height  the height of the outline pass
   */
  private void doOutlinePass(int pass, int texture, float width, float height) {

    RenderUtil.drawTexturedQuad((int) width, (int) height, texture);
  }

  /**
   * Applies color mapping on the given texture
   *
   * @param texture the texture to apply the color mapping to
   * @param width   the width to render the applied texture in
   * @param height  the height to render the applied texture in
   */
  private void doColorMapPass(
      int texture, float width, float height, Framebuffer fboOut, boolean clear) {
    if (clear) fboOut.clear();
    fboOut.bind(false);
    RenderUtil.drawTexturedQuad((int) width, (int) height, texture);
  }
}
