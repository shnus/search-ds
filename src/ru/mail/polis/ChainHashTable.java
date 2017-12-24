package ru.mail.polis;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ChainHashTable<E> extends AbstractSet<E> implements Set<E> {

    private final int INITIAL_CAPACITY = 8;
    //    private final float LOAD_FACTOR = 0.5f;
    private Object[] table;
    private int size;

    public ChainHashTable() {
        this.table = new Object[INITIAL_CAPACITY];
    }

    /**
     * Вставляет элемент в хеш-таблицу.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент в хеш-таблице отсутствовал
     */
    @Override
    public boolean add(E value) {
        int idx = hash(value);
        if (table[idx] == null) {
            table[idx] = new Node<>(value);
        } else {
            Node curr = getNode(idx);
            while (curr.next != null && !value.equals(curr.value)) {
                curr = curr.next;
            }
            if (value.equals(curr.value)) {
                return false;
            }
            curr.next = new Node<>(value);
        }
        size++;
        resize();
        return true;
    }

    /**
     * Удаляет элемент с таким же значением из хеш-таблицы.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в хеш-таблице
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;

        Node prev = null;
        int idx = hash(value);
        Node curr = getNode(idx);
        while (curr != null && !value.equals(curr.value)) {
            prev = curr;
            curr = curr.next;
        }
        if (curr != null) {
            if (prev == null) { //head
                table[idx] = getNode(idx).next;
            } else {
                prev.next = curr.next;
            }
            curr.value = null;
            curr.next = null;
            size--;
            return true;
        }
        return false;
    }

    /**
     * Ищет элемент с таким же значением в хеш-таблице.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в хеш-таблице
     */
    @Override
    public boolean contains(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;

        Node curr = getNode(hash(value));
        while (curr != null && !value.equals(curr.value)) {
            curr = curr.next;
        }
        //Вышли по второму условию
        return curr != null;
    }

    private int hash(E value) {
        return Math.abs(value.hashCode()) % table.length;
    }

    @SuppressWarnings("unchecked")
    private Node<E> getNode(int idx) {
        return (Node<E>) table[idx];
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        if (size * 2 < table.length) {
            return;
        }
        Object[] old = this.table;
        size = 0;
        table = new Object[table.length << 1];
        for (int i = 0; i < old.length; i++) {
            Node<E> node = (Node<E>) old[i];
            if (node != null) {
                Node<E> curr = node;
                while (curr != null) {
                    Node next = curr.next;
                    //FIXME: insert value in head (all unique)
                    add(curr.value);
                    curr.next = null;
                    curr.value = null;
                    curr = next;
                }
                old[i] = null;
            }
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    private static class Node<E> {
        E value;
        Node<E> next;

        Node(E value) {
            this.value = value;
        }

        @Override
        public String toString() {
            List<E> values = new ArrayList<>();
            Node<E> curr = this;
            while (curr != null) {
                values.add(curr.value);
                curr = curr.next;
            }
            return values.toString();
        }
    }

}