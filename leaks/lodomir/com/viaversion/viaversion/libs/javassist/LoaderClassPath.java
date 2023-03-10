/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist;

import com.viaversion.viaversion.libs.javassist.ClassPath;
import com.viaversion.viaversion.libs.javassist.NotFoundException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.URL;

public class LoaderClassPath
implements ClassPath {
    private Reference<ClassLoader> clref;

    public LoaderClassPath(ClassLoader cl) {
        this.clref = new WeakReference<ClassLoader>(cl);
    }

    public String toString() {
        return this.clref.get() == null ? "<null>" : this.clref.get().toString();
    }

    @Override
    public InputStream openClassfile(String classname) throws NotFoundException {
        String cname = classname.replace('.', '/') + ".class";
        ClassLoader cl = this.clref.get();
        if (cl == null) {
            return null;
        }
        InputStream is = cl.getResourceAsStream(cname);
        return is;
    }

    @Override
    public URL find(String classname) {
        String cname = classname.replace('.', '/') + ".class";
        ClassLoader cl = this.clref.get();
        if (cl == null) {
            return null;
        }
        URL url = cl.getResource(cname);
        return url;
    }
}

