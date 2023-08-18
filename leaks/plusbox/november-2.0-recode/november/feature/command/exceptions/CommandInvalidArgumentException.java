/* November.lol © 2023 */
package lol.november.feature.command.exceptions;

import lombok.Getter;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public class CommandInvalidArgumentException extends Exception {

  private final String argument, details;

  public CommandInvalidArgumentException(String argument, String details) {
    this.argument = argument;
    this.details = details;
  }
}
