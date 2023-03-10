/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.nio.charset.Charset;

public interface HttpDataFactory {
    public Attribute createAttribute(HttpRequest var1, String var2);

    public Attribute createAttribute(HttpRequest var1, String var2, String var3);

    public FileUpload createFileUpload(HttpRequest var1, String var2, String var3, String var4, String var5, Charset var6, long var7);

    public void removeHttpDataFromClean(HttpRequest var1, InterfaceHttpData var2);

    public void cleanRequestHttpDatas(HttpRequest var1);

    public void cleanAllHttpDatas();
}

