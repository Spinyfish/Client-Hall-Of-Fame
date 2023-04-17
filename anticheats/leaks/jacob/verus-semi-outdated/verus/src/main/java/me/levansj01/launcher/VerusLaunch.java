package me.levansj01.launcher;

import java.net.UnknownHostException;

public interface VerusLaunch {
    void launch(VerusLauncher launcher) throws UnknownHostException;
    void shutdown() throws InterruptedException;
}
