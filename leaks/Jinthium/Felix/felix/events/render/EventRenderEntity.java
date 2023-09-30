package felix.events.render;
import felix.events.Cancellable;
import net.minecraft.entity.EntityLivingBase;

public class EventRenderEntity extends Cancellable {
	
   private EntityLivingBase entity;
   private boolean pre;
   private float limbSwing;
   private float limbSwingAmount;
   private float ageInTicks;
   private float rotationYawHead;
   private float rotationPitch;
   private float chestRot;
   private float offset;

   public EventRenderEntity(EntityLivingBase entity, boolean pre, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYawHead, float rotationPitch, float chestRot, float offset) {
      this.entity = entity;
      this.pre = pre;
      this.limbSwing = limbSwing;
      this.limbSwingAmount = limbSwingAmount;
      this.ageInTicks = ageInTicks;
      this.rotationYawHead = rotationYawHead;
      this.rotationPitch = rotationPitch;
      this.chestRot = chestRot;
      this.offset = offset;
   }

   public EventRenderEntity(EntityLivingBase entity, boolean pre) {
      this.entity = entity;
      this.pre = pre;
   }

   public EntityLivingBase getEntity() {
      return this.entity;
   }

   public boolean isPre() {
      return this.pre;
   }

   public boolean isPost() {
      return !this.pre;
   }

   public float getLimbSwing() {
      return this.limbSwing;
   }

   public void setLimbSwing(float limbSwing) {
      this.limbSwing = limbSwing;
   }

   public float getLimbSwingAmount() {
      return this.limbSwingAmount;
   }

   public void setLimbSwingAmount(float limbSwingAmount) {
      this.limbSwingAmount = limbSwingAmount;
   }

   public float getAgeInTicks() {
      return this.ageInTicks;
   }

   public void setAgeInTicks(float ageInTicks) {
      this.ageInTicks = ageInTicks;
   }

   public float getRotationYawHead() {
      return this.rotationYawHead;
   }

   public void setRotationYawHead(float rotationYawHead) {
      this.rotationYawHead = rotationYawHead;
   }

   public float getRotationPitch() {
      return this.rotationPitch;
   }

   public void setRotationPitch(float rotationPitch) {
      this.rotationPitch = rotationPitch;
   }

   public float getOffset() {
      return this.offset;
   }

   public void setOffset(float offset) {
      this.offset = offset;
   }

   public float getRotationChest() {
      return this.chestRot;
   }

   public void setRotationChest(float rotationChest) {
      this.chestRot = rotationChest;
   }
}
