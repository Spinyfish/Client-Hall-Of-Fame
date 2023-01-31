package net.java.games.input;

public class LinuxJoystickPOV extends LinuxJoystickAxis {
   private LinuxJoystickAxis hatX;
   private LinuxJoystickAxis hatY;

   LinuxJoystickPOV(Component.Identifier.Axis id, LinuxJoystickAxis hatX, LinuxJoystickAxis hatY) {
      super(id, false);
      this.hatX = hatX;
      this.hatY = hatY;
   }

   protected LinuxJoystickAxis getXAxis() {
      return this.hatX;
   }

   protected LinuxJoystickAxis getYAxis() {
      return this.hatY;
   }

   protected void updateValue() {
      float last_x = this.hatX.getPollData();
      float last_y = this.hatY.getPollData();
      this.resetHasPolled();
      if (last_x == -1.0F && last_y == -1.0F) {
         this.setValue(0.125F);
      } else if (last_x == -1.0F && last_y == 0.0F) {
         this.setValue(1.0F);
      } else if (last_x == -1.0F && last_y == 1.0F) {
         this.setValue(0.875F);
      } else if (last_x == 0.0F && last_y == -1.0F) {
         this.setValue(0.25F);
      } else if (last_x == 0.0F && last_y == 0.0F) {
         this.setValue(0.0F);
      } else if (last_x == 0.0F && last_y == 1.0F) {
         this.setValue(0.75F);
      } else if (last_x == 1.0F && last_y == -1.0F) {
         this.setValue(0.375F);
      } else if (last_x == 1.0F && last_y == 0.0F) {
         this.setValue(0.5F);
      } else if (last_x == 1.0F && last_y == 1.0F) {
         this.setValue(0.625F);
      } else {
         LinuxEnvironmentPlugin.logln("Unknown values x = " + last_x + " | y = " + last_y);
         this.setValue(0.0F);
      }
   }
}
