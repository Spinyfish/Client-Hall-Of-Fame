package dev.eternal.client.util.client;

import dev.eternal.client.ui.notification.Notification;
import dev.eternal.client.ui.notification.NotificationManager;
import dev.eternal.client.ui.notification.NotificationType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

/**
 * A utility class that stores information about Minecraft accounts.
 *
 * @author Eternal
 */
@Getter
@Setter
public class Alt {

  private String email;
  private String password;
  private String username;

  /**
   * @param email    The email of the Minecraft account.
   * @param password The password of the Minecraft account.
   * @author Eternal
   */
  public Alt(String email, String password) {
    this.email = email;
    this.password = password;
    this.username = "???";
  }

  /**
   * @param email    The email of the Minecraft account.
   * @param password The password of the Minecraft account.
   * @param username The username of the Minecraft account.
   * @author Eternal
   */
  public Alt(String email, String password, String username) {
    this.email = email;
    this.password = password;
    this.username = username;
  }

  /**
   * @return True if the login was successful and False if the login failed.
   * @author Eternal
   * Attemps to login to the Minecraft account stored in the class using the email and password.
   */
  public boolean login() {
    if (SessionChanger.getInstance().setUser(this)) {
      username(Minecraft.getMinecraft().session.getUsername());
      NotificationManager.pushNotification(new Notification(
          "Login success",
          String.format("Successfully logged into %s.", username),
          5000,
          NotificationType.SUCCESS
      ));
      return true;
    }
    this.username("Not working");
    NotificationManager.pushNotification(new Notification(
        "Login unsuccessful",
        String.format("Unable to login to %s.", email),
        5000,
        NotificationType.ERROR
    ));
    return false;
  }

  @Override
  public String toString() {
    return String.format("%s:%s:%s", email, password, username);
  }
}
