package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends OpenHashTableEntity> extends AbstractSet<E> implements Set<E> {

    private final int INITIAL_CAPACITY = 8;
    private final float LOAD_FACTOR = 0.5f;
    private OpenHashTableEntity[] table;
    private OpenHashTableEntity deleted = (tableSize, probId) -> -1;
    private int size;//количество элементов в хеш-таблице
    private int tableSize; //размер хещ-таблицы


    public OpenHashTable() {
        this.table = new OpenHashTableEntity[INITIAL_CAPACITY];
        tableSize = table.length;
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

        while (table[hash] == deleted || table[hash] != null) {
            if(table[hash].equals(value))
                return false;
            hash = value.hashCode(getTableSize(), ++probId);
        }
        table[hash] = value;

        size++;
        if(getTableSize()*LOAD_FACTOR <= size)
            resize(getTableSize() * 2);
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
        int hash = value.hashCode(getTableSize(), probId);

        while (table[hash] != null){
            if(table[hash].equals(value)){
                table[hash] = deleted;
                size--;
                if(getTableSize()*LOAD_FACTOR <= size*2)
                    resize(getTableSize() * 2);
                return true;
            }
            hash = value.hashCode(getTableSize(), ++probId);
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
        int hash = value.hashCode(getTableSize(), probId);
        while (table[hash] != null){
            if(table[hash].equals(value)){
                return true;
            }
            hash = value.hashCode(getTableSize(), ++probId);
        }
        return false;
    }

    private void resize(int newSize){
        OpenHashTableEntity[] oldTable = table;
        table = new OpenHashTableEntity[newSize];
        tableSize = table.length;
        size = 0;
        for (OpenHashTableEntity s: oldTable) {
            if(s != null && s != deleted)
                add((E)s);
        }
    }

    @Override
    public int size() {
        return size;
    }

    public int getTableSize() {
        return tableSize;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (OpenHashTableEntity s: table ) {
            if(s != null && !s.equals(deleted))
            str.append(s.toString()).append("\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        OpenHashTable<Student> students = new OpenHashTable<>();

        for(int i = 0; i < 2000; i++) {
            Student std = SimpleStudentGenerator.getInstance().generate();
            students.add(std);
            if(!students.contains(std)) {
                System.out.println(std);
                System.out.println(students.contains(std));
            }
            students.add(std);
            if(!students.contains(std)) {
                System.out.println(std);
                System.out.println(students.contains(std));
            }
            //System.out.println(students.contains(std));
        }

        //System.out.println(students.toString());
    }
}
