package ru.mail.polis;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.IntStream;

/**
 * Created by Nechaev Mikhail
 * Since 13/12/2017.
 */
public abstract class CheckedOpenHashTableEntity implements OpenHashTableEntity {

    /**
     * Проверяет корректность хеш-функции
     * @param tableSize размер таблицы
     * @return true, если с помощью хеш-функции можно обойти все ячейки хеш-таблицы за tableSize раз
     */
    public boolean isHashFunctionValid(int tableSize) {
        SortedSet<Integer> idx = new TreeSet<>();
        IntStream.range(0, tableSize).forEach((probId) -> idx.add(hashCode(tableSize, probId)));
        return idx.first() == 0 && idx.last() == tableSize - 1 && idx.size() == tableSize;
    }
}
