package de.fanta.setting.settings;

public class Slider implements BaseSetting{
    public double curValue;
    private double minValue;
    private double maxValue;
    private double stepValue;

    public Slider(double minValue, double maxValue, double stepValue, double curValue) {
        this.curValue = curValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepValue = stepValue;
    }

    public Object getMinValue() {
        return minValue;
    }

    public Object getMaxValue() {
        return maxValue;
    }

    public double getStepValue() {
        return stepValue;
    }

	@Override
	public Object getValue() {
		return curValue;
	}

	@Override
	public void setValue(Object value) {
		this.curValue = (Double)value;
	}

	@Override
	public void setMinValue(Object value) {
		this.minValue = (Double) value;
	}

	@Override
	public void setMaxValue(Object value) {
		this.maxValue = (Double) value;
	}
}
