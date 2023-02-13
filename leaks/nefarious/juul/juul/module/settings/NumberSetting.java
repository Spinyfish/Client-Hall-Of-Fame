package juul.module.settings;

import juul.util.image.Animation;

public class NumberSetting extends Setting {

	public double value, minimum, maximum, increment;
	public Animation animation = new Animation();
	
	public NumberSetting(String name, String desc, double value, double min, double max, double increment) {
		this.name = name;
		this.description = desc;
		this.value = value;
		this.minimum = min;
		this.maximum = max;
		this.increment = increment;
	}

	public double getValue() {
		return value;
	}
	public float getValueFloat() { return (float) value; }
	public long getValueLong() { return (long) value; }

	public void setValue(double value) {
		if(value >= minimum && value <= maximum)
			this.value = value;
	}

	public double getMinimum() {
		return minimum;
	}

	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}

	public double getMaximum() {
		return maximum;
	}

	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}

	public double getIncrement() {
		return increment;
	}

	public void setIncrement(double increment) {
		this.increment = increment;
	}
	
	public void increment(boolean up) {
		setValue(getValue() + ((up ? 1 : -1) * getIncrement()));
	}
	
}
