package dev.eternal.client.command;

import dev.eternal.client.Client;
import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventChat;
import dev.eternal.client.manager.AbstractManager;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.IChatComponent;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.reflections.scanners.Scanners.SubTypes;

public class CommandManager extends AbstractManager<Command> {

  @Setter
  @Getter
  private String prefix;
  private final Client client = Client.singleton();

  private Command createInstance(Class<?> clazz) {
    try {
      return (Command) clazz.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
        InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void init() {
    Client.singleton().eventBus().register(this);
    this.prefix = ".";
    new Reflections("dev")
        .get(SubTypes.of(Command.class).asClass())
        .stream()
        .map(this::createInstance)
        .forEach(this::add);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <U extends Command> U getByName(String name) {
    Optional<Command> optional = this.stream().filter(command -> command.name().equals(name.toLowerCase()) || command.aliases().contains(name.toLowerCase())).findFirst();
    return (U) optional.orElse(null);
  }

  @Subscribe
  public void handleChat(EventChat eventChat) {
    IChatComponent chatComponent = eventChat.chatComponent();
    String unformattedText = chatComponent.getUnformattedText();
    boolean sending = eventChat.sending();
    if (!sending || !unformattedText.startsWith(client.clientSettings().commandPrefix())) {
      return;
    }
    // POV: dorg code.
    eventChat.cancelled(true);
    String modified = unformattedText.substring(1);
    String[] array = modified.split(" ");
    String commandName = array[0].toLowerCase();
    String[] arguments = Arrays.stream(array).skip(1).toArray(String[]::new);
    try {
      List<Command> commands = client.commandManager().stream()
          .filter(
              command -> command.name().equalsIgnoreCase(commandName) || command.aliases()
                  .contains(commandName))
          .toList();
      if (commands.isEmpty()) {
        client.displayMessage("Command not found.");
        return;
      }
      commands.forEach(command -> command.run(arguments));
    } catch (Exception exception) {
      client.displayMessage("Exception occurred, Check console for stacktrace.");
      System.out.println("Exception:");
      exception.printStackTrace();
    }
  }

}
