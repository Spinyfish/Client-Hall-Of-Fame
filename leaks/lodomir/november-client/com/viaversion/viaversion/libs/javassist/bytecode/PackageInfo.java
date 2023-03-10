/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist.bytecode;

import com.viaversion.viaversion.libs.javassist.bytecode.ConstInfo;
import com.viaversion.viaversion.libs.javassist.bytecode.ConstPool;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

class PackageInfo
extends ConstInfo {
    static final int tag = 20;
    int name;

    public PackageInfo(int moduleName, int index) {
        super(index);
        this.name = moduleName;
    }

    public PackageInfo(DataInputStream in, int index) throws IOException {
        super(index);
        this.name = in.readUnsignedShort();
    }

    public int hashCode() {
        return this.name;
    }

    public boolean equals(Object obj) {
        return obj instanceof PackageInfo && ((PackageInfo)obj).name == this.name;
    }

    @Override
    public int getTag() {
        return 20;
    }

    public String getPackageName(ConstPool cp) {
        return cp.getUtf8Info(this.name);
    }

    @Override
    public int copy(ConstPool src, ConstPool dest, Map<String, String> map) {
        String packageName = src.getUtf8Info(this.name);
        int newName = dest.addUtf8Info(packageName);
        return dest.addModuleInfo(newName);
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(20);
        out.writeShort(this.name);
    }

    @Override
    public void print(PrintWriter out) {
        out.print("Package #");
        out.println(this.name);
    }
}

