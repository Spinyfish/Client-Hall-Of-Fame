/* November.lol © 2023 */
package lol.november.feature.command.impl;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import lol.november.feature.command.Command;
import lol.november.feature.command.Register;
import lol.november.utility.chat.Printer;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Register(
  aliases = { "copyusername", "copyuser", "copyusr" },
  description = "Copies the current player username to your clipboard"
)
public class CopyUsernameCommand extends Command {

  @Override
  public void dispatch(String[] args) throws Exception {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    clipboard.setContents(new StringSelection(mc.session.getUsername()), null);
    Printer.print("Copied your username to your clipboard!");
  }
}
