/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javafx.application.Platform
 *  javafx.embed.swing.JFXPanel
 *  javafx.scene.Parent
 *  javafx.scene.Scene
 *  javafx.scene.web.WebView
 */
package fr.litarvan.openauth.microsoft;

import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javax.swing.JFrame;

public class LoginFrame
extends JFrame {
    private CompletableFuture<String> future;

    public LoginFrame() {
        this.setTitle("Connexion \u00e0 Microsoft");
        this.setSize(750, 750);
        this.setLocationRelativeTo(null);
        this.setContentPane((Container)new JFXPanel());
    }

    public CompletableFuture<String> start(String url) {
        if (this.future != null) {
            return this.future;
        }
        this.future = new CompletableFuture();
        this.addWindowListener(new WindowAdapter(){

            @Override
            public void windowClosing(WindowEvent e) {
                LoginFrame.this.future.completeExceptionally(new MicrosoftAuthenticationException("User closed the authentication window"));
            }
        });
        Platform.runLater(() -> this.init(url));
        return this.future;
    }

    protected void init(String url) {
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        manager.getCookieStore().removeAll();
        WebView webView = new WebView();
        JFXPanel content = (JFXPanel)this.getContentPane();
        content.setScene(new Scene((Parent)webView, (double)this.getWidth(), (double)this.getHeight()));
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("access_token")) {
                this.setVisible(false);
                this.future.complete((String)newValue);
            }
        });
        webView.getEngine().load(url);
        this.setVisible(true);
    }
}

