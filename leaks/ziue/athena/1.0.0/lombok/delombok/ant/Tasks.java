package lombok.delombok.ant;

import java.io.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.*;
import java.lang.reflect.*;
import java.util.*;

class Tasks
{
    public static class Format
    {
        private String value;
        
        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + ((this.value == null) ? 0 : this.value.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            final Format other = (Format)obj;
            if (this.value == null) {
                if (other.value != null) {
                    return false;
                }
            }
            else if (!this.value.equals(other.value)) {
                return false;
            }
            return true;
        }
        
        @Override
        public String toString() {
            return "FormatOption [value=" + this.value + "]";
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
    }
    
    public static class Delombok extends Task
    {
        private File fromDir;
        private File toDir;
        private Path classpath;
        private Path sourcepath;
        private Path modulepath;
        private boolean verbose;
        private String encoding;
        private Path path;
        private List<Format> formatOptions;
        private static ClassLoader shadowLoader;
        
        public Delombok() {
            this.formatOptions = new ArrayList<Format>();
        }
        
        public void setClasspath(final Path classpath) {
            if (this.classpath == null) {
                this.classpath = classpath;
            }
            else {
                this.classpath.append(classpath);
            }
        }
        
        public Path createClasspath() {
            if (this.classpath == null) {
                this.classpath = new Path(this.getProject());
            }
            return this.classpath.createPath();
        }
        
        public void setClasspathRef(final Reference r) {
            this.createClasspath().setRefid(r);
        }
        
        public void setSourcepath(final Path sourcepath) {
            if (this.sourcepath == null) {
                this.sourcepath = sourcepath;
            }
            else {
                this.sourcepath.append(sourcepath);
            }
        }
        
        public Path createSourcepath() {
            if (this.sourcepath == null) {
                this.sourcepath = new Path(this.getProject());
            }
            return this.sourcepath.createPath();
        }
        
        public void setSourcepathRef(final Reference r) {
            this.createSourcepath().setRefid(r);
        }
        
        public void setModulepath(final Path modulepath) {
            if (this.modulepath == null) {
                this.modulepath = modulepath;
            }
            else {
                this.modulepath.append(modulepath);
            }
        }
        
        public Path createModulepath() {
            if (this.modulepath == null) {
                this.modulepath = new Path(this.getProject());
            }
            return this.modulepath.createPath();
        }
        
        public void setModulepathRef(final Reference r) {
            this.createModulepath().setRefid(r);
        }
        
        public void setFrom(final File dir) {
            this.fromDir = dir;
        }
        
        public void setTo(final File dir) {
            this.toDir = dir;
        }
        
        public void setVerbose(final boolean verbose) {
            this.verbose = verbose;
        }
        
        public void setEncoding(final String encoding) {
            this.encoding = encoding;
        }
        
        public void addFileset(final FileSet set) {
            if (this.path == null) {
                this.path = new Path(this.getProject());
            }
            this.path.add((ResourceCollection)set);
        }
        
        public void addFormat(final Format format) {
            this.formatOptions.add(format);
        }
        
        public static Class<?> shadowLoadClass(final String name) {
            try {
                if (Delombok.shadowLoader == null) {
                    try {
                        Class.forName("lombok.core.LombokNode");
                        Delombok.shadowLoader = Delombok.class.getClassLoader();
                    }
                    catch (ClassNotFoundException ex) {
                        final Class<?> launcherMain = Class.forName("lombok.launch.Main");
                        final Method m = launcherMain.getDeclaredMethod("getShadowClassLoader", (Class<?>[])new Class[0]);
                        m.setAccessible(true);
                        Delombok.shadowLoader = (ClassLoader)m.invoke(null, new Object[0]);
                    }
                }
                return Class.forName(name, true, Delombok.shadowLoader);
            }
            catch (Throwable t) {
                if (t instanceof InvocationTargetException) {
                    t = t.getCause();
                }
                if (t instanceof RuntimeException) {
                    throw (RuntimeException)t;
                }
                if (t instanceof Error) {
                    throw (Error)t;
                }
                throw new RuntimeException(t);
            }
        }
        
        public void execute() throws BuildException {
            final Location loc = this.getLocation();
            try {
                final Object instance = shadowLoadClass("lombok.delombok.ant.DelombokTaskImpl").getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                Field[] declaredFields;
                for (int length = (declaredFields = this.getClass().getDeclaredFields()).length, i = 0; i < length; ++i) {
                    final Field selfField = declaredFields[i];
                    if (!selfField.isSynthetic()) {
                        if (!Modifier.isStatic(selfField.getModifiers())) {
                            final Field otherField = instance.getClass().getDeclaredField(selfField.getName());
                            otherField.setAccessible(true);
                            if (selfField.getName().equals("formatOptions")) {
                                final List<String> rep = new ArrayList<String>();
                                for (final Format f : this.formatOptions) {
                                    if (f.getValue() == null) {
                                        throw new BuildException("'value' property required for <format>");
                                    }
                                    rep.add(f.getValue());
                                }
                                otherField.set(instance, rep);
                            }
                            else {
                                otherField.set(instance, selfField.get(this));
                            }
                        }
                    }
                }
                final Method m = instance.getClass().getMethod("execute", Location.class);
                m.invoke(instance, loc);
            }
            catch (Throwable t) {
                if (t instanceof InvocationTargetException) {
                    t = t.getCause();
                }
                if (t instanceof RuntimeException) {
                    throw (RuntimeException)t;
                }
                if (t instanceof Error) {
                    throw (Error)t;
                }
                throw new RuntimeException(t);
            }
        }
    }
}
