package wtf.automn.gui.frame;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class WebPane extends JFrame {

    private String url = "google.com";

    private int width, height;
    private ChangeListener<String> changeListener;

    public WebPane(String title, int width, int height, String url) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.setTitle(title);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((int)dimension.getWidth()/2-width, (int)dimension.getHeight()/2-height, width, height);
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setContentPane(new JFXPanel());
        Platform.runLater(this::loadScene);

    }
    
    public void setChangeListener(ChangeListener<String> changeListener) {
        this.changeListener = changeListener;
    }
    
    boolean ctrlDown = false;

    private void loadScene() {
        WebView webView = new WebView();
        JFXPanel content = ((JFXPanel) this.getContentPane());

        webView.getEngine().load(this.url);
        content.setScene(new Scene(webView, this.getWidth(), this.getHeight()));
        webView.getEngine().locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!newValue.startsWith(url)){
                    Platform.runLater(() -> {
                        webView.getEngine().getLoadWorker().cancel();
                    });
                }
                if(changeListener != null) {
                    changeListener.changed(observable, oldValue, newValue);
                }
            }
        });

        content.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(ctrlDown) {
                    Platform.runLater(() -> {
                        if(webView.getZoom() <= 2.0 && webView.getZoom() >= 0.5) {
                            webView.setZoom(webView.getZoom()-(e.getWheelRotation()*((double)e.getScrollAmount()/100)));
                        }else{
                            if(webView.getZoom() >= 2.0) webView.setZoom(2);
                            if(webView.getZoom() <= 0.5) webView.setZoom(0.5);
                        }
                    });
                }
            }
        });

        content.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == 17) {ctrlDown = true;}
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == 17) {ctrlDown = false;}
            }
        });

        this.setVisible(true);
    }

}
