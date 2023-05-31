package com.github.drapostolos.typeparser;

import static java.beans.PropertyEditorManager.findEditor;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.EnumSet.allOf;
import static java.util.stream.Collectors.toList;

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;

final class DynamicParsers {

    private static final Set<DynamicParser> DEFAULT_DYNAMIC_PARSERS = new LinkedHashSet<DynamicParser>();

    static {
        DEFAULT_DYNAMIC_PARSERS.addAll(mandatory(allOf(ContainerType.class)));
        DEFAULT_DYNAMIC_PARSERS.addAll(mandatory(allOf(NoneContainerType.class)));
    }

    private static List<DynamicParser> mandatory(Collection<? extends MandatoryOrOptionalDynamicParser> parsers) {
    	return parsers.stream()
    	.filter(MandatoryOrOptionalDynamicParser::isMandatory)
    	.collect(toList());
    }

    static Set<DynamicParser> copyDefault() {
        return new LinkedHashSet<DynamicParser>(DEFAULT_DYNAMIC_PARSERS);
    }

    private DynamicParsers() {
        throw new AssertionError("Not meant for instantiation");
    }

    private static interface MandatoryOrOptionalDynamicParser extends DynamicParser{
        default boolean isMandatory() {
        	return true;
        }
    }

    /*
     * This enum represents container types (i.e. types containing other types)
     * such as Collections, arrays, Maps etc.
     */
    enum ContainerType implements MandatoryOrOptionalDynamicParser {
        /**
         * ENUMSET Must be called before COLLECTION enum constant.
         */
        ENUMSET {

            @Override
            public Object parse(String input, ParserHelper helper) {
                if (!helper.getRawTargetClass().equals(EnumSet.class)) {
                    return TRY_NEXT;
                }

                Class<?> elementType = extractElementType(helper);
                @SuppressWarnings("unchecked")
                Collection<Object> collection = Collections.singleton(EnumSet.noneOf(elementType.asSubclass(Enum.class)).clone());
                return populateCollection(collection, elementType, input, helper);
            }

        },
        COLLECTION {

            @Override
            public Object parse(String input, ParserHelper helper) {
                if (!helper.isTargetTypeAssignableTo(Collection.class)) {
                    return TRY_NEXT;
                }

                Collection<Object> collection = instantiateCollection(helper.getRawTargetClass());
                Class<?> elementType = extractElementType(helper);
                return populateCollection(collection, elementType, input, helper);
            }

            private <T> Collection<T> instantiateCollection(Class<? extends T> collectionType) {
                if (collectionType.isInterface()) {
                    return instantiateCollectionFromInterface(collectionType);
                }
                return instantiateCollectionFromClass(collectionType);
            }

            private <T> Collection<T> instantiateCollectionFromClass(Class<? extends T> collectionType) {
                try {
                    @SuppressWarnings("unchecked")
                    Collection<T> temp = (Collection<T>) collectionType.newInstance();
                    return temp;
                } catch (Exception e) {
                    String message = "Cannot instantiate collection of type '%s'";
                    throw new UnsupportedOperationException(String.format(message, collectionType), e);
                }
            }

            private <T> Collection<T> instantiateCollectionFromInterface(Class<? extends T> collectionType) {
                if (List.class.isAssignableFrom(collectionType)) {
                    return new ArrayList<T>();
                } else if (SortedSet.class.isAssignableFrom(collectionType)) {
                    return new TreeSet<T>();
                } else if (Set.class.isAssignableFrom(collectionType)) {
                    return new LinkedHashSet<T>();
                } else if (BlockingDeque.class.isAssignableFrom(collectionType)) {
                    return new LinkedBlockingDeque<T>();
                } else if (Deque.class.isAssignableFrom(collectionType)) {
                    return new ArrayDeque<T>();
                } else if (BlockingQueue.class.isAssignableFrom(collectionType)) {
                    return new LinkedBlockingDeque<T>();
                } else if (Queue.class.isAssignableFrom(collectionType)) {
                    return new LinkedList<T>();
                }
                return new ArrayList<T>();
            }
        },
        MAP {

            private static final int KEY = 0;
            private static final int VALUE = 1;

            @Override
            public Object parse(String input, ParserHelper helper) {
                if (!helper.isTargetTypeAssignableTo(Map.class)) {
                    return TRY_NEXT;
                }
                Class<?> keyType = helper.getParameterizedClassArgumentByIndex(KEY);
                Class<?> valueType = helper.getParameterizedClassArgumentByIndex(VALUE);
                Map<Object, Object> map = instantiateMap(helper.getRawTargetClass());
                for (String entryString : helper.split(input)) {
                    List<String> entry = helper.splitKeyValue(entryString);
                    map.put(helper.parse(entry.get(KEY), keyType), helper.parse(entry.get(VALUE), valueType));
                }
                return map;
            }

            private <K, V> Map<K, V> instantiateMap(Class<?> rawType) {
                if (rawType.isInterface()) {
                    return instantiateMapFromInterface(rawType);
                }
                return instantiateMapFromClass(rawType);
            }

            private <K, V> Map<K, V> instantiateMapFromInterface(Class<?> targetType) {
                if (NavigableMap.class.isAssignableFrom(targetType)) {
                    return new ConcurrentSkipListMap<K, V>();
                }
                if (ConcurrentMap.class.isAssignableFrom(targetType)) {
                    return new ConcurrentHashMap<K, V>();
                }
                if (SortedMap.class.isAssignableFrom(targetType)) {
                    return new TreeMap<K, V>();
                }
                return new LinkedHashMap<K, V>();
            }

            private <K, V> Map<K, V> instantiateMapFromClass(Class<?> rawType) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<K, V> temp = (Map<K, V>) rawType.newInstance();
                    return temp;
                } catch (Exception e) {
                    String message = String.format("Cannot instantiate map of type '%s'", rawType);
                    throw new UnsupportedOperationException(message, e);
                }
            }

        },
        ARRAY {

            @Override
            public Object parse(String input, ParserHelper helper) {
                if (!helper.getRawTargetClass().isArray()) {
                    return TRY_NEXT;
                }
                List<String> strList = helper.split(input);
                Class<?> componentType = helper.getComponentClass();
                Object result = Array.newInstance(componentType, strList.size());
                for (int i = 0; i < strList.size(); i++) {
                    Object element = helper.parse(strList.get(i), componentType);
                    Array.set(result, i, element);
                }
                return result;
            }
        };

        static private Collection<Object> populateCollection(Collection<Object> collection,
                Class<?> elementType, String input, ParserHelper helper) {
            for (String value : helper.split(input)) {
                collection.add(helper.parseType(value, elementType));
            }
            return collection;
        }

        static private Class<?> extractElementType(ParserHelper helper) {
            if (helper.isTargetTypeParameterized()) {
                return helper.getParameterizedClassArgumentByIndex(0);
            }
            // Use String as the default element type for raw Collections.
            return String.class;
        }
    }

    /*
     * This enum represents types that can be contained in a container type.
     */
    enum NoneContainerType implements MandatoryOrOptionalDynamicParser {
        ENUM {

            @Override
            public Object parseImp(String input, ParserHelper helper) {
                if (!helper.getRawTargetClass().isEnum()) {
                    return TRY_NEXT;
                }
                Class<Enum<?>> enumType = helper.getRawTargetClass();
                Enum<?> temp = Enum.valueOf(enumType.asSubclass(Enum.class), input.trim());
                return temp;
            }

        },
        CLASS {

            @Override
            public Object parseImp(String input, ParserHelper helper) {
                Class<?> t = helper.getRawTargetClass();
                if (t != Class.class) {
                    return TRY_NEXT;
                }
                try {
                    return Class.forName(input.trim());
                } catch (ClassNotFoundException e) {
                    throw new UnsupportedOperationException("ClassNotFound: " + e.getMessage(), e);
                }
            }
        },
        CLASS_DECLARING_STATIC_FACTORY_METHOD {

            @Override
            public Object parseImp(final String input, ParserHelper helper) {
                boolean methodFound = false;
                Method method = null;
                Object argument = null;

                // try find a matching method.
                for (Method m : helper.getRawTargetClass().getDeclaredMethods()) {
                    if (isStaticFactoryMethod(m)) {
                        Type argType = m.getGenericParameterTypes()[0];
                        try {
                            argument = helper.parseType(input, argType);
                            method = m;
                            methodFound = true;
                            break;
                        } catch (NoSuchRegisteredParserException e) {
                            continue;
                        } catch (TypeParserException e) {
                            if (e.getCause() instanceof StackOverflowError) {
                                String message = "StackOverflowError: Cyclic argument type "
                                        + "for method '%s' on this type.";
                                throw new StackOverflowError(String.format(message, m));
                            } else {
                                throw e;
                            }
                        }
                    }
                }
                if (!methodFound) {
                    return TRY_NEXT;
                }

                final Object staticMethod = null;
                try {
                    method.setAccessible(true);
                    return method.invoke(staticMethod, argument);
                } catch (Exception e) {
                    String message = "Failed when calling static factory method %s, "
                            + "with error message: %s";
                    message = String.format(message, method, e.getMessage());
                    throw new UnsupportedOperationException(message, e);
                }
            }

            private boolean isStaticFactoryMethod(Method m) {
                return isStatic(m.getModifiers()) &&
                        m.getParameterTypes().length == 1 &&
                        m.getDeclaringClass().isAssignableFrom(m.getReturnType());
            }
        },
        CLASS_DECLARING_SINGLE_ARGUMENT_CONSTRUCTOR {

            @Override
            public Object parseImp(final String input, ParserHelper helper) {
                boolean constructorFound = false;
                Constructor<?> constructor = null;
                Object argument = null;

                // try find a matching constructor.
                for (Constructor<?> c : helper.getRawTargetClass().getDeclaredConstructors()) {
                    if (c.getGenericParameterTypes().length == 1) {
                        Type argType = c.getGenericParameterTypes()[0];
                        try {
                            argument = helper.parseType(input, argType);
                            constructor = c;
                            constructorFound = true;
                            break;
                        } catch (NoSuchRegisteredParserException e) {
                            continue;
                        } catch (TypeParserException e) {
                            if (e.getCause() instanceof StackOverflowError) {
                                String message = "StackOverflowError: Cyclic argument type "
                                        + "for constructor '%s' on this type.";
                                throw new StackOverflowError(String.format(message, c));
                            } else {
                                throw e;
                            }
                        }
                    }
                }
                if (!constructorFound) {
                    return TRY_NEXT;
                }

                constructor.setAccessible(true);
                try {
                    return constructor.newInstance(argument);
                } catch (Exception e) {
                    String message = "Failed when calling constructor %s, with error message: %s";
                    message = String.format(message, constructor, e.getMessage());
                    throw new UnsupportedOperationException(message, e);
                }
            }
        },
        PROPERTY_EDITOR {
            @Override
            public boolean isMandatory() {
                return false;
            }

            @Override
            public Object parseImp(String input, ParserHelper helper) {
                PropertyEditor editor = findEditor(helper.getRawTargetClass());
                if (editor == null) {
                    return TRY_NEXT;
                }
                editor.setAsText(input);
                return editor.getValue();
            }

        };

        abstract Object parseImp(String input, ParserHelper helper);

        @Override
        public final Object parse(String input, ParserHelper helper) {
            if (helper.isNullString(input)) {
                return null;
            }
            return parseImp(input, helper);
        }
    }


}
