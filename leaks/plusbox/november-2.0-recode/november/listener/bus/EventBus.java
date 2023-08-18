/* November.lol © 2023 */
package lol.november.listener.bus;

import io.netty.util.internal.ConcurrentSet;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author aesthetical
 * @since 06/11/23
 */
public class EventBus {

  private final Map<Class<?>, List<Subscriber>> eventListenerMap =
    new LinkedHashMap<>();
  private final Set<Object> subscribers = new ConcurrentSet<>();

  /**
   * Dispatches an event to listeners
   *
   * @param event the event to dispatch
   * @return if the event has been canceled
   */
  public boolean dispatch(Object event) {
    List<Subscriber> subscriptions = eventListenerMap.get(event.getClass());
    if (subscriptions == null || subscriptions.isEmpty()) return false;

    boolean result = false;

    boolean isCancelable = event instanceof Cancelable;

    for (Subscriber subscriber : subscriptions) {
      if (!subscriber.isIgnoreCanceled() || !result) {
        subscriber.listener.invoke(event);
      }

      if (isCancelable) {
        result = ((Cancelable) event).isCanceled();
      }
    }

    return result;
  }

  /**
   * Subscribes an object to the event bus
   *
   * @param subscriber the subscriber object
   */
  public void subscribe(Object subscriber) {
    if (subscribers.contains(subscriber)) return;
    subscribers.add(subscriber);

    Field[] declaredFields = subscriber.getClass().getDeclaredFields();
    for (Field field : declaredFields) {
      if (
        Listener.class.isAssignableFrom(field.getType()) &&
        field.trySetAccessible()
      ) {
        if (!field.isAnnotationPresent(Subscribe.class)) continue;

        Subscribe subscribe = field.getDeclaredAnnotation(Subscribe.class);
        int priority = subscribe.priority();
        boolean ignoreCanceled = subscribe.ignoreCanceled();

        try {
          Listener<?> listener = (Listener<?>) field.get(subscriber);
          Class<?> eventClass = (Class<?>) (
            (ParameterizedType) field.getGenericType()
          ).getActualTypeArguments()[0];

          List<Subscriber> subscriptions = eventListenerMap.computeIfAbsent(
            eventClass,
            x -> new CopyOnWriteArrayList<>()
          );

          subscriptions.add(
            new Subscriber(subscriber, listener, priority, ignoreCanceled)
          );
          eventListenerMap.put(eventClass, subscriptions);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Unsubscribes an object to the event bus
   *
   * @param subscriber the subscriber object
   */
  public void unsubscribe(Object subscriber) {
    if (!subscribers.remove(subscriber)) return;

    for (Class<?> eventClass : eventListenerMap.keySet()) {
      List<Subscriber> subscriptions = eventListenerMap.get(eventClass);
      if (subscriptions == null || subscriptions.isEmpty()) continue;

      subscriptions.removeIf(sub -> sub.getParent().equals(subscriber));
      eventListenerMap.put(eventClass, subscriptions);
    }
  }
}
