package dev.eternal.client.ui.alts;

import dev.eternal.client.util.IMinecraft;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

import java.util.UUID;

@Getter
@Setter
public final class LoginThread extends Thread implements IMinecraft {
  private final String password;
  private final String username;
  private String status;

  public LoginThread(String username, String password) {
    super("Login Thread");
    this.username = username;
    this.password = password;
    status = "\247fWaiting...";
  }

  private Session createSession(String username, String password) {
    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
    try {
      var service = authenticator.loginWithCredentials(username, password);
      return new Session(service.getProfile().getName(), service.getProfile().getId(), service.getAccessToken(), "microsoft");
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  @Override
  public void run() {
    if (password.isBlank()) {
      mc.session = new Session(username, UUID.randomUUID().toString(), "", "mojang");
      status = "\247aSet username to " + username;
      return;
    }
    status = EnumChatFormatting.AQUA + "Authenticating...";
    Session auth = createSession(username, password);
    if (auth == null) {
      status = "\247cFailed";
      return;
    }
    status = "\247aLogged into " + auth.getUsername();
    mc.session = auth;
  }
}

