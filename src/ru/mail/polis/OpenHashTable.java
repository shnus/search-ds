package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private int size;
    private int tableSize;
    private Object[] table;
    private final int INITIAL_CAPACITY = 8;
    private static final float LOAD_FACTOR = 0.5f;
    private static final Object DELETED = new Object();

    public OpenHashTable() {
        table = new Object[INITIAL_CAPACITY];
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
        if (!contains(value)) {
            for (int i = 0; i < table.length; i++) {
                int j = value.hashCode(table.length, i);
                if (table[j] == null || table[j] == DELETED) {
                    table[j] = value;
                    size++;
                    return true;
                } else {
                    i++;
                }
            }
            resize();
            return add(value);
        } else return false;
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
        int i = search(value);
        if (table[i] == null) {
            return false;
        } else {
            table[i] = DELETED;
            size--;
            return true;
        }
    }

    /**
     * Ищет элемент с таким же значением в хеш-таблице.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в хеш-таблице
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object object) {
        return search((E) object) >= 0;
    }

    private int search(E value) {
        int j;
        for (int i = 0; i < table.length; i++) {
            j = value.hashCode(table.length, i);
            if (table[j] != DELETED && table[j] == value) {
                return j;
            }
        }
        return -1;
    }

    private boolean isResize() {
        return Math.abs(size - table.length * LOAD_FACTOR) >= 0;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        if (isResize()) {
            Object[] old = this.table;
            size = 0;
            table = new Object[table.length << 1];
            for (int i = 0; i < old.length; i++) {
                E value = (E) old[i];
                if (value != null && value != DELETED) {
                    add(value);
                    old[i] = null;
                }
            }
        }
    }

    public int getTableSize() {
        return table.length;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }
}
