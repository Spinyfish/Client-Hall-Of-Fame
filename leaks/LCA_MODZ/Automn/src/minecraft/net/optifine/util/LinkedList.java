package net.optifine.util;

import java.util.Iterator;

public class LinkedList<T>
{
    private LinkedList.Node<T> first;
    private LinkedList.Node<T> last;
    private int size;

    public void addFirst(LinkedList.Node<T> cNode)
    {
        this.checkNoParent(cNode);

        if (this.isEmpty())
        {
            this.first = cNode;
            this.last = cNode;
        }
        else
        {
            LinkedList.Node<T> node = this.first;
            cNode.setNext(node);
            node.setPrev(cNode);
            this.first = cNode;
        }

        cNode.setParent(this);
        ++this.size;
    }

    public void addLast(LinkedList.Node<T> cNode)
    {
        this.checkNoParent(cNode);

        if (this.isEmpty())
        {
            this.first = cNode;
            this.last = cNode;
        }
        else
        {
            LinkedList.Node<T> node = this.last;
            cNode.setPrev(node);
            node.setNext(cNode);
            this.last = cNode;
        }

        cNode.setParent(this);
        ++this.size;
    }

    public void addAfter(LinkedList.Node<T> nodePrev, LinkedList.Node<T> cNode)
    {
        if (nodePrev == null)
        {
            this.addFirst(cNode);
        }
        else if (nodePrev == this.last)
        {
            this.addLast(cNode);
        }
        else
        {
            this.checkParent(nodePrev);
            this.checkNoParent(cNode);
            LinkedList.Node<T> nodeNext = nodePrev.getNext();
            nodePrev.setNext(cNode);
            cNode.setPrev(nodePrev);
            nodeNext.setPrev(cNode);
            cNode.setNext(nodeNext);
            cNode.setParent(this);
            ++this.size;
        }
    }

    public LinkedList.Node<T> remove(LinkedList.Node<T> cNode)
    {
        this.checkParent(cNode);
        LinkedList.Node<T> prev = cNode.getPrev();
        LinkedList.Node<T> next = cNode.getNext();

        if (prev != null)
        {
            prev.setNext(next);
        }
        else
        {
            this.first = next;
        }

        if (next != null)
        {
            next.setPrev(prev);
        }
        else
        {
            this.last = prev;
        }

        cNode.setPrev(null);
        cNode.setNext(null);
        cNode.setParent(null);
        --this.size;
        return cNode;
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
            public void remove()
            {
                throw new UnsupportedOperationException("remove");
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

        for (Iterator<Node<T>> it = iterator(); it.hasNext();) {
            Node<T> node = it.next();
            if (stringbuffer.length() > 0)
            {
                stringbuffer.append(", ");
            }

            stringbuffer.append(node.getItem());
        }

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
