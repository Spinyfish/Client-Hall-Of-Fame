package tech.dort.dortware.impl.gui.alt;

public class LastProxy {
    private final String username, password, ip, port;

    public LastProxy(String username, String password, String ip, String port) {
        this.username = username;
        this.password = password;
        this.ip = ip;
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }
}
