package wtf.automn.gui.alt.utils;


import lombok.Getter;
import wtf.automn.Automn;

public class MicrosoftAuthentication {

  @Getter
  private static MicrosoftAuthentication instance;

  public MicrosoftAuthentication() {
    instance = this;
  }

  public void loginWithPopUpWindow() {
    if (MicrosoftLoginWindow.getInstance() == null) {
      new MicrosoftLoginWindow(Automn.instance().accountManager());
    } else if (MicrosoftLoginWindow.getInstance().isVisible()) {
    } else {
      MicrosoftLoginWindow.getInstance().start(Automn.instance().accountManager());
    }
  }

}
