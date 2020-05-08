package lists;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import interfaces.CommonList;

public class FineList<T> implements CommonList<T> {
    /**
     * First list entry
     */
    private Node head;
  
    public volatile int numAdd, numRemove, numContains = 0;
    public String listName = "FineList";
  
    /**
     * Constructor
     */
    public FineList() {
      // Add sentinels to start and end
      head      = new Node(Integer.MIN_VALUE);
      head.next = new Node(Integer.MAX_VALUE);
    }
    /**
     * Add an element.
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
      int key = item.hashCode();
      head.lock();
      Node pred = head;
      try {
        Node curr = pred.next;
        curr.lock();
        try {
          while (curr.key < key) {
            pred.unlock();
            pred = curr;
            curr = curr.next;
            curr.lock();
          }
          if (curr.key == key) {
            numAdd++;
            return false;
          }
          Node newNode = new Node(item);
          newNode.next = curr;
          pred.next = newNode;
          numAdd++;
          return true;
        } finally {
          curr.unlock();
        }
      } finally {
        pred.unlock();
      }
    }
    public boolean initList(T item) {
      int key = item.hashCode();
      head.lock();
      Node pred = head;
      try {
        Node curr = pred.next;
        curr.lock();
        try {
          while (curr.key < key) {
            pred.unlock();
            pred = curr;
            curr = curr.next;
            curr.lock();
          }
          if (curr.key == key) {
            return false;
          }
          Node newNode = new Node(item);
          newNode.next = curr;
          pred.next = newNode;
          return true;
        } finally {
          curr.unlock();
        }
      } finally {
        pred.unlock();
      }
    }
    /**
     * Remove an element.
     * @param item element to remove
     * @return true iff element was present
     */
    public boolean remove(T item) {
      Node pred = null, curr = null;
      int key = item.hashCode();
      head.lock();
      try {
        pred = head;
        curr = pred.next;
        curr.lock();
        try {
          while (curr.key < key) {
            pred.unlock();
            pred = curr;
            curr = curr.next;
            curr.lock();
          }
          if (curr.key == key) {
            pred.next = curr.next;
            numRemove++;
            return true;
          }
          numRemove++;
          return false;
        } finally {
          curr.unlock();
        }
      } finally {
        pred.unlock();
      }
    }
  
    public boolean contains(T item) {
      Node pred = null, curr = null;
      int key = item.hashCode();
      head.lock();
      try {
        pred = head;
        curr = pred.next;
        curr.lock();
        try {
          while (curr.key < key) {
            pred.unlock();
            pred = curr;
            curr = curr.next;
            curr.lock();
          }
          numContains++;
          return (curr.key == key);
        } finally {
          curr.unlock();
        }
      } finally {
        pred.unlock();
      }
    }
  
    public void showFineList() {
      Node pred = null, curr = null;
      int i=0;
      head.lock();
      try {
        pred = head;
        curr = pred.next;
        curr.lock();
        try {
          while (curr.next != null) {
            System.out.println("Posição : "+i+" Item : "+curr.item);
            i++;
            pred.unlock();
            pred = curr;
            curr = curr.next;
            curr.lock();
          }
        } finally {
          curr.unlock();
        }
      } finally {
        pred.unlock();
      }
    }
  
    public int size() {
      int i=0;
      Node curr = this.head.next;
      while (curr.next != null) {
        i++;
        curr = curr.next;
      }
      return i;
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
       * synchronizes individual Node
       */
      Lock lock;
      /**
       * Constructor for usual Node
       * @param item element in list
       */
      Node(T item) {
        this.item = item;
        this.key = item.hashCode();
        this.lock = new ReentrantLock();
      }
      /**
       * Constructor for sentinel Node
       * @param key should be min or max int value
       */
      Node(int key) {
        this.item = null;
        this.key = key;
        this.lock = new ReentrantLock();
      }
      /**
       * Lock Node
       */
      void lock() {lock.lock();}
      /**
       * Unlock Node
       */
      void unlock() {lock.unlock();}
    }
  
    
    public static void main(String[] args) {
          
      CommonList<Integer> l = new FineList<Integer>();
  
      for (int i = 0; i < 100; i++) {
        l.add(i);
      }
  
      System.out.println("num add :"+l.size());
  
    }
    
  }
  
  