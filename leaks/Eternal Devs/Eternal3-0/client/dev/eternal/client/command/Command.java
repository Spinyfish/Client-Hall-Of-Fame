package dev.eternal.client.command;

import dev.eternal.client.Client;
import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventChat;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.List;

public abstract class Command {

  @Getter
  private final String name;
  @Getter
  private final String description;
  @Getter
  private final List<String> aliases;
  protected final Minecraft mc = Minecraft.getMinecraft();
  protected final Client client = Client.singleton();

  public Command(String name, String description, String... aliases) {
    this.name = name;
    this.description = description;
    this.aliases = Arrays.asList(aliases);
  }

  public abstract void run(String... args);

  public void error(String format) {
    Client.singleton().displayMessage(String.format("%s: %s%s", "Invalid arguments! Correct formatting", String.format(".%s/%s %s", name, aliases.get(0), format), "!"));
  }

}
