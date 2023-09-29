package frapppyz.cutefurry.pics.modules.settings.impl;

import frapppyz.cutefurry.pics.Wrapper;
import frapppyz.cutefurry.pics.modules.settings.Setting;
import frapppyz.cutefurry.pics.util.MathUtil;

public class Number extends Setting {

    private double maxval, minval, val, diff;

    public Number(String name, double min, double val, double max, double diff) {
        this.name = name;
        this.minval = min;
        this.val = val;
        this.maxval = max;
        this.diff = diff;
    }

    public void increment() {
        if(val + diff <= maxval){
            val += diff;
        }
    }

    public void decrement() {
        if(val - diff >= minval){
            val -= diff;
        }
    }


    public double getVal() {
        return val;
    }
    public String getValue() {
        return String.valueOf(val);
    }
    public double getMaxval() {
        return maxval;
    }
    public double getMinval() {
        return minval;
    }


    public String getName() {
        return name;
    }
}