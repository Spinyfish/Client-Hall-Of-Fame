package net.optifine.util;

import net.optifine.render.VboRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LinkedListTest {

    public static void main(final String[] args) throws Exception {
        final LinkedList<VboRange> linkedlist = new LinkedList();
        final List<VboRange> list = new ArrayList();
        final List<VboRange> list1 = new ArrayList();
        final Random random = new Random();
        final int i = 100;

        for (int j = 0; j < i; ++j) {
            final VboRange vborange = new VboRange();
            vborange.setPosition(j);
            list.add(vborange);
        }

        for (int k = 0; k < 100000; ++k) {
            checkLists(list, list1, i);
            checkLinkedList(linkedlist, list1.size());

            if (k % 5 == 0) {
                dbgLinkedList(linkedlist);
            }

            if (random.nextBoolean()) {
                if (!list.isEmpty()) {
                    final VboRange vborange3 = list.get(random.nextInt(list.size()));
                    final LinkedList.Node<VboRange> node2 = vborange3.getNode();

                    if (random.nextBoolean()) {
                        linkedlist.addFirst(node2);
                        dbg("Add first: " + vborange3.getPosition());
                    } else if (random.nextBoolean()) {
                        linkedlist.addLast(node2);
                        dbg("Add last: " + vborange3.getPosition());
                    } else {
                        if (list1.isEmpty()) {
                            continue;
                        }

                        final VboRange vborange1 = list1.get(random.nextInt(list1.size()));
                        final LinkedList.Node<VboRange> node1 = vborange1.getNode();
                        linkedlist.addAfter(node1, node2);
                        dbg("Add after: " + vborange1.getPosition() + ", " + vborange3.getPosition());
                    }

                    list.remove(vborange3);
                    list1.add(vborange3);
                }
            } else if (!list1.isEmpty()) {
                final VboRange vborange2 = list1.get(random.nextInt(list1.size()));
                final LinkedList.Node<VboRange> node = vborange2.getNode();
                linkedlist.remove(node);
                dbg("Remove: " + vborange2.getPosition());
                list1.remove(vborange2);
                list.add(vborange2);
            }
        }
    }

    private static void dbgLinkedList(final LinkedList<VboRange> linkedList) {
        final StringBuffer stringbuffer = new StringBuffer();

        linkedList.iterator().forEachRemaining(vboRangeNode -> {
            final LinkedList.Node<VboRange> node = vboRangeNode;
            if (node.getItem() == null) {
                return;
            }
            final VboRange vborange = node.getItem();

            if (stringbuffer.length() > 0) {
                stringbuffer.append(", ");
            }

            stringbuffer.append(vborange.getPosition());
        });

        dbg("List: " + stringbuffer);
    }

    private static void checkLinkedList(final LinkedList<VboRange> linkedList, final int used) {
        if (linkedList.getSize() != used) {
            throw new RuntimeException("Wrong size, linked: " + linkedList.getSize() + ", used: " + used);
        } else {
            int i = 0;

            for (LinkedList.Node<VboRange> node = linkedList.getFirst(); node != null; node = node.getNext()) {
                ++i;
            }

            if (linkedList.getSize() != i) {
                throw new RuntimeException("Wrong count, linked: " + linkedList.getSize() + ", count: " + i);
            } else {
                int j = 0;

                for (LinkedList.Node<VboRange> node1 = linkedList.getLast(); node1 != null; node1 = node1.getPrev()) {
                    ++j;
                }

                if (linkedList.getSize() != j) {
                    throw new RuntimeException("Wrong count back, linked: " + linkedList.getSize() + ", count: " + j);
                }
            }
        }
    }

    private static void checkLists(final List<VboRange> listFree, final List<VboRange> listUsed, final int count) {
        final int i = listFree.size() + listUsed.size();

        if (i != count) {
            throw new RuntimeException("Total size: " + i);
        }
    }

    private static void dbg(final String str) {
        System.out.println(str);
    }
}
