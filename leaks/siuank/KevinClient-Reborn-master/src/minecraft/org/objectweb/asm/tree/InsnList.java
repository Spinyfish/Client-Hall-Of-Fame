package org.objectweb.asm.tree;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import org.objectweb.asm.MethodVisitor;

public class InsnList {
  private int size;
  
  private AbstractInsnNode firstInsn;
  
  private AbstractInsnNode lastInsn;
  
  AbstractInsnNode[] cache;
  
  public int size() { return this.size; }
  
  public AbstractInsnNode getFirst() { return this.firstInsn; }
  
  public AbstractInsnNode getLast() { return this.lastInsn; }
  
  public AbstractInsnNode get(int index) {
    if (index < 0 || index >= this.size)
      throw new IndexOutOfBoundsException(); 
    if (this.cache == null)
      this.cache = toArray(); 
    return this.cache[index];
  }
  
  public boolean contains(AbstractInsnNode insnNode) {
    AbstractInsnNode currentInsn = this.firstInsn;
    while (currentInsn != null && currentInsn != insnNode)
      currentInsn = currentInsn.nextInsn; 
    return (currentInsn != null);
  }
  
  public int indexOf(AbstractInsnNode insnNode) {
    if (this.cache == null)
      this.cache = toArray(); 
    return insnNode.index;
  }
  
  public void accept(MethodVisitor methodVisitor) {
    AbstractInsnNode currentInsn = this.firstInsn;
    while (currentInsn != null) {
      currentInsn.accept(methodVisitor);
      currentInsn = currentInsn.nextInsn;
    } 
  }
  
  public ListIterator<AbstractInsnNode> iterator() { return iterator(0); }
  
  public ListIterator<AbstractInsnNode> iterator(int index) { return new InsnListIterator(index); }
  
  public AbstractInsnNode[] toArray() {
    int currentInsnIndex = 0;
    AbstractInsnNode currentInsn = this.firstInsn;
    AbstractInsnNode[] insnNodeArray = new AbstractInsnNode[this.size];
    while (currentInsn != null) {
      insnNodeArray[currentInsnIndex] = currentInsn;
      currentInsn.index = currentInsnIndex++;
      currentInsn = currentInsn.nextInsn;
    } 
    return insnNodeArray;
  }
  
  public void set(AbstractInsnNode oldInsnNode, AbstractInsnNode newInsnNode) {
    AbstractInsnNode nextInsn = oldInsnNode.nextInsn;
    newInsnNode.nextInsn = nextInsn;
    if (nextInsn != null) {
      nextInsn.previousInsn = newInsnNode;
    } else {
      this.lastInsn = newInsnNode;
    } 
    AbstractInsnNode previousInsn = oldInsnNode.previousInsn;
    newInsnNode.previousInsn = previousInsn;
    if (previousInsn != null) {
      previousInsn.nextInsn = newInsnNode;
    } else {
      this.firstInsn = newInsnNode;
    } 
    if (this.cache != null) {
      int index = oldInsnNode.index;
      this.cache[index] = newInsnNode;
      newInsnNode.index = index;
    } else {
      newInsnNode.index = 0;
    } 
    oldInsnNode.index = -1;
    oldInsnNode.previousInsn = null;
    oldInsnNode.nextInsn = null;
  }
  
  public void add(AbstractInsnNode insnNode) {
    this.size++;
    if (this.lastInsn == null) {
      this.firstInsn = insnNode;
      this.lastInsn = insnNode;
    } else {
      this.lastInsn.nextInsn = insnNode;
      insnNode.previousInsn = this.lastInsn;
    } 
    this.lastInsn = insnNode;
    this.cache = null;
    insnNode.index = 0;
  }
  
  public void add(InsnList insnList) {
    if (insnList.size == 0)
      return; 
    this.size += insnList.size;
    if (this.lastInsn == null) {
      this.firstInsn = insnList.firstInsn;
      this.lastInsn = insnList.lastInsn;
    } else {
      AbstractInsnNode firstInsnListElement = insnList.firstInsn;
      this.lastInsn.nextInsn = firstInsnListElement;
      firstInsnListElement.previousInsn = this.lastInsn;
      this.lastInsn = insnList.lastInsn;
    } 
    this.cache = null;
    insnList.removeAll(false);
  }
  
