package net.optifine.util;

import java.util.Iterator;

public class LinkedList<T>
{
    private LinkedList.Node<T> first;
    private LinkedList.Node<T> last;
    private int size;

    public void addFirst(LinkedList.Node<T> node)
    {
        this.checkNoParent(node);

        if (this.isEmpty())
        {
            this.first = node;
            this.last = node;
        }
        else
        {
            LinkedList.Node<T> nodeNext = this.first;
            node.setNext(nodeNext);
            node.setPrev(node);
            this.first = node;
        }

        node.setParent(this);
        ++this.size;
    }

    public void addLast(LinkedList.Node<T> node)
    {
        this.checkNoParent(node);

        if (this.isEmpty())
        {
            this.first = node;
            this.last = node;
        }
        else
        {
            LinkedList.Node<T> nodePrev = this.last;
            node.setPrev(nodePrev);
            node.setNext(node);
            this.last = node;
        }

        node.setParent(this);
        ++this.size;
    }

    public void addAfter(final Node<T> nodePrev, final Node<T> node) {
        if (nodePrev == null) {
            this.addFirst(node);
            return;
        }
        if (nodePrev == this.last) {
            this.addLast(node);
            return;
        }
        this.checkParent(nodePrev);
        this.checkNoParent(node);
        final Node<T> nodeNext = nodePrev.getNext();
        (nodePrev).setNext(node);
        (node).setPrev(nodePrev);
        (nodeNext).setPrev(node);
        (node).setNext(nodeNext);
         node.setParent(this);
        ++this.size;
    }

    public Node<T> remove(final Node<T> node) {
        this.checkParent(node);
        final Node<T> prev = node.getPrev();
        final Node<T> next = node.getNext();
        if (prev != null) {
            (prev).setNext(next);
        }
        else {
            this.first = next;
        }
        if (next != null) {
            (next).setPrev(prev);
        }
        else {
            this.last = prev;
        }
        (node).setPrev(null);
        (node).setNext(null);
        (node).setParent(null);
        --this.size;
        return node;
    }
    
    public void moveAfter(LinkedList.Node<T> nodePrev, LinkedList.Node<T> node)
    {
        this.remove(node);
        this.addAfter(nodePrev, node);
    }

    public boolean find(LinkedList.Node<T> nodeFind, LinkedList.Node<T> nodeFrom, LinkedList.Node<T> nodeTo)
    {
        this.checkParent(nodeFrom);

        if (nodeTo != null)
        {
            this.checkParent(nodeTo);
        }

        LinkedList.Node<T> node;

        for (node = nodeFrom; node != null && node != nodeTo; node = node.getNext())
        {
            if (node == nodeFind)
            {
                return true;
            }
        }

        if (node != nodeTo)
        {
            throw new IllegalArgumentException("Sublist is not linked, from: " + nodeFrom + ", to: " + nodeTo);
        }
        else
        {
            return false;
        }
    }

    private void checkParent(LinkedList.Node<T> node)
    {
        if (node.parent != this)
        {
            throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + node.parent + ", this: " + this);
        }
    }

    private void checkNoParent(LinkedList.Node<T> node)
    {
        if (node.parent != null)
        {
            throw new IllegalArgumentException("Node has different parent, node: " + node + ", parent: " + node.parent + ", this: " + this);
        }
    }

    public boolean contains(LinkedList.Node<T> node)
    {
        return node.parent == this;
    }

    public Iterator<LinkedList.Node<T>> iterator()
    {
        Iterator<LinkedList.Node<T>> iterator = new Iterator<LinkedList.Node<T>>()
        {
            LinkedList.Node<T> node = LinkedList.this.getFirst();
            public boolean hasNext()
            {
                return this.node != null;
            }
            public LinkedList.Node<T> next()
            {
                LinkedList.Node<T> node = this.node;

                if (this.node != null)
                {
                    this.node = this.node.next;
                }

                return node;
            }
        };
        return iterator;
    }

    public LinkedList.Node<T> getFirst()
    {
        return this.first;
    }

    public LinkedList.Node<T> getLast()
    {
        return this.last;
    }

    public int getSize()
    {
        return this.size;
    }

    public boolean isEmpty()
    {
        return this.size <= 0;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();

//        for (LinkedList.Node<T> node : this)
//        {
//            if (stringbuffer.length() > 0)
//            {
//                stringbuffer.append(", ");
//            }
//
//            stringbuffer.append(node.getItem());
//        }

        return "" + this.size + " [" + stringbuffer.toString() + "]";
    }

    public static class Node<T>
    {
        private final T item;
        private LinkedList.Node<T> prev;
        private LinkedList.Node<T> next;
        private LinkedList<T> parent;

        public Node(T item)
        {
            this.item = item;
        }

        public T getItem()
        {
            return this.item;
        }

        public LinkedList.Node<T> getPrev()
        {
            return this.prev;
        }

        public LinkedList.Node<T> getNext()
        {
            return this.next;
        }

        private void setPrev(LinkedList.Node<T> prev)
        {
            this.prev = prev;
        }

        private void setNext(LinkedList.Node<T> next)
        {
            this.next = next;
        }

        private void setParent(LinkedList<T> parent)
        {
            this.parent = parent;
        }

        public String toString()
        {
            return "" + this.item;
        }
    }
}
