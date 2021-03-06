package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private int INITIAL_CAPACITY = 8;
    private Object[] table;
    private boolean[] isDeleted;
    private int size; //количество элементов в хеш-таблице
    private int tableSize; //размер хещ-таблицы

    public OpenHashTable() {
        //todo
        this.table = new Object[INITIAL_CAPACITY];
        this.isDeleted = new boolean[INITIAL_CAPACITY];
        size = 0;
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
        int probId = 0;
        int hashcode = Math.abs(value.hashCode(table.length, probId++));
        while (probId < table.length) {
            if (table[hashcode] == null) {
                table[hashcode] = value;
                size++;
                break;
            }
            if (table[hashcode].equals(value)) {
                return false;
            }
            hashcode = Math.abs(value.hashCode(table.length, probId++));
        }
        if (probId >= table.length) {
            resize();
            return add(value);
        }
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
        int probId = 0;
        int hashcode;

        while (probId < table.length) {
            hashcode = Math.abs(value.hashCode(table.length, probId++));
            if(table[hashcode]==null){
                if(isDeleted[hashcode]){
                    continue;
                } else {
                    return false;
                }
            }
            if (value.equals(table[hashcode])) {
                table[hashcode] = null;
                isDeleted[hashcode] = true;
                size--;
                return true;
            }
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
        int probId = 0;
        int hashcode;

        while (probId < table.length) {
            hashcode = Math.abs(value.hashCode(table.length, probId++));
            if(table[hashcode]==null){
                if(isDeleted[hashcode]){
                    continue;
                } else {
                    return false;
                }
            }
            if (table[hashcode].equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    public int getTableSize() {
        return tableSize;
    }

    private void resize() {
        int tableSize = 2 * table.length;
        size = 0;
        Object[] oldTable = table;
        table = new Object[tableSize];
        isDeleted = new boolean[tableSize];

        for (int i = 0; i < oldTable.length; i++)
            if (oldTable[i] != null)
                add((E) oldTable[i]);
    }

    @Override
    public Iterator<E> iterator() {
        //    throw new UnsupportedOperationException();
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public E next() {
                return null;
            }
        };
    }

}
