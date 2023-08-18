/* November.lol © 2023 */
package lol.november.feature.command;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import lol.november.November;
import lol.november.feature.command.exec.CommandExecutor;
import lol.november.feature.registry.Registry;
import lol.november.utility.reflect.Reflections;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class CommandRegistry implements Registry<Command> {

  /**
   * The package representation of where commands should be
   */
  private static final String COMMAND_PACKAGE =
    "lol.november.feature.command.impl";

  /**
   * A {@link HashMap} of the command alias linked to its instance
   */
  private final Map<String, Command> commandAliasMap = new HashMap<>();

  /**
   * A {@link LinkedList} of all the command instances
   */
  private final List<Command> commands = new LinkedList<>();

  @SneakyThrows
  @Override
  public void init() {
    Reflections
      .getAllClassesInPackage(COMMAND_PACKAGE)
      .forEach(clazz -> {
        if (!Command.class.isAssignableFrom(clazz)) return;

        try {
          add((Command) clazz.getConstructors()[0].newInstance());
        } catch (
          InstantiationException
          | IllegalAccessException
          | InvocationTargetException e
        ) {
          e.printStackTrace();
        }
      });

    log.info("Discovered {} commands", commands.size());

    November.bus().subscribe(new CommandExecutor(this));
  }

  @Override
  public void add(Command... elements) {
    for (Command command : elements) {
      commands.add(command);
      for (String alias : command.getAliases()) {
        commandAliasMap.put(alias, command);
      }
    }
  }

  @Override
  public void remove(Command... elements) {
    // TODO: scripting
  }

  @Override
  public int size() {
    return commands.size();
  }

  public <T extends Command> T get(String alias) {
    return (T) commandAliasMap.get(alias.toLowerCase());
  }

  @Override
  public Collection<Command> values() {
    return commands;
  }
}
