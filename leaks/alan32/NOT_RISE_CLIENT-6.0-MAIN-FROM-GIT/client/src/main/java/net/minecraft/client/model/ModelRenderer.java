package net.minecraft.client.model;

import com.google.common.collect.Lists;
import net.minecraft.client.renderer.*;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.entity.model.anim.ModelUpdater;
import net.optifine.model.ModelSprite;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ModelRenderer {
    /**
     * The size of the texture file's width in pixels.
     */
    public float textureWidth;

    /**
     * The size of the texture file's height in pixels.
     */
    public float textureHeight;

    /**
     * The X offset into the texture used for displaying this model
     */
    private int textureOffsetX;

    /**
     * The Y offset into the texture used for displaying this model
     */
    private int textureOffsetY;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float rotateAngleX;
    public float rotateAngleY;
    public float rotateAngleZ;
    private boolean compiled;

    /**
     * The GL display list rendered by the Tessellator for this model
     */
    private int displayList;
    public boolean mirror;
    public boolean showModel;

    /**
     * Hides the model.
     */
    public boolean isHidden;
    public List<ModelBox> cubeList;
    public List<ModelRenderer> childModels;
    public final String boxName;
    private final ModelBase baseModel;
    public float offsetX;
    public float offsetY;
    public float offsetZ;
    public List spriteList;
    public boolean mirrorV;
    public float scaleX;
    public float scaleY;
    public float scaleZ;
    private int countResetDisplayList;
    private ResourceLocation textureLocation;
    private String id;
    private ModelUpdater modelUpdater;
    private final RenderGlobal renderGlobal;

    public ModelRenderer(final ModelBase model, final String boxNameIn) {
        this.spriteList = new ArrayList();
        this.mirrorV = false;
        this.scaleX = 1.0F;
        this.scaleY = 1.0F;
        this.scaleZ = 1.0F;
        this.textureLocation = null;
        this.id = null;
        this.renderGlobal = Config.getRenderGlobal();
        this.textureWidth = 64.0F;
        this.textureHeight = 32.0F;
        this.showModel = true;
        this.cubeList = Lists.newArrayList();
        this.baseModel = model;
        model.boxList.add(this);
        this.boxName = boxNameIn;
        this.setTextureSize(model.textureWidth, model.textureHeight);
    }

    public ModelRenderer(final ModelBase model) {
        this(model, null);
    }

    public ModelRenderer(final ModelBase model, final int texOffX, final int texOffY) {
        this(model);
        this.setTextureOffset(texOffX, texOffY);
    }

    /**
     * Sets the current box's rotation points and rotation angles to another box.
     */
    public void addChild(final ModelRenderer renderer) {
        if (this.childModels == null) {
            this.childModels = Lists.newArrayList();
        }

        this.childModels.add(renderer);
    }

    public ModelRenderer setTextureOffset(final int x, final int y) {
        this.textureOffsetX = x;
        this.textureOffsetY = y;
        return this;
    }

    public ModelRenderer addBox(String partName, final float offX, final float offY, final float offZ, final int width, final int height, final int depth) {
        partName = this.boxName + "." + partName;
        final TextureOffset textureoffset = this.baseModel.getTextureOffset(partName);
        this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
        this.cubeList.add((new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F)).setBoxName(partName));
        return this;
    }

    public ModelRenderer addBox(final float offX, final float offY, final float offZ, final int width, final int height, final int depth) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F));
        return this;
    }

    public ModelRenderer addBox(final float p_178769_1_, final float p_178769_2_, final float p_178769_3_, final int p_178769_4_, final int p_178769_5_, final int p_178769_6_, final boolean p_178769_7_) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_178769_1_, p_178769_2_, p_178769_3_, p_178769_4_, p_178769_5_, p_178769_6_, 0.0F, p_178769_7_));
        return this;
    }

    /**
     * Creates a textured box. Args: originX, originY, originZ, width, height, depth, scaleFactor.
     */
    public void addBox(final float p_78790_1_, final float p_78790_2_, final float p_78790_3_, final int width, final int height, final int depth, final float scaleFactor) {
        this.cubeList.add(new ModelBox(this, this.textureOffsetX, this.textureOffsetY, p_78790_1_, p_78790_2_, p_78790_3_, width, height, depth, scaleFactor));
    }

    public void setRotationPoint(final float rotationPointXIn, final float rotationPointYIn, final float rotationPointZIn) {
        this.rotationPointX = rotationPointXIn;
        this.rotationPointY = rotationPointYIn;
        this.rotationPointZ = rotationPointZIn;
    }

    public void render(final float p_78785_1_) {
        if (!this.isHidden && this.showModel) {
            this.checkResetDisplayList();

            if (!this.compiled) {
                this.compileDisplayList(p_78785_1_);
            }

            int i = 0;

            if (this.textureLocation != null && !this.renderGlobal.renderOverlayDamaged) {
                if (this.renderGlobal.renderOverlayEyes) {
                    return;
                }

                i = GlStateManager.getBoundTexture();
                Config.getTextureManager().bindTexture(this.textureLocation);
            }

            if (this.modelUpdater != null) {
                this.modelUpdater.update();
            }

            final boolean flag = this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F;
            GlStateManager.translate(this.offsetX, this.offsetY, this.offsetZ);

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                if (this.rotationPointX == 0.0F && this.rotationPointY == 0.0F && this.rotationPointZ == 0.0F) {
                    if (flag) {
                        GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                    }

                    GlStateManager.callList(this.displayList);

                    if (this.childModels != null) {
                        for (int l = 0; l < this.childModels.size(); ++l) {
                            this.childModels.get(l).render(p_78785_1_);
                        }
                    }

//                    GlStateManager.translate(0, 0, -0.14);
//
//                    Color color = ColorUtil.withAlpha(Color.WHITE, 100);
//
//                    for (double y = 0.3; y <= 0.5; y += 0.08) {
//                        RenderUtil.rectangle(-0.13, y + 0.06, 0.1, 0.01, color);
//                        RenderUtil.rectangle(-0.13, y + 0.06, 0.01, 0.05, color);
//
//                        RenderUtil.rectangle(0.03, y + 0.06, 0.1, 0.01, color);
//                        RenderUtil.rectangle(0.03, y + 0.06, 0.01, 0.05, color);
//
//                        RenderUtil.rectangle(-0.13, y + 0.06, 0.1, 0.05, color);
//                        RenderUtil.rectangle(0.03, y + 0.06, 0.1, 0.05, color);
//                    }
//
//                    RenderUtil.color(Color.WHITE);
//                    GlStateManager.translate(0, 0, 0.14);

                    if (flag) {
                        GlStateManager.scale(1.0F / this.scaleX, 1.0F / this.scaleY, 1.0F / this.scaleZ);
                    }
                } else {
                    GlStateManager.translate(this.rotationPointX * p_78785_1_, this.rotationPointY * p_78785_1_, this.rotationPointZ * p_78785_1_);

                    if (flag) {
                        GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                    }

                    GlStateManager.callList(this.displayList);

                    if (this.childModels != null) {
                        for (int k = 0; k < this.childModels.size(); ++k) {
                            this.childModels.get(k).render(p_78785_1_);
                        }
                    }

                    if (flag) {
                        GlStateManager.scale(1.0F / this.scaleX, 1.0F / this.scaleY, 1.0F / this.scaleZ);
                    }

                    GlStateManager.translate(-this.rotationPointX * p_78785_1_, -this.rotationPointY * p_78785_1_, -this.rotationPointZ * p_78785_1_);
                }
            } else {
                GlStateManager.pushMatrix();
                GlStateManager.translate(this.rotationPointX * p_78785_1_, this.rotationPointY * p_78785_1_, this.rotationPointZ * p_78785_1_);

                if (this.rotateAngleZ != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }

                if (flag) {
                    GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
                }

                GlStateManager.callList(this.displayList);

                if (this.childModels != null) {
                    for (int j = 0; j < this.childModels.size(); ++j) {
                        this.childModels.get(j).render(p_78785_1_);
                    }
                }

                GlStateManager.popMatrix();
            }

            GlStateManager.translate(-this.offsetX, -this.offsetY, -this.offsetZ);

            if (i != 0) {
                GlStateManager.bindTexture(i);
            }
        }
    }

    public void renderWithRotation(final float p_78791_1_) {
        if (!this.isHidden && this.showModel) {
            this.checkResetDisplayList();

            if (!this.compiled) {
                this.compileDisplayList(p_78791_1_);
            }

            int i = 0;

            if (this.textureLocation != null && !this.renderGlobal.renderOverlayDamaged) {
                if (this.renderGlobal.renderOverlayEyes) {
                    return;
                }

                i = GlStateManager.getBoundTexture();
                Config.getTextureManager().bindTexture(this.textureLocation);
            }

            if (this.modelUpdater != null) {
                this.modelUpdater.update();
            }

            final boolean flag = this.scaleX != 1.0F || this.scaleY != 1.0F || this.scaleZ != 1.0F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.rotationPointX * p_78791_1_, this.rotationPointY * p_78791_1_, this.rotationPointZ * p_78791_1_);

            if (this.rotateAngleY != 0.0F) {
                GlStateManager.rotate(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (this.rotateAngleX != 0.0F) {
                GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
            }

            if (this.rotateAngleZ != 0.0F) {
                GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (flag) {
                GlStateManager.scale(this.scaleX, this.scaleY, this.scaleZ);
            }

            GlStateManager.callList(this.displayList);

            if (this.childModels != null) {
                for (int j = 0; j < this.childModels.size(); ++j) {
                    this.childModels.get(j).render(p_78791_1_);
                }
            }

            GlStateManager.popMatrix();

            if (i != 0) {
                GlStateManager.bindTexture(i);
            }
        }
    }

    /**
     * Allows the changing of Angles after a box has been rendered
     */
    public void postRender(final float scale) {
        if (!this.isHidden && this.showModel) {
            this.checkResetDisplayList();

            if (!this.compiled) {
                this.compileDisplayList(scale);
            }

            if (this.rotateAngleX == 0.0F && this.rotateAngleY == 0.0F && this.rotateAngleZ == 0.0F) {
                if (this.rotationPointX != 0.0F || this.rotationPointY != 0.0F || this.rotationPointZ != 0.0F) {
                    GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);
                }
            } else {
                GlStateManager.translate(this.rotationPointX * scale, this.rotationPointY * scale, this.rotationPointZ * scale);

                if (this.rotateAngleZ != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (this.rotateAngleY != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (this.rotateAngleX != 0.0F) {
                    GlStateManager.rotate(this.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }
            }
        }
    }

    /**
     * Compiles a GL display list for this model
     */
    private void compileDisplayList(final float scale) {
        if (this.displayList == 0) {
            this.displayList = GLAllocation.generateDisplayLists(1);
        }

        GL11.glNewList(this.displayList, GL11.GL_COMPILE);
        final WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();

        for (int i = 0; i < this.cubeList.size(); ++i) {
            this.cubeList.get(i).render(worldrenderer, scale);
        }

        for (int j = 0; j < this.spriteList.size(); ++j) {
            final ModelSprite modelsprite = (ModelSprite) this.spriteList.get(j);
            modelsprite.render(Tessellator.getInstance(), scale);
        }

        GL11.glEndList();
        this.compiled = true;
    }

    /**
     * Returns the model renderer with the new texture parameters.
     */
    public ModelRenderer setTextureSize(final int textureWidthIn, final int textureHeightIn) {
        this.textureWidth = (float) textureWidthIn;
        this.textureHeight = (float) textureHeightIn;
        return this;
    }

    public void addSprite(final float p_addSprite_1_, final float p_addSprite_2_, final float p_addSprite_3_, final int p_addSprite_4_, final int p_addSprite_5_, final int p_addSprite_6_, final float p_addSprite_7_) {
        this.spriteList.add(new ModelSprite(this, this.textureOffsetX, this.textureOffsetY, p_addSprite_1_, p_addSprite_2_, p_addSprite_3_, p_addSprite_4_, p_addSprite_5_, p_addSprite_6_, p_addSprite_7_));
    }

    public boolean getCompiled() {
        return this.compiled;
    }

    public int getDisplayList() {
        return this.displayList;
    }

    private void checkResetDisplayList() {
        if (this.countResetDisplayList != Shaders.countResetDisplayLists) {
            this.compiled = false;
            this.countResetDisplayList = Shaders.countResetDisplayLists;
        }
    }

    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }

    public void setTextureLocation(final ResourceLocation p_setTextureLocation_1_) {
        this.textureLocation = p_setTextureLocation_1_;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String p_setId_1_) {
        this.id = p_setId_1_;
    }

    public void addBox(final int[][] p_addBox_1_, final float p_addBox_2_, final float p_addBox_3_, final float p_addBox_4_, final float p_addBox_5_, final float p_addBox_6_, final float p_addBox_7_, final float p_addBox_8_) {
        this.cubeList.add(new ModelBox(this, p_addBox_1_, p_addBox_2_, p_addBox_3_, p_addBox_4_, p_addBox_5_, p_addBox_6_, p_addBox_7_, p_addBox_8_, this.mirror));
    }

    public ModelRenderer getChild(final String p_getChild_1_) {
        if (p_getChild_1_ == null) {
            return null;
        } else {
            if (this.childModels != null) {
                for (int i = 0; i < this.childModels.size(); ++i) {
                    final ModelRenderer modelrenderer = this.childModels.get(i);

                    if (p_getChild_1_.equals(modelrenderer.getId())) {
                        return modelrenderer;
                    }
                }
            }

            return null;
        }
    }

    public ModelRenderer getChildDeep(final String p_getChildDeep_1_) {
        if (p_getChildDeep_1_ == null) {
            return null;
        } else {
            final ModelRenderer modelrenderer = this.getChild(p_getChildDeep_1_);

            if (modelrenderer != null) {
                return modelrenderer;
            } else {
                if (this.childModels != null) {
                    for (int i = 0; i < this.childModels.size(); ++i) {
                        final ModelRenderer modelrenderer1 = this.childModels.get(i);
                        final ModelRenderer modelrenderer2 = modelrenderer1.getChildDeep(p_getChildDeep_1_);

                        if (modelrenderer2 != null) {
                            return modelrenderer2;
                        }
                    }
                }

                return null;
            }
        }
    }

    public void setModelUpdater(final ModelUpdater p_setModelUpdater_1_) {
        this.modelUpdater = p_setModelUpdater_1_;
    }

    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("id: " + this.id + ", boxes: " + (this.cubeList != null ? Integer.valueOf(this.cubeList.size()) : null) + ", submodels: " + (this.childModels != null ? Integer.valueOf(this.childModels.size()) : null));
        return stringbuffer.toString();
    }
}
