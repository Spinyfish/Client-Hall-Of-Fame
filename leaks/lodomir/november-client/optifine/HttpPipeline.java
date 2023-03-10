/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import optifine.HttpListener;
import optifine.HttpPipelineConnection;
import optifine.HttpPipelineRequest;
import optifine.HttpRequest;
import optifine.HttpResponse;

public class HttpPipeline {
    private static Map mapConnections = new HashMap();
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_KEEP_ALIVE = "Keep-Alive";
    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_VALUE_KEEP_ALIVE = "keep-alive";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEADER_VALUE_CHUNKED = "chunked";

    public static void addRequest(String p_addRequest_0_, HttpListener p_addRequest_1_) throws IOException {
        HttpPipeline.addRequest(p_addRequest_0_, p_addRequest_1_, Proxy.NO_PROXY);
    }

    public static void addRequest(String p_addRequest_0_, HttpListener p_addRequest_1_, Proxy p_addRequest_2_) throws IOException {
        HttpRequest httprequest = HttpPipeline.makeRequest(p_addRequest_0_, p_addRequest_2_);
        HttpPipelineRequest httppipelinerequest = new HttpPipelineRequest(httprequest, p_addRequest_1_);
        HttpPipeline.addRequest(httppipelinerequest);
    }

    public static HttpRequest makeRequest(String p_makeRequest_0_, Proxy p_makeRequest_1_) throws IOException {
        URL url = new URL(p_makeRequest_0_);
        if (!url.getProtocol().equals("http")) {
            throw new IOException("Only protocol http is supported: " + url);
        }
        String s = url.getFile();
        String s1 = url.getHost();
        int i = url.getPort();
        if (i <= 0) {
            i = 80;
        }
        String s2 = "GET";
        String s3 = "HTTP/1.1";
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        map.put(HEADER_USER_AGENT, "Java/" + System.getProperty("java.version"));
        map.put(HEADER_HOST, s1);
        map.put(HEADER_ACCEPT, "text/html, image/gif, image/png");
        map.put(HEADER_CONNECTION, HEADER_VALUE_KEEP_ALIVE);
        byte[] abyte = new byte[]{};
        HttpRequest httprequest = new HttpRequest(s1, i, p_makeRequest_1_, s2, s, s3, map, abyte);
        return httprequest;
    }

    public static void addRequest(HttpPipelineRequest p_addRequest_0_) {
        HttpRequest httprequest = p_addRequest_0_.getHttpRequest();
        HttpPipelineConnection httppipelineconnection = HttpPipeline.getConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy());
        while (!httppipelineconnection.addRequest(p_addRequest_0_)) {
            HttpPipeline.removeConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy(), httppipelineconnection);
            httppipelineconnection = HttpPipeline.getConnection(httprequest.getHost(), httprequest.getPort(), httprequest.getProxy());
        }
    }

    private static synchronized HttpPipelineConnection getConnection(String p_getConnection_0_, int p_getConnection_1_, Proxy p_getConnection_2_) {
        String s = HttpPipeline.makeConnectionKey(p_getConnection_0_, p_getConnection_1_, p_getConnection_2_);
        HttpPipelineConnection httppipelineconnection = (HttpPipelineConnection)mapConnections.get(s);
        if (httppipelineconnection == null) {
            httppipelineconnection = new HttpPipelineConnection(p_getConnection_0_, p_getConnection_1_, p_getConnection_2_);
            mapConnections.put(s, httppipelineconnection);
        }
        return httppipelineconnection;
    }

    private static synchronized void removeConnection(String p_removeConnection_0_, int p_removeConnection_1_, Proxy p_removeConnection_2_, HttpPipelineConnection p_removeConnection_3_) {
        String s = HttpPipeline.makeConnectionKey(p_removeConnection_0_, p_removeConnection_1_, p_removeConnection_2_);
        HttpPipelineConnection httppipelineconnection = (HttpPipelineConnection)mapConnections.get(s);
        if (httppipelineconnection == p_removeConnection_3_) {
            mapConnections.remove(s);
        }
    }

    private static String makeConnectionKey(String p_makeConnectionKey_0_, int p_makeConnectionKey_1_, Proxy p_makeConnectionKey_2_) {
        String s = p_makeConnectionKey_0_ + ":" + p_makeConnectionKey_1_ + "-" + p_makeConnectionKey_2_;
        return s;
    }

    public static byte[] get(String p_get_0_) throws IOException {
        return HttpPipeline.get(p_get_0_, Proxy.NO_PROXY);
    }

    public static byte[] get(String p_get_0_, Proxy p_get_1_) throws IOException {
        HttpRequest httprequest = HttpPipeline.makeRequest(p_get_0_, p_get_1_);
        HttpResponse httpresponse = HttpPipeline.executeRequest(httprequest);
        if (httpresponse.getStatus() / 100 != 2) {
            throw new IOException("HTTP response: " + httpresponse.getStatus());
        }
        return httpresponse.getBody();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static HttpResponse executeRequest(HttpRequest p_executeRequest_0_) throws IOException {
        final HashMap map = new HashMap();
        String s = "Response";
        String s1 = "Exception";
        HttpListener httplistener = new HttpListener(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void finished(HttpRequest p_finished_1_, HttpResponse p_finished_2_) {
                Map map2 = map;
                synchronized (map2) {
                    map.put("Response", p_finished_2_);
                    map.notifyAll();
                }
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void failed(HttpRequest p_failed_1_, Exception p_failed_2_) {
                Map map2 = map;
                synchronized (map2) {
                    map.put("Exception", p_failed_2_);
                    map.notifyAll();
                }
            }
        };
        HashMap hashMap = map;
        synchronized (hashMap) {
            HttpPipelineRequest httppipelinerequest = new HttpPipelineRequest(p_executeRequest_0_, httplistener);
            HttpPipeline.addRequest(httppipelinerequest);
            try {
                map.wait();
            }
            catch (InterruptedException var10) {
                throw new InterruptedIOException("Interrupted");
            }
            Exception exception = (Exception)map.get("Exception");
            if (exception != null) {
                if (exception instanceof IOException) {
                    throw (IOException)exception;
                }
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException)exception;
                }
                throw new RuntimeException(exception.getMessage(), exception);
            }
            HttpResponse httpresponse = (HttpResponse)map.get("Response");
            if (httpresponse == null) {
                throw new IOException("Response is null");
            }
            return httpresponse;
        }
    }

    public static boolean hasActiveRequests() {
        for (Object httppipelineconnection : mapConnections.values()) {
            if (!((HttpPipelineConnection)httppipelineconnection).hasActiveRequests()) continue;
            return true;
        }
        return false;
    }
}