  public void insert(AbstractInsnNode insnNode) {
    this.size++;
    if (this.firstInsn == null) {
      this.firstInsn = insnNode;
      this.lastInsn = insnNode;
    } else {
      this.firstInsn.previousInsn = insnNode;
      insnNode.nextInsn = this.firstInsn;
    } 
    this.firstInsn = insnNode;
    this.cache = null;
    insnNode.index = 0;
  }
  
  public void insert(InsnList insnList) {
    if (insnList.size == 0)
      return; 
    this.size += insnList.size;
    if (this.firstInsn == null) {
      this.firstInsn = insnList.firstInsn;
      this.lastInsn = insnList.lastInsn;
    } else {
      AbstractInsnNode lastInsnListElement = insnList.lastInsn;
      this.firstInsn.previousInsn = lastInsnListElement;
      lastInsnListElement.nextInsn = this.firstInsn;
      this.firstInsn = insnList.firstInsn;
    } 
    this.cache = null;
    insnList.removeAll(false);
  }
  
  public void insert(AbstractInsnNode previousInsn, AbstractInsnNode insnNode) {
    this.size++;
    AbstractInsnNode nextInsn = previousInsn.nextInsn;
    if (nextInsn == null) {
      this.lastInsn = insnNode;
    } else {
      nextInsn.previousInsn = insnNode;
    } 
    previousInsn.nextInsn = insnNode;
    insnNode.nextInsn = nextInsn;
    insnNode.previousInsn = previousInsn;
    this.cache = null;
    insnNode.index = 0;
  }
  
  public void insert(AbstractInsnNode previousInsn, InsnList insnList) {
    if (insnList.size == 0)
      return; 
    this.size += insnList.size;
    AbstractInsnNode firstInsnListElement = insnList.firstInsn;
    AbstractInsnNode lastInsnListElement = insnList.lastInsn;
    AbstractInsnNode nextInsn = previousInsn.nextInsn;
    if (nextInsn == null) {
      this.lastInsn = lastInsnListElement;
    } else {
      nextInsn.previousInsn = lastInsnListElement;
    } 
    previousInsn.nextInsn = firstInsnListElement;
    lastInsnListElement.nextInsn = nextInsn;
    firstInsnListElement.previousInsn = previousInsn;
    this.cache = null;
    insnList.removeAll(false);
  }
  
  public void insertBefore(AbstractInsnNode nextInsn, AbstractInsnNode insnNode) {
    this.size++;
    AbstractInsnNode previousInsn = nextInsn.previousInsn;
    if (previousInsn == null) {
      this.firstInsn = insnNode;
    } else {
      previousInsn.nextInsn = insnNode;
    } 
    nextInsn.previousInsn = insnNode;
    insnNode.nextInsn = nextInsn;
    insnNode.previousInsn = previousInsn;
    this.cache = null;
    insnNode.index = 0;
  }
  
  public void insertBefore(AbstractInsnNode nextInsn, InsnList insnList) {
    if (insnList.size == 0)
      return; 
    this.size += insnList.size;
    AbstractInsnNode firstInsnListElement = insnList.firstInsn;
    AbstractInsnNode lastInsnListElement = insnList.lastInsn;
    AbstractInsnNode previousInsn = nextInsn.previousInsn;
    if (previousInsn == null) {
      this.firstInsn = firstInsnListElement;
    } else {
      previousInsn.nextInsn = firstInsnListElement;
    } 
    nextInsn.previousInsn = lastInsnListElement;
    lastInsnListElement.nextInsn = nextInsn;
    firstInsnListElement.previousInsn = previousInsn;
    this.cache = null;
    insnList.removeAll(false);
  }
  
