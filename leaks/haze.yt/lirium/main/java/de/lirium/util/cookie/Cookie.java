package de.lirium.util.cookie;

public class Cookie {
    private final String domain;
    private final String subdomains;
    private final String path;
    private final String secure;
    private final String expiration;
    private final String name;
    private final String value;

    public Cookie(String domain, String subdomains, String path, String secure, String expiration, String name, String value) {
        this.domain = domain;
        this.subdomains = subdomains;
        this.path = path;
        this.secure = secure;
        this.expiration = expiration;
        this.name = name;
        this.value = value;
    }

    public String getDomain() {
        return domain;
    }

    public String getSubdomains() {
        return subdomains;
    }

    public String getPath() {
        return path;
    }

    public String getSecure() {
        return secure;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "domain='" + domain + '\'' +
                ", subdomains='" + subdomains + '\'' +
                ", path='" + path + '\'' +
                ", secure='" + secure + '\'' +
                ", expiration='" + expiration + '\'' +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
