/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.libs.javassist.tools.rmi;

import com.viaversion.viaversion.libs.javassist.CannotCompileException;
import com.viaversion.viaversion.libs.javassist.ClassPool;
import com.viaversion.viaversion.libs.javassist.NotFoundException;
import com.viaversion.viaversion.libs.javassist.tools.rmi.ExportedObject;
import com.viaversion.viaversion.libs.javassist.tools.rmi.RemoteRef;
import com.viaversion.viaversion.libs.javassist.tools.rmi.StubGenerator;
import com.viaversion.viaversion.libs.javassist.tools.web.BadHttpRequest;
import com.viaversion.viaversion.libs.javassist.tools.web.Webserver;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AppletServer
extends Webserver {
    private StubGenerator stubGen;
    private Map<String, ExportedObject> exportedNames = new Hashtable<String, ExportedObject>();
    private List<ExportedObject> exportedObjects = new Vector<ExportedObject>();
    private static final byte[] okHeader = "HTTP/1.0 200 OK\r\n\r\n".getBytes();

    public AppletServer(String port) throws IOException, NotFoundException, CannotCompileException {
        this(Integer.parseInt(port));
    }

    public AppletServer(int port) throws IOException, NotFoundException, CannotCompileException {
        this(ClassPool.getDefault(), new StubGenerator(), port);
    }

    public AppletServer(int port, ClassPool src) throws IOException, NotFoundException, CannotCompileException {
        this(new ClassPool(src), new StubGenerator(), port);
    }

    private AppletServer(ClassPool loader, StubGenerator gen, int port) throws IOException, NotFoundException, CannotCompileException {
        super(port);
        this.stubGen = gen;
        this.addTranslator(loader, gen);
    }

    @Override
    public void run() {
        super.run();
    }

    public synchronized int exportObject(String name, Object obj) throws CannotCompileException {
        Class<?> clazz = obj.getClass();
        ExportedObject eo = new ExportedObject();
        eo.object = obj;
        eo.methods = clazz.getMethods();
        this.exportedObjects.add(eo);
        eo.identifier = this.exportedObjects.size() - 1;
        if (name != null) {
            this.exportedNames.put(name, eo);
        }
        try {
            this.stubGen.makeProxyClass(clazz);
        }
        catch (NotFoundException e) {
            throw new CannotCompileException(e);
        }
        return eo.identifier;
    }

    @Override
    public void doReply(InputStream in, OutputStream out, String cmd) throws IOException, BadHttpRequest {
        if (cmd.startsWith("POST /rmi ")) {
            this.processRMI(in, out);
        } else if (cmd.startsWith("POST /lookup ")) {
            this.lookupName(cmd, in, out);
        } else {
            super.doReply(in, out, cmd);
        }
    }

    private void processRMI(InputStream ins, OutputStream outs) throws IOException {
        ObjectInputStream in = new ObjectInputStream(ins);
        int objectId = in.readInt();
        int methodId = in.readInt();
        Exception err = null;
        Object rvalue = null;
        try {
            ExportedObject eo = this.exportedObjects.get(objectId);
            Object[] args = this.readParameters(in);
            rvalue = this.convertRvalue(eo.methods[methodId].invoke(eo.object, args));
        }
        catch (Exception e) {
            err = e;
            this.logging2(e.toString());
        }
        outs.write(okHeader);
        ObjectOutputStream out = new ObjectOutputStream(outs);
        if (err != null) {
            out.writeBoolean(false);
            out.writeUTF(err.toString());
        } else {
            try {
                out.writeBoolean(true);
                out.writeObject(rvalue);
            }
            catch (NotSerializableException e) {
                this.logging2(e.toString());
            }
            catch (InvalidClassException e) {
                this.logging2(e.toString());
            }
        }
        out.flush();
        out.close();
        in.close();
    }

    private Object[] readParameters(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int n = in.readInt();
        Object[] args = new Object[n];
        for (int i = 0; i < n; ++i) {
            Object a = in.readObject();
            if (a instanceof RemoteRef) {
                RemoteRef ref = (RemoteRef)a;
                ExportedObject eo = this.exportedObjects.get(ref.oid);
                a = eo.object;
            }
            args[i] = a;
        }
        return args;
    }

    private Object convertRvalue(Object rvalue) throws CannotCompileException {
        if (rvalue == null) {
            return null;
        }
        String classname = rvalue.getClass().getName();
        if (this.stubGen.isProxyClass(classname)) {
            return new RemoteRef(this.exportObject(null, rvalue), classname);
        }
        return rvalue;
    }

    private void lookupName(String cmd, InputStream ins, OutputStream outs) throws IOException {
        ObjectInputStream in = new ObjectInputStream(ins);
        String name = DataInputStream.readUTF(in);
        ExportedObject found = this.exportedNames.get(name);
        outs.write(okHeader);
        ObjectOutputStream out = new ObjectOutputStream(outs);
        if (found == null) {
            this.logging2(name + "not found.");
            out.writeInt(-1);
            out.writeUTF("error");
        } else {
            this.logging2(name);
            out.writeInt(found.identifier);
            out.writeUTF(found.object.getClass().getName());
        }
        out.flush();
        out.close();
        in.close();
    }
}

