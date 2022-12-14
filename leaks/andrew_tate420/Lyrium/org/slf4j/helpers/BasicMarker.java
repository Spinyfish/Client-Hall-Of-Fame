// 
// Decompiled by Procyon v0.5.36
// 

package org.slf4j.helpers;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import org.slf4j.Marker;

public class BasicMarker implements Marker
{
    private static final long serialVersionUID = -2849567615646933777L;
    private final String name;
    private final List<Marker> referenceList;
    private static final String OPEN = "[ ";
    private static final String CLOSE = " ]";
    private static final String SEP = ", ";
    
    BasicMarker(final String name) {
        this.referenceList = new CopyOnWriteArrayList<Marker>();
        if (name == null) {
            throw new IllegalArgumentException("A marker name cannot be null");
        }
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void add(final Marker reference) {
        if (reference == null) {
            throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
        }
        if (this.contains(reference)) {
            return;
        }
        if (reference.contains(this)) {
            return;
        }
        this.referenceList.add(reference);
    }
    
    @Override
    public boolean hasReferences() {
        return this.referenceList.size() > 0;
    }
    
    @Deprecated
    @Override
    public boolean hasChildren() {
        return this.hasReferences();
    }
    
    @Override
    public Iterator<Marker> iterator() {
        return this.referenceList.iterator();
    }
    
    @Override
    public boolean remove(final Marker referenceToRemove) {
        return this.referenceList.remove(referenceToRemove);
    }
    
    @Override
    public boolean contains(final Marker other) {
        if (other == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }
        if (this.equals(other)) {
            return true;
        }
        if (this.hasReferences()) {
            for (final Marker ref : this.referenceList) {
                if (ref.contains(other)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean contains(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }
        if (this.name.equals(name)) {
            return true;
        }
        if (this.hasReferences()) {
            for (final Marker ref : this.referenceList) {
                if (ref.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Marker)) {
            return false;
        }
        final Marker other = (Marker)obj;
        return this.name.equals(other.getName());
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String toString() {
        if (!this.hasReferences()) {
            return this.getName();
        }
        final Iterator<Marker> it = this.iterator();
        final StringBuilder sb = new StringBuilder(this.getName());
        sb.append(' ').append("[ ");
        while (it.hasNext()) {
            final Marker reference = it.next();
            sb.append(reference.getName());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" ]");
        return sb.toString();
    }
}
