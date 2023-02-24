package me.kansio.client.value.value;

import lombok.Getter;
import lombok.Setter;
import me.kansio.client.value.Value;

import java.util.ArrayList;
import java.util.Collections;

public class SubSettings {

    @Getter private ArrayList<Value<?>> subSettings = new ArrayList<>();
    @Getter @Setter private String name;

    public SubSettings(String name, Value<?>... values) {
        this.name = name;
        Collections.addAll(subSettings, values);
    }
}
