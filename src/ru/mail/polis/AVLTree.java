package ru.mail.polis;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class AVLTree<E extends Comparable<E>> extends BinarySearchTree<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;

    private Node<E> root;
    private int size;

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean contains(Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        return findNode(value) != null;
    }

    @SuppressWarnings("unchecked")
    private Node<E> findNode(Object object) {
        E value = (E) object;
        if (root != null) {
            Node<E> current = root;
            while (current != null) {
                int cmp = compare(current.value, value);
                if (cmp == 0) {
                    return current;
                } else if (cmp < 0) {
                    current = current.right;
                } else {
                    current = current.left;
                }
            }
        }
        return null;
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
        if (root == null) {
            root = new Node<>(value, null);
            size++;
            return true;
        }
        Node<E> current = root;
        while (true) {
            Node<E> parent = current;
            int cmp = compare(current.value, value);
            if (cmp == 0)
                return false;
            else if (cmp > 0) {
                current = current.left;
            } else {
                current = current.right;
            }
            if (current == null) {
                if (cmp > 0) {
                    parent.left = new Node<>(value, parent);
                } else {
                    parent.right = new Node<>(value, parent);
                }
                reBalance(parent);
                size++;
                return true;
            }
        }
    }

    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в дереве
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(Object object) {
        if (root == null)
            return false;
        Node<E> child = root;
        E value = (E) object;
        while (child != null) {
            Node<E> current = child;
            int cmp = compare(value, current.value);
            if (cmp == 0) {
                delete(current);
                size--;
                return true;
            } else if (cmp > 0) {
                child = current.right;
            } else {
                child = current.left;
            }
        }
        return false;
    }

    private void delete(Node<E> node) {
        if (node.left == null && node.right == null) {
            if (node.parent == null) {
                root = null;
            } else {
                Node<E> parent = node.parent;
                if (parent.left == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
                reBalance(parent);
            }
            return;
        }
        Node<E> child;
        if (node.left != null) {
            child = node.left;
            while (child.right != null) child = child.right;
            node.value = child.value;
            delete(child);
        } else {
            child = node.right;
            while (child.left != null) child = child.left;
            node.value = child.value;
            delete(child);
        }
    }

    private void reBalance(Node<E> node) {
        setBalance(node);
        if (node.balance == -2) {
            if (getHeight(node.left.left) >= getHeight(node.left.right))
                node = rotateRight(node);
            else
                node = bigRotateLeft(node);
        } else if (node.balance == 2) {
            if (getHeight(node.right.right) >= getHeight(node.right.left))
                node = rotateLeft(node);
            else
                node = bigRotateRight(node);
        }
        if (node.parent != null) {
            reBalance(node.parent);
        } else {
            root = node;
        }
    }

    private Node<E> rotateLeft(Node<E> node) {
        Node<E> right = node.right;
        right.parent = node.parent;
        node.right = right.left;
        if (node.right != null)
            node.right.parent = node;
        right.left = node;
        node.parent = right;
        if (right.parent != null) {
            if (right.parent.right == node) {
                right.parent.right = right;
            } else {
                right.parent.left = right;
            }
        }
        setBalance(node, right);
        return right;
    }

    private Node<E> rotateRight(Node<E> node) {
        Node<E> left = node.left;
        left.parent = node.parent;
        node.left = left.right;
        if (node.left != null)
            node.left.parent = node;
        left.right = node;
        node.parent = left;
        if (left.parent != null) {
            if (left.parent.right == node) {
                left.parent.right = left;
            } else {
                left.parent.left = left;
            }
        }
        setBalance(node, left);
        return left;
    }

    private Node<E> bigRotateLeft(Node<E> node) {
        node.left = rotateLeft(node.left);
        return rotateRight(node);
    }

    private Node<E> bigRotateRight(Node<E> node) {
        node.right = rotateRight(node.right);
        return rotateLeft(node);
    }

    private int getHeight(Node<E> node) {
        return node == null ? -1 : node.height;
    }

    @SafeVarargs
    private final void setBalance(Node<E>... nodes) {
        for (Node<E> n : nodes) {
            setHeight(n);
            n.balance = getHeight(n.right) - getHeight(n.left);
        }
    }

    private void setHeight(Node<E> node) {
        if (node != null) {
            node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
        }
    }

    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        Node<E> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no last element");
        }
        Node<E> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
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

    private static class Node<T> {
        private T value;
        private Node<T> left;
        private Node<T> right;
        private Node<T> parent;
        private int height;
        private int balance;

        private Node(T value, Node<T> parent) {
            this.value = value;
            this.parent = parent;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            sb.append("d=").append(value);
            if (left != null) {
                sb.append(", l=").append(left);
            }
            if (right != null) {
                sb.append(", r=").append(right);
            }
            sb.append('}');
            return sb.toString();
        }
    }


}
