package net.superblaubeere27.valuesystem;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import crispy.util.animation.Translate;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;


public class BooleanValue extends Value<Boolean> {

    public BooleanValue(String name, Boolean defaultValue) {
        super(name, defaultValue, null, () -> true);
    }
    @Getter
    @Setter
    private Translate translate = new Translate(0,0);
    @Getter
    @Setter
    private Translate textTranslate = new Translate(0,0);

    public BooleanValue(String name, Boolean defaultValue, Supplier<Boolean> visible) {
        super(name, defaultValue, null, visible);
    }

    @Override
    public void addToJsonObject(JsonObject obj) {
        obj.addProperty(getName(), getObject());
    }

    @Override
    public void fromJsonObject(JsonObject obj) {
        if (obj.has(getName())) {
            JsonElement element = obj.get(getName());

            if (element instanceof JsonPrimitive && ((JsonPrimitive) element).isBoolean()) {
                setObject(element.getAsBoolean());
            } else {
                throw new IllegalArgumentException("Entry '" + getName() + "' is not valid");
            }
        } else {
            throw new IllegalArgumentException("Object does not have '" + getName() + "'");
        }
    }
}