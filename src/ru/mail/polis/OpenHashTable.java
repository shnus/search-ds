package ru.mail.polis;

import java.time.LocalDate;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class OpenHashTable<E extends Student> extends AbstractSet<E> implements Set<E> {

    private final int INITIAL_CAPACITY = 8;
    private final float LOAD_FACTOR = 0.5f;
    private Student[] table;
    private Student deleted = new Student("Deleted", "Deleted", Student.Gender.MALE, LocalDate.MIN, -1, -1);
    private int size;


    public OpenHashTable() {
        this.table = new Student[INITIAL_CAPACITY];
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
        int hash = value.hashCode(table.length, probId);

        while (table[hash] == deleted || table[hash] != null) {
            hash = value.hashCode(table.length, ++probId);
        }
        table[hash] = value;

        size++;
        if(table.length*LOAD_FACTOR <= size)
            resize(table.length * 2);
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
        int hash = value.hashCode(table.length, probId);

        while (table[hash] != null){
            if(table[hash].equals(value)){
                table[hash] = deleted;
                size--;
                if(table.length*LOAD_FACTOR <= size*2)
                    resize(table.length * 2);
                return true;
            }
            hash = value.hashCode(table.length, ++probId);
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
        int hash = value.hashCode(table.length, probId);

        while (table[hash] != null){
            if(table[hash].equals(value)){
                return true;
            }
            hash = value.hashCode(table.length, ++probId);
        }
        return false;
    }

    private void resize(int newSize){
        //todo
        Student[] newTable = new Student[newSize];
        for (Student s:table) {
            if(s != null && s != deleted) {
                int probId = 0;
                int hash = s.hashCode(newTable.length, probId);
                while (newTable[hash] != null) {
                    hash = s.hashCode(table.length, ++probId);
                }
                newTable[hash] = s;
            }
        }
        table = newTable;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Student s: table ) {
            if(s != null && !s.equals(deleted))
            str.append(s.toString()).append("\n");
        }
        return str.toString();
    }

    public static void main(String[] args) {
        OpenHashTable<Student> students = new OpenHashTable<>();

        for(int i = 0; i < 10; i++) {
            Student std = SimpleStudentGenerator.getInstance().generate();
            students.add(std);
        }

        System.out.println(students.toString());
    }
}
