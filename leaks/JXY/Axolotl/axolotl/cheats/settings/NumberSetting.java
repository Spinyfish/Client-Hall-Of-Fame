package axolotl.cheats.settings;

public class NumberSetting extends Setting {

	public int index, code = 2;
	public double currentValue, minValue, maxValue, increment;
	
	public NumberSetting(String name, double startingValue, double minValue, double maxValue, double increment) {
		super("NumberSetting");
		this.name = name;
		this.currentValue = startingValue;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.increment = Math.floor(increment * 1000) / 1000;
	}

	@Override
	public Object getObjectValue() {
		return currentValue;
	}

	@Override
	public void setValue(Object value) {
		currentValue = (double)value;
	}
	
	public void increase(int a) {
		boolean positive = a != 0;
		currentValue = (getNumberValue() + (positive ? 1 : -1) * increment);
		if(currentValue < minValue) currentValue = minValue;
		if(currentValue > maxValue) currentValue = maxValue;
	}
	
	public double getMin() {
		return minValue;
	}
	
	public double getMax() {
		return maxValue;
	}

	@Override
	public String getValue() {
		return name + ": " + getNumberValue();
	}

	public double getNumberValue() {
		return (Math.floor(currentValue * 100) / 100);
	}

	public String getSpecificValue() {
		return getNumberValue() + "";
	}

	public void setValue(double d) {
		currentValue = d;
	}

}
