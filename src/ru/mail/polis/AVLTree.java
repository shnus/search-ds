package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class AVLTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;

    private class Node {
        private E value;
        private int balance;
        private int height;
        private Node left;
        private Node right;
        private Node parent;

        Node(E value, Node parent) {
            this.value = value;
            this.parent = parent;
        }
    }

    private Node root;
    private int size;

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Вставляет элемент в дерево.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент в дереве отсутствовал
     */
    @Override
    public boolean add(E value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            root = new Node(value, null);
        } else {
            Node curr = root;
            while (true) {
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return false;
                } else if (cmp < 0) {
                    if (curr.right != null) {
                        curr = curr.right;
                    } else {
                        curr.right = new Node(value, curr);
                        break;
                    }
                } else /*if (cmp > 0)*/ {
                    if (curr.left != null) {
                        curr = curr.left;
                    } else {
                        curr.left = new Node(value, curr);
                        break;
                    }
                }
            }
            rebalance(curr);
        }
        size++;
        return true;
    }

    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо удалить
     * @return true, если элемент содержался в дереве
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E)object;
        if (root == null)
            return false;

        Node child = root;
        while (child != null) {
            Node node = child;
            child = compare(value,node.value) >= 0 ? node.right : node.left;
            if (compare(value,node.value) == 0) {
                delete(node);
                size--;
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет необходимый узел из дерева.
     *
     * @param node конкретный узел для удаления
     */
    private void delete(Node node) {
        if (node.left == null && node.right == null) {
            if (node.parent == null) {
                root = null;
            } else {
                Node parent = node.parent;
                if (parent.left == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
                rebalance(parent);
            }
            return;
        }

        if (node.left != null) {
            Node child = node.left;
            while (child.right != null) child = child.right;
            node.value = child.value;
            delete(child);
        } else {
            Node child = node.right;
            while (child.left != null) child = child.left;
            node.value = child.value;
            delete(child);
        }
    }

    /**
     * Ищет элемент с таким же значением в дереве.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо поискать
     * @return true, если такой элемент содержится в дереве
     */
    @Override
    public boolean contains(Object object) {
        if (object == null) {
            throw new NullPointerException("value is null");
        }
        @SuppressWarnings("unchecked")
        E key = (E) object;
        if (root != null) {
            Node curr = root;
            while (curr != null) {
                int cmp = compare(curr.value, key);
                if (cmp == 0) {
                    return true;
                } else if (cmp < 0) {
                    curr = curr.right;
                } else {
                    curr = curr.left;
                }
            }
        }
        return false;
    }

    /**
     * Ищет наименьший элемент в дереве
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.value;
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("last");
        }
        Node curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.value;
    }

    /**
     * Балансировка дерева, если высоты поддеревьев различаются больше чем на 1
     * @param node элемент относительно которого балансируем дерево
     */
    private void rebalance(Node node) {
        setBalance(node);

        if (node.balance == -2) {
            if (height(node.left.left) >= height(node.left.right))
                node = rotateRight(node);
            else
                node = rotateLeftThenRight(node);

        } else if (node.balance == 2) {
            if (height(node.right.right) >= height(node.right.left))
                node = rotateLeft(node);
            else
                node = rotateRightThenLeft(node);
        }

        if (node.parent != null) {
            rebalance(node.parent);
        } else {
            root = node;
        }
    }

    /**
     * Малое левое вращение
     * @param a элемент относительно которого вращаем дерево/поддерево
     */
    private Node rotateLeft(Node a) {

        Node b = a.right;
        b.parent = a.parent;

        a.right = b.left;

        if (a.right != null)
            a.right.parent = a;

        b.left = a;
        a.parent = b;

        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }

        setBalance(a);
        setBalance(b);

        return b;
    }

    /**
     * Малое правое вращение
     * @param a элемент относительно которого вращаем дерево/поддерево
     */
    private Node rotateRight(Node a) {

        Node b = a.left;
        b.parent = a.parent;

        a.left = b.right;

        if (a.left != null)
            a.left.parent = a;

        b.right = a;
        a.parent = b;

        if (b.parent != null) {
            if (b.parent.right == a) {
                b.parent.right = b;
            } else {
                b.parent.left = b;
            }
        }

        setBalance(a);
        setBalance(b);

        return b;
    }

    /**
     * Большое левое вращение. Аналог двух поворотов подряд, сперва левое, затем правое
     * @param node элемент относительно которого вращаем дерево/поддерево
     */
    private Node rotateLeftThenRight(Node node) {
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }

    /**
     * Большое правое вращение. Аналог двух поворотов подряд, сперва правое, затем левое
     * @param node элемент относительно которого вращаем дерево/поддерево
     */
    private Node rotateRightThenLeft(Node node) {
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    /**
     * Возвращает высоту для заданного узла
     * @param node элемент у которого запрашиваем высоту
     */
    private int height(Node node) {
        if (node == null)
            return -1;
        return node.height;
    }

    /**
     * Устанавливаем новое значение balance. Таким образом определяем нужно ли балансировка
     * @param node элемент у которого обновляем значение balance
     */
    private void setBalance(Node node) {
        reheight(node);
        node.balance = height(node.right) - height(node.left);
    }

    /**
     * Обновление значения высоту для заданного узла
     * @param node элемент у которого обновляем высоту
     */
    private void reheight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "AVLTree{" +
                "tree=" + root +
                "size=" + size + ", " +
                '}';
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        throw new UnsupportedOperationException("subSet");
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        throw new UnsupportedOperationException("headSet");
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        throw new UnsupportedOperationException("tailSet");
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("iterator");
    }

    /**
     * Обходит дерево и проверяет что высоты двух поддеревьев
     * различны по высоте не более чем на 1
     *
     * @throws NotBalancedTreeException если высоты отличаются более чем на один
     */
    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        traverseTreeAndCheckBalanced(root);
    }

    private int traverseTreeAndCheckBalanced(Node curr) throws NotBalancedTreeException {
        if (curr == null) {
            return 1;
        }
        int leftHeight = traverseTreeAndCheckBalanced(curr.left);
        int rightHeight = traverseTreeAndCheckBalanced(curr.right);
        if (Math.abs(leftHeight - rightHeight) > 1) {
            throw NotBalancedTreeException.create("The heights of the two child subtrees of any node must be differ by at most one",
                    leftHeight, rightHeight, curr.toString());
        }
        return Math.max(leftHeight, rightHeight) + 1;
    }

}
