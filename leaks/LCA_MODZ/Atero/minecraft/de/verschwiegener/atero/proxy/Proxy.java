package de.verschwiegener.atero.proxy;

public class Proxy {

    private String IP;
    private int port;
    private ProxyType type;

    public Proxy(String IP, int port, ProxyType type) {
	this.IP = IP;
	this.port = port;
	this.type = type;
    }

    public String getIP() {
	return IP;
    }

    public void setIP(String iP) {
	IP = iP;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }

    public ProxyType getType() {
	return type;
    }

    public void setType(ProxyType type) {
	this.type = type;
    }

}
