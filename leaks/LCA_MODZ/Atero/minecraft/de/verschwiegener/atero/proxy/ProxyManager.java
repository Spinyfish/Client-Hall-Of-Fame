package de.verschwiegener.atero.proxy;

import java.awt.Color;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.ui.multiplayer.GuiProxy;

public class ProxyManager {

    private ArrayList<Proxy> proxys = new ArrayList<>();
    private Future<?> testProxy;

    private Proxy currentProxy;
    private boolean useProxy;

    private GuiProxy gui;

    public ProxyManager() {
	gui = new GuiProxy();
    }

    public ArrayList<Proxy> getProxys() {
	return proxys;
    }
    public GuiProxy getGui() {
	return gui;
    }

    public void setProxys(ArrayList<Proxy> proxys) {
	this.proxys = proxys;
    }

    public Proxy getCurrentProxy() {
	return currentProxy;
    }

    public void setCurrentProxy(Proxy currentProxy) {
	this.currentProxy = currentProxy;
	testProxy = Management.instance.EXECUTOR_SERVICE.submit(() -> {
	    pingHost(currentProxy);
	});
    }

    public boolean isUseProxy() {
	return useProxy;
    }

    public void setUseProxy(boolean useProxy) {
	this.useProxy = useProxy;
    }

    private void pingHost(Proxy proxy) {
	Socket socket = new Socket();
	try {
	    socket.connect(new InetSocketAddress(proxy.getIP(), proxy.getPort()), 5000);
	    gui.setProxyState("Connected");
	    gui.setMessageColor(Management.instance.colorBlue);
	    useProxy = true;
	    socket.close();
	} catch (IOException e) {
	    e.printStackTrace();
	    if(e.toString().contains("already connected")) {
		gui.setProxyState("Already Connected");
		    gui.setMessageColor(Management.instance.colorBlue);
	    }else {
		gui.setProxyState("Invalid Proxy");
		    gui.setMessageColor(Color.RED);
	    }
	}
    }

}
