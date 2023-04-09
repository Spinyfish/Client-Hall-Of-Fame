package tech.dort.dortware.api.property.impl;

import tech.dort.dortware.api.property.Value;

public class CustomStringValue extends Value<String> {
    public CustomStringValue(String name, Object owner, String value) {
        super(name, owner, value);
    }

}
