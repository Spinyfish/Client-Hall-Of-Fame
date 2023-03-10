/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.chmv8;

import io.netty.util.internal.IntegerHolder;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.chmv8.CountedCompleter;
import io.netty.util.internal.chmv8.ForkJoinPool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import sun.misc.Unsafe;

public class ConcurrentHashMapV8<K, V>
implements ConcurrentMap<K, V>,
Serializable {
    private static final long serialVersionUID = 7249069246763182397L;
    private static final int MAXIMUM_CAPACITY = 0x40000000;
    private static final int DEFAULT_CAPACITY = 16;
    static final int MAX_ARRAY_SIZE = 0x7FFFFFF7;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private static final float LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;
    static final int UNTREEIFY_THRESHOLD = 6;
    static final int MIN_TREEIFY_CAPACITY = 64;
    private static final int MIN_TRANSFER_STRIDE = 16;
    static final int MOVED = -1;
    static final int TREEBIN = -2;
    static final int RESERVED = -3;
    static final int HASH_BITS = Integer.MAX_VALUE;
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("segments", Segment[].class), new ObjectStreamField("segmentMask", Integer.TYPE), new ObjectStreamField("segmentShift", Integer.TYPE)};
    volatile transient Node<K, V>[] table;
    private volatile transient Node<K, V>[] nextTable;
    private volatile transient long baseCount;
    private volatile transient int sizeCtl;
    private volatile transient int transferIndex;
    private volatile transient int transferOrigin;
    private volatile transient int cellsBusy;
    private volatile transient CounterCell[] counterCells;
    private transient KeySetView<K, V> keySet;
    private transient ValuesView<K, V> values;
    private transient EntrySetView<K, V> entrySet;
    static final AtomicInteger counterHashCodeGenerator = new AtomicInteger();
    static final int SEED_INCREMENT = 1640531527;
    private static final Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long TRANSFERORIGIN;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;

    static final int spread(int h) {
        return (h ^ h >>> 16) & Integer.MAX_VALUE;
    }

    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        return (n |= n >>> 16) < 0 ? 1 : (n >= 0x40000000 ? 0x40000000 : n + 1);
    }

    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c = x.getClass();
            if (c == String.class) {
                return c;
            }
            Type[] ts = c.getGenericInterfaces();
            if (ts != null) {
                for (int i = 0; i < ts.length; ++i) {
                    Type[] as;
                    ParameterizedType p;
                    Type t = ts[i];
                    if (!(t instanceof ParameterizedType) || (p = (ParameterizedType)t).getRawType() != Comparable.class || (as = p.getActualTypeArguments()) == null || as.length != 1 || as[0] != c) continue;
                    return c;
                }
            }
        }
        return null;
    }

    static int compareComparables(Class<?> kc, Object k, Object x) {
        return x == null || x.getClass() != kc ? 0 : ((Comparable)k).compareTo(x);
    }

    static final <K, V> Node<K, V> tabAt(Node<K, V>[] tab, int i) {
        return (Node)U.getObjectVolatile(tab, ((long)i << ASHIFT) + ABASE);
    }

    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i, Node<K, V> c, Node<K, V> v) {
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }

    static final <K, V> void setTabAt(Node<K, V>[] tab, int i, Node<K, V> v) {
        U.putObjectVolatile(tab, ((long)i << ASHIFT) + ABASE, v);
    }

    public ConcurrentHashMapV8() {
    }

    public ConcurrentHashMapV8(int initialCapacity) {
        int cap;
        if (initialCapacity < 0) {
            throw new IllegalArgumentException();
        }
        this.sizeCtl = cap = initialCapacity >= 0x20000000 ? 0x40000000 : ConcurrentHashMapV8.tableSizeFor(initialCapacity + (initialCapacity >>> 1) + 1);
    }

    public ConcurrentHashMapV8(Map<? extends K, ? extends V> m) {
        this.sizeCtl = 16;
        this.putAll(m);
    }

    public ConcurrentHashMapV8(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 1);
    }

    public ConcurrentHashMapV8(int initialCapacity, float loadFactor, int concurrencyLevel) {
        long size;
        int cap;
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0) {
            throw new IllegalArgumentException();
        }
        if (initialCapacity < concurrencyLevel) {
            initialCapacity = concurrencyLevel;
        }
        this.sizeCtl = cap = (size = (long)(1.0 + (double)((float)initialCapacity / loadFactor))) >= 0x40000000L ? 0x40000000 : ConcurrentHashMapV8.tableSizeFor((int)size);
    }

    @Override
    public int size() {
        long n = this.sumCount();
        return n < 0L ? 0 : (n > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)n);
    }

    @Override
    public boolean isEmpty() {
        return this.sumCount() <= 0L;
    }

    @Override
    public V get(Object key) {
        Node<K, V> e;
        int n;
        int h = ConcurrentHashMapV8.spread(key.hashCode());
        Node<K, V>[] tab = this.table;
        if (this.table != null && (n = tab.length) > 0 && (e = ConcurrentHashMapV8.tabAt(tab, n - 1 & h)) != null) {
            Object ek;
            int eh = e.hash;
            if (eh == h) {
                ek = e.key;
                if (ek == key || ek != null && key.equals(ek)) {
                    return e.val;
                }
            } else if (eh < 0) {
                Node<K, V> p = e.find(h, key);
                return p != null ? (V)p.val : null;
            }
            while ((e = e.next) != null) {
                if (e.hash != h || (ek = e.key) != key && (ek == null || !key.equals(ek))) continue;
                return e.val;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        return this.get(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        Node<K, V>[] t = this.table;
        if (this.table != null) {
            Node<K, V> p;
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            while ((p = it.advance()) != null) {
                Object v = p.val;
                if (v != value && (v == null || !value.equals(v))) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public V put(K key, V value) {
        return this.putVal(key, value, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        int binCount;
        block19: {
            V oldVal;
            int i;
            if (key == null || value == null) {
                throw new NullPointerException();
            }
            int hash = ConcurrentHashMapV8.spread(key.hashCode());
            binCount = 0;
            Node<K, V>[] tab = this.table;
            while (true) {
                int n;
                if (tab == null || (n = tab.length) == 0) {
                    tab = this.initTable();
                    continue;
                }
                i = n - 1 & hash;
                Node<K, V> f = ConcurrentHashMapV8.tabAt(tab, i);
                if (f == null) {
                    if (!ConcurrentHashMapV8.casTabAt(tab, i, null, new Node<K, V>(hash, key, value, null))) continue;
                    break block19;
                }
                int fh = f.hash;
                if (fh == -1) {
                    tab = this.helpTransfer(tab, f);
                    continue;
                }
                oldVal = null;
                Node<K, V> node = f;
                synchronized (node) {
                    block20: {
                        if (ConcurrentHashMapV8.tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                binCount = 1;
                                Node<K, V> e = f;
                                while (true) {
                                    Object ek;
                                    if (e.hash == hash && ((ek = e.key) == key || ek != null && key.equals(ek))) {
                                        oldVal = e.val;
                                        if (!onlyIfAbsent) {
                                            e.val = value;
                                        }
                                        break block20;
                                    }
                                    Node<K, V> pred = e;
                                    e = e.next;
                                    if (e == null) {
                                        pred.next = new Node<K, V>(hash, key, value, null);
                                        break block20;
                                    }
                                    ++binCount;
                                }
                            }
                            if (f instanceof TreeBin) {
                                binCount = 2;
                                TreeNode<K, V> p = ((TreeBin)f).putTreeVal(hash, key, value);
                                if (p != null) {
                                    oldVal = p.val;
                                    if (!onlyIfAbsent) {
                                        p.val = value;
                                    }
                                }
                            }
                        }
                    }
                }
                if (binCount != 0) break;
            }
            if (binCount >= 8) {
                this.treeifyBin(tab, i);
            }
            if (oldVal != null) {
                return oldVal;
            }
        }
        this.addCount(1L, binCount);
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.tryPresize(m.size());
        for (Map.Entry<K, V> e : m.entrySet()) {
            this.putVal(e.getKey(), e.getValue(), false);
        }
    }

    @Override
    public V remove(Object key) {
        return this.replaceNode(key, null, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    final V replaceNode(Object key, V value, Object cv) {
        int i;
        Node<K, V> f;
        int n;
        int hash = ConcurrentHashMapV8.spread(key.hashCode());
        Node<K, V>[] tab = this.table;
        while (tab != null && (n = tab.length) != 0 && (f = ConcurrentHashMapV8.tabAt(tab, i = n - 1 & hash)) != null) {
            int fh = f.hash;
            if (fh == -1) {
                tab = this.helpTransfer(tab, f);
                continue;
            }
            Object oldVal = null;
            boolean validated = false;
            Node<K, V> node = f;
            synchronized (node) {
                if (ConcurrentHashMapV8.tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        validated = true;
                        Node<K, V> e = f;
                        Node<K, V> pred = null;
                        do {
                            Object ek;
                            if (e.hash == hash && ((ek = e.key) == key || ek != null && key.equals(ek))) {
                                Object ev = e.val;
                                if (cv == null || cv == ev || ev != null && cv.equals(ev)) {
                                    oldVal = ev;
                                    if (value != null) {
                                        e.val = value;
                                    } else if (pred != null) {
                                        pred.next = e.next;
                                    } else {
                                        ConcurrentHashMapV8.setTabAt(tab, i, e.next);
                                    }
                                }
                                break;
                            }
                            pred = e;
                        } while ((e = e.next) != null);
                    } else if (f instanceof TreeBin) {
                        TreeNode p;
                        validated = true;
                        TreeBin t = (TreeBin)f;
                        TreeNode r = t.root;
                        if (r != null && (p = r.findTreeNode(hash, key, null)) != null) {
                            Object pv = p.val;
                            if (cv == null || cv == pv || pv != null && cv.equals(pv)) {
                                oldVal = pv;
                                if (value != null) {
                                    p.val = value;
                                } else if (t.removeTreeNode(p)) {
                                    ConcurrentHashMapV8.setTabAt(tab, i, ConcurrentHashMapV8.untreeify(t.first));
                                }
                            }
                        }
                    }
                }
            }
            if (!validated) continue;
            if (oldVal == null) break;
            if (value == null) {
                this.addCount(-1L, -1);
            }
            return (V)oldVal;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        long delta = 0L;
        int i = 0;
        Node<K, V>[] tab = this.table;
        while (tab != null && i < tab.length) {
            Node<K, V> f = ConcurrentHashMapV8.tabAt(tab, i);
            if (f == null) {
                ++i;
                continue;
            }
            int fh = f.hash;
            if (fh == -1) {
                tab = this.helpTransfer(tab, f);
                i = 0;
                continue;
            }
            Node<K, V> node = f;
            synchronized (node) {
                if (ConcurrentHashMapV8.tabAt(tab, i) == f) {
                    Node<K, V> p;
                    Node<K, V> node2 = fh >= 0 ? f : (p = f instanceof TreeBin ? ((TreeBin)f).first : null);
                    while (p != null) {
                        --delta;
                        p = p.next;
                    }
                    ConcurrentHashMapV8.setTabAt(tab, i++, null);
                }
            }
        }
        if (delta != 0L) {
            this.addCount(delta, -1);
        }
    }

    public KeySetView<K, V> keySet() {
        KeySetView<K, V> ks = this.keySet;
        return ks != null ? ks : (this.keySet = new KeySetView(this, null));
    }

    @Override
    public Collection<V> values() {
        ValuesView<K, V> vs = this.values;
        return vs != null ? vs : (this.values = new ValuesView(this));
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        EntrySetView<K, V> es = this.entrySet;
        return es != null ? es : (this.entrySet = new EntrySetView(this));
    }

    @Override
    public int hashCode() {
        int h = 0;
        Node<K, V>[] t = this.table;
        if (this.table != null) {
            Node<K, V> p;
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            while ((p = it.advance()) != null) {
                h += p.key.hashCode() ^ p.val.hashCode();
            }
        }
        return h;
    }

    public String toString() {
        Node<K, V>[] t = this.table;
        int f = this.table == null ? 0 : t.length;
        Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        Node<K, V> p = it.advance();
        if (p != null) {
            while (true) {
                Object k = p.key;
                Object v = p.val;
                sb.append((Object)(k == this ? "(this Map)" : k));
                sb.append('=');
                sb.append((Object)(v == this ? "(this Map)" : v));
                p = it.advance();
                if (p == null) break;
                sb.append(',').append(' ');
            }
        }
        return sb.append('}').toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o != this) {
            Node<K, V> p;
            if (!(o instanceof Map)) {
                return false;
            }
            Map m = (Map)o;
            Node<K, V>[] t = this.table;
            int f = this.table == null ? 0 : t.length;
            Traverser<K, V> it = new Traverser<K, V>(t, f, 0, f);
            while ((p = it.advance()) != null) {
                Object val = p.val;
                Object v = m.get(p.key);
                if (v != null && (v == val || v.equals(val))) continue;
                return false;
            }
            for (Map.Entry e : m.entrySet()) {
                V v;
                Object mv;
                Object mk = e.getKey();
                if (mk != null && (mv = e.getValue()) != null && (v = this.get(mk)) != null && (mv == v || mv.equals(v))) continue;
                return false;
            }
        }
        return true;
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int ssize;
        int sshift = 0;
        for (ssize = 1; ssize < 16; ssize <<= 1) {
            ++sshift;
        }
        int segmentShift = 32 - sshift;
        int segmentMask = ssize - 1;
        Segment[] segments = new Segment[16];
        for (int i = 0; i < segments.length; ++i) {
            segments[i] = new Segment(0.75f);
        }
        s.putFields().put("segments", segments);
        s.putFields().put("segmentShift", segmentShift);
        s.putFields().put("segmentMask", segmentMask);
        s.writeFields();
        Node<K, V>[] t = this.table;
        if (this.table != null) {
            Node<K, V> p;
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            while ((p = it.advance()) != null) {
                s.writeObject(p.key);
                s.writeObject(p.val);
            }
        }
        s.writeObject(null);
        s.writeObject(null);
        segments = null;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        this.sizeCtl = -1;
        s.defaultReadObject();
        long size = 0L;
        Node<Object, Object> p = null;
        while (true) {
            Object k = s.readObject();
            Object v = s.readObject();
            if (k == null || v == null) break;
            p = new Node<Object, Object>(ConcurrentHashMapV8.spread(k.hashCode()), k, v, p);
            ++size;
        }
        if (size == 0L) {
            this.sizeCtl = 0;
        } else {
            int n;
            if (size >= 0x20000000L) {
                n = 0x40000000;
            } else {
                int sz = (int)size;
                n = ConcurrentHashMapV8.tableSizeFor(sz + (sz >>> 1) + 1);
            }
            Node[] tab = new Node[n];
            int mask = n - 1;
            long added = 0L;
            while (p != null) {
                boolean insertAtFront;
                Node next = p.next;
                int h = p.hash;
                int j = h & mask;
                Node<K, V> first = ConcurrentHashMapV8.tabAt(tab, j);
                if (first == null) {
                    insertAtFront = true;
                } else {
                    Object k = p.key;
                    if (first.hash < 0) {
                        TreeBin t = (TreeBin)first;
                        if (t.putTreeVal(h, k, p.val) == null) {
                            ++added;
                        }
                        insertAtFront = false;
                    } else {
                        int binCount = 0;
                        insertAtFront = true;
                        Node<Object, Object> q = first;
                        while (q != null) {
                            Object qk;
                            if (q.hash == h && ((qk = q.key) == k || qk != null && k.equals(qk))) {
                                insertAtFront = false;
                                break;
                            }
                            ++binCount;
                            q = q.next;
                        }
                        if (insertAtFront && binCount >= 8) {
                            insertAtFront = false;
                            ++added;
                            p.next = first;
                            TreeNode hd = null;
                            TreeNode tl = null;
                            q = p;
                            while (q != null) {
                                TreeNode t = new TreeNode(q.hash, q.key, q.val, null, null);
                                t.prev = tl;
                                if (t.prev == null) {
                                    hd = t;
                                } else {
                                    tl.next = t;
                                }
                                tl = t;
                                q = q.next;
                            }
                            ConcurrentHashMapV8.setTabAt(tab, j, new TreeBin(hd));
                        }
                    }
                }
                if (insertAtFront) {
                    ++added;
                    p.next = first;
                    ConcurrentHashMapV8.setTabAt(tab, j, p);
                }
                p = next;
            }
            this.table = tab;
            this.sizeCtl = n - (n >>> 2);
            this.baseCount = added;
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return this.putVal(key, value, true);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (key == null) {
            throw new NullPointerException();
        }
        return value != null && this.replaceNode(key, null, value) != null;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (key == null || oldValue == null || newValue == null) {
            throw new NullPointerException();
        }
        return this.replaceNode(key, newValue, oldValue) != null;
    }

    @Override
    public V replace(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException();
        }
        return this.replaceNode(key, value, null);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V v = this.get(key);
        return v == null ? defaultValue : v;
    }

    @Override
    public void forEach(BiAction<? super K, ? super V> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        Node<K, V>[] t = this.table;
        if (this.table != null) {
            Node<K, V> p;
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            while ((p = it.advance()) != null) {
                action.apply(p.key, p.val);
            }
        }
    }

    @Override
    public void replaceAll(BiFun<? super K, ? super V, ? extends V> function) {
        if (function == null) {
            throw new NullPointerException();
        }
        Node<K, V>[] t = this.table;
        if (this.table != null) {
            Node<K, V> p;
            Traverser<K, V> it = new Traverser<K, V>(t, t.length, 0, t.length);
            while ((p = it.advance()) != null) {
                V newValue;
                Object oldValue = p.val;
                Object key = p.key;
                do {
                    if ((newValue = function.apply(key, oldValue)) != null) continue;
                    throw new NullPointerException();
                } while (this.replaceNode(key, newValue, oldValue) == null && (oldValue = this.get(key)) != null);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V computeIfAbsent(K key, Fun<? super K, ? extends V> mappingFunction) {
        int binCount;
        Object val;
        block30: {
            boolean added;
            int i;
            if (key == null || mappingFunction == null) {
                throw new NullPointerException();
            }
            int h = ConcurrentHashMapV8.spread(key.hashCode());
            val = null;
            binCount = 0;
            Node<K, V>[] tab = this.table;
            while (true) {
                Node node;
                int n;
                if (tab == null || (n = tab.length) == 0) {
                    tab = this.initTable();
                    continue;
                }
                i = n - 1 & h;
                Node<K, V> f = ConcurrentHashMapV8.tabAt(tab, i);
                if (f == null) {
                    ReservationNode r;
                    node = r = new ReservationNode();
                    synchronized (node) {
                        if (ConcurrentHashMapV8.casTabAt(tab, i, null, r)) {
                            binCount = 1;
                            Node<K, Object> node2 = null;
                            try {
                                V v = mappingFunction.apply(key);
                                val = v;
                                if (v != null) {
                                    node2 = new Node<K, Object>(h, key, val, null);
                                }
                            }
                            finally {
                                ConcurrentHashMapV8.setTabAt(tab, i, node2);
                            }
                        }
                    }
                    if (binCount == 0) continue;
                    break block30;
                }
                int fh = f.hash;
                if (fh == -1) {
                    tab = this.helpTransfer(tab, f);
                    continue;
                }
                added = false;
                node = f;
                synchronized (node) {
                    block31: {
                        if (ConcurrentHashMapV8.tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                binCount = 1;
                                Node<K, V> e = f;
                                while (true) {
                                    Object ek;
                                    if (e.hash == h && ((ek = e.key) == key || ek != null && key.equals(ek))) {
                                        val = e.val;
                                        break block31;
                                    }
                                    Node<K, V> pred = e;
                                    e = e.next;
                                    if (e == null) {
                                        V v = mappingFunction.apply(key);
                                        val = v;
                                        if (v != null) {
                                            added = true;
                                            pred.next = new Node<K, Object>(h, key, val, null);
                                        }
                                        break block31;
                                    }
                                    ++binCount;
                                }
                            }
                            if (f instanceof TreeBin) {
                                TreeNode p;
                                binCount = 2;
                                TreeBin t = (TreeBin)f;
                                TreeNode r = t.root;
                                if (r != null && (p = r.findTreeNode(h, key, null)) != null) {
                                    val = p.val;
                                } else {
                                    V v = mappingFunction.apply(key);
                                    val = v;
                                    if (v != null) {
                                        added = true;
                                        t.putTreeVal(h, key, val);
                                    }
                                }
                            }
                        }
                    }
                }
                if (binCount != 0) break;
            }
            if (binCount >= 8) {
                this.treeifyBin(tab, i);
            }
            if (!added) {
                return (V)val;
            }
        }
        if (val != null) {
            this.addCount(1L, binCount);
        }
        return (V)val;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V computeIfPresent(K key, BiFun<? super K, ? super V, ? extends V> remappingFunction) {
        if (key == null || remappingFunction == null) {
            throw new NullPointerException();
        }
        int h = ConcurrentHashMapV8.spread(key.hashCode());
        V val = null;
        int delta = 0;
        int binCount = 0;
        Node<K, V>[] tab = this.table;
        while (true) {
            int n;
            if (tab == null || (n = tab.length) == 0) {
                tab = this.initTable();
                continue;
            }
            int i = n - 1 & h;
            Node<K, V> f = ConcurrentHashMapV8.tabAt(tab, i);
            if (f == null) break;
            int fh = f.hash;
            if (fh == -1) {
                tab = this.helpTransfer(tab, f);
                continue;
            }
            Node<K, V> node = f;
            synchronized (node) {
                if (ConcurrentHashMapV8.tabAt(tab, i) == f) {
                    if (fh >= 0) {
                        binCount = 1;
                        Node<K, V> e = f;
                        Node<K, V> pred = null;
                        while (true) {
                            Object ek;
                            if (e.hash == h && ((ek = e.key) == key || ek != null && key.equals(ek))) {
                                val = remappingFunction.apply(key, e.val);
                                if (val != null) {
                                    e.val = val;
                                } else {
                                    delta = -1;
                                    Node en = e.next;
                                    if (pred != null) {
                                        pred.next = en;
                                    } else {
                                        ConcurrentHashMapV8.setTabAt(tab, i, en);
                                    }
                                }
                            } else {
                                pred = e;
                                e = e.next;
                                if (e != null) {
                                    ++binCount;
                                    continue;
                                }
                            }
                            break;
                        }
                    } else if (f instanceof TreeBin) {
                        TreeNode p;
                        binCount = 2;
                        TreeBin t = (TreeBin)f;
                        TreeNode r = t.root;
                        if (r != null && (p = r.findTreeNode(h, key, null)) != null) {
                            val = remappingFunction.apply(key, p.val);
                            if (val != null) {
                                p.val = val;
                            } else {
                                delta = -1;
                                if (t.removeTreeNode(p)) {
                                    ConcurrentHashMapV8.setTabAt(tab, i, ConcurrentHashMapV8.untreeify(t.first));
                                }
                            }
                        }
                    }
                }
            }
            if (binCount != 0) break;
        }
        if (delta != 0) {
            this.addCount(delta, binCount);
        }
        return val;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V compute(K key, BiFun<? super K, ? super V, ? extends V> remappingFunction) {
        int binCount;
        int delta;
        Object val;
        block36: {
            int i;
            if (key == null || remappingFunction == null) {
                throw new NullPointerException();
            }
            int h = ConcurrentHashMapV8.spread(key.hashCode());
            val = null;
            delta = 0;
            binCount = 0;
            Node<K, V>[] tab = this.table;
            while (true) {
                int n;
                if (tab == null || (n = tab.length) == 0) {
                    tab = this.initTable();
                    continue;
                }
                i = n - 1 & h;
                Node<K, V> f = ConcurrentHashMapV8.tabAt(tab, i);
                if (f == null) {
                    ReservationNode r;
                    ReservationNode reservationNode = r = new ReservationNode();
                    synchronized (reservationNode) {
                        if (ConcurrentHashMapV8.casTabAt(tab, i, null, r)) {
                            binCount = 1;
                            Node<K, Object> node = null;
                            try {
                                V v = remappingFunction.apply(key, null);
                                val = v;
                                if (v != null) {
                                    delta = 1;
                                    node = new Node<K, Object>(h, key, val, null);
                                }
                            }
                            finally {
                                ConcurrentHashMapV8.setTabAt(tab, i, node);
                            }
                        }
                    }
                    if (binCount == 0) continue;
                    break block36;
                }
                int fh = f.hash;
                if (fh == -1) {
                    tab = this.helpTransfer(tab, f);
                    continue;
                }
                Node<K, V> node = f;
                synchronized (node) {
                    block37: {
                        if (ConcurrentHashMapV8.tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                binCount = 1;
                                Node<K, V> e = f;
                                Node<K, V> pred = null;
                                while (true) {
                                    Object ek;
                                    if (e.hash == h && ((ek = e.key) == key || ek != null && key.equals(ek))) {
                                        val = remappingFunction.apply(key, e.val);
                                        if (val != null) {
                                            e.val = val;
                                        } else {
                                            delta = -1;
                                            Node en = e.next;
                                            if (pred != null) {
                                                pred.next = en;
                                            } else {
                                                ConcurrentHashMapV8.setTabAt(tab, i, en);
                                            }
                                        }
                                        break block37;
                                    }
                                    pred = e;
                                    e = e.next;
                                    if (e == null) {
                                        val = remappingFunction.apply(key, null);
                                        if (val != null) {
                                            delta = 1;
                                            pred.next = new Node<K, Object>(h, key, val, null);
                                        }
                                        break block37;
                                    }
                                    ++binCount;
                                }
                            }
                            if (f instanceof TreeBin) {
                                binCount = 1;
                                TreeBin t = (TreeBin)f;
                                TreeNode r = t.root;
                                TreeNode p = r != null ? r.findTreeNode(h, key, null) : null;
                                Object pv = p == null ? null : p.val;
                                val = remappingFunction.apply(key, pv);
                                if (val != null) {
                                    if (p != null) {
                                        p.val = val;
                                    } else {
                                        delta = 1;
                                        t.putTreeVal(h, key, val);
                                    }
                                } else if (p != null) {
                                    delta = -1;
                                    if (t.removeTreeNode(p)) {
                                        ConcurrentHashMapV8.setTabAt(tab, i, ConcurrentHashMapV8.untreeify(t.first));
                                    }
                                }
                            }
                        }
                    }
                }
                if (binCount != 0) break;
            }
            if (binCount >= 8) {
                this.treeifyBin(tab, i);
            }
        }
        if (delta != 0) {
            this.addCount(delta, binCount);
        }
        return val;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public V merge(K key, V value, BiFun<? super V, ? super V, ? extends V> remappingFunction) {
        int binCount;
        int delta;
        Object val;
        block26: {
            int i;
            if (key == null || value == null || remappingFunction == null) {
                throw new NullPointerException();
            }
            int h = ConcurrentHashMapV8.spread(key.hashCode());
            val = null;
            delta = 0;
            binCount = 0;
            Node<K, V>[] tab = this.table;
            while (true) {
                int n;
                if (tab == null || (n = tab.length) == 0) {
                    tab = this.initTable();
                    continue;
                }
                i = n - 1 & h;
                Node<K, V> f = ConcurrentHashMapV8.tabAt(tab, i);
                if (f == null) {
                    if (!ConcurrentHashMapV8.casTabAt(tab, i, null, new Node<K, V>(h, key, value, null))) continue;
                    delta = 1;
                    val = value;
                    break block26;
                }
                int fh = f.hash;
                if (fh == -1) {
                    tab = this.helpTransfer(tab, f);
                    continue;
                }
                Node<K, V> node = f;
                synchronized (node) {
                    block27: {
                        if (ConcurrentHashMapV8.tabAt(tab, i) == f) {
                            if (fh >= 0) {
                                binCount = 1;
                                Node<K, V> e = f;
                                Node<K, V> pred = null;
                                while (true) {
                                    Object ek;
                                    if (e.hash == h && ((ek = e.key) == key || ek != null && key.equals(ek))) {
                                        val = remappingFunction.apply(e.val, value);
                                        if (val != null) {
                                            e.val = val;
                                        } else {
                                            delta = -1;
                                            Node en = e.next;
                                            if (pred != null) {
                                                pred.next = en;
                                            } else {
                                                ConcurrentHashMapV8.setTabAt(tab, i, en);
                                            }
                                        }
                                        break block27;
                                    }
                                    pred = e;
                                    e = e.next;
                                    if (e == null) {
                                        delta = 1;
                                        val = value;
                                        pred.next = new Node<K, Object>(h, key, val, null);
                                        break block27;
                                    }
                                    ++binCount;
                                }
                            }
                            if (f instanceof TreeBin) {
                                binCount = 2;
                                TreeBin t = (TreeBin)f;
                                TreeNode r = t.root;
                                TreeNode p = r == null ? null : r.findTreeNode(h, key, null);
                                val = p == null ? value : remappingFunction.apply(p.val, value);
                                if (val != null) {
                                    if (p != null) {
                                        p.val = val;
                                    } else {
                                        delta = 1;
                                        t.putTreeVal(h, key, val);
                                    }
                                } else if (p != null) {
                                    delta = -1;
                                    if (t.removeTreeNode(p)) {
                                        ConcurrentHashMapV8.setTabAt(tab, i, ConcurrentHashMapV8.untreeify(t.first));
                                    }
                                }
                            }
                        }
                    }
                }
                if (binCount != 0) break;
            }
            if (binCount >= 8) {
                this.treeifyBin(tab, i);
            }
        }
        if (delta != 0) {
            this.addCount(delta, binCount);
        }
        return val;
    }

    @Deprecated
    public boolean contains(Object value) {
        return this.containsValue(value);
    }

    public Enumeration<K> keys() {
        Node<K, V>[] t = this.table;
        int f = this.table == null ? 0 : t.length;
        return new KeyIterator<K, V>(t, f, 0, f, this);
    }

    public Enumeration<V> elements() {
        Node<K, V>[] t = this.table;
        int f = this.table == null ? 0 : t.length;
        return new ValueIterator<K, V>(t, f, 0, f, this);
    }

    public long mappingCount() {
        long n = this.sumCount();
        return n < 0L ? 0L : n;
    }

    public static <K> KeySetView<K, Boolean> newKeySet() {
        return new KeySetView<K, Boolean>(new ConcurrentHashMapV8(), Boolean.TRUE);
    }

    public static <K> KeySetView<K, Boolean> newKeySet(int initialCapacity) {
        return new KeySetView<K, Boolean>(new ConcurrentHashMapV8(initialCapacity), Boolean.TRUE);
    }

    public KeySetView<K, V> keySet(V mappedValue) {
        if (mappedValue == null) {
            throw new NullPointerException();
        }
        return new KeySetView(this, mappedValue);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final Node<K, V>[] initTable() {
        Node<K, V>[] tab;
        block6: {
            int sc;
            while (true) {
                tab = this.table;
                if (this.table != null && tab.length != 0) break block6;
                sc = this.sizeCtl;
                if (sc < 0) {
                    Thread.yield();
                    continue;
                }
                if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) break;
            }
            try {
                tab = this.table;
                if (this.table == null || tab.length == 0) {
                    int n = sc > 0 ? sc : 16;
                    Node[] nt = new Node[n];
                    tab = nt;
                    this.table = nt;
                    sc = n - (n >>> 2);
                }
            }
            finally {
                this.sizeCtl = sc;
            }
        }
        return tab;
    }

    private final void addCount(long x, int check) {
        long s;
        long b;
        CounterCell[] as = this.counterCells;
        if (this.counterCells != null || !U.compareAndSwapLong(this, BASECOUNT, b = this.baseCount, s = b + x)) {
            long v;
            CounterCell a;
            int m;
            boolean uncontended = true;
            InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
            IntegerHolder hc = threadLocals.counterHashCode();
            if (hc == null || as == null || (m = as.length - 1) < 0 || (a = as[m & hc.value]) == null || !(uncontended = U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
                this.fullAddCount(threadLocals, x, hc, uncontended);
                return;
            }
            if (check <= 1) {
                return;
            }
            s = this.sumCount();
        }
        if (check >= 0) {
            int sc;
            while (s >= (long)(sc = this.sizeCtl)) {
                Node<K, V>[] tab = this.table;
                if (this.table == null || tab.length >= 0x40000000) break;
                if (sc < 0) {
                    if (sc == -1 || this.transferIndex <= this.transferOrigin) break;
                    Node<K, V>[] nt = this.nextTable;
                    if (this.nextTable == null) break;
                    if (U.compareAndSwapInt(this, SIZECTL, sc, sc - 1)) {
                        this.transfer(tab, nt);
                    }
                } else if (U.compareAndSwapInt(this, SIZECTL, sc, -2)) {
                    this.transfer(tab, null);
                }
                s = this.sumCount();
            }
        }
    }

    final Node<K, V>[] helpTransfer(Node<K, V>[] tab, Node<K, V> f) {
        if (f instanceof ForwardingNode) {
            Node<K, V>[] nextTab = ((ForwardingNode)f).nextTable;
            if (((ForwardingNode)f).nextTable != null) {
                int sc;
                if (nextTab == this.nextTable && tab == this.table && this.transferIndex > this.transferOrigin && (sc = this.sizeCtl) < -1 && U.compareAndSwapInt(this, SIZECTL, sc, sc - 1)) {
                    this.transfer(tab, nextTab);
                }
                return nextTab;
            }
        }
        return this.table;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void tryPresize(int size) {
        int sc;
        int c;
        int n = c = size >= 0x20000000 ? 0x40000000 : ConcurrentHashMapV8.tableSizeFor(size + (size >>> 1) + 1);
        while ((sc = this.sizeCtl) >= 0) {
            int n2;
            Node<K, V>[] tab = this.table;
            if (tab == null || (n2 = tab.length) == 0) {
                int n3 = n2 = sc > c ? sc : c;
                if (!U.compareAndSwapInt(this, SIZECTL, sc, -1)) continue;
                try {
                    if (this.table != tab) continue;
                    Node[] nt = new Node[n2];
                    this.table = nt;
                    sc = n2 - (n2 >>> 2);
                    continue;
                }
                finally {
                    this.sizeCtl = sc;
                    continue;
                }
            }
            if (c <= sc || n2 >= 0x40000000) break;
            if (tab != this.table || !U.compareAndSwapInt(this, SIZECTL, sc, -2)) continue;
            this.transfer(tab, null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void transfer(Node<K, V>[] tab, Node<K, V>[] nextTab) {
        int n = tab.length;
        int stride = NCPU > 1 ? (n >>> 3) / NCPU : n;
        if (stride < 16) {
            stride = 16;
        }
        if (nextTab == null) {
            try {
                Node[] nt = new Node[n << 1];
                nextTab = nt;
            }
            catch (Throwable ex) {
                this.sizeCtl = Integer.MAX_VALUE;
                return;
            }
            this.nextTable = nextTab;
            this.transferOrigin = n;
            this.transferIndex = n;
            ForwardingNode<K, V> rev = new ForwardingNode<K, V>(tab);
            int k = n;
            while (k > 0) {
                int nextk;
                int m;
                for (m = nextk = k > stride ? k - stride : 0; m < k; ++m) {
                    nextTab[m] = rev;
                }
                for (m = n + nextk; m < n + k; ++m) {
                    nextTab[m] = rev;
                }
                k = nextk;
                U.putOrderedInt(this, TRANSFERORIGIN, k);
            }
        }
        int nextn = nextTab.length;
        ForwardingNode<K, V> fwd = new ForwardingNode<K, V>(nextTab);
        boolean advance = true;
        boolean finishing = false;
        int i = 0;
        int bound = 0;
        while (true) {
            if (advance) {
                if (--i >= bound || finishing) {
                    advance = false;
                    continue;
                }
                int nextIndex = this.transferIndex;
                if (nextIndex <= this.transferOrigin) {
                    i = -1;
                    advance = false;
                    continue;
                }
                int nextBound = nextIndex > stride ? nextIndex - stride : 0;
                if (!U.compareAndSwapInt(this, TRANSFERINDEX, nextIndex, nextBound)) continue;
                bound = nextBound;
                i = nextIndex - 1;
                advance = false;
                continue;
            }
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                if (finishing) {
                    this.nextTable = null;
                    this.table = nextTab;
                    this.sizeCtl = (n << 1) - (n >>> 1);
                    return;
                }
                do {
                    sc = this.sizeCtl;
                } while (!U.compareAndSwapInt(this, SIZECTL, sc, ++sc));
                if (sc != -1) {
                    return;
                }
                advance = true;
                finishing = true;
                i = n;
                continue;
            }
            TreeBin f = ConcurrentHashMapV8.tabAt(tab, i);
            if (f == null) {
                if (!ConcurrentHashMapV8.casTabAt(tab, i, null, fwd)) continue;
                ConcurrentHashMapV8.setTabAt(nextTab, i, null);
                ConcurrentHashMapV8.setTabAt(nextTab, i + n, null);
                advance = true;
                continue;
            }
            int fh = f.hash;
            if (fh == -1) {
                advance = true;
                continue;
            }
            TreeBin treeBin = f;
            synchronized (treeBin) {
                if (ConcurrentHashMapV8.tabAt(tab, i) == f) {
                    Node hn;
                    Node ln;
                    if (fh >= 0) {
                        int runBit = fh & n;
                        TreeBin lastRun = f;
                        Node p = f.next;
                        while (p != null) {
                            int b = p.hash & n;
                            if (b != runBit) {
                                runBit = b;
                                lastRun = p;
                            }
                            p = p.next;
                        }
                        if (runBit == 0) {
                            ln = lastRun;
                            hn = null;
                        } else {
                            hn = lastRun;
                            ln = null;
                        }
                        p = f;
                        while (p != lastRun) {
                            int ph = p.hash;
                            Object pk = p.key;
                            Object pv = p.val;
                            if ((ph & n) == 0) {
                                ln = new Node(ph, pk, pv, ln);
                            } else {
                                hn = new Node(ph, pk, pv, hn);
                            }
                            p = p.next;
                        }
                        ConcurrentHashMapV8.setTabAt(nextTab, i, ln);
                        ConcurrentHashMapV8.setTabAt(nextTab, i + n, hn);
                        ConcurrentHashMapV8.setTabAt(tab, i, fwd);
                        advance = true;
                    } else if (f instanceof TreeBin) {
                        TreeBin t = f;
                        TreeNode lo = null;
                        TreeNode loTail = null;
                        TreeNode hi = null;
                        TreeNode hiTail = null;
                        int lc = 0;
                        int hc = 0;
                        Node e = t.first;
                        while (e != null) {
                            int h = e.hash;
                            TreeNode p = new TreeNode(h, e.key, e.val, null, null);
                            if ((h & n) == 0) {
                                p.prev = loTail;
                                if (p.prev == null) {
                                    lo = p;
                                } else {
                                    loTail.next = p;
                                }
                                loTail = p;
                                ++lc;
                            } else {
                                p.prev = hiTail;
                                if (p.prev == null) {
                                    hi = p;
                                } else {
                                    hiTail.next = p;
                                }
                                hiTail = p;
                                ++hc;
                            }
                            e = e.next;
                        }
                        TreeBin treeBin2 = lc <= 6 ? ConcurrentHashMapV8.untreeify(lo) : (ln = hc != 0 ? new TreeBin(lo) : t);
                        hn = hc <= 6 ? ConcurrentHashMapV8.untreeify(hi) : (lc != 0 ? new TreeBin(hi) : t);
                        ConcurrentHashMapV8.setTabAt(nextTab, i, ln);
                        ConcurrentHashMapV8.setTabAt(nextTab, i + n, hn);
                        ConcurrentHashMapV8.setTabAt(tab, i, fwd);
                        advance = true;
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void treeifyBin(Node<K, V>[] tab, int index) {
        if (tab != null) {
            int n = tab.length;
            if (n < 64) {
                int sc;
                if (tab == this.table && (sc = this.sizeCtl) >= 0 && U.compareAndSwapInt(this, SIZECTL, sc, -2)) {
                    this.transfer(tab, null);
                }
            } else {
                Node<K, V> b = ConcurrentHashMapV8.tabAt(tab, index);
                if (b != null && b.hash >= 0) {
                    Node<K, V> node = b;
                    synchronized (node) {
                        if (ConcurrentHashMapV8.tabAt(tab, index) == b) {
                            TreeNode hd = null;
                            TreeNode tl = null;
                            Node<K, V> e = b;
                            while (e != null) {
                                TreeNode p = new TreeNode(e.hash, e.key, e.val, null, null);
                                p.prev = tl;
                                if (p.prev == null) {
                                    hd = p;
                                } else {
                                    tl.next = p;
                                }
                                tl = p;
                                e = e.next;
                            }
                            ConcurrentHashMapV8.setTabAt(tab, index, new TreeBin(hd));
                        }
                    }
                }
            }
        }
    }

    static <K, V> Node<K, V> untreeify(Node<K, V> b) {
        Node hd = null;
        Node tl = null;
        Node<K, V> q = b;
        while (q != null) {
            Node p = new Node(q.hash, q.key, q.val, null);
            if (tl == null) {
                hd = p;
            } else {
                tl.next = p;
            }
            tl = p;
            q = q.next;
        }
        return hd;
    }

    final int batchFor(long b) {
        long n;
        if (b == Long.MAX_VALUE || (n = this.sumCount()) <= 1L || n < b) {
            return 0;
        }
        int sp = ForkJoinPool.getCommonPoolParallelism() << 2;
        return b <= 0L || (n /= b) >= (long)sp ? sp : (int)n;
    }

    public void forEach(long parallelismThreshold, BiAction<? super K, ? super V> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        new ForEachMappingTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, action).invoke();
    }

    public <U> void forEach(long parallelismThreshold, BiFun<? super K, ? super V, ? extends U> transformer, Action<? super U> action) {
        if (transformer == null || action == null) {
            throw new NullPointerException();
        }
        new ForEachTransformedMappingTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action).invoke();
    }

    public <U> U search(long parallelismThreshold, BiFun<? super K, ? super V, ? extends U> searchFunction) {
        if (searchFunction == null) {
            throw new NullPointerException();
        }
        return (U)new SearchMappingsTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference()).invoke();
    }

    public <U> U reduce(long parallelismThreshold, BiFun<? super K, ? super V, ? extends U> transformer, BiFun<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (U)new MapReduceMappingsTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, reducer).invoke();
    }

    public double reduceToDouble(long parallelismThreshold, ObjectByObjectToDouble<? super K, ? super V> transformer, double basis, DoubleByDoubleToDouble reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Double)new MapReduceMappingsToDoubleTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public long reduceToLong(long parallelismThreshold, ObjectByObjectToLong<? super K, ? super V> transformer, long basis, LongByLongToLong reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Long)new MapReduceMappingsToLongTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public int reduceToInt(long parallelismThreshold, ObjectByObjectToInt<? super K, ? super V> transformer, int basis, IntByIntToInt reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Integer)new MapReduceMappingsToIntTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public void forEachKey(long parallelismThreshold, Action<? super K> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        new ForEachKeyTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, action).invoke();
    }

    public <U> void forEachKey(long parallelismThreshold, Fun<? super K, ? extends U> transformer, Action<? super U> action) {
        if (transformer == null || action == null) {
            throw new NullPointerException();
        }
        new ForEachTransformedKeyTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action).invoke();
    }

    public <U> U searchKeys(long parallelismThreshold, Fun<? super K, ? extends U> searchFunction) {
        if (searchFunction == null) {
            throw new NullPointerException();
        }
        return (U)new SearchKeysTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference()).invoke();
    }

    public K reduceKeys(long parallelismThreshold, BiFun<? super K, ? super K, ? extends K> reducer) {
        if (reducer == null) {
            throw new NullPointerException();
        }
        return (K)new ReduceKeysTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, reducer).invoke();
    }

    public <U> U reduceKeys(long parallelismThreshold, Fun<? super K, ? extends U> transformer, BiFun<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (U)new MapReduceKeysTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, reducer).invoke();
    }

    public double reduceKeysToDouble(long parallelismThreshold, ObjectToDouble<? super K> transformer, double basis, DoubleByDoubleToDouble reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Double)new MapReduceKeysToDoubleTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public long reduceKeysToLong(long parallelismThreshold, ObjectToLong<? super K> transformer, long basis, LongByLongToLong reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Long)new MapReduceKeysToLongTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public int reduceKeysToInt(long parallelismThreshold, ObjectToInt<? super K> transformer, int basis, IntByIntToInt reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Integer)new MapReduceKeysToIntTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public void forEachValue(long parallelismThreshold, Action<? super V> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        new ForEachValueTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, action).invoke();
    }

    public <U> void forEachValue(long parallelismThreshold, Fun<? super V, ? extends U> transformer, Action<? super U> action) {
        if (transformer == null || action == null) {
            throw new NullPointerException();
        }
        new ForEachTransformedValueTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action).invoke();
    }

    public <U> U searchValues(long parallelismThreshold, Fun<? super V, ? extends U> searchFunction) {
        if (searchFunction == null) {
            throw new NullPointerException();
        }
        return (U)new SearchValuesTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference()).invoke();
    }

    public V reduceValues(long parallelismThreshold, BiFun<? super V, ? super V, ? extends V> reducer) {
        if (reducer == null) {
            throw new NullPointerException();
        }
        return new ReduceValuesTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, reducer).invoke();
    }

    public <U> U reduceValues(long parallelismThreshold, Fun<? super V, ? extends U> transformer, BiFun<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (U)new MapReduceValuesTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, reducer).invoke();
    }

    public double reduceValuesToDouble(long parallelismThreshold, ObjectToDouble<? super V> transformer, double basis, DoubleByDoubleToDouble reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Double)new MapReduceValuesToDoubleTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public long reduceValuesToLong(long parallelismThreshold, ObjectToLong<? super V> transformer, long basis, LongByLongToLong reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Long)new MapReduceValuesToLongTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public int reduceValuesToInt(long parallelismThreshold, ObjectToInt<? super V> transformer, int basis, IntByIntToInt reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Integer)new MapReduceValuesToIntTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public void forEachEntry(long parallelismThreshold, Action<? super Map.Entry<K, V>> action) {
        if (action == null) {
            throw new NullPointerException();
        }
        new ForEachEntryTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, action).invoke();
    }

    public <U> void forEachEntry(long parallelismThreshold, Fun<Map.Entry<K, V>, ? extends U> transformer, Action<? super U> action) {
        if (transformer == null || action == null) {
            throw new NullPointerException();
        }
        new ForEachTransformedEntryTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, transformer, action).invoke();
    }

    public <U> U searchEntries(long parallelismThreshold, Fun<Map.Entry<K, V>, ? extends U> searchFunction) {
        if (searchFunction == null) {
            throw new NullPointerException();
        }
        return (U)new SearchEntriesTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, searchFunction, new AtomicReference()).invoke();
    }

    public Map.Entry<K, V> reduceEntries(long parallelismThreshold, BiFun<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer) {
        if (reducer == null) {
            throw new NullPointerException();
        }
        return (Map.Entry)new ReduceEntriesTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, reducer).invoke();
    }

    public <U> U reduceEntries(long parallelismThreshold, Fun<Map.Entry<K, V>, ? extends U> transformer, BiFun<? super U, ? super U, ? extends U> reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (U)new MapReduceEntriesTask<K, V, U>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, reducer).invoke();
    }

    public double reduceEntriesToDouble(long parallelismThreshold, ObjectToDouble<Map.Entry<K, V>> transformer, double basis, DoubleByDoubleToDouble reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Double)new MapReduceEntriesToDoubleTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public long reduceEntriesToLong(long parallelismThreshold, ObjectToLong<Map.Entry<K, V>> transformer, long basis, LongByLongToLong reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Long)new MapReduceEntriesToLongTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    public int reduceEntriesToInt(long parallelismThreshold, ObjectToInt<Map.Entry<K, V>> transformer, int basis, IntByIntToInt reducer) {
        if (transformer == null || reducer == null) {
            throw new NullPointerException();
        }
        return (Integer)new MapReduceEntriesToIntTask<K, V>(null, this.batchFor(parallelismThreshold), 0, 0, this.table, null, transformer, basis, reducer).invoke();
    }

    final long sumCount() {
        CounterCell[] as = this.counterCells;
        long sum = this.baseCount;
        if (as != null) {
            for (int i = 0; i < as.length; ++i) {
                CounterCell a = as[i];
                if (a == null) continue;
                sum += a.value;
            }
        }
        return sum;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private final void fullAddCount(InternalThreadLocalMap threadLocals, long x, IntegerHolder hc, boolean wasUncontended) {
        int h;
        if (hc == null) {
            hc = new IntegerHolder();
            int s = counterHashCodeGenerator.addAndGet(1640531527);
            hc.value = s == 0 ? 1 : s;
            h = hc.value;
            threadLocals.setCounterHashCode(hc);
        } else {
            h = hc.value;
        }
        boolean collide = false;
        while (true) {
            long v;
            int n;
            CounterCell[] as = this.counterCells;
            if (this.counterCells != null && (n = as.length) > 0) {
                CounterCell a = as[n - 1 & h];
                if (a == null) {
                    if (this.cellsBusy == 0) {
                        CounterCell r = new CounterCell(x);
                        if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                            boolean created = false;
                            try {
                                int j;
                                int m;
                                CounterCell[] rs = this.counterCells;
                                if (this.counterCells != null && (m = rs.length) > 0 && rs[j = m - 1 & h] == null) {
                                    rs[j] = r;
                                    created = true;
                                }
                            }
                            finally {
                                this.cellsBusy = 0;
                            }
                            if (!created) continue;
                            break;
                        }
                    }
                    collide = false;
                } else if (!wasUncontended) {
                    wasUncontended = true;
                } else {
                    v = a.value;
                    if (U.compareAndSwapLong(a, CELLVALUE, v, v + x)) break;
                    if (this.counterCells != as || n >= NCPU) {
                        collide = false;
                    } else if (!collide) {
                        collide = true;
                    } else if (this.cellsBusy == 0 && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                        try {
                            if (this.counterCells == as) {
                                CounterCell[] rs = new CounterCell[n << 1];
                                for (int i = 0; i < n; ++i) {
                                    rs[i] = as[i];
                                }
                                this.counterCells = rs;
                            }
                        }
                        finally {
                            this.cellsBusy = 0;
                        }
                        collide = false;
                        continue;
                    }
                }
                h ^= h << 13;
                h ^= h >>> 17;
                h ^= h << 5;
                continue;
            }
            if (this.cellsBusy == 0 && this.counterCells == as && U.compareAndSwapInt(this, CELLSBUSY, 0, 1)) {
                boolean init = false;
                try {
                    if (this.counterCells == as) {
                        CounterCell[] rs = new CounterCell[2];
                        rs[h & 1] = new CounterCell(x);
                        this.counterCells = rs;
                        init = true;
                    }
                }
                finally {
                    this.cellsBusy = 0;
                }
                if (!init) continue;
                break;
            }
            v = this.baseCount;
            if (U.compareAndSwapLong(this, BASECOUNT, v, v + x)) break;
        }
        hc.value = h;
    }

    private static Unsafe getUnsafe() {
        try {
            return Unsafe.getUnsafe();
        }
        catch (SecurityException tryReflectionInstead) {
            try {
                return AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>(){

                    @Override
                    public Unsafe run() throws Exception {
                        Class<Unsafe> k = Unsafe.class;
                        for (Field f : k.getDeclaredFields()) {
                            f.setAccessible(true);
                            Object x = f.get(null);
                            if (!k.isInstance(x)) continue;
                            return (Unsafe)k.cast(x);
                        }
                        throw new NoSuchFieldError("the Unsafe");
                    }
                });
            }
            catch (PrivilegedActionException e) {
                throw new RuntimeException("Could not initialize intrinsics", e.getCause());
            }
        }
    }

    static {
        try {
            U = ConcurrentHashMapV8.getUnsafe();
            Class<ConcurrentHashMapV8> k = ConcurrentHashMapV8.class;
            SIZECTL = U.objectFieldOffset(k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset(k.getDeclaredField("transferIndex"));
            TRANSFERORIGIN = U.objectFieldOffset(k.getDeclaredField("transferOrigin"));
            BASECOUNT = U.objectFieldOffset(k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset(k.getDeclaredField("cellsBusy"));
            Class<CounterCell> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset(ck.getDeclaredField("value"));
            Class<Node[]> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & scale - 1) != 0) {
                throw new Error("data type scale not a power of two");
            }
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        }
        catch (Exception e) {
            throw new Error(e);
        }
    }

    static final class CounterHashCode {
        int code;

        CounterHashCode() {
        }
    }

    static final class CounterCell {
        volatile long p0;
        volatile long p1;
        volatile long p2;
        volatile long p3;
        volatile long p4;
        volatile long p5;
        volatile long p6;
        volatile long value;
        volatile long q0;
        volatile long q1;
        volatile long q2;
        volatile long q3;
        volatile long q4;
        volatile long q5;
        volatile long q6;

        CounterCell(long x) {
            this.value = x;
        }
    }

    static final class MapReduceMappingsToIntTask<K, V>
    extends BulkTask<K, V, Integer> {
        final ObjectByObjectToInt<? super K, ? super V> transformer;
        final IntByIntToInt reducer;
        final int basis;
        int result;
        MapReduceMappingsToIntTask<K, V> rights;
        MapReduceMappingsToIntTask<K, V> nextRight;

        MapReduceMappingsToIntTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceMappingsToIntTask<K, V> nextRight, ObjectByObjectToInt<? super K, ? super V> transformer, int basis, IntByIntToInt reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Integer getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            IntByIntToInt reducer;
            ObjectByObjectToInt<K, V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceMappingsToIntTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.key, p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceMappingsToIntTask t = (MapReduceMappingsToIntTask)c;
                    MapReduceMappingsToIntTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceEntriesToIntTask<K, V>
    extends BulkTask<K, V, Integer> {
        final ObjectToInt<Map.Entry<K, V>> transformer;
        final IntByIntToInt reducer;
        final int basis;
        int result;
        MapReduceEntriesToIntTask<K, V> rights;
        MapReduceEntriesToIntTask<K, V> nextRight;

        MapReduceEntriesToIntTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceEntriesToIntTask<K, V> nextRight, ObjectToInt<Map.Entry<K, V>> transformer, int basis, IntByIntToInt reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Integer getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            IntByIntToInt reducer;
            ObjectToInt<Map.Entry<K, V>> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceEntriesToIntTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceEntriesToIntTask t = (MapReduceEntriesToIntTask)c;
                    MapReduceEntriesToIntTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceValuesToIntTask<K, V>
    extends BulkTask<K, V, Integer> {
        final ObjectToInt<? super V> transformer;
        final IntByIntToInt reducer;
        final int basis;
        int result;
        MapReduceValuesToIntTask<K, V> rights;
        MapReduceValuesToIntTask<K, V> nextRight;

        MapReduceValuesToIntTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceValuesToIntTask<K, V> nextRight, ObjectToInt<? super V> transformer, int basis, IntByIntToInt reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Integer getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            IntByIntToInt reducer;
            ObjectToInt<V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceValuesToIntTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceValuesToIntTask t = (MapReduceValuesToIntTask)c;
                    MapReduceValuesToIntTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceKeysToIntTask<K, V>
    extends BulkTask<K, V, Integer> {
        final ObjectToInt<? super K> transformer;
        final IntByIntToInt reducer;
        final int basis;
        int result;
        MapReduceKeysToIntTask<K, V> rights;
        MapReduceKeysToIntTask<K, V> nextRight;

        MapReduceKeysToIntTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceKeysToIntTask<K, V> nextRight, ObjectToInt<? super K> transformer, int basis, IntByIntToInt reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Integer getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            IntByIntToInt reducer;
            ObjectToInt<K> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceKeysToIntTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.key));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceKeysToIntTask t = (MapReduceKeysToIntTask)c;
                    MapReduceKeysToIntTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceMappingsToLongTask<K, V>
    extends BulkTask<K, V, Long> {
        final ObjectByObjectToLong<? super K, ? super V> transformer;
        final LongByLongToLong reducer;
        final long basis;
        long result;
        MapReduceMappingsToLongTask<K, V> rights;
        MapReduceMappingsToLongTask<K, V> nextRight;

        MapReduceMappingsToLongTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceMappingsToLongTask<K, V> nextRight, ObjectByObjectToLong<? super K, ? super V> transformer, long basis, LongByLongToLong reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Long getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            LongByLongToLong reducer;
            ObjectByObjectToLong<K, V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                long r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceMappingsToLongTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.key, p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceMappingsToLongTask t = (MapReduceMappingsToLongTask)c;
                    MapReduceMappingsToLongTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceEntriesToLongTask<K, V>
    extends BulkTask<K, V, Long> {
        final ObjectToLong<Map.Entry<K, V>> transformer;
        final LongByLongToLong reducer;
        final long basis;
        long result;
        MapReduceEntriesToLongTask<K, V> rights;
        MapReduceEntriesToLongTask<K, V> nextRight;

        MapReduceEntriesToLongTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceEntriesToLongTask<K, V> nextRight, ObjectToLong<Map.Entry<K, V>> transformer, long basis, LongByLongToLong reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Long getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            LongByLongToLong reducer;
            ObjectToLong<Map.Entry<K, V>> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                long r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceEntriesToLongTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceEntriesToLongTask t = (MapReduceEntriesToLongTask)c;
                    MapReduceEntriesToLongTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceValuesToLongTask<K, V>
    extends BulkTask<K, V, Long> {
        final ObjectToLong<? super V> transformer;
        final LongByLongToLong reducer;
        final long basis;
        long result;
        MapReduceValuesToLongTask<K, V> rights;
        MapReduceValuesToLongTask<K, V> nextRight;

        MapReduceValuesToLongTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceValuesToLongTask<K, V> nextRight, ObjectToLong<? super V> transformer, long basis, LongByLongToLong reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Long getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            LongByLongToLong reducer;
            ObjectToLong<V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                long r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceValuesToLongTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceValuesToLongTask t = (MapReduceValuesToLongTask)c;
                    MapReduceValuesToLongTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceKeysToLongTask<K, V>
    extends BulkTask<K, V, Long> {
        final ObjectToLong<? super K> transformer;
        final LongByLongToLong reducer;
        final long basis;
        long result;
        MapReduceKeysToLongTask<K, V> rights;
        MapReduceKeysToLongTask<K, V> nextRight;

        MapReduceKeysToLongTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceKeysToLongTask<K, V> nextRight, ObjectToLong<? super K> transformer, long basis, LongByLongToLong reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Long getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            LongByLongToLong reducer;
            ObjectToLong<K> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                long r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceKeysToLongTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.key));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceKeysToLongTask t = (MapReduceKeysToLongTask)c;
                    MapReduceKeysToLongTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceMappingsToDoubleTask<K, V>
    extends BulkTask<K, V, Double> {
        final ObjectByObjectToDouble<? super K, ? super V> transformer;
        final DoubleByDoubleToDouble reducer;
        final double basis;
        double result;
        MapReduceMappingsToDoubleTask<K, V> rights;
        MapReduceMappingsToDoubleTask<K, V> nextRight;

        MapReduceMappingsToDoubleTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceMappingsToDoubleTask<K, V> nextRight, ObjectByObjectToDouble<? super K, ? super V> transformer, double basis, DoubleByDoubleToDouble reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Double getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            DoubleByDoubleToDouble reducer;
            ObjectByObjectToDouble<K, V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                double r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceMappingsToDoubleTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.key, p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceMappingsToDoubleTask t = (MapReduceMappingsToDoubleTask)c;
                    MapReduceMappingsToDoubleTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceEntriesToDoubleTask<K, V>
    extends BulkTask<K, V, Double> {
        final ObjectToDouble<Map.Entry<K, V>> transformer;
        final DoubleByDoubleToDouble reducer;
        final double basis;
        double result;
        MapReduceEntriesToDoubleTask<K, V> rights;
        MapReduceEntriesToDoubleTask<K, V> nextRight;

        MapReduceEntriesToDoubleTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceEntriesToDoubleTask<K, V> nextRight, ObjectToDouble<Map.Entry<K, V>> transformer, double basis, DoubleByDoubleToDouble reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Double getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            DoubleByDoubleToDouble reducer;
            ObjectToDouble<Map.Entry<K, V>> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                double r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceEntriesToDoubleTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceEntriesToDoubleTask t = (MapReduceEntriesToDoubleTask)c;
                    MapReduceEntriesToDoubleTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceValuesToDoubleTask<K, V>
    extends BulkTask<K, V, Double> {
        final ObjectToDouble<? super V> transformer;
        final DoubleByDoubleToDouble reducer;
        final double basis;
        double result;
        MapReduceValuesToDoubleTask<K, V> rights;
        MapReduceValuesToDoubleTask<K, V> nextRight;

        MapReduceValuesToDoubleTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceValuesToDoubleTask<K, V> nextRight, ObjectToDouble<? super V> transformer, double basis, DoubleByDoubleToDouble reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Double getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            DoubleByDoubleToDouble reducer;
            ObjectToDouble<V> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                double r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceValuesToDoubleTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.val));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceValuesToDoubleTask t = (MapReduceValuesToDoubleTask)c;
                    MapReduceValuesToDoubleTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceKeysToDoubleTask<K, V>
    extends BulkTask<K, V, Double> {
        final ObjectToDouble<? super K> transformer;
        final DoubleByDoubleToDouble reducer;
        final double basis;
        double result;
        MapReduceKeysToDoubleTask<K, V> rights;
        MapReduceKeysToDoubleTask<K, V> nextRight;

        MapReduceKeysToDoubleTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceKeysToDoubleTask<K, V> nextRight, ObjectToDouble<? super K> transformer, double basis, DoubleByDoubleToDouble reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.basis = basis;
            this.reducer = reducer;
        }

        @Override
        public final Double getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            DoubleByDoubleToDouble reducer;
            ObjectToDouble<K> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                double r = this.basis;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceKeysToDoubleTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, r, reducer);
                    this.rights.fork();
                }
                while ((p = this.advance()) != null) {
                    r = reducer.apply(r, transformer.apply(p.key));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceKeysToDoubleTask t = (MapReduceKeysToDoubleTask)c;
                    MapReduceKeysToDoubleTask<K, V> s = t.rights;
                    while (s != null) {
                        t.result = reducer.apply(t.result, s.result);
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceMappingsTask<K, V, U>
    extends BulkTask<K, V, U> {
        final BiFun<? super K, ? super V, ? extends U> transformer;
        final BiFun<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceMappingsTask<K, V, U> rights;
        MapReduceMappingsTask<K, V, U> nextRight;

        MapReduceMappingsTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceMappingsTask<K, V, U> nextRight, BiFun<? super K, ? super V, ? extends U> transformer, BiFun<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFun<U, U, U> reducer;
            BiFun<K, V, U> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceMappingsTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.key, p.val);
                    if (u == null) continue;
                    r = r == null ? u : reducer.apply(r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceMappingsTask t = (MapReduceMappingsTask)c;
                    MapReduceMappingsTask<K, V, U> s = t.rights;
                    while (s != null) {
                        U sr = s.result;
                        if (sr != null) {
                            U tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceEntriesTask<K, V, U>
    extends BulkTask<K, V, U> {
        final Fun<Map.Entry<K, V>, ? extends U> transformer;
        final BiFun<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceEntriesTask<K, V, U> rights;
        MapReduceEntriesTask<K, V, U> nextRight;

        MapReduceEntriesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceEntriesTask<K, V, U> nextRight, Fun<Map.Entry<K, V>, ? extends U> transformer, BiFun<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFun<U, U, U> reducer;
            Fun<Map.Entry<K, V>, U> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceEntriesTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p);
                    if (u == null) continue;
                    r = r == null ? u : reducer.apply(r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceEntriesTask t = (MapReduceEntriesTask)c;
                    MapReduceEntriesTask<K, V, U> s = t.rights;
                    while (s != null) {
                        U sr = s.result;
                        if (sr != null) {
                            U tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceValuesTask<K, V, U>
    extends BulkTask<K, V, U> {
        final Fun<? super V, ? extends U> transformer;
        final BiFun<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceValuesTask<K, V, U> rights;
        MapReduceValuesTask<K, V, U> nextRight;

        MapReduceValuesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceValuesTask<K, V, U> nextRight, Fun<? super V, ? extends U> transformer, BiFun<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFun<U, U, U> reducer;
            Fun<V, U> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceValuesTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.val);
                    if (u == null) continue;
                    r = r == null ? u : reducer.apply(r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceValuesTask t = (MapReduceValuesTask)c;
                    MapReduceValuesTask<K, V, U> s = t.rights;
                    while (s != null) {
                        U sr = s.result;
                        if (sr != null) {
                            U tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class MapReduceKeysTask<K, V, U>
    extends BulkTask<K, V, U> {
        final Fun<? super K, ? extends U> transformer;
        final BiFun<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceKeysTask<K, V, U> rights;
        MapReduceKeysTask<K, V, U> nextRight;

        MapReduceKeysTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, MapReduceKeysTask<K, V, U> nextRight, Fun<? super K, ? extends U> transformer, BiFun<? super U, ? super U, ? extends U> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.transformer = transformer;
            this.reducer = reducer;
        }

        @Override
        public final U getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFun<U, U, U> reducer;
            Fun<K, U> transformer = this.transformer;
            if (transformer != null && (reducer = this.reducer) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new MapReduceKeysTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, transformer, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.key);
                    if (u == null) continue;
                    r = r == null ? u : reducer.apply(r, u);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    MapReduceKeysTask t = (MapReduceKeysTask)c;
                    MapReduceKeysTask<K, V, U> s = t.rights;
                    while (s != null) {
                        U sr = s.result;
                        if (sr != null) {
                            U tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class ReduceEntriesTask<K, V>
    extends BulkTask<K, V, Map.Entry<K, V>> {
        final BiFun<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer;
        Map.Entry<K, V> result;
        ReduceEntriesTask<K, V> rights;
        ReduceEntriesTask<K, V> nextRight;

        ReduceEntriesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, ReduceEntriesTask<K, V> nextRight, BiFun<Map.Entry<K, V>, Map.Entry<K, V>, ? extends Map.Entry<K, V>> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        @Override
        public final Map.Entry<K, V> getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFun<Map.Entry<K, V>, Map.Entry<K, V>, Map.Entry<K, V>> reducer = this.reducer;
            if (reducer != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new ReduceEntriesTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, reducer);
                    this.rights.fork();
                }
                Node r = null;
                while ((p = this.advance()) != null) {
                    r = r == null ? p : reducer.apply(r, p);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    ReduceEntriesTask t = (ReduceEntriesTask)c;
                    ReduceEntriesTask<K, V> s = t.rights;
                    while (s != null) {
                        Map.Entry<K, V> sr = s.result;
                        if (sr != null) {
                            Map.Entry<K, V> tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class ReduceValuesTask<K, V>
    extends BulkTask<K, V, V> {
        final BiFun<? super V, ? super V, ? extends V> reducer;
        V result;
        ReduceValuesTask<K, V> rights;
        ReduceValuesTask<K, V> nextRight;

        ReduceValuesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, ReduceValuesTask<K, V> nextRight, BiFun<? super V, ? super V, ? extends V> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        @Override
        public final V getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFun<V, V, V> reducer = this.reducer;
            if (reducer != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new ReduceValuesTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    Object v = p.val;
                    r = r == null ? v : reducer.apply(r, v);
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    ReduceValuesTask t = (ReduceValuesTask)c;
                    ReduceValuesTask<K, V> s = t.rights;
                    while (s != null) {
                        V sr = s.result;
                        if (sr != null) {
                            V tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class ReduceKeysTask<K, V>
    extends BulkTask<K, V, K> {
        final BiFun<? super K, ? super K, ? extends K> reducer;
        K result;
        ReduceKeysTask<K, V> rights;
        ReduceKeysTask<K, V> nextRight;

        ReduceKeysTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, ReduceKeysTask<K, V> nextRight, BiFun<? super K, ? super K, ? extends K> reducer) {
            super(p, b, i, f, t);
            this.nextRight = nextRight;
            this.reducer = reducer;
        }

        @Override
        public final K getRawResult() {
            return this.result;
        }

        @Override
        public final void compute() {
            BiFun<K, K, K> reducer = this.reducer;
            if (reducer != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    this.rights = new ReduceKeysTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, this.rights, reducer);
                    this.rights.fork();
                }
                Object r = null;
                while ((p = this.advance()) != null) {
                    Object u = p.key;
                    r = r == null ? u : (u == null ? r : reducer.apply(r, u));
                }
                this.result = r;
                for (CountedCompleter<?> c = this.firstComplete(); c != null; c = c.nextComplete()) {
                    ReduceKeysTask t = (ReduceKeysTask)c;
                    ReduceKeysTask<K, V> s = t.rights;
                    while (s != null) {
                        K sr = s.result;
                        if (sr != null) {
                            K tr = t.result;
                            t.result = tr == null ? sr : reducer.apply(tr, sr);
                        }
                        s = t.rights = s.nextRight;
                    }
                }
            }
        }
    }

    static final class SearchMappingsTask<K, V, U>
    extends BulkTask<K, V, U> {
        final BiFun<? super K, ? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchMappingsTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, BiFun<? super K, ? super V, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            AtomicReference<U> result;
            BiFun<K, V, U> searchFunction = this.searchFunction;
            if (searchFunction != null && (result = this.result) != null) {
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    if (result.get() != null) {
                        return;
                    }
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new SearchMappingsTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, searchFunction, result).fork();
                }
                while (result.get() == null) {
                    Node p = this.advance();
                    if (p == null) {
                        this.propagateCompletion();
                        break;
                    }
                    U u = searchFunction.apply(p.key, p.val);
                    if (u == null) continue;
                    if (!result.compareAndSet(null, u)) break;
                    this.quietlyCompleteRoot();
                    break;
                }
            }
        }
    }

    static final class SearchEntriesTask<K, V, U>
    extends BulkTask<K, V, U> {
        final Fun<Map.Entry<K, V>, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchEntriesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Fun<Map.Entry<K, V>, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            AtomicReference<U> result;
            Fun<Map.Entry<K, V>, U> searchFunction = this.searchFunction;
            if (searchFunction != null && (result = this.result) != null) {
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    if (result.get() != null) {
                        return;
                    }
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new SearchEntriesTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, searchFunction, result).fork();
                }
                while (result.get() == null) {
                    Node p = this.advance();
                    if (p == null) {
                        this.propagateCompletion();
                        break;
                    }
                    U u = searchFunction.apply(p);
                    if (u == null) continue;
                    if (result.compareAndSet(null, u)) {
                        this.quietlyCompleteRoot();
                    }
                    return;
                }
            }
        }
    }

    static final class SearchValuesTask<K, V, U>
    extends BulkTask<K, V, U> {
        final Fun<? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchValuesTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Fun<? super V, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            AtomicReference<U> result;
            Fun<V, U> searchFunction = this.searchFunction;
            if (searchFunction != null && (result = this.result) != null) {
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    if (result.get() != null) {
                        return;
                    }
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new SearchValuesTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, searchFunction, result).fork();
                }
                while (result.get() == null) {
                    Node p = this.advance();
                    if (p == null) {
                        this.propagateCompletion();
                        break;
                    }
                    U u = searchFunction.apply(p.val);
                    if (u == null) continue;
                    if (!result.compareAndSet(null, u)) break;
                    this.quietlyCompleteRoot();
                    break;
                }
            }
        }
    }

    static final class SearchKeysTask<K, V, U>
    extends BulkTask<K, V, U> {
        final Fun<? super K, ? extends U> searchFunction;
        final AtomicReference<U> result;

        SearchKeysTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Fun<? super K, ? extends U> searchFunction, AtomicReference<U> result) {
            super(p, b, i, f, t);
            this.searchFunction = searchFunction;
            this.result = result;
        }

        @Override
        public final U getRawResult() {
            return this.result.get();
        }

        @Override
        public final void compute() {
            AtomicReference<U> result;
            Fun<K, U> searchFunction = this.searchFunction;
            if (searchFunction != null && (result = this.result) != null) {
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    if (result.get() != null) {
                        return;
                    }
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new SearchKeysTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, searchFunction, result).fork();
                }
                while (result.get() == null) {
                    Node p = this.advance();
                    if (p == null) {
                        this.propagateCompletion();
                        break;
                    }
                    U u = searchFunction.apply(p.key);
                    if (u == null) continue;
                    if (!result.compareAndSet(null, u)) break;
                    this.quietlyCompleteRoot();
                    break;
                }
            }
        }
    }

    static final class ForEachTransformedMappingTask<K, V, U>
    extends BulkTask<K, V, Void> {
        final BiFun<? super K, ? super V, ? extends U> transformer;
        final Action<? super U> action;

        ForEachTransformedMappingTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, BiFun<? super K, ? super V, ? extends U> transformer, Action<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        @Override
        public final void compute() {
            Action<U> action;
            BiFun<K, V, U> transformer = this.transformer;
            if (transformer != null && (action = this.action) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachTransformedMappingTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, transformer, action).fork();
                }
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.key, p.val);
                    if (u == null) continue;
                    action.apply(u);
                }
                this.propagateCompletion();
            }
        }
    }

    static final class ForEachTransformedEntryTask<K, V, U>
    extends BulkTask<K, V, Void> {
        final Fun<Map.Entry<K, V>, ? extends U> transformer;
        final Action<? super U> action;

        ForEachTransformedEntryTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Fun<Map.Entry<K, V>, ? extends U> transformer, Action<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        @Override
        public final void compute() {
            Action<U> action;
            Fun<Map.Entry<K, V>, U> transformer = this.transformer;
            if (transformer != null && (action = this.action) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachTransformedEntryTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, transformer, action).fork();
                }
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p);
                    if (u == null) continue;
                    action.apply(u);
                }
                this.propagateCompletion();
            }
        }
    }

    static final class ForEachTransformedValueTask<K, V, U>
    extends BulkTask<K, V, Void> {
        final Fun<? super V, ? extends U> transformer;
        final Action<? super U> action;

        ForEachTransformedValueTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Fun<? super V, ? extends U> transformer, Action<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        @Override
        public final void compute() {
            Action<U> action;
            Fun<V, U> transformer = this.transformer;
            if (transformer != null && (action = this.action) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachTransformedValueTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, transformer, action).fork();
                }
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.val);
                    if (u == null) continue;
                    action.apply(u);
                }
                this.propagateCompletion();
            }
        }
    }

    static final class ForEachTransformedKeyTask<K, V, U>
    extends BulkTask<K, V, Void> {
        final Fun<? super K, ? extends U> transformer;
        final Action<? super U> action;

        ForEachTransformedKeyTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Fun<? super K, ? extends U> transformer, Action<? super U> action) {
            super(p, b, i, f, t);
            this.transformer = transformer;
            this.action = action;
        }

        @Override
        public final void compute() {
            Action<U> action;
            Fun<K, U> transformer = this.transformer;
            if (transformer != null && (action = this.action) != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachTransformedKeyTask<K, V, U>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, transformer, action).fork();
                }
                while ((p = this.advance()) != null) {
                    U u = transformer.apply(p.key);
                    if (u == null) continue;
                    action.apply(u);
                }
                this.propagateCompletion();
            }
        }
    }

    static final class ForEachMappingTask<K, V>
    extends BulkTask<K, V, Void> {
        final BiAction<? super K, ? super V> action;

        ForEachMappingTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, BiAction<? super K, ? super V> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        @Override
        public final void compute() {
            BiAction<K, V> action = this.action;
            if (action != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachMappingTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, action).fork();
                }
                while ((p = this.advance()) != null) {
                    action.apply(p.key, p.val);
                }
                this.propagateCompletion();
            }
        }
    }

    static final class ForEachEntryTask<K, V>
    extends BulkTask<K, V, Void> {
        final Action<? super Map.Entry<K, V>> action;

        ForEachEntryTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Action<? super Map.Entry<K, V>> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        @Override
        public final void compute() {
            Action<Map.Entry<K, V>> action = this.action;
            if (action != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachEntryTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, action).fork();
                }
                while ((p = this.advance()) != null) {
                    action.apply(p);
                }
                this.propagateCompletion();
            }
        }
    }

    static final class ForEachValueTask<K, V>
    extends BulkTask<K, V, Void> {
        final Action<? super V> action;

        ForEachValueTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Action<? super V> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        @Override
        public final void compute() {
            Action<V> action = this.action;
            if (action != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachValueTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, action).fork();
                }
                while ((p = this.advance()) != null) {
                    action.apply(p.val);
                }
                this.propagateCompletion();
            }
        }
    }

    static final class ForEachKeyTask<K, V>
    extends BulkTask<K, V, Void> {
        final Action<? super K> action;

        ForEachKeyTask(BulkTask<K, V, ?> p, int b, int i, int f, Node<K, V>[] t, Action<? super K> action) {
            super(p, b, i, f, t);
            this.action = action;
        }

        @Override
        public final void compute() {
            Action<K> action = this.action;
            if (action != null) {
                Node p;
                int f;
                int h;
                int i = this.baseIndex;
                while (this.batch > 0 && (h = (f = this.baseLimit) + i >>> 1) > i) {
                    this.addToPendingCount(1);
                    this.baseLimit = h;
                    new ForEachKeyTask<K, V>(this, this.batch >>>= 1, this.baseLimit, f, this.tab, action).fork();
                }
                while ((p = this.advance()) != null) {
                    action.apply(p.key);
                }
                this.propagateCompletion();
            }
        }
    }

    static abstract class BulkTask<K, V, R>
    extends CountedCompleter<R> {
        Node<K, V>[] tab;
        Node<K, V> next;
        int index;
        int baseIndex;
        int baseLimit;
        final int baseSize;
        int batch;

        BulkTask(BulkTask<K, V, ?> par, int b, int i, int f, Node<K, V>[] t) {
            super(par);
            this.batch = b;
            this.index = this.baseIndex = i;
            this.tab = t;
            if (t == null) {
                this.baseLimit = 0;
                this.baseSize = 0;
            } else if (par == null) {
                this.baseSize = this.baseLimit = t.length;
            } else {
                this.baseLimit = f;
                this.baseSize = par.baseSize;
            }
        }

        final Node<K, V> advance() {
            Node<K, V> e = this.next;
            if (e != null) {
                e = e.next;
            }
            while (true) {
                int n;
                Node<K, V>[] t;
                block9: {
                    block8: {
                        int i;
                        if (e != null) {
                            this.next = e;
                            return this.next;
                        }
                        if (this.baseIndex >= this.baseLimit) break block8;
                        t = this.tab;
                        if (this.tab != null && (n = t.length) > (i = this.index) && i >= 0) break block9;
                    }
                    this.next = null;
                    return null;
                }
                e = ConcurrentHashMapV8.tabAt(t, this.index);
                if (e != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        this.tab = ((ForwardingNode)e).nextTable;
                        e = null;
                        continue;
                    }
                    e = e instanceof TreeBin ? ((TreeBin)e).first : null;
                }
                if ((this.index += this.baseSize) < n) continue;
                this.index = ++this.baseIndex;
            }
        }
    }

    static final class EntrySetView<K, V>
    extends CollectionView<K, V, Map.Entry<K, V>>
    implements Set<Map.Entry<K, V>>,
    Serializable {
        private static final long serialVersionUID = 2249069246763182397L;

        EntrySetView(ConcurrentHashMapV8<K, V> map) {
            super(map);
        }

        @Override
        public boolean contains(Object o) {
            Object v;
            Object r;
            Map.Entry e;
            Object k;
            return o instanceof Map.Entry && (k = (e = (Map.Entry)o).getKey()) != null && (r = this.map.get(k)) != null && (v = e.getValue()) != null && (v == r || v.equals(r));
        }

        @Override
        public boolean remove(Object o) {
            Object v;
            Map.Entry e;
            Object k;
            return o instanceof Map.Entry && (k = (e = (Map.Entry)o).getKey()) != null && (v = e.getValue()) != null && this.map.remove(k, v);
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            ConcurrentHashMapV8 m = this.map;
            Node<K, V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new EntryIterator(t, f, 0, f, m);
        }

        @Override
        public boolean add(Map.Entry<K, V> e) {
            return this.map.putVal(e.getKey(), e.getValue(), false) == null;
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<K, V>> c) {
            boolean added = false;
            for (Map.Entry<K, V> e : c) {
                if (!this.add(e)) continue;
                added = true;
            }
            return added;
        }

        @Override
        public final int hashCode() {
            int h = 0;
            Node<K, V>[] t = this.map.table;
            if (this.map.table != null) {
                Node p;
                Traverser it = new Traverser(t, t.length, 0, t.length);
                while ((p = it.advance()) != null) {
                    h += p.hashCode();
                }
            }
            return h;
        }

        @Override
        public final boolean equals(Object o) {
            Set c;
            return o instanceof Set && ((c = (Set)o) == this || this.containsAll(c) && c.containsAll(this));
        }

        public ConcurrentHashMapSpliterator<Map.Entry<K, V>> spliterator166() {
            ConcurrentHashMapV8 m = this.map;
            long n = m.sumCount();
            Node<K, V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new EntrySpliterator(t, f, 0, f, n < 0L ? 0L : n, m);
        }

        @Override
        public void forEach(Action<? super Map.Entry<K, V>> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node<K, V>[] t = this.map.table;
            if (this.map.table != null) {
                Node p;
                Traverser it = new Traverser(t, t.length, 0, t.length);
                while ((p = it.advance()) != null) {
                    action.apply(new MapEntry(p.key, p.val, this.map));
                }
            }
        }
    }

    static final class ValuesView<K, V>
    extends CollectionView<K, V, V>
    implements Collection<V>,
    Serializable {
        private static final long serialVersionUID = 2249069246763182397L;

        ValuesView(ConcurrentHashMapV8<K, V> map) {
            super(map);
        }

        @Override
        public final boolean contains(Object o) {
            return this.map.containsValue(o);
        }

        @Override
        public final boolean remove(Object o) {
            if (o != null) {
                Iterator<V> it = this.iterator();
                while (it.hasNext()) {
                    if (!o.equals(it.next())) continue;
                    it.remove();
                    return true;
                }
            }
            return false;
        }

        @Override
        public final Iterator<V> iterator() {
            ConcurrentHashMapV8 m = this.map;
            Node<K, V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new ValueIterator(t, f, 0, f, m);
        }

        @Override
        public final boolean add(V e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean addAll(Collection<? extends V> c) {
            throw new UnsupportedOperationException();
        }

        public ConcurrentHashMapSpliterator<V> spliterator166() {
            ConcurrentHashMapV8 m = this.map;
            long n = m.sumCount();
            Node<K, V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new ValueSpliterator(t, f, 0, f, n < 0L ? 0L : n);
        }

        @Override
        public void forEach(Action<? super V> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node<K, V>[] t = this.map.table;
            if (this.map.table != null) {
                Node p;
                Traverser it = new Traverser(t, t.length, 0, t.length);
                while ((p = it.advance()) != null) {
                    action.apply(p.val);
                }
            }
        }
    }

    public static class KeySetView<K, V>
    extends CollectionView<K, V, K>
    implements Set<K>,
    Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;

        KeySetView(ConcurrentHashMapV8<K, V> map, V value) {
            super(map);
            this.value = value;
        }

        public V getMappedValue() {
            return this.value;
        }

        @Override
        public boolean contains(Object o) {
            return this.map.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return this.map.remove(o) != null;
        }

        @Override
        public Iterator<K> iterator() {
            ConcurrentHashMapV8 m = this.map;
            Node<K, V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new KeyIterator(t, f, 0, f, m);
        }

        @Override
        public boolean add(K e) {
            V v = this.value;
            if (v == null) {
                throw new UnsupportedOperationException();
            }
            return this.map.putVal(e, v, true) == null;
        }

        @Override
        public boolean addAll(Collection<? extends K> c) {
            boolean added = false;
            V v = this.value;
            if (v == null) {
                throw new UnsupportedOperationException();
            }
            for (K e : c) {
                if (this.map.putVal(e, v, true) != null) continue;
                added = true;
            }
            return added;
        }

        @Override
        public int hashCode() {
            int h = 0;
            for (K e : this) {
                h += e.hashCode();
            }
            return h;
        }

        @Override
        public boolean equals(Object o) {
            Set c;
            return o instanceof Set && ((c = (Set)o) == this || this.containsAll(c) && c.containsAll(this));
        }

        public ConcurrentHashMapSpliterator<K> spliterator166() {
            ConcurrentHashMapV8 m = this.map;
            long n = m.sumCount();
            Node<K, V>[] t = m.table;
            int f = m.table == null ? 0 : t.length;
            return new KeySpliterator(t, f, 0, f, n < 0L ? 0L : n);
        }

        @Override
        public void forEach(Action<? super K> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node<K, V>[] t = this.map.table;
            if (this.map.table != null) {
                Node p;
                Traverser it = new Traverser(t, t.length, 0, t.length);
                while ((p = it.advance()) != null) {
                    action.apply(p.key);
                }
            }
        }
    }

    static abstract class CollectionView<K, V, E>
    implements Collection<E>,
    Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        final ConcurrentHashMapV8<K, V> map;
        private static final String oomeMsg = "Required array size too large";

        CollectionView(ConcurrentHashMapV8<K, V> map) {
            this.map = map;
        }

        public ConcurrentHashMapV8<K, V> getMap() {
            return this.map;
        }

        @Override
        public final void clear() {
            this.map.clear();
        }

        @Override
        public final int size() {
            return this.map.size();
        }

        @Override
        public final boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public abstract Iterator<E> iterator();

        @Override
        public abstract boolean contains(Object var1);

        @Override
        public abstract boolean remove(Object var1);

        @Override
        public final Object[] toArray() {
            long sz = this.map.mappingCount();
            if (sz > 0x7FFFFFF7L) {
                throw new OutOfMemoryError(oomeMsg);
            }
            int n = (int)sz;
            Object[] r = new Object[n];
            int i = 0;
            for (E e : this) {
                if (i == n) {
                    if (n >= 0x7FFFFFF7) {
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    n = n >= 0x3FFFFFFB ? 0x7FFFFFF7 : (n += (n >>> 1) + 1);
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = e;
            }
            return i == n ? r : Arrays.copyOf(r, i);
        }

        @Override
        public final <T> T[] toArray(T[] a) {
            long sz = this.map.mappingCount();
            if (sz > 0x7FFFFFF7L) {
                throw new OutOfMemoryError(oomeMsg);
            }
            int m = (int)sz;
            T[] r = a.length >= m ? a : (Object[])Array.newInstance(a.getClass().getComponentType(), m);
            int n = r.length;
            int i = 0;
            for (E e : this) {
                if (i == n) {
                    if (n >= 0x7FFFFFF7) {
                        throw new OutOfMemoryError(oomeMsg);
                    }
                    n = n >= 0x3FFFFFFB ? 0x7FFFFFF7 : (n += (n >>> 1) + 1);
                    r = Arrays.copyOf(r, n);
                }
                r[i++] = e;
            }
            if (a == r && i < n) {
                r[i] = null;
                return r;
            }
            return i == n ? r : Arrays.copyOf(r, i);
        }

        public final String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator<E> it = this.iterator();
            if (it.hasNext()) {
                while (true) {
                    E e;
                    sb.append((Object)((e = it.next()) == this ? "(this Collection)" : e));
                    if (!it.hasNext()) break;
                    sb.append(',').append(' ');
                }
            }
            return sb.append(']').toString();
        }

        @Override
        public final boolean containsAll(Collection<?> c) {
            if (c != this) {
                for (Object e : c) {
                    if (e != null && this.contains(e)) continue;
                    return false;
                }
            }
            return true;
        }

        @Override
        public final boolean removeAll(Collection<?> c) {
            boolean modified = false;
            Iterator<E> it = this.iterator();
            while (it.hasNext()) {
                if (!c.contains(it.next())) continue;
                it.remove();
                modified = true;
            }
            return modified;
        }

        @Override
        public final boolean retainAll(Collection<?> c) {
            boolean modified = false;
            Iterator<E> it = this.iterator();
            while (it.hasNext()) {
                if (c.contains(it.next())) continue;
                it.remove();
                modified = true;
            }
            return modified;
        }
    }

    static final class EntrySpliterator<K, V>
    extends Traverser<K, V>
    implements ConcurrentHashMapSpliterator<Map.Entry<K, V>> {
        final ConcurrentHashMapV8<K, V> map;
        long est;

        EntrySpliterator(Node<K, V>[] tab, int size, int index, int limit, long est, ConcurrentHashMapV8<K, V> map) {
            super(tab, size, index, limit);
            this.map = map;
            this.est = est;
        }

        @Override
        public ConcurrentHashMapSpliterator<Map.Entry<K, V>> trySplit() {
            EntrySpliterator<K, V> entrySpliterator;
            int i = this.baseIndex;
            int f = this.baseLimit;
            int h = i + f >>> 1;
            if (h <= i) {
                entrySpliterator = null;
            } else {
                this.baseLimit = h;
                EntrySpliterator<K, V> entrySpliterator2 = new EntrySpliterator<K, V>(this.tab, this.baseSize, this.baseLimit, f, this.est >>>= 1, this.map);
                entrySpliterator = entrySpliterator2;
            }
            return entrySpliterator;
        }

        @Override
        public void forEachRemaining(Action<? super Map.Entry<K, V>> action) {
            Node p;
            if (action == null) {
                throw new NullPointerException();
            }
            while ((p = this.advance()) != null) {
                action.apply(new MapEntry(p.key, p.val, this.map));
            }
        }

        @Override
        public boolean tryAdvance(Action<? super Map.Entry<K, V>> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node p = this.advance();
            if (p == null) {
                return false;
            }
            action.apply(new MapEntry(p.key, p.val, this.map));
            return true;
        }

        @Override
        public long estimateSize() {
            return this.est;
        }
    }

    static final class ValueSpliterator<K, V>
    extends Traverser<K, V>
    implements ConcurrentHashMapSpliterator<V> {
        long est;

        ValueSpliterator(Node<K, V>[] tab, int size, int index, int limit, long est) {
            super(tab, size, index, limit);
            this.est = est;
        }

        @Override
        public ConcurrentHashMapSpliterator<V> trySplit() {
            ValueSpliterator<K, V> valueSpliterator;
            int i = this.baseIndex;
            int f = this.baseLimit;
            int h = i + f >>> 1;
            if (h <= i) {
                valueSpliterator = null;
            } else {
                this.baseLimit = h;
                ValueSpliterator<K, V> valueSpliterator2 = new ValueSpliterator<K, V>(this.tab, this.baseSize, this.baseLimit, f, this.est >>>= 1);
                valueSpliterator = valueSpliterator2;
            }
            return valueSpliterator;
        }

        @Override
        public void forEachRemaining(Action<? super V> action) {
            Node p;
            if (action == null) {
                throw new NullPointerException();
            }
            while ((p = this.advance()) != null) {
                action.apply(p.val);
            }
        }

        @Override
        public boolean tryAdvance(Action<? super V> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node p = this.advance();
            if (p == null) {
                return false;
            }
            action.apply(p.val);
            return true;
        }

        @Override
        public long estimateSize() {
            return this.est;
        }
    }

    static final class KeySpliterator<K, V>
    extends Traverser<K, V>
    implements ConcurrentHashMapSpliterator<K> {
        long est;

        KeySpliterator(Node<K, V>[] tab, int size, int index, int limit, long est) {
            super(tab, size, index, limit);
            this.est = est;
        }

        @Override
        public ConcurrentHashMapSpliterator<K> trySplit() {
            KeySpliterator<K, V> keySpliterator;
            int i = this.baseIndex;
            int f = this.baseLimit;
            int h = i + f >>> 1;
            if (h <= i) {
                keySpliterator = null;
            } else {
                this.baseLimit = h;
                KeySpliterator<K, V> keySpliterator2 = new KeySpliterator<K, V>(this.tab, this.baseSize, this.baseLimit, f, this.est >>>= 1);
                keySpliterator = keySpliterator2;
            }
            return keySpliterator;
        }

        @Override
        public void forEachRemaining(Action<? super K> action) {
            Node p;
            if (action == null) {
                throw new NullPointerException();
            }
            while ((p = this.advance()) != null) {
                action.apply(p.key);
            }
        }

        @Override
        public boolean tryAdvance(Action<? super K> action) {
            if (action == null) {
                throw new NullPointerException();
            }
            Node p = this.advance();
            if (p == null) {
                return false;
            }
            action.apply(p.key);
            return true;
        }

        @Override
        public long estimateSize() {
            return this.est;
        }
    }

    static final class MapEntry<K, V>
    implements Map.Entry<K, V> {
        final K key;
        V val;
        final ConcurrentHashMapV8<K, V> map;

        MapEntry(K key, V val, ConcurrentHashMapV8<K, V> map) {
            this.key = key;
            this.val = val;
            this.map = map;
        }

        @Override
        public K getKey() {
            return this.key;
        }

        @Override
        public V getValue() {
            return this.val;
        }

        @Override
        public int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public String toString() {
            return this.key + "=" + this.val;
        }

        @Override
        public boolean equals(Object o) {
            Object v;
            Map.Entry e;
            Object k;
            return !(!(o instanceof Map.Entry) || (k = (e = (Map.Entry)o).getKey()) == null || (v = e.getValue()) == null || k != this.key && !k.equals(this.key) || v != this.val && !v.equals(this.val));
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new NullPointerException();
            }
            V v = this.val;
            this.val = value;
            this.map.put(this.key, value);
            return v;
        }
    }

    static final class EntryIterator<K, V>
    extends BaseIterator<K, V>
    implements Iterator<Map.Entry<K, V>> {
        EntryIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMapV8<K, V> map) {
            super(tab, index, size, limit, map);
        }

        @Override
        public final Map.Entry<K, V> next() {
            Node p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            Object k = p.key;
            Object v = p.val;
            this.lastReturned = p;
            this.advance();
            return new MapEntry(k, v, this.map);
        }
    }

    static final class ValueIterator<K, V>
    extends BaseIterator<K, V>
    implements Iterator<V>,
    Enumeration<V> {
        ValueIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMapV8<K, V> map) {
            super(tab, index, size, limit, map);
        }

        @Override
        public final V next() {
            Node p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            Object v = p.val;
            this.lastReturned = p;
            this.advance();
            return v;
        }

        @Override
        public final V nextElement() {
            return this.next();
        }
    }

    static final class KeyIterator<K, V>
    extends BaseIterator<K, V>
    implements Iterator<K>,
    Enumeration<K> {
        KeyIterator(Node<K, V>[] tab, int index, int size, int limit, ConcurrentHashMapV8<K, V> map) {
            super(tab, index, size, limit, map);
        }

        @Override
        public final K next() {
            Node p = this.next;
            if (p == null) {
                throw new NoSuchElementException();
            }
            Object k = p.key;
            this.lastReturned = p;
            this.advance();
            return k;
        }

        @Override
        public final K nextElement() {
            return this.next();
        }
    }

    static class BaseIterator<K, V>
    extends Traverser<K, V> {
        final ConcurrentHashMapV8<K, V> map;
        Node<K, V> lastReturned;

        BaseIterator(Node<K, V>[] tab, int size, int index, int limit, ConcurrentHashMapV8<K, V> map) {
            super(tab, size, index, limit);
            this.map = map;
            this.advance();
        }

        public final boolean hasNext() {
            return this.next != null;
        }

        public final boolean hasMoreElements() {
            return this.next != null;
        }

        public final void remove() {
            Node<K, V> p = this.lastReturned;
            if (p == null) {
                throw new IllegalStateException();
            }
            this.lastReturned = null;
            this.map.replaceNode(p.key, null, null);
        }
    }

    static class Traverser<K, V> {
        Node<K, V>[] tab;
        Node<K, V> next;
        int index;
        int baseIndex;
        int baseLimit;
        final int baseSize;

        Traverser(Node<K, V>[] tab, int size, int index, int limit) {
            this.tab = tab;
            this.baseSize = size;
            this.baseIndex = this.index = index;
            this.baseLimit = limit;
            this.next = null;
        }

        final Node<K, V> advance() {
            Node<K, V> e = this.next;
            if (e != null) {
                e = e.next;
            }
            while (true) {
                int n;
                Node<K, V>[] t;
                block9: {
                    block8: {
                        int i;
                        if (e != null) {
                            this.next = e;
                            return this.next;
                        }
                        if (this.baseIndex >= this.baseLimit) break block8;
                        t = this.tab;
                        if (this.tab != null && (n = t.length) > (i = this.index) && i >= 0) break block9;
                    }
                    this.next = null;
                    return null;
                }
                e = ConcurrentHashMapV8.tabAt(t, this.index);
                if (e != null && e.hash < 0) {
                    if (e instanceof ForwardingNode) {
                        this.tab = ((ForwardingNode)e).nextTable;
                        e = null;
                        continue;
                    }
                    e = e instanceof TreeBin ? ((TreeBin)e).first : null;
                }
                if ((this.index += this.baseSize) < n) continue;
                this.index = ++this.baseIndex;
            }
        }
    }

    static final class TreeBin<K, V>
    extends Node<K, V> {
        TreeNode<K, V> root;
        volatile TreeNode<K, V> first;
        volatile Thread waiter;
        volatile int lockState;
        static final int WRITER = 1;
        static final int WAITER = 2;
        static final int READER = 4;
        private static final Unsafe U;
        private static final long LOCKSTATE;

        TreeBin(TreeNode<K, V> b) {
            super(-2, null, null, null);
            this.first = b;
            TreeNode r = null;
            TreeNode x = b;
            while (x != null) {
                TreeNode next = (TreeNode)x.next;
                x.right = null;
                x.left = null;
                if (r == null) {
                    x.parent = null;
                    x.red = false;
                    r = x;
                } else {
                    TreeNode xp;
                    int dir;
                    Object key = x.key;
                    int hash = x.hash;
                    Class<?> kc = null;
                    TreeNode p = r;
                    do {
                        int ph;
                        dir = (ph = p.hash) > hash ? -1 : (ph < hash ? 1 : (kc != null || (kc = ConcurrentHashMapV8.comparableClassFor(key)) != null ? ConcurrentHashMapV8.compareComparables(kc, key, p.key) : 0));
                        xp = p;
                    } while ((p = dir <= 0 ? p.left : p.right) != null);
                    x.parent = xp;
                    if (dir <= 0) {
                        xp.left = x;
                    } else {
                        xp.right = x;
                    }
                    r = TreeBin.balanceInsertion(r, x);
                }
                x = next;
            }
            this.root = r;
        }

        private final void lockRoot() {
            if (!U.compareAndSwapInt(this, LOCKSTATE, 0, 1)) {
                this.contendedLock();
            }
        }

        private final void unlockRoot() {
            this.lockState = 0;
        }

        private final void contendedLock() {
            boolean waiting = false;
            while (true) {
                int s;
                if (((s = this.lockState) & 1) == 0) {
                    if (!U.compareAndSwapInt(this, LOCKSTATE, s, 1)) continue;
                    if (waiting) {
                        this.waiter = null;
                    }
                    return;
                }
                if ((s & 2) == 0) {
                    if (!U.compareAndSwapInt(this, LOCKSTATE, s, s | 2)) continue;
                    waiting = true;
                    this.waiter = Thread.currentThread();
                    continue;
                }
                if (!waiting) continue;
                LockSupport.park(this);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        final Node<K, V> find(int h, Object k) {
            if (k != null) {
                Node e = this.first;
                while (e != null) {
                    int s = this.lockState;
                    if ((s & 3) != 0) {
                        Object ek;
                        if (e.hash == h && ((ek = e.key) == k || ek != null && k.equals(ek))) {
                            return e;
                        }
                    } else if (U.compareAndSwapInt(this, LOCKSTATE, s, s + 4)) {
                        TreeNode<K, V> p;
                        try {
                            TreeNode<K, V> r = this.root;
                            p = r == null ? null : r.findTreeNode(h, k, null);
                        }
                        finally {
                            Thread w;
                            int ls;
                            while (!U.compareAndSwapInt(this, LOCKSTATE, ls = this.lockState, ls - 4)) {
                            }
                            if (ls == 6 && (w = this.waiter) != null) {
                                LockSupport.unpark(w);
                            }
                        }
                        return p;
                    }
                    e = e.next;
                }
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        final TreeNode<K, V> putTreeVal(int h, K k, V v) {
            block21: {
                TreeNode<K, V> xp;
                int dir;
                Class<?> kc = null;
                TreeNode<K, V> p = this.root;
                do {
                    if (p == null) {
                        this.root = new TreeNode<K, V>(h, k, v, null, null);
                        this.first = this.root;
                        break block21;
                    }
                    int ph = p.hash;
                    if (ph > h) {
                        dir = -1;
                    } else if (ph < h) {
                        dir = 1;
                    } else {
                        Object pk = p.key;
                        if (pk == k || pk != null && k.equals(pk)) {
                            return p;
                        }
                        if (kc == null && (kc = ConcurrentHashMapV8.comparableClassFor(k)) == null || (dir = ConcurrentHashMapV8.compareComparables(kc, k, pk)) == 0) {
                            if (p.left == null) {
                                dir = 1;
                            } else {
                                TreeNode q;
                                TreeNode pr = p.right;
                                if (pr == null || (q = pr.findTreeNode(h, k, kc)) == null) {
                                    dir = -1;
                                } else {
                                    return q;
                                }
                            }
                        }
                    }
                    xp = p;
                } while ((p = dir < 0 ? p.left : p.right) != null);
                TreeNode<K, V> f = this.first;
                TreeNode<K, V> x = new TreeNode<K, V>(h, k, v, f, xp);
                this.first = x;
                if (f != null) {
                    f.prev = x;
                }
                if (dir < 0) {
                    xp.left = x;
                } else {
                    xp.right = x;
                }
                if (!xp.red) {
                    x.red = true;
                } else {
                    this.lockRoot();
                    try {
                        this.root = TreeBin.balanceInsertion(this.root, x);
                    }
                    finally {
                        this.unlockRoot();
                    }
                }
            }
            assert (TreeBin.checkInvariants(this.root));
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        final boolean removeTreeNode(TreeNode<K, V> p) {
            TreeNode rl;
            TreeNode next = (TreeNode)p.next;
            TreeNode pred = p.prev;
            if (pred == null) {
                this.first = next;
            } else {
                pred.next = next;
            }
            if (next != null) {
                next.prev = pred;
            }
            if (this.first == null) {
                this.root = null;
                return true;
            }
            TreeNode<K, V> r = this.root;
            if (r == null || r.right == null || (rl = r.left) == null || rl.left == null) {
                return true;
            }
            this.lockRoot();
            try {
                TreeNode pp;
                TreeNode replacement;
                TreeNode pl = p.left;
                TreeNode pr = p.right;
                if (pl != null && pr != null) {
                    TreeNode sl;
                    TreeNode s = pr;
                    while ((sl = s.left) != null) {
                        s = sl;
                    }
                    boolean c = s.red;
                    s.red = p.red;
                    p.red = c;
                    TreeNode sr = s.right;
                    TreeNode pp2 = p.parent;
                    if (s == pr) {
                        p.parent = s;
                        s.right = p;
                    } else {
                        TreeNode sp = s.parent;
                        p.parent = sp;
                        if (p.parent != null) {
                            if (s == sp.left) {
                                sp.left = p;
                            } else {
                                sp.right = p;
                            }
                        }
                        s.right = pr;
                        pr.parent = s;
                    }
                    p.left = null;
                    s.left = pl;
                    pl.parent = s;
                    p.right = sr;
                    if (p.right != null) {
                        sr.parent = p;
                    }
                    if ((s.parent = pp2) == null) {
                        r = s;
                    } else if (p == pp2.left) {
                        pp2.left = s;
                    } else {
                        pp2.right = s;
                    }
                    replacement = sr != null ? sr : p;
                } else {
                    replacement = pl != null ? pl : (pr != null ? pr : p);
                }
                if (replacement != p) {
                    replacement.parent = p.parent;
                    pp = replacement.parent;
                    if (pp == null) {
                        r = replacement;
                    } else if (p == pp.left) {
                        pp.left = replacement;
                    } else {
                        pp.right = replacement;
                    }
                    p.parent = null;
                    p.right = null;
                    p.left = null;
                }
                TreeNode<K, V> treeNode = this.root = p.red ? r : TreeBin.balanceDeletion(r, replacement);
                if (p == replacement && (pp = p.parent) != null) {
                    if (p == pp.left) {
                        pp.left = null;
                    } else if (p == pp.right) {
                        pp.right = null;
                    }
                    p.parent = null;
                }
            }
            finally {
                this.unlockRoot();
            }
            assert (TreeBin.checkInvariants(this.root));
            return false;
        }

        static <K, V> TreeNode<K, V> rotateLeft(TreeNode<K, V> root, TreeNode<K, V> p) {
            TreeNode r;
            if (p != null && (r = p.right) != null) {
                p.right = r.left;
                TreeNode rl = p.right;
                if (p.right != null) {
                    rl.parent = p;
                }
                TreeNode pp = r.parent = p.parent;
                if (r.parent == null) {
                    root = r;
                    r.red = false;
                } else if (pp.left == p) {
                    pp.left = r;
                } else {
                    pp.right = r;
                }
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        static <K, V> TreeNode<K, V> rotateRight(TreeNode<K, V> root, TreeNode<K, V> p) {
            TreeNode l;
            if (p != null && (l = p.left) != null) {
                p.left = l.right;
                TreeNode lr = p.left;
                if (p.left != null) {
                    lr.parent = p;
                }
                TreeNode pp = l.parent = p.parent;
                if (l.parent == null) {
                    root = l;
                    l.red = false;
                } else if (pp.right == p) {
                    pp.right = l;
                } else {
                    pp.left = l;
                }
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        static <K, V> TreeNode<K, V> balanceInsertion(TreeNode<K, V> root, TreeNode<K, V> x) {
            x.red = true;
            while (true) {
                TreeNode xpp;
                TreeNode xp;
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                if (!xp.red || (xpp = xp.parent) == null) {
                    return root;
                }
                TreeNode xppl = xpp.left;
                if (xp == xppl) {
                    TreeNode xppr = xpp.right;
                    if (xppr != null && xppr.red) {
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                        continue;
                    }
                    if (x == xp.right) {
                        x = xp;
                        root = TreeBin.rotateLeft(root, x);
                        xp = x.parent;
                        TreeNode treeNode = xpp = xp == null ? null : xp.parent;
                    }
                    if (xp == null) continue;
                    xp.red = false;
                    if (xpp == null) continue;
                    xpp.red = true;
                    root = TreeBin.rotateRight(root, xpp);
                    continue;
                }
                if (xppl != null && xppl.red) {
                    xppl.red = false;
                    xp.red = false;
                    xpp.red = true;
                    x = xpp;
                    continue;
                }
                if (x == xp.left) {
                    x = xp;
                    root = TreeBin.rotateRight(root, x);
                    xp = x.parent;
                    TreeNode treeNode = xpp = xp == null ? null : xp.parent;
                }
                if (xp == null) continue;
                xp.red = false;
                if (xpp == null) continue;
                xpp.red = true;
                root = TreeBin.rotateLeft(root, xpp);
            }
        }

        static <K, V> TreeNode<K, V> balanceDeletion(TreeNode<K, V> root, TreeNode<K, V> x) {
            while (x != null && x != root) {
                TreeNode sr;
                TreeNode sl;
                TreeNode xp = x.parent;
                if (xp == null) {
                    x.red = false;
                    return x;
                }
                if (x.red) {
                    x.red = false;
                    return root;
                }
                TreeNode xpl = xp.left;
                if (xpl == x) {
                    TreeNode xpr = xp.right;
                    if (xpr != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = TreeBin.rotateLeft(root, xp);
                        xp = x.parent;
                        TreeNode treeNode = xpr = xp == null ? null : xp.right;
                    }
                    if (xpr == null) {
                        x = xp;
                        continue;
                    }
                    sl = xpr.left;
                    sr = xpr.right;
                    if (!(sr != null && sr.red || sl != null && sl.red)) {
                        xpr.red = true;
                        x = xp;
                        continue;
                    }
                    if (sr == null || !sr.red) {
                        if (sl != null) {
                            sl.red = false;
                        }
                        xpr.red = true;
                        root = TreeBin.rotateRight(root, xpr);
                        xp = x.parent;
                        TreeNode treeNode = xpr = xp == null ? null : xp.right;
                    }
                    if (xpr != null) {
                        xpr.red = xp == null ? false : xp.red;
                        sr = xpr.right;
                        if (sr != null) {
                            sr.red = false;
                        }
                    }
                    if (xp != null) {
                        xp.red = false;
                        root = TreeBin.rotateLeft(root, xp);
                    }
                    x = root;
                    continue;
                }
                if (xpl != null && xpl.red) {
                    xpl.red = false;
                    xp.red = true;
                    root = TreeBin.rotateRight(root, xp);
                    xp = x.parent;
                    TreeNode treeNode = xpl = xp == null ? null : xp.left;
                }
                if (xpl == null) {
                    x = xp;
                    continue;
                }
                sl = xpl.left;
                sr = xpl.right;
                if (!(sl != null && sl.red || sr != null && sr.red)) {
                    xpl.red = true;
                    x = xp;
                    continue;
                }
                if (sl == null || !sl.red) {
                    if (sr != null) {
                        sr.red = false;
                    }
                    xpl.red = true;
                    root = TreeBin.rotateLeft(root, xpl);
                    xp = x.parent;
                    TreeNode treeNode = xpl = xp == null ? null : xp.left;
                }
                if (xpl != null) {
                    xpl.red = xp == null ? false : xp.red;
                    sl = xpl.left;
                    if (sl != null) {
                        sl.red = false;
                    }
                }
                if (xp != null) {
                    xp.red = false;
                    root = TreeBin.rotateRight(root, xp);
                }
                x = root;
            }
            return root;
        }

        static <K, V> boolean checkInvariants(TreeNode<K, V> t) {
            TreeNode tp = t.parent;
            TreeNode tl = t.left;
            TreeNode tr = t.right;
            TreeNode tb = t.prev;
            TreeNode tn = (TreeNode)t.next;
            if (tb != null && tb.next != t) {
                return false;
            }
            if (tn != null && tn.prev != t) {
                return false;
            }
            if (tp != null && t != tp.left && t != tp.right) {
                return false;
            }
            if (tl != null && (tl.parent != t || tl.hash > t.hash)) {
                return false;
            }
            if (tr != null && (tr.parent != t || tr.hash < t.hash)) {
                return false;
            }
            if (t.red && tl != null && tl.red && tr != null && tr.red) {
                return false;
            }
            if (tl != null && !TreeBin.checkInvariants(tl)) {
                return false;
            }
            return tr == null || TreeBin.checkInvariants(tr);
        }

        static {
            try {
                U = ConcurrentHashMapV8.getUnsafe();
                Class<TreeBin> k = TreeBin.class;
                LOCKSTATE = U.objectFieldOffset(k.getDeclaredField("lockState"));
            }
            catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    static final class TreeNode<K, V>
    extends Node<K, V> {
        TreeNode<K, V> parent;
        TreeNode<K, V> left;
        TreeNode<K, V> right;
        TreeNode<K, V> prev;
        boolean red;

        TreeNode(int hash, K key, V val, Node<K, V> next, TreeNode<K, V> parent) {
            super(hash, key, val, next);
            this.parent = parent;
        }

        @Override
        Node<K, V> find(int h, Object k) {
            return this.findTreeNode(h, k, null);
        }

        final TreeNode<K, V> findTreeNode(int h, Object k, Class<?> kc) {
            if (k != null) {
                TreeNode<K, V> p = this;
                do {
                    TreeNode<K, V> q;
                    int dir;
                    TreeNode<K, V> pl = p.left;
                    TreeNode<K, V> pr = p.right;
                    int ph = p.hash;
                    if (ph > h) {
                        p = pl;
                        continue;
                    }
                    if (ph < h) {
                        p = pr;
                        continue;
                    }
                    Object pk = p.key;
                    if (pk == k || pk != null && k.equals(pk)) {
                        return p;
                    }
                    if (pl == null && pr == null) break;
                    if ((kc != null || (kc = ConcurrentHashMapV8.comparableClassFor(k)) != null) && (dir = ConcurrentHashMapV8.compareComparables(kc, k, pk)) != 0) {
                        p = dir < 0 ? pl : pr;
                        continue;
                    }
                    if (pl == null) {
                        p = pr;
                        continue;
                    }
                    if (pr == null || (q = pr.findTreeNode(h, k, kc)) == null) {
                        p = pl;
                        continue;
                    }
                    return q;
                } while (p != null);
            }
            return null;
        }
    }

    static final class ReservationNode<K, V>
    extends Node<K, V> {
        ReservationNode() {
            super(-3, null, null, null);
        }

        @Override
        Node<K, V> find(int h, Object k) {
            return null;
        }
    }

    static final class ForwardingNode<K, V>
    extends Node<K, V> {
        final Node<K, V>[] nextTable;

        ForwardingNode(Node<K, V>[] tab) {
            super(-1, null, null, null);
            this.nextTable = tab;
        }

        @Override
        Node<K, V> find(int h, Object k) {
            Node<K, V>[] tab = this.nextTable;
            block0: while (true) {
                Node<K, V> e;
                int n;
                if (k == null || tab == null || (n = tab.length) == 0 || (e = ConcurrentHashMapV8.tabAt(tab, n - 1 & h)) == null) {
                    return null;
                }
                do {
                    Object ek;
                    int eh;
                    if ((eh = e.hash) == h && ((ek = e.key) == k || ek != null && k.equals(ek))) {
                        return e;
                    }
                    if (eh >= 0) continue;
                    if (e instanceof ForwardingNode) {
                        tab = ((ForwardingNode)e).nextTable;
                        continue block0;
                    }
                    return e.find(h, k);
                } while ((e = e.next) != null);
                break;
            }
            return null;
        }
    }

    static class Segment<K, V>
    extends ReentrantLock
    implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;

        Segment(float lf) {
            this.loadFactor = lf;
        }
    }

    static class Node<K, V>
    implements Map.Entry<K, V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K, V> next;

        Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        @Override
        public final K getKey() {
            return this.key;
        }

        @Override
        public final V getValue() {
            return this.val;
        }

        @Override
        public final int hashCode() {
            return this.key.hashCode() ^ this.val.hashCode();
        }

        public final String toString() {
            return this.key + "=" + this.val;
        }

        @Override
        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean equals(Object o) {
            V u;
            Object v;
            Map.Entry e;
            Object k;
            return !(!(o instanceof Map.Entry) || (k = (e = (Map.Entry)o).getKey()) == null || (v = e.getValue()) == null || k != this.key && !k.equals(this.key) || v != (u = this.val) && !v.equals(u));
        }

        Node<K, V> find(int h, Object k) {
            Node<K, V> e = this;
            if (k != null) {
                do {
                    K ek;
                    if (e.hash != h || (ek = e.key) != k && (ek == null || !k.equals(ek))) continue;
                    return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }

    public static interface IntByIntToInt {
        public int apply(int var1, int var2);
    }

    public static interface LongByLongToLong {
        public long apply(long var1, long var3);
    }

    public static interface DoubleByDoubleToDouble {
        public double apply(double var1, double var3);
    }

    public static interface ObjectByObjectToInt<A, B> {
        public int apply(A var1, B var2);
    }

    public static interface ObjectByObjectToLong<A, B> {
        public long apply(A var1, B var2);
    }

    public static interface ObjectByObjectToDouble<A, B> {
        public double apply(A var1, B var2);
    }

    public static interface ObjectToInt<A> {
        public int apply(A var1);
    }

    public static interface ObjectToLong<A> {
        public long apply(A var1);
    }

    public static interface ObjectToDouble<A> {
        public double apply(A var1);
    }

    public static interface BiFun<A, B, T> {
        public T apply(A var1, B var2);
    }

    public static interface Fun<A, T> {
        public T apply(A var1);
    }

    public static interface BiAction<A, B> {
        public void apply(A var1, B var2);
    }

    public static interface Action<A> {
        public void apply(A var1);
    }

    public static interface ConcurrentHashMapSpliterator<T> {
        public ConcurrentHashMapSpliterator<T> trySplit();

        public long estimateSize();

        public void forEachRemaining(Action<? super T> var1);

        public boolean tryAdvance(Action<? super T> var1);
    }
}

