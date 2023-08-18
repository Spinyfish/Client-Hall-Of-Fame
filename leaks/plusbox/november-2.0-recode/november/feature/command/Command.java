/* November.lol © 2023 */
package lol.november.feature.command;

import lombok.Getter;
import net.minecraft.client.Minecraft;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public abstract class Command {

  /**
   * The {@link Minecraft} game instance
   */
  protected final Minecraft mc = Minecraft.getMinecraft();

  private final String[] aliases;
  private final String description, syntax;

  public Command() {
    if (!getClass().isAnnotationPresent(Register.class)) {
      throw new RuntimeException(
        "@Register is not present on the top of " + getClass()
      );
    }

    Register register = getClass().getDeclaredAnnotation(Register.class);

    aliases = register.aliases();
    description = register.description();
    syntax = register.syntax();
  }

  /**
   * Dispatches this command
   *
   * @param args the command arguments excluding the command name
   * @throws Exception
   */
  public abstract void dispatch(String[] args) throws Exception;

  /**
   * Suggests an argument on tab
   *
   * @param argument      the current argument value
   * @param argumentIndex the index of the argument
   * @return the suggestion or null if none
   */
  public String suggest(String argument, int argumentIndex) {
    return null;
  }
}
