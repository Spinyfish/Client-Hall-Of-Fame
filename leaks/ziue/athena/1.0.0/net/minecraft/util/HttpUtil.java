package net.minecraft.util;

import java.util.concurrent.atomic.*;
import java.util.*;
import net.minecraft.server.*;
import org.apache.commons.io.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import com.google.common.util.concurrent.*;
import org.apache.logging.log4j.*;

public class HttpUtil
{
    public static final ListeningExecutorService field_180193_a;
    private static final AtomicInteger downloadThreadsStarted;
    private static final Logger logger;
    
    public static String buildPostString(final Map<String, Object> data) {
        final StringBuilder stringbuilder = new StringBuilder();
        for (final Map.Entry<String, Object> entry : data.entrySet()) {
            if (stringbuilder.length() > 0) {
                stringbuilder.append('&');
            }
            try {
                stringbuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            }
            catch (UnsupportedEncodingException unsupportedencodingexception1) {
                unsupportedencodingexception1.printStackTrace();
            }
            if (entry.getValue() != null) {
                stringbuilder.append('=');
                try {
                    stringbuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                }
                catch (UnsupportedEncodingException unsupportedencodingexception2) {
                    unsupportedencodingexception2.printStackTrace();
                }
            }
        }
        return stringbuilder.toString();
    }
    
    public static String postMap(final URL url, final Map<String, Object> data, final boolean skipLoggingErrors) {
        return post(url, buildPostString(data), skipLoggingErrors);
    }
    
    private static String post(final URL url, final String content, final boolean skipLoggingErrors) {
        try {
            Proxy proxy = (MinecraftServer.getServer() == null) ? null : MinecraftServer.getServer().getServerProxy();
            if (proxy == null) {
                proxy = Proxy.NO_PROXY;
            }
            final HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection(proxy);
            httpurlconnection.setRequestMethod("POST");
            httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpurlconnection.setRequestProperty("Content-Length", "" + content.getBytes().length);
            httpurlconnection.setRequestProperty("Content-Language", "en-US");
            httpurlconnection.setUseCaches(false);
            httpurlconnection.setDoInput(true);
            httpurlconnection.setDoOutput(true);
            final DataOutputStream dataoutputstream = new DataOutputStream(httpurlconnection.getOutputStream());
            dataoutputstream.writeBytes(content);
            dataoutputstream.flush();
            dataoutputstream.close();
            final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
            final StringBuffer stringbuffer = new StringBuffer();
            String s;
            while ((s = bufferedreader.readLine()) != null) {
                stringbuffer.append(s);
                stringbuffer.append('\r');
            }
            bufferedreader.close();
            return stringbuffer.toString();
        }
        catch (Exception exception) {
            if (!skipLoggingErrors) {
                HttpUtil.logger.error("Could not post to " + url, (Throwable)exception);
            }
            return "";
        }
    }
    
