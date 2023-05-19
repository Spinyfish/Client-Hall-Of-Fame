package club.marsh.bloom.api.value;



import java.lang.Number;
import java.util.function.Supplier;

import club.marsh.bloom.impl.utils.other.MathUtils;
import club.marsh.bloom.impl.utils.render.Translate;

public class NumberValue<T extends Number> extends Value<NumberValue> {



	public T value;

    public T min;
    
    public boolean dragging;

    public Translate getTranslate() {
		return translate;
	}


	public T max;
    
    public Translate translate = new Translate(0,0);
    
    public int rounding = 2;

    public T getValue() {
		return value;
	}

	public T getMin() {
		return min;
	}

	public T getMax() {
		return max;
	}

	public boolean isDragging() {
		return dragging;
	}

    public T getObject() {
        return value;
    }
    
    public void setObject(T v) {
        value = (T) ((Number) (MathUtils.roundToPlace(v.doubleValue(),rounding)));
        value = (T) (value);
        this.hitboxname = name + ":" + value;
    }
    
    public void setDragging(boolean dragging) {
    	this.dragging = dragging;
    }

    public NumberValue(String name, T value, T min, T max, Supplier<Boolean> visible) {
        super(false);
        this.value = value;
        this.hitboxname = name + ":" + value;
        this.name = name;
        this.min = min;
        this.max = max;
        this.visible = visible;
        this.dragging = false;
    }
    
    public NumberValue(String name, T value, T min, T max, int rounding, Supplier<Boolean> visible) {
        super(false);
        this.value = value;
        this.rounding = rounding;
        this.hitboxname = name + ":" + value;
        this.name = name;
        this.min = min;
        this.max = max;
        this.visible = visible;
    }
    
    public NumberValue(String name, T value, T min, T max, int rounding) {
        super(false);
        this.value = value;
        this.rounding = rounding;
        this.hitboxname = name + ":" + value;
        this.name = name;
        this.min = min;
        this.max = max;
        this.visible = () -> true;
    }


    public NumberValue(String name, T value, T min, T max) {
        super(false);
        this.visible = () -> true;
        this.value = value;
        this.name = name;
        this.min = min;
        this.hitboxname = name + ":" + value;
        this.max = max;
    }


}
