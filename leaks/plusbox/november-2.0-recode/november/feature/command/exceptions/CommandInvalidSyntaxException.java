/* November.lol © 2023 */
package lol.november.feature.command.exceptions;

import lombok.Getter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public class CommandInvalidSyntaxException extends Exception {

  private final String argument;

  public CommandInvalidSyntaxException(String argument) {
    this.argument = argument;
  }
}
