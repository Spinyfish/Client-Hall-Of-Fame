package net.minecraft.util;

public class TupleIntJsonSerializable
{
    private int integerValue;
    private IJsonSerializable jsonSerializableValue;
    
    public int getIntegerValue() {
        return this.integerValue;
    }
    
    public void setIntegerValue(final int integerValueIn) {
        this.integerValue = integerValueIn;
    }
    
    public <T extends IJsonSerializable> T getJsonSerializableValue() {
        return (T)this.jsonSerializableValue;
    }
    
    public void setJsonSerializableValue(final IJsonSerializable jsonSerializableValueIn) {
        this.jsonSerializableValue = jsonSerializableValueIn;
    }
}
