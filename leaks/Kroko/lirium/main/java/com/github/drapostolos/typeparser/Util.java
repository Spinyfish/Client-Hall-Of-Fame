package com.github.drapostolos.typeparser;

import static java.util.Arrays.asList;

import java.lang.reflect.Type;

final class Util {

    private Util() {
        throw new AssertionError("Not meant for instantiation");
    }

    static SplitStrategy defaultSplitStrategy() {
        return (input, helper)  -> asList(input.split(","));
    }

    static String makeNullArgumentErrorMsg(String argName) {
        return String.format("Argument named '%s' is illegally set to null!", argName);
    }

    /**
     * @param o
     * @return %s {instance of: %s}
     */
    static String objectToString(Object o) {
        return String.format("%s {instance of: %s}", o, o.getClass());
    }

    static String formatErrorMessage(String input, String preprocessed, TargetType targetType, String message) {
        String messageTemplate = "\n\t"
                + "Can not parse \"%s\" {preprocessed: %s} "
                + "to type \"%s\" {instance of: %s} \n\t"
                + "due to: %s";
        String preprocessedInput = formatPreprocessedInput(preprocessed);
        String targetTypeName = getTargetTypeName(targetType.targetType());
        String className = targetType.targetType().getClass().getName();
        return String.format(messageTemplate,
                input, preprocessedInput, targetTypeName, className, message);
    }

    private static String formatPreprocessedInput(String preprocessedInput) {
        if (preprocessedInput == null) {
            return null;
        }
        return String.format("\"%s\"", preprocessedInput);
    }

    private static String getTargetTypeName(Type targetType) {
        if (targetType instanceof Class) {
            Class<?> c = (Class<?>) targetType;
            return c.getCanonicalName();
        }
        return targetType.toString();
    }
}
