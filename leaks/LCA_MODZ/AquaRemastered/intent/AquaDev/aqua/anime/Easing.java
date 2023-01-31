package intent.AquaDev.aqua.anime;

public interface Easing {
   Easing LINEAR = (t, b, c, d) -> c * t / d + b;
   Easing QUAD_IN = (t, b, c, d) -> {
      float var4;
      return c * (var4 = t / d) * var4 + b;
   };
   Easing QUAD_OUT = (t, b, c, d) -> {
      float var4;
      return -c * (var4 = t / d) * (var4 - 2.0F) + b;
   };
   Easing QUAD_IN_OUT = (t, b, c, d) -> {
      float var4;
      return (var4 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var4 * var4 + b : -c / 2.0F * (--var4 * (var4 - 2.0F) - 1.0F) + b;
   };
   Easing CUBIC_IN = (t, b, c, d) -> {
      float var4;
      return c * (var4 = t / d) * var4 * var4 + b;
   };
   Easing CUBIC_OUT = (t, b, c, d) -> {
      float var4;
      return c * ((var4 = t / d - 1.0F) * var4 * var4 + 1.0F) + b;
   };
   Easing CUBIC_IN_OUT = (t, b, c, d) -> {
      float var4;
      float var5;
      return (var4 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var4 * var4 * var4 + b : c / 2.0F * ((var5 = var4 - 2.0F) * var5 * var5 + 2.0F) + b;
   };
   Easing QUARTIC_IN = (t, b, c, d) -> {
      float var4;
      return c * (var4 = t / d) * var4 * var4 * var4 + b;
   };
   Easing QUARTIC_OUT = (t, b, c, d) -> {
      float var4;
      return -c * ((var4 = t / d - 1.0F) * var4 * var4 * var4 - 1.0F) + b;
   };
   Easing QUARTIC_IN_OUT = (t, b, c, d) -> {
      float var4;
      float var5;
      return (var4 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var4 * var4 * var4 * var4 + b : -c / 2.0F * ((var5 = var4 - 2.0F) * var5 * var5 * var5 - 2.0F) + b;
   };
   Easing QUINTIC_IN = (t, b, c, d) -> {
      float var4;
      return c * (var4 = t / d) * var4 * var4 * var4 * var4 + b;
   };
   Easing QUINTIC_OUT = (t, b, c, d) -> {
      float var4;
      return c * ((var4 = t / d - 1.0F) * var4 * var4 * var4 * var4 + 1.0F) + b;
   };
   Easing QUINTIC_IN_OUT = (t, b, c, d) -> {
      float var4;
      float var5;
      return (var4 = t / (d / 2.0F)) < 1.0F
         ? c / 2.0F * var4 * var4 * var4 * var4 * var4 + b
         : c / 2.0F * ((var5 = var4 - 2.0F) * var5 * var5 * var5 * var5 + 2.0F) + b;
   };
   Easing SINE_IN = (t, b, c, d) -> -c * (float)Math.cos((double)(t / d) * (Math.PI / 2)) + c + b;
   Easing SINE_OUT = (t, b, c, d) -> c * (float)Math.sin((double)(t / d) * (Math.PI / 2)) + b;
   Easing SINE_IN_OUT = (t, b, c, d) -> -c / 2.0F * ((float)Math.cos(Math.PI * (double)t / (double)d) - 1.0F) + b;
   Easing EXPO_IN = (t, b, c, d) -> t == 0.0F ? b : c * (float)Math.pow(2.0, (double)(10.0F * (t / d - 1.0F))) + b;
   Easing EXPO_OUT = (t, b, c, d) -> t == d ? b + c : c * (-((float)Math.pow(2.0, (double)(-10.0F * t / d))) + 1.0F) + b;
   Easing EXPO_IN_OUT = (t, b, c, d) -> {
      if (t == 0.0F) {
         return b;
      } else if (t == d) {
         return b + c;
      } else {
         float var4;
         return (var4 = t / (d / 2.0F)) < 1.0F
            ? c / 2.0F * (float)Math.pow(2.0, (double)(10.0F * (var4 - 1.0F))) + b
            : c / 2.0F * (-((float)Math.pow(2.0, (double)(-10.0F * --var4))) + 2.0F) + b;
      }
   };
   Easing CIRC_IN = (t, b, c, d) -> {
      float var4;
      return -c * ((float)Math.sqrt((double)(1.0F - (var4 = t / d) * var4)) - 1.0F) + b;
   };
   Easing CIRC_OUT = (t, b, c, d) -> {
      float var4;
      return c * (float)Math.sqrt((double)(1.0F - (var4 = t / d - 1.0F) * var4)) + b;
   };
   Easing CIRC_IN_OUT = (t, b, c, d) -> {
      float var4;
      float var5;
      return (var4 = t / (d / 2.0F)) < 1.0F
         ? -c / 2.0F * ((float)Math.sqrt((double)(1.0F - var4 * var4)) - 1.0F) + b
         : c / 2.0F * ((float)Math.sqrt((double)(1.0F - (var5 = var4 - 2.0F) * var5)) + 1.0F) + b;
   };
   Easing.Elastic ELASTIC_IN = new Easing.ElasticIn();
   Easing.Elastic ELASTIC_OUT = new Easing.ElasticOut();
   Easing.Elastic ELASTIC_IN_OUT = new Easing.ElasticInOut();
   Easing.Back BACK_IN = new Easing.BackIn();
   Easing.Back BACK_OUT = new Easing.BackOut();
   Easing.Back BACK_IN_OUT = new Easing.BackInOut();
   Easing BOUNCE_OUT = (t, b, c, d) -> {
      if ((t = t / d) < 0.36363637F) {
         return c * 7.5625F * t * t + b;
      } else if (t < 0.72727275F) {
         float var7;
         return c * (7.5625F * (var7 = t - 0.54545456F) * var7 + 0.75F) + b;
      } else {
         float var5;
         float var6;
         return t < 0.90909094F
            ? c * (7.5625F * (var5 = t - 0.8181818F) * var5 + 0.9375F) + b
            : c * (7.5625F * (var6 = t - 0.95454544F) * var6 + 0.984375F) + b;
      }
   };
   Easing BOUNCE_IN = (t, b, c, d) -> c - BOUNCE_OUT.ease(d - t, 0.0F, c, d) + b;
   Easing BOUNCE_IN_OUT = (t, b, c, d) -> t < d / 2.0F
         ? BOUNCE_IN.ease(t * 2.0F, 0.0F, c, d) * 0.5F + b
         : BOUNCE_OUT.ease(t * 2.0F - d, 0.0F, c, d) * 0.5F + c * 0.5F + b;

   float ease(float var1, float var2, float var3, float var4);

   public abstract static class Back implements Easing {
      public static final float DEFAULT_OVERSHOOT = 1.70158F;
      private float overshoot;

      public Back() {
         this(1.70158F);
      }

      public Back(float overshoot) {
         this.overshoot = overshoot;
      }

      public float getOvershoot() {
         return this.overshoot;
      }

      public void setOvershoot(float overshoot) {
         this.overshoot = overshoot;
      }
   }

   public static class BackIn extends Easing.Back {
      public BackIn() {
      }

      public BackIn(float overshoot) {
         super(overshoot);
      }

      @Override
      public float ease(float t, float b, float c, float d) {
         float s = this.getOvershoot();
         float var6;
         return c * (var6 = t / d) * var6 * ((s + 1.0F) * var6 - s) + b;
      }
   }

   public static class BackInOut extends Easing.Back {
      public BackInOut() {
      }

      public BackInOut(float overshoot) {
         super(overshoot);
      }

      @Override
      public float ease(float t, float b, float c, float d) {
         float s = this.getOvershoot();
         float var6;
         float var7;
         float var8;
         float var9;
         return (var6 = t / (d / 2.0F)) < 1.0F
            ? c / 2.0F * var6 * var6 * (((var8 = (float)((double)s * 1.525)) + 1.0F) * var6 - var8) + b
            : c / 2.0F * ((var7 = var6 - 2.0F) * var7 * (((var9 = (float)((double)s * 1.525)) + 1.0F) * var7 + var9) + 2.0F) + b;
      }
   }

   public static class BackOut extends Easing.Back {
      public BackOut() {
      }

      public BackOut(float overshoot) {
         super(overshoot);
      }

      @Override
      public float ease(float t, float b, float c, float d) {
         float s = this.getOvershoot();
         float var6;
         return c * ((var6 = t / d - 1.0F) * var6 * ((s + 1.0F) * var6 + s) + 1.0F) + b;
      }
   }

   public abstract static class Elastic implements Easing {
      private float amplitude;
      private float period;

      public Elastic(float amplitude, float period) {
         this.amplitude = amplitude;
         this.period = period;
      }

      public Elastic() {
         this(-1.0F, 0.0F);
      }

      public float getPeriod() {
         return this.period;
      }

      public void setPeriod(float period) {
         this.period = period;
      }

      public float getAmplitude() {
         return this.amplitude;
      }

      public void setAmplitude(float amplitude) {
         this.amplitude = amplitude;
      }
   }

   public static class ElasticIn extends Easing.Elastic {
      public ElasticIn(float amplitude, float period) {
         super(amplitude, period);
      }

      public ElasticIn() {
      }

      @Override
      public float ease(float t, float b, float c, float d) {
         float a = this.getAmplitude();
         float p = this.getPeriod();
         if (t == 0.0F) {
            return b;
         } else if ((t = t / d) == 1.0F) {
            return b + c;
         } else {
            if (p == 0.0F) {
               p = d * 0.3F;
            }

            float s = 0.0F;
            if (a < Math.abs(c)) {
               a = c;
               s = p / 4.0F;
            } else {
               s = p / (float) (Math.PI * 2) * (float)Math.asin((double)(c / a));
            }

            return -(a * (float)Math.pow(2.0, (double)(10.0F * --t)) * (float)Math.sin((double)(t * d - s) * (Math.PI * 2) / (double)p)) + b;
         }
      }
   }

   public static class ElasticInOut extends Easing.Elastic {
      public ElasticInOut(float amplitude, float period) {
         super(amplitude, period);
      }

      public ElasticInOut() {
      }

      @Override
      public float ease(float t, float b, float c, float d) {
         float a = this.getAmplitude();
         float p = this.getPeriod();
         if (t == 0.0F) {
            return b;
         } else if ((t = t / (d / 2.0F)) == 2.0F) {
            return b + c;
         } else {
            if (p == 0.0F) {
               p = d * 0.45000002F;
            }

            float s = 0.0F;
            if (a < Math.abs(c)) {
               a = c;
               s = p / 4.0F;
            } else {
               s = p / (float) (Math.PI * 2) * (float)Math.asin((double)(c / a));
            }

            return t < 1.0F
               ? -0.5F * a * (float)Math.pow(2.0, (double)(10.0F * --t)) * (float)Math.sin((double)(t * d - s) * (Math.PI * 2) / (double)p) + b
               : a * (float)Math.pow(2.0, (double)(-10.0F * --t)) * (float)Math.sin((double)(t * d - s) * (Math.PI * 2) / (double)p) * 0.5F + c + b;
         }
      }
   }

   public static class ElasticOut extends Easing.Elastic {
      public ElasticOut(float amplitude, float period) {
         super(amplitude, period);
      }

      public ElasticOut() {
      }

      @Override
      public float ease(float t, float b, float c, float d) {
         float a = this.getAmplitude();
         float p = this.getPeriod();
         if (t == 0.0F) {
            return b;
         } else if ((t = t / d) == 1.0F) {
            return b + c;
         } else {
            if (p == 0.0F) {
               p = d * 0.3F;
            }

            float s = 0.0F;
            if (a < Math.abs(c)) {
               a = c;
               s = p / 4.0F;
            } else {
               s = p / (float) (Math.PI * 2) * (float)Math.asin((double)(c / a));
            }

            return a * (float)Math.pow(2.0, (double)(-10.0F * t)) * (float)Math.sin((double)(t * d - s) * (Math.PI * 2) / (double)p) + c + b;
         }
      }
   }
}
