package juul.module.settings;

import juul.util.image.Animation;

public class RangeSetting extends Setting {

	public double start, end, minimum, maximum, increment;
	public Animation startAnimation = new Animation(), endAnimation = new Animation();
	
	public RangeSetting(String name, String desc, double start, double end, double min, double max, double increment) {
		this.name = name;
		this.description = desc;
		this.start = start;
		this.end = end;
		this.minimum = min;
		this.maximum = max;
		this.increment = increment;
	}

	public double getStart() {
		return start;
	}

	public void setStart(double start) {
		if(start <= end && start >= minimum && start <= maximum)
			this.start = start;
	}

	public double getEnd() {
		return end;
	}

	public void setEnd(double end) {
		if(end >= start && end >= minimum && end <= maximum)
			this.end = end;
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
	
	public void increment(boolean end, boolean up) {
		if(end)
			setStart(getStart() + ((up ? 1 : -1) * getIncrement()));
		else
			setEnd(getEnd() + ((up ? 1 : -1) * getIncrement()));
	}
	
}
