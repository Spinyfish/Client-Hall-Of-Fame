package fr.litarvan.openauth.microsoft;

import javax.swing.*;
import java.util.concurrent.*;
import javafx.embed.swing.*;
import java.awt.*;
import java.awt.event.*;
import javafx.application.*;
import javafx.scene.web.*;
import javafx.scene.*;
import javafx.beans.value.*;

public class LoginFrame extends JFrame
{
    private CompletableFuture<String> future;
    private boolean completed;
    
    public LoginFrame() {
        this.setTitle("Microsoft Authentication");
        this.setSize(750, 750);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(2);
        this.setContentPane(new JFXPanel());
    }
    
    public CompletableFuture<String> start(final String url) {
        if (this.future != null) {
            return this.future;
        }
        this.future = new CompletableFuture<String>();
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                if (!LoginFrame.this.completed) {
                    LoginFrame.this.future.complete(null);
                }
            }
        });
        Platform.runLater(() -> this.init(url));
        return this.future;
    }
    
    protected void init(final String url) {
        final WebView webView = new WebView();
        final JFXPanel content = (JFXPanel)this.getContentPane();
        content.setScene(new Scene(webView, this.getWidth(), this.getHeight()));
        webView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.contains("access_token")) {
                this.future.complete(newValue);
                this.completed = true;
                this.dispose();
            }
            return;
        });
        webView.getEngine().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36");
        webView.getEngine().load(url);
        this.setVisible(true);
    }
}
