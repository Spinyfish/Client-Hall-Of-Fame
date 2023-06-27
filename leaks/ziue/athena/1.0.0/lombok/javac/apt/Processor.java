package lombok.javac.apt;

import com.sun.tools.javac.util.*;
import lombok.permit.*;
import java.lang.reflect.*;
import com.sun.tools.javac.processing.*;
import javax.tools.*;
import java.net.*;
import java.io.*;
import javax.lang.model.*;
import java.util.*;
import javax.lang.model.element.*;
import javax.annotation.processing.*;

@Deprecated
@SupportedAnnotationTypes({ "*" })
public class Processor extends AbstractProcessor
{
    @Override
    public void init(final ProcessingEnvironment procEnv) {
        super.init(procEnv);
        if (System.getProperty("lombok.disable") != null) {
            return;
        }
        procEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Wrong usage of 'lombok.javac.apt.Processor'. " + this.report(procEnv));
    }
    
    private String report(final ProcessingEnvironment procEnv) {
        final String data = this.collectData(procEnv);
        try {
            return this.writeFile(data);
        }
        catch (Exception ex) {
            return "Report:\n\n" + data;
        }
    }
    
    private String writeFile(final String data) throws IOException {
        final File file = File.createTempFile("lombok-processor-report-", ".txt");
        final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(data);
        writer.close();
        return "Report written to '" + file.getCanonicalPath() + "'\n";
    }
    
    private String collectData(final ProcessingEnvironment procEnv) {
        final StringBuilder message = new StringBuilder();
        message.append("Problem report for usages of 'lombok.javac.apt.Processor'\n\n");
        this.listOptions(message, procEnv);
        this.findServices(message, procEnv.getFiler());
        this.addStacktrace(message);
        this.listProperties(message);
        return message.toString();
    }
    
    private void listOptions(final StringBuilder message, final ProcessingEnvironment procEnv) {
        try {
            final JavacProcessingEnvironment environment = (JavacProcessingEnvironment)procEnv;
            final Options instance = Options.instance(environment.getContext());
            final Field field = Permit.getField((Class)Options.class, "values");
            final Map<String, String> values = (Map<String, String>)field.get(instance);
            if (values.isEmpty()) {
                message.append("Options: empty\n\n");
                return;
            }
            message.append("Compiler Options:\n");
            for (final Map.Entry<String, String> value : values.entrySet()) {
                message.append("- ");
                string(message, value.getKey());
                message.append(" = ");
                string(message, value.getValue());
                message.append("\n");
            }
            message.append("\n");
        }
        catch (Exception ex) {
            message.append("No options available\n\n");
        }
    }
    
    private void findServices(final StringBuilder message, final Filer filer) {
        try {
            final Field filerFileManagerField = Permit.getField((Class)JavacFiler.class, "fileManager");
            final JavaFileManager jfm = (JavaFileManager)filerFileManagerField.get(filer);
            final ClassLoader processorClassLoader = jfm.hasLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH) ? jfm.getClassLoader(StandardLocation.ANNOTATION_PROCESSOR_PATH) : jfm.getClassLoader(StandardLocation.CLASS_PATH);
            final Enumeration<URL> resources = processorClassLoader.getResources("META-INF/services/javax.annotation.processing.Processor");
            if (!resources.hasMoreElements()) {
                message.append("No processors discovered\n\n");
                return;
            }
            message.append("Discovered processors:\n");
            while (resources.hasMoreElements()) {
                final URL processorUrl = resources.nextElement();
                message.append("- '").append(processorUrl).append("'");
                final InputStream content = (InputStream)processorUrl.getContent();
                if (content != null) {
                    try {
                        final InputStreamReader reader = new InputStreamReader(content, "UTF-8");
                        final StringWriter sw = new StringWriter();
                        final char[] buffer = new char[8192];
                        int read = 0;
                        while ((read = reader.read(buffer)) != -1) {
                            sw.write(buffer, 0, read);
                        }
                        final String wholeFile = sw.toString();
                        if (wholeFile.contains("lombok.javac.apt.Processor")) {
                            message.append(" <= problem\n");
                        }
                        else {
                            message.append(" (ok)\n");
                        }
                        message.append("    ").append(wholeFile.replace("\n", "\n    ")).append("\n");
                    }
                    finally {
                        content.close();
                    }
                    content.close();
                }
            }
        }
        catch (Exception ex) {
            message.append("Filer information unavailable\n");
        }
        message.append("\n");
    }
    
    private void addStacktrace(final StringBuilder message) {
        final StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        if (stackTraceElements != null) {
            message.append("Called from\n");
            for (int i = 1; i < stackTraceElements.length; ++i) {
                final StackTraceElement element = stackTraceElements[i];
                if (!element.getClassName().equals("lombok.javac.apt.Processor")) {
                    message.append("- ").append(element).append("\n");
                }
            }
        }
        else {
            message.append("No stacktrace available\n");
        }
        message.append("\n");
    }
    
    private void listProperties(final StringBuilder message) {
        final Properties properties = System.getProperties();
        final ArrayList<String> propertyNames = new ArrayList<String>(properties.stringPropertyNames());
        Collections.sort(propertyNames);
        message.append("Properties: \n");
        for (final String propertyName : propertyNames) {
            if (propertyName.startsWith("user.")) {
                continue;
            }
            message.append("- ").append(propertyName).append(" = ");
            string(message, System.getProperty(propertyName));
            message.append("\n");
        }
        message.append("\n");
    }
    
    private static void string(final StringBuilder sb, final String s) {
        if (s == null) {
            sb.append("null");
            return;
        }
        sb.append("\"");
        for (int i = 0; i < s.length(); ++i) {
            sb.append(escape(s.charAt(i)));
        }
        sb.append("\"");
    }
    
    private static String escape(final char ch) {
        switch (ch) {
            case '\b': {
                return "\\b";
            }
            case '\f': {
                return "\\f";
            }
            case '\n': {
                return "\\n";
            }
            case '\r': {
                return "\\r";
            }
            case '\t': {
                return "\\t";
            }
            case '\'': {
                return "\\'";
            }
            case '\"': {
                return "\\\"";
            }
            case '\\': {
                return "\\\\";
            }
            default: {
                if (ch < ' ') {
                    return String.format("\\%03o", (int)ch);
                }
                return String.valueOf(ch);
            }
        }
    }
    
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
    
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        return false;
    }
}
