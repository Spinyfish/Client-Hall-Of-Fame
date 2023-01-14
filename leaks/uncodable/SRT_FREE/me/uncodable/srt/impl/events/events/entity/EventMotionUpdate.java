package me.uncodable.srt.impl.events.events.entity;

import me.uncodable.srt.impl.events.api.Event;

public class EventMotionUpdate extends Event {
   private double posX;
   private double posY;
   private double posZ;
   private float rotationYaw;
   private float rotationPitch;
   private boolean onGround;
   private EventMotionUpdate.State state;

   public EventMotionUpdate(double posX, double posY, double posZ, float rotationYaw, float rotationPitch, boolean onGround, EventMotionUpdate.State state) {
      this.posX = posX;
      this.posY = posY;
      this.posZ = posZ;
      this.rotationYaw = rotationYaw;
      this.rotationPitch = rotationPitch;
      this.onGround = onGround;
      this.state = state;
   }

   public double getPosX() {
      return this.posX;
   }

   public double getPosY() {
      return this.posY;
   }

   public double getPosZ() {
      return this.posZ;
   }

   public float getRotationYaw() {
      return this.rotationYaw;
   }

   public float getRotationPitch() {
      return this.rotationPitch;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public EventMotionUpdate.State getState() {
      return this.state;
   }

   public void setPosX(double posX) {
      this.posX = posX;
   }

   public void setPosY(double posY) {
      this.posY = posY;
   }

   public void setPosZ(double posZ) {
      this.posZ = posZ;
   }

   public void setRotationYaw(float rotationYaw) {
      this.rotationYaw = rotationYaw;
   }

   public void setRotationPitch(float rotationPitch) {
      this.rotationPitch = rotationPitch;
   }

   public void setOnGround(boolean onGround) {
      this.onGround = onGround;
   }

   public void setState(EventMotionUpdate.State state) {
      this.state = state;
   }

   public static enum State {
      PRE,
      POST;
   }
}
