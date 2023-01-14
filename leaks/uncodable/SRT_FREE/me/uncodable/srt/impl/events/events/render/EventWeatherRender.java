package me.uncodable.srt.impl.events.events.render;

import me.uncodable.srt.impl.events.api.Event;

public class EventWeatherRender extends Event {
   private float rainStrength;

   public EventWeatherRender(float rainStrength) {
      this.rainStrength = rainStrength;
   }

   public float getRainStrength() {
      return this.rainStrength;
   }

   public void setRainStrength(float rainStrength) {
      this.rainStrength = rainStrength;
   }
}
