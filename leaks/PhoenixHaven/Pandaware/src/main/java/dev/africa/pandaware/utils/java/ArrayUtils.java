package dev.africa.pandaware.utils.java;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class ArrayUtils {
    public <T> ArrayList<T> getArrayListFromStream(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean arrayContainsIgnoreCase(List<String> listIn, String stringIn) {
        return listIn.stream().anyMatch(stringIn::equalsIgnoreCase);
    }
}
