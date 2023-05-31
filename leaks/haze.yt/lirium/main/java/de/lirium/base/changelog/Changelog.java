package de.lirium.base.changelog;

import de.lirium.base.feature.Feature;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor @Getter
public class Changelog implements Feature {

    private final String name;

    private final HashMap<ChangeType, ArrayList<String>> changes = new HashMap<>();


    public void add(String line) {
        Arrays.stream(ChangeType.values()).forEach(type -> {
            if(!changes.containsKey(type))
                changes.put(type, new ArrayList<>());
        });
        final char identifier = line.charAt(0);
        final ChangeType type = Arrays.stream(ChangeType.values()).filter(changeType -> changeType.identifier == identifier).findAny().orElse(null);
        if(type != null) {
            final String change = type.display.replace('&', '§') + line.substring(1);
            changes.get(type).add(change);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}
