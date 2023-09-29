package wtf.automn.command;

import net.minecraft.client.Minecraft;

import java.util.List;

public abstract class Command {

  private final String description;
  private final String syntax;
  private final String[] aliases;
  protected Minecraft mc = Minecraft.getMinecraft();
  private String name;

  public Command(String name, String description, String syntax, String[] aliases) {
    this.name = name;
    this.description = description;
    this.syntax = syntax;
    this.aliases = aliases;
  }

  public abstract void execute(String[] args);

  public abstract List<String> autocomplete(String[] args);

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String[] getAliases() {
    return aliases;
  }

  public String getDescription() {
    return description;
  }

  public String getSyntax() {
    return syntax;
  }
}
