package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private int size; //количество элементов в хеш-таблице
    private int tableSize; //размер хе-таблицы
    private final int INIT_SIZE = 8;
    private final float LOAD_FACTOR = 0.5f;
    private final OpenHashTableEntity ITWAS = (tableSize, probId) -> -1;
    private OpenHashTableEntity[] table;


    public OpenHashTable() {
        this.table = new OpenHashTableEntity[this.INIT_SIZE];
        this.tableSize = this.table.length;
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
        int hash = value.hashCode(getTableSize(), probId);
        while (table[hash] == ITWAS || table[hash] != null) {
            if(this.table[hash].equals(value))
                return false;
            hash = value.hashCode(getTableSize(), probId++);
        }
        table[hash] = value;
        size++;
        if(tableSize * LOAD_FACTOR <= this.size)
            resize(tableSize);
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
        int hash = value.hashCode(this.tableSize, probId);
        while (table[hash] != null){
            if(table[hash].equals(value)){
                table[hash] = ITWAS;
                size--;
                if(tableSize * LOAD_FACTOR <= 2 * size)
                    resize(tableSize);
                return true;
            }
            hash = value.hashCode(tableSize, probId++);
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
        int hash;
        E value = (E) object;
        for (int probId = 0; probId < table.length; probId++) {
            hash = value.hashCode(table.length, probId);
            if (table[hash] != ITWAS && table[hash] != null) {
                if(table[hash].equals(value))
                    return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    private void resize(int newSize){
        OpenHashTableEntity[] temp = table;
        table = new OpenHashTableEntity[2 * newSize];
        tableSize = table.length;
        size = 0;
        for (OpenHashTableEntity elem: temp) {
            if(elem != null && elem != ITWAS)
                add((E) elem);
        }
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (OpenHashTableEntity elem: table ) {
            if(elem != null && elem != ITWAS)
                str.append(elem.toString()).append("\n");
        }
        return str.toString();
    }

    public int getTableSize() {
        return this.tableSize;
    }
}