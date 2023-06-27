package co.gongzh.procbridge;

import java.util.concurrent.*;
import org.jetbrains.annotations.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class Client
{
    @NotNull
    private final String host;
    private final int port;
    private final long timeout;
    @Nullable
    private final Executor executor;
    public static final long FOREVER = 0L;
    
    public Client(@NotNull final String host, final int port, final long timeout, @Nullable final Executor executor) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.executor = executor;
    }
    
    public Client(@NotNull final String host, final int port) {
        this(host, port, 0L, null);
    }
    
    @NotNull
    public final String getHost() {
        return this.host;
    }
    
    public final int getPort() {
        return this.port;
    }
    
    public long getTimeout() {
        return this.timeout;
    }
    
    @Nullable
    public Executor getExecutor() {
        return this.executor;
    }
    
    @Nullable
    public final Object request(@Nullable final String method, @Nullable final Object payload) throws ClientException, TimeoutException, ServerException {
        final StatusCode[] respStatusCode = { null };
        final Object[] respPayload = { null };
        final Throwable[] innerException = { null };
        try (final Socket socket = new Socket(this.host, this.port)) {
            final Socket socket2;
            OutputStream os;
            InputStream is;
            Map.Entry<StatusCode, Object> entry;
            final Object o;
            final Object o2;
            final Throwable t3;
            final Throwable t6;
            final Object o3;
            final Runnable task = () -> {
                try {
                    os = socket2.getOutputStream();
                    try {
                        is = socket2.getInputStream();
                        try {
                            Protocol.writeRequest(os, method, payload);
                            entry = Protocol.readResponse(is);
                            o[0] = (StatusCode)entry.getKey();
                            o2[0] = entry.getValue();
                        }
                        catch (Throwable t2) {
                            throw t2;
                        }
                        finally {
                            if (is != null) {
                                if (t3 != null) {
                                    try {
                                        is.close();
                                    }
                                    catch (Throwable t4) {
                                        t3.addSuppressed(t4);
                                    }
                                }
                                else {
                                    is.close();
                                }
                            }
                        }
                    }
                    catch (Throwable t5) {
                        throw t5;
                    }
                    finally {
                        if (os != null) {
                            if (t6 != null) {
                                try {
                                    os.close();
                                }
                                catch (Throwable t7) {
                                    t6.addSuppressed(t7);
                                }
                            }
                            else {
                                os.close();
                            }
                        }
                    }
                }
                catch (Exception ex) {
                    o3[0] = ex;
                }
                return;
            };
            if (this.timeout <= 0L) {
                task.run();
            }
            else {
                final TimeoutExecutor guard = new TimeoutExecutor(this.timeout, this.executor);
                guard.execute(task);
            }
        }
        catch (IOException ex2) {
            throw new ClientException(ex2);
        }
        if (innerException[0] != null) {
            throw new RuntimeException(innerException[0]);
        }
        if (respStatusCode[0] != StatusCode.GOOD_RESPONSE) {
            throw new ServerException((String)respPayload[0]);
        }
        return respPayload[0];
    }
}
