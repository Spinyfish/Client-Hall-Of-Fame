package net.optifine.http;

import java.util.Map;

public class FileUploadThread extends Thread {
    private final String urlString;
    private final Map headers;
    private final byte[] content;
    private final IFileUploadListener listener;

    public FileUploadThread(final String urlString, final Map headers, final byte[] content, final IFileUploadListener listener) {
        this.urlString = urlString;
        this.headers = headers;
        this.content = content;
        this.listener = listener;
    }

    public void run() {
        try {
            HttpUtils.post(this.urlString, this.headers, this.content);
            this.listener.fileUploadFinished(this.urlString, this.content, null);
        } catch (final Exception exception) {
            this.listener.fileUploadFinished(this.urlString, this.content, exception);
        }
    }

    public String getUrlString() {
        return this.urlString;
    }

    public byte[] getContent() {
        return this.content;
    }

    public IFileUploadListener getListener() {
        return this.listener;
    }
}
