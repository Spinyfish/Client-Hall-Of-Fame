package net.java.games.input;

import java.io.IOException;

public abstract class AbstractComponent implements Component {
   private final String name;
   private final Component.Identifier id;
   private boolean has_polled;
   private float value;
   private float event_value;

   protected AbstractComponent(String name, Component.Identifier id) {
      this.name = name;
      this.id = id;
   }

   public Component.Identifier getIdentifier() {
      return this.id;
   }

   public boolean isAnalog() {
      return false;
   }

   public float getDeadZone() {
      return 0.0F;
   }

   public final float getPollData() {
      if (!this.has_polled && !this.isRelative()) {
         this.has_polled = true;

         try {
            this.setPollData(this.poll());
         } catch (IOException var2) {
            ControllerEnvironment.log("Failed to poll component: " + var2);
         }
      }

      return this.value;
   }

   final void resetHasPolled() {
      this.has_polled = false;
   }

   final void setPollData(float value) {
      this.value = value;
   }

   final float getEventValue() {
      return this.event_value;
   }

   final void setEventValue(float event_value) {
      this.event_value = event_value;
   }

   public String getName() {
      return this.name;
   }

   public String toString() {
      return this.name;
   }

   protected abstract float poll() throws IOException;
}
