/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jna;

import com.sun.jna.AltCallingConvention;
import com.sun.jna.Function;
import com.sun.jna.InvocationMapper;
import com.sun.jna.NativeLibrary;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public interface Library {
    public static final String OPTION_TYPE_MAPPER = "type-mapper";
    public static final String OPTION_FUNCTION_MAPPER = "function-mapper";
    public static final String OPTION_INVOCATION_MAPPER = "invocation-mapper";
    public static final String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
    public static final String OPTION_STRING_ENCODING = "string-encoding";
    public static final String OPTION_ALLOW_OBJECTS = "allow-objects";
    public static final String OPTION_CALLING_CONVENTION = "calling-convention";
    public static final String OPTION_OPEN_FLAGS = "open-flags";
    public static final String OPTION_CLASSLOADER = "classloader";

    public static class Handler
    implements InvocationHandler {
        static final Method OBJECT_TOSTRING;
        static final Method OBJECT_HASHCODE;
        static final Method OBJECT_EQUALS;
        private final NativeLibrary nativeLibrary;
        private final Class<?> interfaceClass;
        private final Map<String, Object> options;
        private final InvocationMapper invocationMapper;
        private final Map<Method, FunctionInfo> functions = new WeakHashMap<Method, FunctionInfo>();

        public Handler(String libname, Class<?> interfaceClass, Map<String, ?> options) {
            int callingConvention;
            if (libname != null && "".equals(libname.trim())) {
                throw new IllegalArgumentException("Invalid library name \"" + libname + "\"");
            }
            if (!interfaceClass.isInterface()) {
                throw new IllegalArgumentException(libname + " does not implement an interface: " + interfaceClass.getName());
            }
            this.interfaceClass = interfaceClass;
            this.options = new HashMap(options);
            int n = callingConvention = AltCallingConvention.class.isAssignableFrom(interfaceClass) ? 63 : 0;
            if (this.options.get(Library.OPTION_CALLING_CONVENTION) == null) {
                this.options.put(Library.OPTION_CALLING_CONVENTION, callingConvention);
            }
            if (this.options.get(Library.OPTION_CLASSLOADER) == null) {
                this.options.put(Library.OPTION_CLASSLOADER, interfaceClass.getClassLoader());
            }
            this.nativeLibrary = NativeLibrary.getInstance(libname, this.options);
            this.invocationMapper = (InvocationMapper)this.options.get(Library.OPTION_INVOCATION_MAPPER);
        }

        public NativeLibrary getNativeLibrary() {
            return this.nativeLibrary;
        }

        public String getLibraryName() {
            return this.nativeLibrary.getName();
        }

        public Class<?> getInterfaceClass() {
            return this.interfaceClass;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] inArgs) throws Throwable {
            if (OBJECT_TOSTRING.equals(method)) {
                return "Proxy interface to " + this.nativeLibrary;
            }
            if (OBJECT_HASHCODE.equals(method)) {
                return this.hashCode();
            }
            if (OBJECT_EQUALS.equals(method)) {
                Object o = inArgs[0];
                if (o != null && Proxy.isProxyClass(o.getClass())) {
                    return Function.valueOf(Proxy.getInvocationHandler(o) == this);
                }
                return Boolean.FALSE;
            }
            FunctionInfo f = this.functions.get(method);
            if (f == null) {
                Map<Method, FunctionInfo> map = this.functions;
                synchronized (map) {
                    f = this.functions.get(method);
                    if (f == null) {
                        boolean isVarArgs = Function.isVarArgs(method);
                        InvocationHandler handler = null;
                        if (this.invocationMapper != null) {
                            handler = this.invocationMapper.getInvocationHandler(this.nativeLibrary, method);
                        }
                        Function function = null;
                        Class<?>[] parameterTypes = null;
                        HashMap<String, Object> options = null;
                        if (handler == null) {
                            function = this.nativeLibrary.getFunction(method.getName(), method);
                            parameterTypes = method.getParameterTypes();
                            options = new HashMap<String, Object>(this.options);
                            options.put("invoking-method", method);
                        }
                        f = new FunctionInfo(handler, function, parameterTypes, isVarArgs, options);
                        this.functions.put(method, f);
                    }
                }
            }
            if (f.isVarArgs) {
                inArgs = Function.concatenateVarArgs(inArgs);
            }
            if (f.handler != null) {
                return f.handler.invoke(proxy, method, inArgs);
            }
            return f.function.invoke(method, f.parameterTypes, method.getReturnType(), inArgs, f.options);
        }

        static {
            try {
                OBJECT_TOSTRING = Object.class.getMethod("toString", new Class[0]);
                OBJECT_HASHCODE = Object.class.getMethod("hashCode", new Class[0]);
                OBJECT_EQUALS = Object.class.getMethod("equals", Object.class);
            }
            catch (Exception e) {
                throw new Error("Error retrieving Object.toString() method");
            }
        }

        private static final class FunctionInfo {
            final InvocationHandler handler;
            final Function function;
            final boolean isVarArgs;
            final Map<String, ?> options;
            final Class<?>[] parameterTypes;

            FunctionInfo(InvocationHandler handler, Function function, Class<?>[] parameterTypes, boolean isVarArgs, Map<String, ?> options) {
                this.handler = handler;
                this.function = function;
                this.isVarArgs = isVarArgs;
                this.options = options;
                this.parameterTypes = parameterTypes;
            }
        }
    }
}

