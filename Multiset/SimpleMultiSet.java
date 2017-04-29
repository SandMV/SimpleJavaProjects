package ru.compscicenter.java2016.collections;

/**
 * Created by sandulmv on 28.10.16.
 */

import java.util.*;

public class SimpleMultiSet<E> extends AbstractCollection<E> implements MultiSet<E> {
    private HashMap<E, Collection<E>> multiset;
    private int size;

    public SimpleMultiSet() {
        multiset = new HashMap<>();
        size = 0;
    }

    public SimpleMultiSet(Collection<? extends E> c) {
        this();
        this.addAll(c);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new SimpleMultiSetIterator();
    }

    @Override
    public boolean add(E e) {
        int countBeforeAddition = add(e, 1);
        int countAfterAddition = count(e);
        return countAfterAddition != countBeforeAddition;
    }

    @Override
    public int add(E e, int occurrences) {
        if (occurrences < 0) {
            throw new IllegalArgumentException("occurrences must be more than 0!");
        }
        if (occurrences == 0) {
            return count(e);
        }
        if (!multiset.containsKey(e)) {
            multiset.put(e, new ArrayList<E>());
        }
        Collection<E> listOfValues = multiset.get(e);
        int count = listOfValues.size();
        for (int i = 0; i < occurrences; i++) {
            listOfValues.add(e);
        }
        size += occurrences;
        return count;
    }

    @Override
    public boolean remove(Object e) {
        return remove(e, 1) > 0;
    }

    @Override
    public int remove(Object e, int occurrences) {
        if (occurrences < 0) {
            throw new IllegalArgumentException("occurrences must be more than -1!");
        }
        Collection<E> listOfValues = multiset.getOrDefault(e, Collections.emptyList());
        int count = listOfValues.size();
        while (occurrences > 0 && listOfValues.remove(e)) {
            occurrences--;
            size--;
        }
        if (count(e) == 0) {
            multiset.remove(e);
        }
        return count;
    }

    @Override
    public int count(Object e) {
        return multiset.getOrDefault(e, Collections.emptyList()).size();
    }

    @Override
    public boolean contains(Object o) {
        return multiset.containsKey(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return multiset.keySet().containsAll(c);
    }

    @Override
    public void clear() {
        multiset.clear();
        size = 0;
    }

    @Override
    public boolean equals(Object o) {
        try {
            if (o == this) {
                return true;
            }
            if (o instanceof MultiSet) {
                MultiSet<E> otherSet = (MultiSet<E>) o;
                if (otherSet.size() != this.size()) {
                    return false;
                }
                for (E key : multiset.keySet()) {
                    if (this.count(key) != otherSet.count(key)) {
                        return false;
                    }
                }
                return true;
            }
        } catch (ClassCastException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        Iterator<E> multiSetIterator = iterator();
        while (multiSetIterator.hasNext()) {
            E obj = multiSetIterator.next();
            if (obj != null) {
                hashCode += obj.hashCode();
            } else {
                hashCode += 1;
            }
        }
        return hashCode;
    }

    private class SimpleMultiSetIterator implements Iterator<E> {
        private final Iterator<Map.Entry<E, Collection<E>>> entryIterator;
        private Iterator<E> listIterator;

        SimpleMultiSetIterator() {
            entryIterator = multiset.entrySet().iterator();
            listIterator = Collections.<E>emptyList().iterator();
        }

        @Override
        public boolean hasNext() {
            if (!listIterator.hasNext()) {
                if (entryIterator.hasNext()) {
                    listIterator = entryIterator.next().getValue().iterator();
                    return true;
                }
                return false;
            }
            return true;
        }

        @Override
        public E next() {
            if (!listIterator.hasNext()) {
                listIterator = entryIterator.next().getValue().iterator();
            }
            return listIterator.next();
        }

        @Override
        public void remove() {
            listIterator.remove();
            if (!listIterator.hasNext()) {
                entryIterator.remove();
            }
            SimpleMultiSet.this.size--;
        }
    }
}