    public static ListenableFuture<Object> downloadResourcePack(final File saveFile, final String packUrl, final Map<String, String> p_180192_2_, final int maxSize, final IProgressUpdate p_180192_4_, final Proxy p_180192_5_) {
        final ListenableFuture<?> listenablefuture = (ListenableFuture<?>)HttpUtil.field_180193_a.submit((Runnable)new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpurlconnection = null;
                InputStream inputstream = null;
                OutputStream outputstream = null;
                while (true) {
                    if (p_180192_4_ != null) {
                        p_180192_4_.resetProgressAndMessage("Downloading Resource Pack");
                        p_180192_4_.displayLoadingString("Making Request...");
                        try {
                            final byte[] abyte = new byte[4096];
                            final URL url = new URL(packUrl);
                            httpurlconnection = (HttpURLConnection)url.openConnection(p_180192_5_);
                            float f = 0.0f;
                            float f2 = (float)p_180192_2_.entrySet().size();
                            for (final Map.Entry<String, String> entry : p_180192_2_.entrySet()) {
                                httpurlconnection.setRequestProperty(entry.getKey(), entry.getValue());
                                if (p_180192_4_ != null) {
                                    p_180192_4_.setLoadingProgress((int)(++f / f2 * 100.0f));
                                }
                            }
                            inputstream = httpurlconnection.getInputStream();
                            f2 = (float)httpurlconnection.getContentLength();
                            final int i = httpurlconnection.getContentLength();
                            if (p_180192_4_ != null) {
                                p_180192_4_.displayLoadingString(String.format("Downloading file (%.2f MB)...", f2 / 1000.0f / 1000.0f));
                            }
                            if (saveFile.exists()) {
                                final long j = saveFile.length();
                                if (j == i) {
                                    if (p_180192_4_ != null) {
                                        p_180192_4_.setDoneWorking();
                                    }
                                    return;
                                }
                                HttpUtil.logger.warn("Deleting " + saveFile + " as it does not match what we currently have (" + i + " vs our " + j + ").");
                                FileUtils.deleteQuietly(saveFile);
                            }
                            else if (saveFile.getParentFile() != null) {
                                saveFile.getParentFile().mkdirs();
                            }
                            outputstream = new DataOutputStream(new FileOutputStream(saveFile));
                            if (maxSize > 0 && f2 > maxSize) {
                                if (p_180192_4_ != null) {
                                    p_180192_4_.setDoneWorking();
                                }
                                throw new IOException("Filesize is bigger than maximum allowed (file is " + f + ", limit is " + maxSize + ")");
                            }
                            int k = 0;
                            while ((k = inputstream.read(abyte)) >= 0) {
                                f += k;
                                if (p_180192_4_ != null) {
                                    p_180192_4_.setLoadingProgress((int)(f / f2 * 100.0f));
                                }
                                if (maxSize > 0 && f > maxSize) {
                                    if (p_180192_4_ != null) {
                                        p_180192_4_.setDoneWorking();
                                    }
                                    throw new IOException("Filesize was bigger than maximum allowed (got >= " + f + ", limit was " + maxSize + ")");
                                }
                                if (Thread.interrupted()) {
                                    HttpUtil.logger.error("INTERRUPTED");
                                    if (p_180192_4_ != null) {
                                        p_180192_4_.setDoneWorking();
                                    }
                                    return;
                                }
                                outputstream.write(abyte, 0, k);
                            }
                            if (p_180192_4_ != null) {
                                p_180192_4_.setDoneWorking();
                            }
                        }
                        catch (Throwable throwable) {
                            throwable.printStackTrace();
                            if (httpurlconnection != null) {
                                final InputStream inputstream2 = httpurlconnection.getErrorStream();
                                try {
                                    HttpUtil.logger.error(IOUtils.toString(inputstream2));
                                }
                                catch (IOException ioexception) {
                                    ioexception.printStackTrace();
                                }
                            }
                            if (p_180192_4_ != null) {
                                p_180192_4_.setDoneWorking();
                            }
                        }
                        finally {
                            IOUtils.closeQuietly(inputstream);
                            IOUtils.closeQuietly(outputstream);
                        }
                        return;
                    }
                    continue;
                }
            }
        });
        return (ListenableFuture<Object>)listenablefuture;
    }
    
    public static int getSuitableLanPort() throws IOException {
        ServerSocket serversocket = null;
        int i = -1;
        try {
            serversocket = new ServerSocket(0);
            i = serversocket.getLocalPort();
        }
        finally {
            try {
                if (serversocket != null) {
                    serversocket.close();
                }
            }
            catch (IOException ex) {}
        }
        return i;
    }
    
    public static String get(final URL url) throws IOException {
        final HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
        httpurlconnection.setRequestMethod("GET");
        final BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
        final StringBuilder stringbuilder = new StringBuilder();
        String s;
        while ((s = bufferedreader.readLine()) != null) {
            stringbuilder.append(s);
            stringbuilder.append('\r');
        }
        bufferedreader.close();
        return stringbuilder.toString();
    }
    
    static {
        field_180193_a = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Downloader %d").build()));
        downloadThreadsStarted = new AtomicInteger(0);
        logger = LogManager.getLogger();
    }
}
