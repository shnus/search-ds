package ru.mail.polis;

/**
 * Created by Nechaev Mikhail
 * Since 13/12/2017.
 */

public interface OpenHashTableEntity {

    /**
     * Вычисляет значение хеша объекта (другими словами значение индекса ячейки в хеш-таблице)
     * на основании её размера и номера пробы,
     * где номер пробы равен попытке найти свободную ячейку.
     *
     * Если методу поочередно дать на вход все значения probIdx, от 0 до tableSize - 1, при фиксированном tableSize,
     * то на выходе должна получиться последовательности из всех возможных индексов ячеек хеш-таблицы
     *
     * @param tableSize — текущий размер хеш-таблицы
     * @param probId — номер пробы. Значение от 0 до tableSize - 1
     * @return значение вычисленного хеша
     * @throws IllegalArgumentException если probIdx < 0 или probIdx >= tableSize
     */
    int hashCode(int tableSize, int probId) throws IllegalArgumentException;

}