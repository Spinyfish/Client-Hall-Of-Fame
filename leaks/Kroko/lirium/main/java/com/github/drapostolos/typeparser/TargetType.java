package com.github.drapostolos.typeparser;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

final class TargetType {

    private final Type targetType;
    private Class<?> rawTargetType;
    private List<Class<?>> parameterizedClassArguments;

    TargetType(Type targetType) {
        this.targetType = targetType;
    }
    
    boolean isTargetTypeParameterized() {
        if (targetType instanceof ParameterizedType) {
            return true;
        }
        return false;
    }

    Type targetType() {
        return targetType;
    }

    Class<?> rawTargetType(){
        if(rawTargetType == null){
            rawTargetType = extractRawTargetType();
        }
        return rawTargetType;
        
    }
    
    private Class<?> extractRawTargetType() {
        if (targetType instanceof Class) {
            return (Class<?>) targetType;
        }
        if (isTargetTypeParameterized()) {
            ParameterizedType type = (ParameterizedType) targetType;
            return (Class<?>) type.getRawType();
        }
        if (targetType instanceof GenericArrayType) {
            GenericArrayType array = (GenericArrayType) targetType;
            Type componentType = array.getGenericComponentType();
            if (componentType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) componentType;
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();
                return Array.newInstance(rawType, 0).getClass();
            }
        }
        return targetType.getClass();
    }

    final public List<Class<?>> getParameterizedClassArguments() {
        if (parameterizedClassArguments == null) {
            parameterizedClassArguments = extractParameterizedClassArguments();
        }
        return parameterizedClassArguments;
    }

    List<Class<?>> extractParameterizedClassArguments() {
        if (!(isTargetTypeParameterized())) {
            String message = String.format("type must be parameterized: %s", Util.objectToString(targetType));
            throw new UnsupportedOperationException(message);
        }

        ParameterizedType pt = (ParameterizedType) targetType;
        ArrayList<Class<?>> result = new ArrayList<Class<?>>();
        for (Type typeArgument : pt.getActualTypeArguments()) {
            if (typeArgument instanceof Class) {
                result.add((Class<?>) typeArgument);
                continue;
            }
            if (typeArgument instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) typeArgument).getRawType();
                if (rawType instanceof Class) {
                    result.add((Class<?>) rawType);
                    continue;
                }
            }
            String message = "That type contains illegal type argument: '%s' [%s]";
            message = String.format(message, typeArgument, typeArgument.getClass());
            throw new UnsupportedOperationException(message);
        }
        return result;
    }

    @Override
    public String toString() {
        return targetType.toString();
    }

	public boolean isPrimitive() {
		return targetType instanceof Class && ((Class<?>) targetType).isPrimitive();
	}

}
