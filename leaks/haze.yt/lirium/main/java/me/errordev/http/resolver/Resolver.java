package me.errordev.http.resolver;

public class Resolver {

    public static ResolvedIP resolveIP(final String ipAddress, final int port) {
        final ServerAddress address = ServerAddress.func_78860_a(ipAddress + ":" + port);
        return new ResolvedIP(address.getIP(), address.getPort());
    }

}