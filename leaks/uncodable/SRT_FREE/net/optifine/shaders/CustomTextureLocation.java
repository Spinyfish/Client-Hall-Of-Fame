package net.optifine.shaders;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class CustomTextureLocation implements ICustomTexture {
   private final int textureUnit;
   private final ResourceLocation location;
   private final int variant;
   private ITextureObject texture;

   public CustomTextureLocation(int textureUnit, ResourceLocation location, int variant) {
      this.textureUnit = textureUnit;
      this.location = location;
      this.variant = variant;
   }

   public ITextureObject getTexture() {
      if (this.texture == null) {
         TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
         this.texture = texturemanager.getTexture(this.location);
         if (this.texture == null) {
            this.texture = new SimpleTexture(this.location);
            texturemanager.loadTexture(this.location, this.texture);
            this.texture = texturemanager.getTexture(this.location);
         }
      }

      return this.texture;
   }

   @Override
   public int getTextureId() {
      ITextureObject itextureobject = this.getTexture();
      if (this.variant != 0 && itextureobject instanceof AbstractTexture) {
         AbstractTexture abstracttexture = (AbstractTexture)itextureobject;
         MultiTexID multitexid = abstracttexture.multiTex;
         if (multitexid != null) {
            if (this.variant == 1) {
               return multitexid.norm;
            }

            if (this.variant == 2) {
               return multitexid.spec;
            }
         }
      }

      return itextureobject.getGlTextureId();
   }

   @Override
   public int getTextureUnit() {
      return this.textureUnit;
   }

   @Override
   public void deleteTexture() {
   }

   @Override
   public int getTarget() {
      return 3553;
   }

   @Override
   public String toString() {
      return "textureUnit: "
         + this.textureUnit
         + ", location: "
         + this.location
         + ", glTextureId: "
         + (this.texture != null ? this.texture.getGlTextureId() : "");
   }
}
