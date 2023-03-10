/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import java.util.Set;

public interface Cookie
extends Comparable<Cookie> {
    public String getName();

    public String getValue();

    public void setValue(String var1);

    public String getDomain();

    public void setDomain(String var1);

    public String getPath();

    public void setPath(String var1);

    public String getComment();

    public void setComment(String var1);

    public long getMaxAge();

    public void setMaxAge(long var1);

    public int getVersion();

    public void setVersion(int var1);

    public boolean isSecure();

    public void setSecure(boolean var1);

    public boolean isHttpOnly();

    public void setHttpOnly(boolean var1);

    public String getCommentUrl();

    public void setCommentUrl(String var1);

    public boolean isDiscard();

    public void setDiscard(boolean var1);

    public Set<Integer> getPorts();

    public void setPorts(int ... var1);

    public void setPorts(Iterable<Integer> var1);
}

