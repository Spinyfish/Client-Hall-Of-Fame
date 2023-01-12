package dev.eternal.client.util.client;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

/**
 * @author Eternal & Eric Golde
 */
public class SessionChanger {

  private static SessionChanger instance;

  public static SessionChanger getInstance() {
    if (instance == null) {
      instance = new SessionChanger();
    }
    return instance;
  }

  /**
   * @param email    The Email of the account.
   * @param password The password of the account.
   * @return True if the login was successful and False if the login failed.
   * @author Eternal
   */
  public boolean setUser(String email, String password) {
    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
    MicrosoftAuthResult result = null;
    try {
      result = authenticator.loginWithCredentials(email, password);
      System.out.printf("Logged in with '%s'%n", result.getProfile().getName());
      Minecraft.getMinecraft().session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "legacy");
      return true;
    } catch (MicrosoftAuthenticationException e) {
      e.printStackTrace();
    }
    return false;
  }

  /**
   * @param alt Alt object containing the account information.
   * @return True if the login was successful and False if the login failed.
   * @author Eternal
   * @see Alt
   */
  public boolean setUser(Alt alt) {
    return setUser(alt.email(), alt.password());
  }

  /**
   * Sets the session.
   *
   * @param session the session to switch on
   */
  private void setSession(Session session) {
    Minecraft.getMinecraft().session = session;
  }

  /**
   * Login offline mode Just like MCP does
   *
   * @param username the desired username to be used
   */
  public void setUserOffline(String username) {
    Session session = new Session(username, username, "0", "legacy");
    setSession(session);
  }
}
