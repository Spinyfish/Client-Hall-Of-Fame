package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ClassUtils.Interfaces;

public class ClassInheratanceMultiMap extends AbstractSet
{
    private final Multimap map = HashMultimap.create();
    private final Set knownKeys = Sets.newIdentityHashSet();
    private final Class baseClass;

    public ClassInheratanceMultiMap(Class baseClassIn)
    {
        this.baseClass = baseClassIn;
        this.knownKeys.add(baseClassIn);
    }

    public void createLookup(Class clazz)
    {
        Iterator var2 = this.map.get(this.getLookupKey(clazz, false)).iterator();

        while (var2.hasNext())
        {
            Object var3 = var2.next();

            if (clazz.isAssignableFrom(var3.getClass()))
            {
                this.map.put(clazz, var3);
            }
        }

        this.knownKeys.add(clazz);
    }

    protected Class getLookupKey(Class clazz, boolean create)
    {
        Iterator var3 = ClassUtils.hierarchy(clazz, Interfaces.INCLUDE).iterator();
        Class var4;

        do
        {
            if (!var3.hasNext())
            {
                throw new IllegalArgumentException("Don\'t know how to search for " + clazz);
            }

            var4 = (Class)var3.next();
        }
        while (!this.knownKeys.contains(var4));

        if (var4 == this.baseClass && create)
        {
            this.createLookup(clazz);
        }

        return var4;
    }

    public boolean add(Object p_add_1_)
    {
        Iterator var2 = this.knownKeys.iterator();

        while (var2.hasNext())
        {
            Class var3 = (Class)var2.next();

            if (var3.isAssignableFrom(p_add_1_.getClass()))
            {
                this.map.put(var3, p_add_1_);
            }
        }

        return true;
    }

    public boolean remove(Object p_remove_1_)
    {
        Object var2 = p_remove_1_;
        boolean var3 = false;
        Iterator var4 = this.knownKeys.iterator();

        while (var4.hasNext())
        {
            Class var5 = (Class)var4.next();

            if (var5.isAssignableFrom(var2.getClass()))
            {
                var3 |= this.map.remove(var5, var2);
            }
        }

        return var3;
    }

    public Iterable getByClass(final Class clazz)
    {
        return new Iterable()
        {
            public Iterator iterator()
            {
                Iterator var1 = ClassInheratanceMultiMap.this.map.get(ClassInheratanceMultiMap.this.getLookupKey(clazz, true)).iterator();
                return Iterators.filter(var1, clazz);
            }
        };
    }

    public Iterator iterator()
    {
        final Iterator var1 = this.map.get(this.baseClass).iterator();
        return new AbstractIterator()
        {
            protected Object computeNext()
            {
                return !var1.hasNext() ? this.endOfData() : var1.next();
            }
        };
    }

    public int size()
    {
        return this.map.get(this.baseClass).size();
    }
}
