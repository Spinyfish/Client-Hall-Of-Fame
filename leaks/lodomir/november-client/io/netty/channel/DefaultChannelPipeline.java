/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel;

import io.netty.channel.AbstractChannel;
import io.netty.channel.AbstractChannelHandlerContext;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPipelineException;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

final class DefaultChannelPipeline
implements ChannelPipeline {
    static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
    private static final WeakHashMap<Class<?>, String>[] nameCaches = new WeakHashMap[Runtime.getRuntime().availableProcessors()];
    final AbstractChannel channel;
    final AbstractChannelHandlerContext head;
    final AbstractChannelHandlerContext tail;
    private final Map<String, AbstractChannelHandlerContext> name2ctx = new HashMap<String, AbstractChannelHandlerContext>(4);
    final Map<EventExecutorGroup, EventExecutor> childExecutors = new IdentityHashMap<EventExecutorGroup, EventExecutor>();

    public DefaultChannelPipeline(AbstractChannel channel) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        this.channel = channel;
        this.tail = new TailContext(this);
        this.head = new HeadContext(this);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }

    @Override
    public Channel channel() {
        return this.channel;
    }

    @Override
    public ChannelPipeline addFirst(String name, ChannelHandler handler) {
        return this.addFirst(null, name, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            this.checkDuplicateName(name);
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
            this.addFirst0(name, newCtx);
        }
        return this;
    }

    private void addFirst0(String name, AbstractChannelHandlerContext newCtx) {
        DefaultChannelPipeline.checkMultiplicity(newCtx);
        AbstractChannelHandlerContext nextCtx = this.head.next;
        newCtx.prev = this.head;
        newCtx.next = nextCtx;
        this.head.next = newCtx;
        nextCtx.prev = newCtx;
        this.name2ctx.put(name, newCtx);
        this.callHandlerAdded(newCtx);
    }

    @Override
    public ChannelPipeline addLast(String name, ChannelHandler handler) {
        return this.addLast(null, name, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            this.checkDuplicateName(name);
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
            this.addLast0(name, newCtx);
        }
        return this;
    }

    private void addLast0(String name, AbstractChannelHandlerContext newCtx) {
        AbstractChannelHandlerContext prev;
        DefaultChannelPipeline.checkMultiplicity(newCtx);
        newCtx.prev = prev = this.tail.prev;
        newCtx.next = this.tail;
        prev.next = newCtx;
        this.tail.prev = newCtx;
        this.name2ctx.put(name, newCtx);
        this.callHandlerAdded(newCtx);
    }

    @Override
    public ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
        return this.addBefore(null, baseName, name, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            AbstractChannelHandlerContext ctx = this.getContextOrDie(baseName);
            this.checkDuplicateName(name);
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
            this.addBefore0(name, ctx, newCtx);
        }
        return this;
    }

    private void addBefore0(String name, AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
        DefaultChannelPipeline.checkMultiplicity(newCtx);
        newCtx.prev = ctx.prev;
        newCtx.next = ctx;
        ctx.prev.next = newCtx;
        ctx.prev = newCtx;
        this.name2ctx.put(name, newCtx);
        this.callHandlerAdded(newCtx);
    }

    @Override
    public ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
        return this.addAfter(null, baseName, name, handler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            AbstractChannelHandlerContext ctx = this.getContextOrDie(baseName);
            this.checkDuplicateName(name);
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(this, group, name, handler);
            this.addAfter0(name, ctx, newCtx);
        }
        return this;
    }

    private void addAfter0(String name, AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
        this.checkDuplicateName(name);
        DefaultChannelPipeline.checkMultiplicity(newCtx);
        newCtx.prev = ctx;
        newCtx.next = ctx.next;
        ctx.next.prev = newCtx;
        ctx.next = newCtx;
        this.name2ctx.put(name, newCtx);
        this.callHandlerAdded(newCtx);
    }

    @Override
    public ChannelPipeline addFirst(ChannelHandler ... handlers) {
        return this.addFirst((EventExecutorGroup)null, handlers);
    }

    @Override
    public ChannelPipeline addFirst(EventExecutorGroup executor, ChannelHandler ... handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers");
        }
        if (handlers.length == 0 || handlers[0] == null) {
            return this;
        }
        for (int size = 1; size < handlers.length && handlers[size] != null; ++size) {
        }
        for (int i = size - 1; i >= 0; --i) {
            ChannelHandler h = handlers[i];
            this.addFirst(executor, this.generateName(h), h);
        }
        return this;
    }

    @Override
    public ChannelPipeline addLast(ChannelHandler ... handlers) {
        return this.addLast((EventExecutorGroup)null, handlers);
    }

    @Override
    public ChannelPipeline addLast(EventExecutorGroup executor, ChannelHandler ... handlers) {
        if (handlers == null) {
            throw new NullPointerException("handlers");
        }
        for (ChannelHandler h : handlers) {
            if (h == null) break;
            this.addLast(executor, this.generateName(h), h);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String generateName(ChannelHandler handler) {
        String name;
        WeakHashMap<Class<?>, String> cache = nameCaches[(int)(Thread.currentThread().getId() % (long)nameCaches.length)];
        Class<?> handlerType = handler.getClass();
        Object object = cache;
        synchronized (object) {
            name = cache.get(handlerType);
            if (name == null) {
                name = DefaultChannelPipeline.generateName0(handlerType);
                cache.put(handlerType, name);
            }
        }
        object = this;
        synchronized (object) {
            if (this.name2ctx.containsKey(name)) {
                String baseName = name.substring(0, name.length() - 1);
                int i = 1;
                while (true) {
                    String newName;
                    if (!this.name2ctx.containsKey(newName = baseName + i)) {
                        name = newName;
                        break;
                    }
                    ++i;
                }
            }
        }
        return name;
    }

    private static String generateName0(Class<?> handlerType) {
        return StringUtil.simpleClassName(handlerType) + "#0";
    }

    @Override
    public ChannelPipeline remove(ChannelHandler handler) {
        this.remove(this.getContextOrDie(handler));
        return this;
    }

    @Override
    public ChannelHandler remove(String name) {
        return this.remove(this.getContextOrDie(name)).handler();
    }

    @Override
    public <T extends ChannelHandler> T remove(Class<T> handlerType) {
        return (T)this.remove(this.getContextOrDie(handlerType)).handler();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private AbstractChannelHandlerContext remove(final AbstractChannelHandlerContext ctx) {
        AbstractChannelHandlerContext context;
        Future<?> future;
        assert (ctx != this.head && ctx != this.tail);
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            if (!ctx.channel().isRegistered() || ctx.executor().inEventLoop()) {
                this.remove0(ctx);
                return ctx;
            }
            future = ctx.executor().submit(new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    DefaultChannelPipeline defaultChannelPipeline = DefaultChannelPipeline.this;
                    synchronized (defaultChannelPipeline) {
                        DefaultChannelPipeline.this.remove0(ctx);
                    }
                }
            });
            context = ctx;
        }
        DefaultChannelPipeline.waitForFuture(future);
        return context;
    }

    void remove0(AbstractChannelHandlerContext ctx) {
        AbstractChannelHandlerContext next;
        AbstractChannelHandlerContext prev = ctx.prev;
        prev.next = next = ctx.next;
        next.prev = prev;
        this.name2ctx.remove(ctx.name());
        this.callHandlerRemoved(ctx);
    }

    @Override
    public ChannelHandler removeFirst() {
        if (this.head.next == this.tail) {
            throw new NoSuchElementException();
        }
        return this.remove(this.head.next).handler();
    }

    @Override
    public ChannelHandler removeLast() {
        if (this.head.next == this.tail) {
            throw new NoSuchElementException();
        }
        return this.remove(this.tail.prev).handler();
    }

    @Override
    public ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
        this.replace(this.getContextOrDie(oldHandler), newName, newHandler);
        return this;
    }

    @Override
    public ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
        return this.replace(this.getContextOrDie(oldName), newName, newHandler);
    }

    @Override
    public <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
        return (T)this.replace(this.getContextOrDie(oldHandlerType), newName, newHandler);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ChannelHandler replace(final AbstractChannelHandlerContext ctx, final String newName, ChannelHandler newHandler) {
        Future<?> future;
        assert (ctx != this.head && ctx != this.tail);
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            DefaultChannelHandlerContext newCtx;
            boolean sameName = ctx.name().equals(newName);
            if (!sameName) {
                this.checkDuplicateName(newName);
            }
            if (!(newCtx = new DefaultChannelHandlerContext(this, ctx.executor, newName, newHandler)).channel().isRegistered() || newCtx.executor().inEventLoop()) {
                this.replace0(ctx, newName, newCtx);
                return ctx.handler();
            }
            future = newCtx.executor().submit(new Runnable(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                @Override
                public void run() {
                    DefaultChannelPipeline defaultChannelPipeline = DefaultChannelPipeline.this;
                    synchronized (defaultChannelPipeline) {
                        DefaultChannelPipeline.this.replace0(ctx, newName, newCtx);
                    }
                }
            });
        }
        DefaultChannelPipeline.waitForFuture(future);
        return ctx.handler();
    }

    private void replace0(AbstractChannelHandlerContext oldCtx, String newName, AbstractChannelHandlerContext newCtx) {
        DefaultChannelPipeline.checkMultiplicity(newCtx);
        AbstractChannelHandlerContext prev = oldCtx.prev;
        AbstractChannelHandlerContext next = oldCtx.next;
        newCtx.prev = prev;
        newCtx.next = next;
        prev.next = newCtx;
        next.prev = newCtx;
        if (!oldCtx.name().equals(newName)) {
            this.name2ctx.remove(oldCtx.name());
        }
        this.name2ctx.put(newName, newCtx);
        oldCtx.prev = newCtx;
        oldCtx.next = newCtx;
        this.callHandlerAdded(newCtx);
        this.callHandlerRemoved(oldCtx);
    }

    private static void checkMultiplicity(ChannelHandlerContext ctx) {
        ChannelHandler handler = ctx.handler();
        if (handler instanceof ChannelHandlerAdapter) {
            ChannelHandlerAdapter h = (ChannelHandlerAdapter)handler;
            if (!h.isSharable() && h.added) {
                throw new ChannelPipelineException(h.getClass().getName() + " is not a @Sharable handler, so can't be added or removed multiple times.");
            }
            h.added = true;
        }
    }

    private void callHandlerAdded(final ChannelHandlerContext ctx) {
        if (ctx.channel().isRegistered() && !ctx.executor().inEventLoop()) {
            ctx.executor().execute(new Runnable(){

                @Override
                public void run() {
                    DefaultChannelPipeline.this.callHandlerAdded0(ctx);
                }
            });
            return;
        }
        this.callHandlerAdded0(ctx);
    }

    private void callHandlerAdded0(ChannelHandlerContext ctx) {
        try {
            ctx.handler().handlerAdded(ctx);
        }
        catch (Throwable t) {
            boolean removed;
            block5: {
                removed = false;
                try {
                    this.remove((AbstractChannelHandlerContext)ctx);
                    removed = true;
                }
                catch (Throwable t2) {
                    if (!logger.isWarnEnabled()) break block5;
                    logger.warn("Failed to remove a handler: " + ctx.name(), t2);
                }
            }
            if (removed) {
                this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; removed.", t));
            }
            this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; also failed to remove.", t));
        }
    }

    private void callHandlerRemoved(final AbstractChannelHandlerContext ctx) {
        if (ctx.channel().isRegistered() && !ctx.executor().inEventLoop()) {
            ctx.executor().execute(new Runnable(){

                @Override
                public void run() {
                    DefaultChannelPipeline.this.callHandlerRemoved0(ctx);
                }
            });
            return;
        }
        this.callHandlerRemoved0(ctx);
    }

    private void callHandlerRemoved0(AbstractChannelHandlerContext ctx) {
        try {
            ctx.handler().handlerRemoved(ctx);
            ctx.setRemoved();
        }
        catch (Throwable t) {
            this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerRemoved() has thrown an exception.", t));
        }
    }

    private static void waitForFuture(java.util.concurrent.Future<?> future) {
        try {
            future.get();
        }
        catch (ExecutionException ex) {
            PlatformDependent.throwException(ex.getCause());
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public ChannelHandler first() {
        ChannelHandlerContext first = this.firstContext();
        if (first == null) {
            return null;
        }
        return first.handler();
    }

    @Override
    public ChannelHandlerContext firstContext() {
        AbstractChannelHandlerContext first = this.head.next;
        if (first == this.tail) {
            return null;
        }
        return this.head.next;
    }

    @Override
    public ChannelHandler last() {
        AbstractChannelHandlerContext last = this.tail.prev;
        if (last == this.head) {
            return null;
        }
        return last.handler();
    }

    @Override
    public ChannelHandlerContext lastContext() {
        AbstractChannelHandlerContext last = this.tail.prev;
        if (last == this.head) {
            return null;
        }
        return last;
    }

    @Override
    public ChannelHandler get(String name) {
        ChannelHandlerContext ctx = this.context(name);
        if (ctx == null) {
            return null;
        }
        return ctx.handler();
    }

    @Override
    public <T extends ChannelHandler> T get(Class<T> handlerType) {
        ChannelHandlerContext ctx = this.context(handlerType);
        if (ctx == null) {
            return null;
        }
        return (T)ctx.handler();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ChannelHandlerContext context(String name) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        DefaultChannelPipeline defaultChannelPipeline = this;
        synchronized (defaultChannelPipeline) {
            return this.name2ctx.get(name);
        }
    }

    @Override
    public ChannelHandlerContext context(ChannelHandler handler) {
        if (handler == null) {
            throw new NullPointerException("handler");
        }
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            if (ctx.handler() == handler) {
                return ctx;
            }
            ctx = ctx.next;
        }
        return null;
    }

    @Override
    public ChannelHandlerContext context(Class<? extends ChannelHandler> handlerType) {
        if (handlerType == null) {
            throw new NullPointerException("handlerType");
        }
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            if (handlerType.isAssignableFrom(ctx.handler().getClass())) {
                return ctx;
            }
            ctx = ctx.next;
        }
        return null;
    }

    @Override
    public List<String> names() {
        ArrayList<String> list = new ArrayList<String>();
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != null) {
            list.add(ctx.name());
            ctx = ctx.next;
        }
        return list;
    }

    @Override
    public Map<String, ChannelHandler> toMap() {
        LinkedHashMap<String, ChannelHandler> map = new LinkedHashMap<String, ChannelHandler>();
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != this.tail) {
            map.put(ctx.name(), ctx.handler());
            ctx = ctx.next;
        }
        return map;
    }

    @Override
    public Iterator<Map.Entry<String, ChannelHandler>> iterator() {
        return this.toMap().entrySet().iterator();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(StringUtil.simpleClassName(this));
        buf.append('{');
        AbstractChannelHandlerContext ctx = this.head.next;
        while (ctx != this.tail) {
            buf.append('(');
            buf.append(ctx.name());
            buf.append(" = ");
            buf.append(ctx.handler().getClass().getName());
            buf.append(')');
            ctx = ctx.next;
            if (ctx == this.tail) break;
            buf.append(", ");
        }
        buf.append('}');
        return buf.toString();
    }

    @Override
    public ChannelPipeline fireChannelRegistered() {
        this.head.fireChannelRegistered();
        return this;
    }

    @Override
    public ChannelPipeline fireChannelUnregistered() {
        this.head.fireChannelUnregistered();
        if (!this.channel.isOpen()) {
            this.teardownAll();
        }
        return this;
    }

    private void teardownAll() {
        this.tail.prev.teardown();
    }

    @Override
    public ChannelPipeline fireChannelActive() {
        this.head.fireChannelActive();
        if (this.channel.config().isAutoRead()) {
            this.channel.read();
        }
        return this;
    }

    @Override
    public ChannelPipeline fireChannelInactive() {
        this.head.fireChannelInactive();
        return this;
    }

    @Override
    public ChannelPipeline fireExceptionCaught(Throwable cause) {
        this.head.fireExceptionCaught(cause);
        return this;
    }

    @Override
    public ChannelPipeline fireUserEventTriggered(Object event) {
        this.head.fireUserEventTriggered(event);
        return this;
    }

    @Override
    public ChannelPipeline fireChannelRead(Object msg) {
        this.head.fireChannelRead(msg);
        return this;
    }

    @Override
    public ChannelPipeline fireChannelReadComplete() {
        this.head.fireChannelReadComplete();
        if (this.channel.config().isAutoRead()) {
            this.read();
        }
        return this;
    }

    @Override
    public ChannelPipeline fireChannelWritabilityChanged() {
        this.head.fireChannelWritabilityChanged();
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress) {
        return this.tail.bind(localAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress) {
        return this.tail.connect(remoteAddress);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.tail.connect(remoteAddress, localAddress);
    }

    @Override
    public ChannelFuture disconnect() {
        return this.tail.disconnect();
    }

    @Override
    public ChannelFuture close() {
        return this.tail.close();
    }

    @Override
    public ChannelFuture deregister() {
        return this.tail.deregister();
    }

    @Override
    public ChannelPipeline flush() {
        this.tail.flush();
        return this;
    }

    @Override
    public ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
        return this.tail.bind(localAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
        return this.tail.connect(remoteAddress, promise);
    }

    @Override
    public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
        return this.tail.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public ChannelFuture disconnect(ChannelPromise promise) {
        return this.tail.disconnect(promise);
    }

    @Override
    public ChannelFuture close(ChannelPromise promise) {
        return this.tail.close(promise);
    }

    @Override
    public ChannelFuture deregister(ChannelPromise promise) {
        return this.tail.deregister(promise);
    }

    @Override
    public ChannelPipeline read() {
        this.tail.read();
        return this;
    }

    @Override
    public ChannelFuture write(Object msg) {
        return this.tail.write(msg);
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        return this.tail.write(msg, promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
        return this.tail.writeAndFlush(msg, promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
        return this.tail.writeAndFlush(msg);
    }

    private void checkDuplicateName(String name) {
        if (this.name2ctx.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate handler name: " + name);
        }
    }

    private AbstractChannelHandlerContext getContextOrDie(String name) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(name);
        if (ctx == null) {
            throw new NoSuchElementException(name);
        }
        return ctx;
    }

    private AbstractChannelHandlerContext getContextOrDie(ChannelHandler handler) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handler);
        if (ctx == null) {
            throw new NoSuchElementException(handler.getClass().getName());
        }
        return ctx;
    }

    private AbstractChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> handlerType) {
        AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handlerType);
        if (ctx == null) {
            throw new NoSuchElementException(handlerType.getName());
        }
        return ctx;
    }

    static /* synthetic */ String access$300(Class x0) {
        return DefaultChannelPipeline.generateName0(x0);
    }

    static {
        for (int i = 0; i < nameCaches.length; ++i) {
            DefaultChannelPipeline.nameCaches[i] = new WeakHashMap();
        }
    }

    static final class HeadContext
    extends AbstractChannelHandlerContext
    implements ChannelOutboundHandler {
        private static final String HEAD_NAME = DefaultChannelPipeline.access$300(HeadContext.class);
        protected final Channel.Unsafe unsafe;

        HeadContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, HEAD_NAME, false, true);
            this.unsafe = pipeline.channel().unsafe();
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            this.unsafe.bind(localAddress, promise);
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            this.unsafe.connect(remoteAddress, localAddress, promise);
        }

        @Override
        public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            this.unsafe.disconnect(promise);
        }

        @Override
        public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            this.unsafe.close(promise);
        }

        @Override
        public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            this.unsafe.deregister(promise);
        }

        @Override
        public void read(ChannelHandlerContext ctx) {
            this.unsafe.beginRead();
        }

        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            this.unsafe.write(msg, promise);
        }

        @Override
        public void flush(ChannelHandlerContext ctx) throws Exception {
            this.unsafe.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.fireExceptionCaught(cause);
        }
    }

    static final class TailContext
    extends AbstractChannelHandlerContext
    implements ChannelInboundHandler {
        private static final String TAIL_NAME = DefaultChannelPipeline.access$300(TailContext.class);

        TailContext(DefaultChannelPipeline pipeline) {
            super(pipeline, null, TAIL_NAME, true, false);
        }

        @Override
        public ChannelHandler handler() {
            return this;
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.warn("An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.", cause);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            try {
                logger.debug("Discarded inbound message {} that reached at the tail of the pipeline. Please check your pipeline configuration.", msg);
            }
            finally {
                ReferenceCountUtil.release(msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        }
    }
}

