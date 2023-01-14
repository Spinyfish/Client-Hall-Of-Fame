package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public class PositionedSoundRecord extends PositionedSound {
   public static PositionedSoundRecord create(ResourceLocation soundResource, float pitch) {
      return new PositionedSoundRecord(soundResource, 0.25F, pitch, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static PositionedSoundRecord create(ResourceLocation soundResource) {
      return new PositionedSoundRecord(soundResource, 1.0F, 1.0F, ISound.AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
   }

   public static PositionedSoundRecord create(ResourceLocation soundResource, float xPosition, float yPosition, float zPosition) {
      return new PositionedSoundRecord(soundResource, 4.0F, 1.0F, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition);
   }

   public PositionedSoundRecord(ResourceLocation soundResource, float volume, float pitch, float xPosition, float yPosition, float zPosition) {
      this(soundResource, volume, pitch, ISound.AttenuationType.LINEAR, xPosition, yPosition, zPosition);
   }

   private PositionedSoundRecord(
      ResourceLocation soundResource, float volume, float pitch, ISound.AttenuationType attenuationType, float xPosition, float yPosition, float zPosition
   ) {
      super(soundResource);
      this.volume = volume;
      this.pitch = pitch;
      this.xPosF = xPosition;
      this.yPosF = yPosition;
      this.zPosF = zPosition;
      this.repeat = false;
      this.repeatDelay = 0;
      this.attenuationType = attenuationType;
   }
}