  public void remove(AbstractInsnNode insnNode) {
    this.size--;
    AbstractInsnNode nextInsn = insnNode.nextInsn;
    AbstractInsnNode previousInsn = insnNode.previousInsn;
    if (nextInsn == null) {
      if (previousInsn == null) {
        this.firstInsn = null;
        this.lastInsn = null;
      } else {
        previousInsn.nextInsn = null;
        this.lastInsn = previousInsn;
      } 
    } else if (previousInsn == null) {
      this.firstInsn = nextInsn;
      nextInsn.previousInsn = null;
    } else {
      previousInsn.nextInsn = nextInsn;
      nextInsn.previousInsn = previousInsn;
    } 
    this.cache = null;
    insnNode.index = -1;
    insnNode.previousInsn = null;
    insnNode.nextInsn = null;
  }
  
  void removeAll(boolean mark) {
    if (mark) {
      AbstractInsnNode currentInsn = this.firstInsn;
      while (currentInsn != null) {
        AbstractInsnNode next = currentInsn.nextInsn;
        currentInsn.index = -1;
        currentInsn.previousInsn = null;
        currentInsn.nextInsn = null;
        currentInsn = next;
      } 
    } 
    this.size = 0;
    this.firstInsn = null;
    this.lastInsn = null;
    this.cache = null;
  }
  
  public void clear() { removeAll(false); }
  
  public void resetLabels() {
    AbstractInsnNode currentInsn = this.firstInsn;
    while (currentInsn != null) {
      if (currentInsn instanceof LabelNode)
        ((LabelNode)currentInsn).resetLabel(); 
      currentInsn = currentInsn.nextInsn;
    } 
  }
  
  private final class InsnListIterator implements ListIterator {
    AbstractInsnNode nextInsn;
    
    AbstractInsnNode previousInsn;
    
    AbstractInsnNode remove;

    InsnList this$0;

    InsnListIterator(int index) {
      if (index == this$0.size()) {
        this.nextInsn = null;
        this.previousInsn = this$0.getLast();
      } else {
        this.nextInsn = this$0.get(index);
        this.previousInsn = this.nextInsn.previousInsn;
      } 
    }
    
    public boolean hasNext() { return (this.nextInsn != null); }
    
    public Object next() {
      if (this.nextInsn == null)
        throw new NoSuchElementException(); 
      AbstractInsnNode result = this.nextInsn;
      this.previousInsn = result;
      this.nextInsn = result.nextInsn;
      this.remove = result;
      return result;
    }
    
    public void remove() {
      if (this.remove != null) {
        if (this.remove == this.nextInsn) {
          this.nextInsn = this.nextInsn.nextInsn;
        } else {
          this.previousInsn = this.previousInsn.previousInsn;
        } 
        InsnList.this.remove(this.remove);
        this.remove = null;
      } else {
        throw new IllegalStateException();
      } 
    }
    
    public boolean hasPrevious() { return (this.previousInsn != null); }
    
    public Object previous() {
      AbstractInsnNode result = this.previousInsn;
      this.nextInsn = result;
      this.previousInsn = result.previousInsn;
      this.remove = result;
      return result;
    }
    
    public int nextIndex() {
      if (this.nextInsn == null)
        return InsnList.this.size(); 
      if (InsnList.this.cache == null)
        InsnList.this.cache = InsnList.this.toArray(); 
      return this.nextInsn.index;
    }
    
    public int previousIndex() {
      if (this.previousInsn == null)
        return -1; 
      if (InsnList.this.cache == null)
        InsnList.this.cache = InsnList.this.toArray(); 
      return this.previousInsn.index;
    }
    
    public void add(Object o) {
      if (this.nextInsn != null) {
        InsnList.this.insertBefore(this.nextInsn, (AbstractInsnNode)o);
      } else if (this.previousInsn != null) {
        InsnList.this.insert(this.previousInsn, (AbstractInsnNode)o);
      } else {
        InsnList.this.add((AbstractInsnNode)o);
      } 
      this.previousInsn = (AbstractInsnNode)o;
      this.remove = null;
    }
    
    public void set(Object o) {
      if (this.remove != null) {
        InsnList.this.set(this.remove, (AbstractInsnNode)o);
        if (this.remove == this.previousInsn) {
          this.previousInsn = (AbstractInsnNode)o;
        } else {
          this.nextInsn = (AbstractInsnNode)o;
        } 
      } else {
        throw new IllegalStateException();
      } 
    }
  }
}
