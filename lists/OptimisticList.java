/*
PUCRS
Programação Concorrente - Prof. Fernando Dotti
Gibson Weinert
*/

package lists;
/*
 * OptimisticList.java
 *
 * Created on January 4, 2006, 1:49 PM
 *
 * From "Multiprocessor Synchronization and Concurrent Data Structures",
 * by Maurice Herlihy and Nir Shavit.
 * Copyright 2006 Elsevier Inc. All rights reserved.
 */

import java.util.Random;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import interfaces.CommonList;


/**
 * Optimistic List implementation.
 * @param T Item type.
 * @author Maurice Herlihy
 */
public class OptimisticList<T> implements CommonList<T> {
  /**
   * First list entry
   */
  private Entry head;
  
  public volatile int numAdd, numRemove, numContains = 0;
  public String listName = "OptimisticList";
 
  /**
   * Constructor
   */
  public OptimisticList() {
    this.head  = new Entry(Integer.MIN_VALUE);
    this.head.next = new Entry(Integer.MAX_VALUE);
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
    while (true) {
      Entry pred = this.head;
      Entry curr = pred.next;
      while (curr.key < key) { // gibsonw modified
        pred = curr; curr = curr.next;
      }
      pred.lock(); curr.lock();
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present
            numAdd++;
            return false;
          } else {               // not present
            Entry entry = new Entry(item);
            entry.next = curr;
            pred.next = entry;
            numAdd++;
            return true;
          }
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  public boolean initList(T item) {
    int key = item.hashCode();
    while (true) {
      Entry pred = this.head;
      Entry curr = pred.next;
      while (curr.key <= key) {
        pred = curr; curr = curr.next;
      }
      pred.lock(); curr.lock();
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present
            return false;
          } else {               // not present
            Entry entry = new Entry(item);
            entry.next = curr;
            pred.next = entry;
            return true;
          }
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  /**
   * Remove an element.
   * @param item element to remove
   * @return true iff element was present
   */
  public boolean remove(T item) {
    int key = item.hashCode();
    while (true) {
      Entry pred = this.head;
      Entry curr = pred.next;
      while (curr.key < key) {
        pred = curr; curr = curr.next;
      }
      pred.lock(); curr.lock();
      try {
        if (validate(pred, curr)) {
          if (curr.key == key) { // present in list
            pred.next = curr.next;
            numRemove++;
            return true;
          } else {               // not present in list
            numRemove++;
            return false;
          }
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  /**
   * Test whether element is present
   * @param item element to test
   * @return true iff element is present
   */
  public boolean contains(T item) {
    int key = item.hashCode();
    while (true) {
      Entry pred = this.head; // sentinel node;
      Entry curr = pred.next;
      while (curr.key < key) {
        pred = curr; curr = curr.next;
      }
      try {
        pred.lock(); curr.lock();
        if (validate(pred, curr)) {
          numContains++;
          return (curr.key == key);
        }
      } finally {                // always unlock
        pred.unlock(); curr.unlock();
      }
    }
  }
  public void showList() {
    int i=0;
    Entry curr = this.head; // sentinel node;
    while (curr.next != null) {
      System.out.println("Posição : "+i+" Item : "+curr.item);
      i++;
      curr = curr.next;
    }
  }
  
  public int size() {
    int i=0;
    Entry curr = this.head.next;; // sentinel node;
    while (curr.next != null) {
      i++;
      curr = curr.next;
    }
    return i;
  }
  /**
     * Check that prev and curr are still in list and adjacent
     * @param pred predecessor node
     * @param curr current node
     * @return whther predecessor and current have changed
     */
  private boolean validate(Entry pred, Entry curr) {
    Entry entry = head;
    while (entry.key <= pred.key) {
      if (entry == pred)
        return pred.next == curr;
      entry = entry.next;
    }
    return false;
  }
  /**
   * list entry
   */
  private class Entry {
    /**
     * actual item
     */
    T item;
    /**
     * item's hash code
     */
    int key;
    /**
     * next entry in list
     */
    Entry next;
    /**
     * Synchronizes entry.
     */
    Lock lock;
    /**
     * Constructor for usual entry
     * @param item element in list
     */
    Entry(T item) {
      this.item = item;
      this.key = item.hashCode();
      lock = new ReentrantLock();
    }
    /**
     * Constructor for sentinel entry
     * @param key should be min or max int value
     */
    Entry(int key) {
      this.key = key;
      lock = new ReentrantLock();
    }
    /**
     * Lock entry
     */
    void lock() {lock.lock();}
    /**
     * Unlock entry
     */
    void unlock() {lock.unlock();}
  }

  
  public static void main(String[] args) {
        
    CommonList<Integer> l = new OptimisticList<Integer>();

    Random rand = new Random();
    int addOK = 0;
    int removeOK = 0;
    boolean flgAdd,flgRemove = false;

    int sizeList = 100;
    int rndSizeList = (int) (sizeList*2);


    for (int i = 0; i < sizeList; i++) {
      l.add(i);
    }

    for (int i = 0; i < sizeList; i++) {
      flgRemove = l.remove(rand.nextInt(rndSizeList));
      if (flgRemove) {removeOK++;}

      flgAdd = l.add(rand.nextInt(rndSizeList));
      if (flgAdd) {addOK++;}
    }

/*
    for (int i = 0; i < sizeList; i++) {
      flgRemove = l.remove(i);
      if (flgRemove) {removeOK++;}

      flgAdd = l.add(i);
      if (flgAdd) {addOK++;}
    }
*/

    System.out.println("add - removes ; size() - numItensList : " + (addOK - removeOK) + " ; " + (l.size() - sizeList) );

    //l.showList();
    System.out.println("Tamanho da Lista :"+l.size());

  }
}
