/* November.lol © 2023 */
package lol.november.scripting.wrapper.module;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import lol.november.feature.module.Category;
import lol.november.feature.module.Module;
import lol.november.listener.bus.Listener;
import lol.november.listener.bus.Subscribe;
import lol.november.listener.event.net.EventPacket;
import lol.november.listener.event.render.EventRender2D;
import lol.november.protect.DontObfuscate;
import lol.november.scripting.wrapper.Scripting;
import lol.november.scripting.wrapper.event.LuaEventPacket;
import lol.november.scripting.wrapper.event.LuaEventRender2D;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Scripting
@DontObfuscate
public class LuaModule extends Module {

  /**
   * A {@link HashMap} containing the java event into the lua usable event
   */
  private static final Map<Class<?>, Class<?>> luaEvents = new HashMap<>();

  static {
    luaEvents.put(EventRender2D.class, LuaEventRender2D.class);
    luaEvents.put(EventPacket.In.class, LuaEventPacket.class);
    luaEvents.put(EventPacket.Out.class, LuaEventPacket.class);
  }

  private final String name, description;
  private final Category category;

  /**
   * A map of the event names with their lua event handlers ({@link LuaClosure})
   */
  private final Map<String, LuaClosure> eventHandlers = new HashMap<>();

  /**
   * Creates a lua module
   *
   * @param name         the name of the module
   * @param description  the description of the module
   * @param categoryName the category enum name
   */
  public LuaModule(String name, String description, String categoryName) {
    this.name = name;
    this.description = description;
    category = Category.valueOf(categoryName.toUpperCase());
  }

  @Subscribe
  private final Listener<EventRender2D> render2D = event ->
    callEvent("render2D", event);

  @Subscribe
  private final Listener<EventPacket.In> packetIn = event ->
    callEvent("packet", event);

  @Subscribe
  private final Listener<EventPacket.Out> packetOut = event ->
    callEvent("packet", event);

  @Override
  public String name() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Category getCategory() {
    return category;
  }

  /**
   * Event callback for lua
   * DO NOT FUCKING CALL THIS IN JAVA I WILL SEND A PIPE BOMB IN THE MAIL
   *
   * @param eventName the event name to subscribe to
   * @param function  the function body
   */
  public void eventCallback(String eventName, LuaClosure function) {
    eventHandlers.put(eventName, function);
  }

  /**
   * An event bridge to the lua script module
   *
   * @param name  the name of the event
   * @param event the event object
   */
  private void callEvent(String name, Object event) {
    LuaClosure function = eventHandlers.getOrDefault(name, null);
    if (function != null) {
      try {
        function.call(javaEventToLua(event));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Creates a lua event based off the actual event
   *
   * @param event the event object
   * @return the {@link LuaValue} of the event
   */
  private static LuaValue javaEventToLua(Object event) {
    Class<?> luaEvent = luaEvents.getOrDefault(event.getClass(), null);
    if (luaEvent == null) throw new RuntimeException(
      "Attempted to create lua event for " + event
    );

    try {
      return CoerceJavaToLua.coerce(
        luaEvent.getConstructors()[0].newInstance(event)
      );
    } catch (
      InstantiationException
      | IllegalAccessException
      | InvocationTargetException e
    ) {
      throw new RuntimeException(e);
    }
  }
}
