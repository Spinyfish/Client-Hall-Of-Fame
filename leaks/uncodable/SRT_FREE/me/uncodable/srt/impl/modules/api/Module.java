package me.uncodable.srt.impl.modules.api;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.IListener;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public abstract class Module implements IListener {
   protected static final String INTERNAL_GENERAL_COMBO_BOX = "INTERNAL_GENERAL_COMBO_BOX";
   protected static final Minecraft MC = Minecraft.getMinecraft();
   private final ModuleInfo info;
   protected boolean enabled;
   protected int primaryKey;
   protected EntityLivingBase targetEntity;

   public Module(int primaryKey, boolean enabled) {
      this.primaryKey = primaryKey;
      this.enabled = enabled;
      this.info = this.getClass().getAnnotation(ModuleInfo.class);
      Ries.INSTANCE.getSettingManager().addCheckbox(this, "INTERNAL_HIDDEN", "Hidden");
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void toggle() {
      this.enabled = !this.enabled;
      if (Ries.INSTANCE.getModuleManager().getModuleByName("Safeguard").isEnabled() && !this.getInfo().legit()) {
         Ries.INSTANCE
            .msg(
               String.format(
                  "Module \"%s\" was disabled to provide a fair gameplay experience. You cannot toggle illegitimate modules while §bsafeguard§7 is enabled.",
                  this.getInfo().name()
               )
            );
         this.enabled = false;
      } else {
         if (this.enabled) {
            if (this.info.exp()) {
               Ries.INSTANCE.msg(String.format("This module (%s) is under development. Bugs may occur during the usage of this module.", this.info.name()));
            }

            this.onEnable();
         } else {
            this.onDisable();
         }
      }
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public void setPrimaryKey(int primaryKey) {
      this.primaryKey = primaryKey;
   }

   public void setTargetEntity(EntityLivingBase targetEntity) {
      this.targetEntity = targetEntity;
   }

   public ModuleInfo getInfo() {
      return this.info;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public int getPrimaryKey() {
      return this.primaryKey;
   }

   public EntityLivingBase getTargetEntity() {
      return this.targetEntity;
   }

   public static enum Category {
      COMBAT("Combat"),
      MOVEMENT("Movement"),
      PLAYER("Player"),
      VISUAL("Visual"),
      WORLD("World"),
      MISCELLANEOUS("Miscellaneous"),
      EXPLOIT("Exploit"),
      GHOST("Ghost"),
      LEGIT("Legit"),
      FUZZER("Fuzzer");

      private final String categoryName;

      private Category(String categoryName) {
         this.categoryName = categoryName;
      }

      public String getCategoryName() {
         return this.categoryName;
      }
   }
}
