package juul.util.value;

import java.security.SecureRandom;

public class AdaptiveValue {

    float initial, value, condition, target;

    public AdaptiveValue(float value)
    {
        this.value = value;
        this.initial = value;
    }

    public AdaptiveValue(float value,float condition, float target)
    {
        this.value = value;
        this.initial = value;
        this.condition = condition;
        this.target = target;
    }

    public void increaseValue(float increment) { this.value += increment; }
    public void decreaseValue(float increment) { this.value -= increment; }

    public float getValue() { return this.value; }
    public void setValue(float value) { this.value = value; }

    public float getRandomizedValue(float range) {
        float min = this.value - range;
        float max = this.value + range;

        return new SecureRandom().nextFloat() * (max - min) + min;
    }

    // reset current value to the initial value
    public void reset() { this.value = this.initial; }

    // return the initial value
    public float getInitialValue() { return this.initial; }

}
