package co.gongzh.procbridge;

import org.jetbrains.annotations.*;
import java.net.*;
import java.util.concurrent.*;
import java.io.*;
import java.util.*;

public class Server
{
    private final int port;
    @NotNull
    private final IDelegate delegate;
    private ExecutorService executor;
    private ServerSocket serverSocket;
    private boolean started;
    @Nullable
    private PrintStream logger;
    
    public Server(final int port, @NotNull final IDelegate delegate) {
        this.port = port;
        this.delegate = delegate;
        this.started = false;
        this.executor = null;
        this.serverSocket = null;
        this.logger = System.err;
    }
    
    public final synchronized boolean isStarted() {
        return this.started;
    }
    
    public final int getPort() {
        return this.port;
    }
    
    @Nullable
    public PrintStream getLogger() {
        return this.logger;
    }
    
    public void setLogger(@Nullable final PrintStream logger) {
        this.logger = logger;
    }
    
    public synchronized void start() {
        if (this.started) {
            throw new IllegalStateException("server already started");
        }
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(this.port);
        }
        catch (IOException e) {
            throw new ServerException(e);
        }
        this.serverSocket = serverSocket;
        final ExecutorService executor = Executors.newCachedThreadPool();
        final ServerSocket serverSocket2;
        Socket socket;
        Connection conn;
        final Executor executor2;
        (this.executor = executor).execute(() -> {
            try {
                while (true) {
                    socket = serverSocket2.accept();
                    conn = new Connection(socket, this.delegate);
                    synchronized (this) {
                        if (!this.started) {
                            return;
                        }
                        else {
                            executor2.execute(conn);
                        }
                    }
                }
            }
            catch (IOException ignored) {
                return;
            }
        });
        this.started = true;
    }
    
    public synchronized void stop() {
        if (!this.started) {
            throw new IllegalStateException("server does not started");
        }
        this.executor.shutdown();
        this.executor = null;
        try {
            this.serverSocket.close();
        }
        catch (IOException ex) {}
        this.serverSocket = null;
        this.started = false;
    }
    
    final class Connection implements Runnable
    {
        private final Socket socket;
        private final IDelegate delegate;
        
        Connection(final Socket socket, final IDelegate delegate) {
            this.socket = socket;
            this.delegate = delegate;
        }
        
        @Override
        public void run() {
            try (final OutputStream os = this.socket.getOutputStream();
                 final InputStream is = this.socket.getInputStream()) {
                final Map.Entry<String, Object> req = Protocol.readRequest(is);
                final String method = req.getKey();
                final Object payload = req.getValue();
                Object result = null;
                Exception exception = null;
                try {
                    result = this.delegate.handleRequest(method, payload);
                }
                catch (Exception ex) {
                    exception = ex;
                }
                if (exception != null) {
                    Protocol.writeBadResponse(os, exception.getMessage());
                }
                else {
                    Protocol.writeGoodResponse(os, result);
                }
            }
            catch (Exception ex2) {
                if (Server.this.logger != null) {
                    ex2.printStackTrace(Server.this.logger);
                }
            }
        }
    }
}
