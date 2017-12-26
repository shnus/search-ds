package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private int size; //количество элементов в хеш-таблице
    private int tableSize; //размер хещ-таблицы
    //хещ, слющай!
    private final int INIT_SIZE = 8;
    private final float LOAD_FACTOR = 0.5f;
    private final OpenHashTableEntity DELETED = (tableSize, attempt) -> -1;
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
        int attempt = 0;
        int hash = value.hashCode(getTableSize(), attempt);

        while (this.table[hash] == this.DELETED || this.table[hash] != null) {
            if(this.table[hash].equals(value))
                return false;
            attempt++;
            hash = value.hashCode(getTableSize(), attempt);
        }

        this.table[hash] = value;
        this.size++;

        if(this.tableSize * this.LOAD_FACTOR <= this.size)
            resize(this.tableSize);
        return true;
        //Используйте value.hashCode(tableSize, probId) для вычисления хеша
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
        int attempt = 0;
        int hash = value.hashCode(this.tableSize, attempt);

        while (this.table[hash] != null){
            if(this.table[hash].equals(value)){
                this.table[hash] = this.DELETED;
                this.size--;
                if(this.tableSize * this.LOAD_FACTOR <= 2 * this.size)
                    resize(this.tableSize);
                return true;
            }
            attempt++;
            hash = value.hashCode(this.tableSize, attempt);
        }
        return false;
        //Используйте value.hashCode(tableSize, probId) для вычисления хеша
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
        return search((E) object) >= 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    private void resize(int newSize){
        OpenHashTableEntity[] temp = this.table;
        this.table = new OpenHashTableEntity[2 * newSize];
        this.tableSize = this.table.length;
        this.size = 0;
        for (OpenHashTableEntity elem: temp) {
            if(elem != null && elem != this.DELETED)
                add((E) elem);
        }
    }
    private int search(E value) {
        int hash;
        for (int attempt = 0; attempt < this.table.length; attempt++) {
            hash = value.hashCode(this.table.length, attempt);
            if (this.table[hash] != this.DELETED && this.table[hash] != null) {
                if(this.table[hash].equals(value))
                    return hash;
            }
        }
        return -1;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (OpenHashTableEntity elem: this.table ) {
            if(elem != null && elem != this.DELETED)
                str.append(elem.toString()).append("\n");
        }
        return str.toString();
    }

    public int getTableSize() {
        return this.tableSize;
    }
}