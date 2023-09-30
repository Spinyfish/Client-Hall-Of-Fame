package com.github.drapostolos.typeparser;

import java.lang.reflect.Type;
import java.util.List;

abstract class Helper {

    private final TargetType tt;

    Helper(TargetType targetType) {
        tt = targetType;
    }

    @Override
    public String toString() {
        return Util.objectToString(tt.targetType());
    }

    /**
     * Returns the type to parse the input string to.
     * 
     * @return the {@link Type} to parse to.
     */
    public final Type getTargetType() {
        return tt.targetType();
    }

    /**
     * When the {@code targetType} is a parameterized type this method
     * returns a list with the type arguments.
     * <p>
     * All type arguments must be none parameterized types (i.e. nested parameterized types are not
     * supported), with one exception: {@link Class}. <br>
     * 
     * @return List of {@link Class} types.
     * @throws UnsupportedOperationException if the {@code targetType} is not a parameterized type.
     * @throws UnsupportedOperationException if any of the parameterized type arguments is of a
     *         parameterized type (with exception of {@link Class}).
     */
    public final List<Class<?>> getParameterizedClassArguments() {
        return tt.getParameterizedClassArguments();
    }

    /**
     * When the {@code targetType} is an array, this method
     * returns the component type.
     * <p/>
     * The component type must be of a non-parameterized type, with one exception: {@link Class<?>}.
     * <br/>
     * 
     * @return the component type of the array.
     * @throws UnsupportedOperationException if the {@code targetType} is not of array type.
     * @throws UnsupportedOperationException if the component type is of parameterized type
     *         (with exception of {@link Class}).
     */
    Class<?> getComponentClass() {
        if (tt.rawTargetType().isArray()) {
            return tt.rawTargetType().getComponentType();
        }
        throw new UnsupportedOperationException("type is not an array.");
    }

    /**
     * Convenient method that returns the specified element in the list (as returned from method
     * {@link ParserHelper#getParameterizedClassArguments()}).
     *
     * @param <T> the expected return type.
     * @param index index of the element to return
     * @return the element at the specified position in the list (as returned from method
     *         {@link ParserHelper#getParameterizedClassArguments()}).
     * @throws IndexOutOfBoundsException if the index is out of range
     *         (<code>index &lt; 0 || index &gt;= size()</code>)
     * @throws UnsupportedOperationException if the {@code targetType} is not a parameterized type.
     * @throws UnsupportedOperationException if any of the parameterized type arguments is of a
     *         parameterized type (with exception of {@link Class}).
     */
    public final <T> Class<T> getParameterizedClassArgumentByIndex(int index) {
        List<Class<?>> list = getParameterizedClassArguments();
        if (index > list.size() - 1 || index < 0) {
            String message = "index %s is out of bounds. Should be within [0, %s]";
            throw new IndexOutOfBoundsException(String.format(message, index, list.size() - 1));
        }
        @SuppressWarnings("unchecked")
        Class<T> temp = (Class<T>) list.get(index);
        return temp;
    }

    /**
     * Returns the {@link Class} object of the targetType.
     * Assuming targeType is an instance of {@link Class},
     * otherwise throws an {@link UnsupportedOperationException}.
     * 
     * @param <T> the expected return type.
     * @return {@link Class} object of the targetType.
     * @throws UnsupportedOperationException if targetType is not an instance of {@link Class}.
     */
    public final <T> Class<T> getTargetClass() {
        if (tt.targetType() instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<T> temp = (Class<T>) tt.targetType();
            return temp;
        }
        String message = "%s cannot be casted to java.lang.Class";
        message = String.format(message, Util.objectToString(tt.targetType()));
        throw new UnsupportedOperationException(message);
    }

    /**
     * Checks if {@code targetType} is of Parameterized type, return true if it is,
     * otherwise false.
     * <p>
     * Note!<br>
     * If {@code targetType} is of a raw collection type (e.g. java.lang.List.class), then false is
     * returned.
     * 
     * @return true if {@code targetType} is of Parameterized type, otherwise false.
     */
    public final boolean isTargetTypeParameterized() {
        return tt.isTargetTypeParameterized();
    }

    /**
     * Checks if the raw format of {@code targetType} is assignable to the given {@code type}.
     * <p>
     * Examples: <br>
     * <code>isTargetTypeAssignableTo(Number.class); // true</code> if targetType is Integer.class<br>
     * <code>isTargetTypeAssignableTo(List.class); // true</code> if targetType is
     * {@code List<Long>} <br>
     * 
     * @param type check if {@code targetType} is assignable to this type.
     * @return true if {@code targetType} is assignable to the given {@code type}, otherwise false.
     */
    public final boolean isTargetTypeAssignableTo(Class<?> type) {
        return type.isAssignableFrom(tt.rawTargetType());
    }

    /**
     * Checks if {@code targetType} is equal to the given {@code type}.
     * 
     * @param type to compare with {@code targetType}.
     * @return true if {@code targetType} is equal to the given {@code type}.
     */
    public final boolean isTargetTypeEqualTo(Class<?> type) {
        return tt.targetType().equals(type);
    }

    /**
     * Checks if {@code targetType} is equal to the generic type as represented by
     * {@code genericType}.
     * 
     * @param genericType a generic type to compare with {@code targetType}.
     * @return true if {@code targetType} is equal to the generic type represented by the given
     *         {@code genericType}.
     */
    public final boolean isTargetTypeEqualTo(GenericType<?> genericType) {
        return tt.targetType().equals(genericType.getType());
    }

    /**
     * Checks if at least one of the given {@code types} is equal to
     * the raw {@code targetType} (as returned from {@link #getRawTargetClass()}).
     * 
     * @param types to check equality with the raw {@code targetType}
     * @return true if at least one of the given {@code types} is equal to the raw
     *         {@code targetType}.
     */
    public final boolean isRawTargetClassAnyOf(Class<?>... types) {
        for (Class<?> type : types) {
            if (type.equals(tt.rawTargetType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the raw format of {@code targetType}.
     * <p>
     * Example <br>
     * String =&gt; String.class<br>
     * {@code List<Integer>} =&gt; List.class<br>
     * {@code String[]} =&gt; String[].class<br>
     * {@code Class<?>[]} =&gt; Class[].class<br>
     * 
     * @param <T> the expected return type.
     * @return the raw format of {@code targetType}
     */
    @SuppressWarnings("unchecked")
    public final <T> Class<T> getRawTargetClass() {
        return (Class<T>) tt.rawTargetType();
    }
}
