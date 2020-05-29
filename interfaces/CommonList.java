/*
PUCRS
Programação Concorrente - Prof. Fernando Dotti
Gibson Weinert
*/

package interfaces;

public interface CommonList<T> {
    
    public boolean add(T item);

    public boolean remove(T item);

    public boolean contains(T item);

    public int size();

    public void resetCountes();

    public int getAdds();

    public int getRemoves();

    public int getContains();

    public String getListName();
    
}