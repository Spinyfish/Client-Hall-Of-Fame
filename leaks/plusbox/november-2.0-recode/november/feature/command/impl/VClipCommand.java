/* November.lol © 2023 */
package lol.november.feature.command.impl;

import lol.november.feature.command.Command;
import lol.november.feature.command.Register;
import lol.november.feature.command.exceptions.CommandInvalidArgumentException;
import lol.november.feature.command.exceptions.CommandInvalidSyntaxException;
import lol.november.utility.chat.Printer;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  aliases = { "vclip", "verticalclip", "clipup", "clipdown" },
  description = "Clips down or up blocks",
  syntax = "[blocks]"
)
public class VClipCommand extends Command {

  @Override
  public void dispatch(String[] args) throws Exception {
    if (args.length == 0) throw new CommandInvalidSyntaxException("blocks");

    double value = Double.NaN;
    try {
      value = Double.parseDouble(args[0]);
    } catch (NumberFormatException ignored) {}

    if (Double.isNaN(value)) throw new CommandInvalidArgumentException(
      "blocks",
      "The value should be a valid number"
    );

    mc.thePlayer.setPosition(
      mc.thePlayer.posX,
      mc.thePlayer.posY + value,
      mc.thePlayer.posZ
    );
    Printer.print(
      "Attempted to clip " +
      (value >= 0.0 ? "up" : "down") +
      " &9" +
      value +
      "&7 blocks"
    );
  }
}
