package de.fanta.msauth;

public class MicrosoftAuthentication {

    private static MicrosoftAuthentication instance;

    public MicrosoftAuthentication() {
        instance = this;
    }

    public static MicrosoftAuthentication getInstance() {
        return instance == null ? new MicrosoftAuthentication() : instance;
    }

    public void loginWithPopUpWindow() {
        if (MicrosoftLoginWindow.getInstance() == null) new MicrosoftLoginWindow();
        else if (MicrosoftLoginWindow.getInstance().isVisible()) System.out.println("Login window already open");
        else MicrosoftLoginWindow.getInstance().start();
    }

}
