package dev.eternal.client.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
  //List of registered classes.
  private final List<Object> eventObjects = new CopyOnWriteArrayList<>();
  //List of Methods in the classes that are annotated with the "@Subscribe" annotation.
  private final List<Method> eventMethods = new CopyOnWriteArrayList<>();
  //Map of the Methods and their Owners.
  private final Map<Method, Object> methodObjectMap = new ConcurrentHashMap<>();

  public void register(Object object) {
    eventObjects.add(object);
    updateMethods();
  }

  public void unregister(Object object) {
    if (!eventObjects.contains(object))
      throw new RuntimeException(String.format("Class: %s was not registered!", object));

    eventObjects.remove(object);
    updateMethods();
  }

  public void post(Object event) {
    eventMethods.stream()
        .filter(method -> method.getParameterTypes()[0] == event.getClass())
        .forEach(method -> {
          try {
            method.invoke(methodObjectMap.get(method), event);
          } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
          }
        });
  }

  private void updateMethods() {
    eventMethods.clear();
    eventObjects.stream()
        .map(Object::getClass)
        .map(Class::getDeclaredMethods)
        .map(Arrays::asList)
        .flatMap(List::stream)
        .filter(method -> method.isAnnotationPresent(Subscribe.class) && method.getParameterTypes().length == 1)
        .forEach(this::methodSetup);
    eventMethods.sort(Comparator.comparingInt(value -> value.getDeclaredAnnotation(Subscribe.class).priority().ordinal()));
  }

  private void methodSetup(Method method) {
    eventMethods.add(method);
    methodObjectMap.put(method,
        eventObjects.stream().filter(o -> method.getDeclaringClass().equals(o.getClass())).findFirst().orElseThrow(RuntimeException::new));
  }
}