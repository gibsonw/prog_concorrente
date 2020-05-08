package lists;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import interfaces.CommonList;

/**
 * List using coarse-grained synchronization.
 * 
 * @param T Item type.
 * @author Maurice Herlihy
 */

public class CoarseList<T> implements CommonList<T> {
 
    public volatile int numAdd, numRemove, numContains = 0;
    public final String listName = "CoarseList";
    /**
     * First list Node
     */
    private Node head;
    /**
     * Last list Node
     */
    private Node tail;
    /**
     * Synchronizes access to list
     */
    private Lock lock = new ReentrantLock();

    /**
     * Constructor
     */
    public CoarseList() {
        // Add sentinels to start and end
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = this.tail;
    }

    /**
     * Add an element.
     * 
     * @param item element to add
     * @return true iff element was not there already
     */

    public void resetCountes() {
        this.numAdd = 0;
        this.numRemove = 0;
        this.numContains = 0;
    }

    public int getAdds() {
        return this.numAdd;
    }

    public int getRemoves() {
        return this.numRemove;
    }

    public int getContains() {
        return this.numContains;
    }

    public String getListName() {
        return this.listName;
    }

    public boolean add(T item) {
      Node pred, curr;
      int key = item.hashCode();
      lock.lock();
      try {
        pred = head;
        curr = pred.next;
        while (curr.key < key) {
          pred = curr;
          curr = curr.next;
        }
        if (key == curr.key) {
          numAdd++;
          return false;
        } else {
          Node node = new Node(item);
          node.next = curr;
          pred.next = node;
          numAdd++;
          return true;
        }
      } finally {
        lock.unlock();
      }
    }
  
    /**
     * Remove an element.
     * @param item element to remove
     * @return true iff element was present
     */
    public boolean remove(T item) {
      Node pred, curr;
      int key = item.hashCode();
      lock.lock();
      try {
        pred = this.head;
        curr = pred.next;
        while (curr.key < key) {
          pred = curr;
          curr = curr.next;
        }
        if (key == curr.key) {  // present
          pred.next = curr.next;
          numRemove++;
          return true;
        } else {
          numRemove++;
          return false;         // not present
        }
      } finally {               // always unlock
        lock.unlock();
      }
    }
    /**
     * Test whether element is present
     * @param item element to test
     * @return true iff element is present
     */
    public boolean contains(T item) {
      Node pred, curr;
      int key = item.hashCode();
      lock.lock();
      try {
        pred = head;
        curr = pred.next;
        while (curr.key < key) {
          pred = curr;
          curr = curr.next;
        }
        numContains++;
        return (key == curr.key);
      } finally {               // always unlock
        lock.unlock();
      }
    }
  
    public int size() {
      Node pred = this.head;
      Node curr = pred.next;
      int count = 0;
      while (curr.item != null) {
        ++count;
        pred = curr;
        curr = curr.next;
      }
      return count;
    }
    
    
    /**
     * list Node
     */
    private class Node {
      /**
       * actual item
       */
      T item;
      /**
       * item's hash code
       */
      int key;
      /**
       * next Node in list
       */
      Node next;
      /**
       * Constructor for usual Node
       * @param item element in list
       */
      Node(T item) {
        this.item = item;
        this.key = item.hashCode();
      }
      /**
       * Constructor for sentinel Node
       * @param key should be min or max int value
       */
      Node(int key) {
        this.item = null;
        this.key = key;
      }
    }
  
    
    public static void main(String[] args) {
          
      CommonList<Integer> l = new CoarseList<Integer>();
  
      for (int i = 0; i < 100; i++) {
        l.add(i);
      }
  
      System.out.println("Tamanho da Lista :"+l.size());
  
    }
  
  }