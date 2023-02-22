package net.optifine.shaders.config;

import net.minecraft.src.Config;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Properties;

public class Property {
    private final int[] values = null;
    private int defaultValue = 0;
    private String propertyName = null;
    private String[] propertyValues = null;
    private String userName = null;
    private String[] userValues = null;
    private int value = 0;

    public Property(final String propertyName, final String[] propertyValues, final String userName, final String[] userValues, final int defaultValue) {
        this.propertyName = propertyName;
        this.propertyValues = propertyValues;
        this.userName = userName;
        this.userValues = userValues;
        this.defaultValue = defaultValue;

        if (propertyValues.length != userValues.length) {
            throw new IllegalArgumentException("Property and user values have different lengths: " + propertyValues.length + " != " + userValues.length);
        } else if (defaultValue >= 0 && defaultValue < propertyValues.length) {
            this.value = defaultValue;
        } else {
            throw new IllegalArgumentException("Invalid default value: " + defaultValue);
        }
    }

    public boolean setPropertyValue(final String propVal) {
        if (propVal == null) {
            this.value = this.defaultValue;
            return false;
        } else {
            this.value = ArrayUtils.indexOf(this.propertyValues, propVal);

            if (this.value >= 0 && this.value < this.propertyValues.length) {
                return true;
            } else {
                this.value = this.defaultValue;
                return false;
            }
        }
    }

    public void nextValue() {
        ++this.value;

        if (this.value < 0 || this.value >= this.propertyValues.length) {
            this.value = 0;
        }
    }

    public void setValue(final int val) {
        this.value = val;

        if (this.value < 0 || this.value >= this.propertyValues.length) {
            this.value = this.defaultValue;
        }
    }

    public int getValue() {
        return this.value;
    }

    public String getUserValue() {
        return this.userValues[this.value];
    }

    public String getPropertyValue() {
        return this.propertyValues[this.value];
    }

    public String getUserName() {
        return this.userName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void resetValue() {
        this.value = this.defaultValue;
    }

    public boolean loadFrom(final Properties props) {
        this.resetValue();

        if (props == null) {
            return false;
        } else {
            final String s = props.getProperty(this.propertyName);
            return s != null && this.setPropertyValue(s);
        }
    }

    public void saveTo(final Properties props) {
        if (props != null) {
            props.setProperty(this.getPropertyName(), this.getPropertyValue());
        }
    }

    public String toString() {
        return "" + this.propertyName + "=" + this.getPropertyValue() + " [" + Config.arrayToString(this.propertyValues) + "], value: " + this.value;
    }
}
