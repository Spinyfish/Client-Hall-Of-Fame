package org.json;

import java.io.*;
import java.math.*;
import java.util.*;

public class JSONWriter
{
    private static final int maxdepth = 200;
    private boolean comma;
    protected char mode;
    private final JSONObject[] stack;
    private int top;
    protected Appendable writer;
    
    public JSONWriter(final Appendable w) {
        this.comma = false;
        this.mode = 'i';
        this.stack = new JSONObject[200];
        this.top = 0;
        this.writer = w;
    }
    
    private JSONWriter append(final String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null pointer");
        }
        if (this.mode != 'o') {
            if (this.mode != 'a') {
                throw new JSONException("Value out of sequence.");
            }
        }
        try {
            if (this.comma && this.mode == 'a') {
                this.writer.append(',');
            }
            this.writer.append(string);
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
        if (this.mode == 'o') {
            this.mode = 'k';
        }
        this.comma = true;
        return this;
    }
    
    public JSONWriter array() throws JSONException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            this.push(null);
            this.append("[");
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced array.");
    }
    
    private JSONWriter end(final char m, final char c) throws JSONException {
        if (this.mode != m) {
            throw new JSONException((m == 'a') ? "Misplaced endArray." : "Misplaced endObject.");
        }
        this.pop(m);
        try {
            this.writer.append(c);
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
        this.comma = true;
        return this;
    }
    
    public JSONWriter endArray() throws JSONException {
        return this.end('a', ']');
    }
    
    public JSONWriter endObject() throws JSONException {
        return this.end('k', '}');
    }
    
    public JSONWriter key(final String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null key.");
        }
        if (this.mode == 'k') {
            try {
                final JSONObject topObject = this.stack[this.top - 1];
                if (topObject.has(string)) {
                    throw new JSONException("Duplicate key \"" + string + "\"");
                }
                topObject.put(string, true);
                if (this.comma) {
                    this.writer.append(',');
                }
                this.writer.append(JSONObject.quote(string));
                this.writer.append(':');
                this.comma = false;
                this.mode = 'o';
                return this;
            }
            catch (IOException e) {
                throw new JSONException(e);
            }
        }
        throw new JSONException("Misplaced key.");
    }
    
    public JSONWriter object() throws JSONException {
        if (this.mode == 'i') {
            this.mode = 'o';
        }
        if (this.mode == 'o' || this.mode == 'a') {
            this.append("{");
            this.push(new JSONObject());
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced object.");
    }
    
    private void pop(final char c) throws JSONException {
        if (this.top <= 0) {
            throw new JSONException("Nesting error.");
        }
        final char m = (this.stack[this.top - 1] == null) ? 'a' : 'k';
        if (m != c) {
            throw new JSONException("Nesting error.");
        }
        --this.top;
        this.mode = ((this.top == 0) ? 'd' : ((this.stack[this.top - 1] == null) ? 'a' : 'k'));
    }
    
    private void push(final JSONObject jo) throws JSONException {
        if (this.top >= 200) {
            throw new JSONException("Nesting too deep.");
        }
        this.stack[this.top] = jo;
        this.mode = ((jo == null) ? 'a' : 'k');
        ++this.top;
    }
    
    public static String valueToString(final Object value) throws JSONException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof JSONString) {
            Object object;
            try {
                object = ((JSONString)value).toJSONString();
            }
            catch (Exception e) {
                throw new JSONException(e);
            }
            if (object instanceof String) {
                return (String)object;
            }
            throw new JSONException("Bad value from toJSONString: " + object);
        }
        else {
            if (value instanceof Number) {
                final String numberAsString = JSONObject.numberToString((Number)value);
                try {
                    final BigDecimal unused = new BigDecimal(numberAsString);
                    return numberAsString;
                }
                catch (NumberFormatException ex) {
                    return JSONObject.quote(numberAsString);
                }
            }
            if (value instanceof Boolean || value instanceof JSONObject || value instanceof JSONArray) {
                return value.toString();
            }
            if (value instanceof Map) {
                final Map<?, ?> map = (Map<?, ?>)value;
                return new JSONObject(map).toString();
            }
            if (value instanceof Collection) {
                final Collection<?> coll = (Collection<?>)value;
                return new JSONArray(coll).toString();
            }
            if (value.getClass().isArray()) {
                return new JSONArray(value).toString();
            }
            if (value instanceof Enum) {
                return JSONObject.quote(((Enum)value).name());
            }
            return JSONObject.quote(value.toString());
        }
    }
    
    public JSONWriter value(final boolean b) throws JSONException {
        return this.append(b ? "true" : "false");
    }
    
    public JSONWriter value(final double d) throws JSONException {
        return this.value((Object)d);
    }
    
    public JSONWriter value(final long l) throws JSONException {
        return this.append(Long.toString(l));
    }
    
    public JSONWriter value(final Object object) throws JSONException {
        return this.append(valueToString(object));
    }
}
