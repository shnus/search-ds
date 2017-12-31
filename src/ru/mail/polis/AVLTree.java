package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class AVLTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;

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
        if (root == null) {
            root = new Node(value);
            size++;
        } else {
            try {
                root = insert(root, value);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return true;
    }

    private Node insert(Node root, E key) {
        if (root == null) {
            size++;
            return new Node(key);
        }
        int cmp = compare(key, root.value);
        if (cmp > 0) {
            Node node = insert(root.right, key);
            node.parent = root;
            root.right = node;
        } else if (cmp < 0) {
            Node node = insert(root.left, key);
            node.parent = root;
            root.left = node;
        } else {
            throw new IllegalArgumentException("This key already exists");
        }

        return balance(root);
    }

    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param object элемент который необходимо вставить
     * @return true, если элемент содержался в дереве
     */
    @Override
    public boolean remove(Object object) {
        @SuppressWarnings("unchecked")
        E value = (E) object;
        try {
            root = remove(root,value);
            size--;
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private Node remove(Node node, E value) throws NoSuchElementException {
        if (node == null) {
            throw new NoSuchElementException();
        } else {
            int cmp = compare(node.value,value);

            if (cmp < 0) {
                node.right = remove(node.right,value);
            } else if (cmp > 0) {
                node.left = remove(node.left,value);
            } else {
                Node leftNode = node.left;
                Node rightNode = node.right;
                if (rightNode == null) {
                    return leftNode;
                }
                Node minNode = findMin(rightNode);
                minNode.right = removeMin(rightNode);
                minNode.left = leftNode;
                return balance(minNode);
            }

            return balance(node);
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
        @SuppressWarnings("unchecked")
        E value = (E) object;
        return find(root, value) != null;
    }

    private Node find(Node root, E key) {
        while (root != null) {
            int cmp = compare(key, root.value);
            if (cmp > 0) {
                root = root.right;
            } else if (cmp < 0) {
                root = root.left;
            } else {
                return root;
            }
        }
        return null;
    }

    /**
     * Ищет наименьший элемент в дереве
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (root == null) {
            throw new NoSuchElementException("first");
        }

        return findMin(root).value;
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (root == null) {
            throw new NoSuchElementException("last");
        }

        return findMax(root).value;
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

    private Node balance(Node v) {
        updateHeight(v);
        if (diff(v) == -2) {
            // rotateLeft or bigRotateLeft
            if (diff(v.right) > 0) {
                v.right = rotateRight(v.right);
            }
            return rotateLeft(v);
        } else if (diff(v) == 2) {
            // rotateRight or bigRotateRight
            if (diff(v.left) < 0) {
                v.left = rotateLeft(v.left);
            }
            return rotateRight(v);
        }

        // no balancing needed
        return v;
    }

    private int height(Node v) {
        return v == null ? 0 : v.height;
    }

    private void updateHeight(Node v) {
        v.height = Math.max(height(v.left), height(v.right)) + 1;
    }

    private int diff(Node v) {
        return height(v.left) - height(v.right);
    }

    private Node rotateLeft(Node v) {
        Node x = v.right;

        x.parent = v.parent;
        v.parent = x;
        v.right = x.left;
        x.left = v;

        updateHeight(v);
        updateHeight(x);

        return x;
    }

    private Node rotateRight(Node v) {
        Node x = v.left;

        x.parent = v.parent;
        v.parent = x;
        v.left = x.right;
        x.right = v;

        updateHeight(v);
        updateHeight(x);

        return x;
    }

    private Node findMin(Node root) {
        if (root == null) return null;
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    private Node findMax(Node root) {
        if (root == null) return null;
        while (root.right != null) {
            root = root.right;
        }
        return root;
    }

    private Node removeMin(Node v) {
        if (v.left == null) return v.right;
        v.left = removeMin(v.left);
        return balance(v);
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

    class Node {
        E value;
        int height;
        Node parent, left, right;

        Node(E value) {
            this.value = value;
            this.height = 1;
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
