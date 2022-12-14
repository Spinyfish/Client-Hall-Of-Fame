// 
// Decompiled by Procyon v0.5.36
// 

package net.optifine.util;

import java.util.Iterator;

public class LinkedList<T>
{
    private Node<T> first;
    private Node<T> last;
    private int size;
    
    public void addFirst(final Node<T> cNode) {
        this.checkNoParent(cNode);
        if (this.isEmpty()) {
            this.first = cNode;
            this.last = cNode;
        }
        else {
            final Node<T> node = this.first;
            ((Node<Object>)cNode).setNext((Node<Object>)node);
            ((Node<Object>)node).setPrev((Node<Object>)cNode);
            this.first = cNode;
        }
        ((Node<Object>)cNode).setParent(this);
        ++this.size;
    }
    
    public void addLast(final Node<T> cNode) {
        this.checkNoParent(cNode);
        if (this.isEmpty()) {
            this.first = cNode;
            this.last = cNode;
        }
        else {
            final Node<T> node = this.last;
            ((Node<Object>)cNode).setPrev((Node<Object>)node);
            ((Node<Object>)node).setNext((Node<Object>)cNode);
            this.last = cNode;
        }
        ((Node<Object>)cNode).setParent(this);
        ++this.size;
    }
    
    public void addAfter(final Node<T> nodePrev, final Node<T> cNode) {
        if (nodePrev == null) {
            this.addFirst(cNode);
        }
        else if (nodePrev == this.last) {
            this.addLast(cNode);
        }
        else {
            this.checkParent(nodePrev);
            this.checkNoParent(cNode);
            final Node<T> nodeNext = nodePrev.getNext();
            ((Node<Object>)nodePrev).setNext((Node<Object>)cNode);
            ((Node<Object>)cNode).setPrev((Node<Object>)nodePrev);
            ((Node<Object>)nodeNext).setPrev((Node<Object>)cNode);
            ((Node<Object>)cNode).setNext((Node<Object>)nodeNext);
            ((Node<Object>)cNode).setParent(this);
            ++this.size;
        }
    }
    
    public Node<T> remove(final Node<T> cNode) {
        this.checkParent(cNode);
        final Node<T> prev = cNode.getPrev();
        final Node<T> next = cNode.getNext();
        if (prev != null) {
            ((Node<Object>)prev).setNext((Node<Object>)next);
        }
        else {
            this.first = next;
        }
        if (next != null) {
            ((Node<Object>)next).setPrev((Node<Object>)prev);
        }
        else {
            this.last = prev;
        }
        ((Node<Object>)cNode).setPrev(null);
        ((Node<Object>)cNode).setNext(null);
        ((Node<Object>)cNode).setParent(null);
        --this.size;
        return cNode;
    }
    
    public void moveAfter(final Node<T> nodePrev, final Node<T> node) {
        this.remove(node);
        this.addAfter(nodePrev, node);
    }
    
    public boolean find(final Node<T> nodeFind, final Node<T> nodeFrom, final Node<T> nodeTo) {
        this.checkParent(nodeFrom);
        if (nodeTo != null) {
            this.checkParent(nodeTo);
        }
        Node<T> node;
        for (node = nodeFrom; node != null && node != nodeTo; node = node.getNext()) {
            if (node == nodeFind) {
                return true;
            }
        }
        if (node != nodeTo) {
            throw new IllegalArgumentException("Sublist is not linked, from: " + nodeFrom + ", to: " + nodeTo);
        }
        return false;
    }
    
    private void checkParent(final Node<T> node) {
        if (((Node<Object>)node).parent != this) {
            throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + ((Node<Object>)node).parent + ", this: " + this);
        }
    }
    
    private void checkNoParent(final Node<T> node) {
        if (((Node<Object>)node).parent != null) {
            throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + ((Node<Object>)node).parent + ", this: " + this);
        }
    }
    
    public boolean contains(final Node<T> node) {
        return ((Node<Object>)node).parent == this;
    }
    
    public Iterator<Node<T>> iterator() {
        final Iterator<Node<T>> iterator = new Iterator<Node<T>>() {
            Node<T> node = LinkedList.this.getFirst();
            
            @Override
            public boolean hasNext() {
                return this.node != null;
            }
            
            @Override
            public Node<T> next() {
                final Node<T> node = this.node;
                if (this.node != null) {
                    this.node = (Node<T>)((Node<Object>)this.node).next;
                }
                return node;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }
        };
        return iterator;
    }
    
    public Node<T> getFirst() {
        return this.first;
    }
    
    public Node<T> getLast() {
        return this.last;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public boolean isEmpty() {
        return this.size <= 0;
    }
    
    @Override
    public String toString() {
        final StringBuffer stringbuffer = new StringBuffer();
        for (final Node<T> node : this) {
            if (stringbuffer.length() > 0) {
                stringbuffer.append(", ");
            }
            stringbuffer.append(node.getItem());
        }
        return "" + this.size + " [" + stringbuffer.toString() + "]";
    }
    
    public static class Node<T>
    {
        private final T item;
        private Node<T> prev;
        private Node<T> next;
        private LinkedList<T> parent;
        
        public Node(final T item) {
            this.item = item;
        }
        
        public T getItem() {
            return this.item;
        }
        
        public Node<T> getPrev() {
            return this.prev;
        }
        
        public Node<T> getNext() {
            return this.next;
        }
        
        private void setPrev(final Node<T> prev) {
            this.prev = prev;
        }
        
        private void setNext(final Node<T> next) {
            this.next = next;
        }
        
        private void setParent(final LinkedList<T> parent) {
            this.parent = parent;
        }
        
        @Override
        public String toString() {
            return "" + this.item;
        }
    }
}
