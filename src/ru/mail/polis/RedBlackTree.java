package ru.mail.polis;

import java.util.*;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private Node<E> root;
    private int size;
    private Node<E> nil;

    {
        nil = new Node<>();
        nil.left = nil.right = nil;
        nil.color = Color.BLACK;
        nil.value = null;
        root = nil;
    }

    public RedBlackTree() {
        this(null);
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    private void leftRotate(Node<E> node) {
        Node<E> temp = node.right;
        node.right = temp.left;

        if (temp.left != nil) {
            temp.left.parent = node;
        }

        temp.parent = node.parent;

        if (node.parent == null) {
            root = temp;
        } else if (node == node.parent.left) {
            node.parent.left = temp;
        } else {
            node.parent.right = temp;
        }

        temp.left = node;
        node.parent = temp;
    }

    void rightRotate(Node<E> node) {
        Node<E> temp = node.left;
        node.left = temp.right;

        if (temp.right != nil) {
            temp.right.parent = node;
        }

        temp.parent = node.parent;

        if (node.parent == null) {
            root = temp;
        } else if (node == node.parent.left) {
            node.parent.left = temp;
        } else {
            node.parent.right = temp;
        }

        temp.right = node;
        node.parent = temp;
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
        if (contains(value))
            return false;
        Node<E> insertNode = insert(value);
        Node<E> temp;

        while (insertNode != root && insertNode.parent.color == Color.RED) {
            if (insertNode.parent == insertNode.parent.parent.left) {
                temp = insertNode.parent.parent.right;

                if (temp.color == Color.RED) {
                    insertNode.parent.color = Color.BLACK;
                    temp.color = Color.BLACK;
                    insertNode.parent.parent.color = Color.RED;
                    insertNode = insertNode.parent.parent;
                } else {
                    if (insertNode == insertNode.parent.right) {
                        insertNode = insertNode.parent;
                        leftRotate(insertNode);
                    }

                    insertNode.parent.color = Color.BLACK;
                    insertNode.parent.parent.color = Color.RED;
                    rightRotate(insertNode.parent.parent);
                }
            } else {
                temp = insertNode.parent.parent.left;

                if (temp.color == Color.RED) {
                    insertNode.parent.color = Color.BLACK;
                    temp.color = Color.BLACK;
                    insertNode.parent.parent.color = Color.RED;
                    insertNode = insertNode.parent.parent;
                } else {
                    if (insertNode == insertNode.parent.left) {
                        insertNode = insertNode.parent;
                        rightRotate(insertNode);
                    }

                    insertNode.parent.color = Color.BLACK;
                    insertNode.parent.parent.color = Color.RED;
                    leftRotate(insertNode.parent.parent);
                }
            }
        }

        root.color = Color.BLACK;
        size++;
        return true;
    }


    private Node<E> insert(E value) {
        if (root == nil) {
            root = new Node<>();
            root.value = value;
            root.color = Color.RED;
            root.left = root.right = nil;
            return root;
        }

        Node<E> insertNode = root;
        Node<E> parentNode = null;

        while (insertNode != nil) {
            parentNode = insertNode;

            if (compare(insertNode.value, value) > 0) {
                insertNode = insertNode.left;
            } else {
                insertNode = insertNode.right;
            }
        }

        insertNode = new Node<>();
        insertNode.value = value;
        insertNode.color = Color.RED;
        insertNode.left = insertNode.right = nil;

        if (compare(value, parentNode.value) < 0) {
            parentNode.left = insertNode;
        } else {
            parentNode.right = insertNode;
        }

        insertNode.parent = parentNode;
        return insertNode;
    }

    private Node<E> findMin(Node<E> node) {
        while (node.left != nil) {
            node = node.left;
        }
        return node;
    }

    private Node<E> findMax(Node<E> node) {
        while (node.right != nil) {
            node = node.right;
        }
        return node;
    }

    private Node<E> son(Node<E> node) {
        if (node.right != nil) {
            return findMin(node.right);
        }
        Node<E> y = node.parent;
        while (y != nil && node == y.right) {
            node = y;
            y = y.parent;
        }
        return y;
    }

    private Node<E> findNode(E node) {
        Node<E> tmp = root;
        while (tmp != nil && compare(tmp.value, node) != 0) {
            if (compare(node, tmp.value) < 0) {
                tmp = tmp.left;
            } else {
                tmp = tmp.right;
            }
        }
        return tmp;
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
        Node<E> removeNode = findNode((E) object);
        if (removeNode == nil || removeNode == null) {
            return false;
        }
        Node<E> temp = nil, temp1 = nil;

        if (removeNode.left == nil || removeNode.right == nil) {
            temp1 = removeNode;
        } else {
            temp1 = son(removeNode);
        }

        if (temp1.left != nil) {
            temp = temp1.left;
        } else {
            temp = temp1.right;
        }

        temp.parent = temp1.parent;

        if (temp1.parent == null) {
            root = temp;
        } else if (temp1 == temp1.parent.left) {
            temp1.parent.left = temp;
        } else {
            temp1.parent.right = temp;
        }

        if (temp1 != removeNode) {
            removeNode.value = temp1.value;
        }

        if (temp1.color == Color.BLACK) {
            deleteFix(temp);
        }
        size--;
        return true;
    }

    void deleteFix(Node<E> node) {
        Node<E> temp;

        while (node != root && node.color == Color.BLACK) {
            if (node == node.parent.left) {
                temp = node.parent.right;

                if (temp.color == Color.RED) {
                    temp.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    leftRotate(node.parent);
                    temp = node.parent.right;
                }

                if (temp.left.color == Color.BLACK && temp.right.color == Color.BLACK) {
                    temp.color = Color.RED;
                    node = node.parent;
                } else {
                    if (temp.right.color == Color.BLACK) {
                        temp.left.color = Color.BLACK;
                        temp.color = Color.RED;
                        rightRotate(temp);
                        temp = node.parent.right;
                    }

                    temp.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    temp.right.color = Color.BLACK;
                    leftRotate(node.parent);
                    node = root;
                }
            } else {
                temp = node.parent.left;

                if (temp.color == Color.RED) {
                    temp.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    rightRotate(node.parent);
                    temp = node.parent.left;
                }

                if (temp.left.color == Color.BLACK && temp.right.color == Color.BLACK) {
                    temp.color = Color.RED;
                    node = node.parent;
                } else {
                    if (temp.left.color == Color.BLACK) {
                        temp.right.color = Color.BLACK;
                        temp.color = Color.RED;
                        leftRotate(temp);
                        temp = node.parent.left;
                    }

                    temp.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    temp.left.color = Color.BLACK;
                    rightRotate(node.parent);
                    node = root;
                }
            }
        }

        node.color = Color.BLACK;
    }

    /**
     * Ищет элемент с таким же значением в дереве.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо поискать
     * @return true, если такой элемент содержится в дереве
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(Object value) {
        E x = (E) value;
        Node<E> tmp = root;
        while (tmp != nil) {
            if (compare(tmp.value, x) == 0) {
                return true;
            }
            if (compare(tmp.value, x) > 0) {
                tmp = tmp.left;
            } else {
                tmp = tmp.right;
            }
        }
        return false;
    }

    /**
     * Ищет наименьший элемент в дереве
     *
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("first");
        }
        return findMin(root).value;
    }

    /**
     * Ищет наибольший элемент в дереве
     *
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        if (isEmpty()) {
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

    private void inorderTraverse(Node<E> curr, StringBuilder sb) {
        if (curr == nil) {
            return;
        }
        inorderTraverse(curr.left, sb);
        sb.append(curr.value).append(",");
        inorderTraverse(curr.right, sb);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RBT{");
        sb.append("size=").append(size).append(", ");
        sb.append("tree={");
        inorderTraverse(root, sb);
        sb.append("}}");
        return sb.toString();
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
     * Обходит дерево и проверяет выполнение свойств сбалансированного красно-чёрного дерева
     * <p>
     * 1) Корень всегда чёрный.
     * 2) Если узел красный, то его потомки должны быть чёрными (обратное не всегда верно)
     * 3) Все пути от узла до листьев содержат одинаковое количество чёрных узлов (чёрная высота)
     *
     * @throws NotBalancedTreeException если какое-либо свойство невыполнено
     */
    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        if (root != nil) {
            if (root.color != Color.BLACK) {
                throw new NotBalancedTreeException("Root must be black");
            }
            traverseTreeAndCheckBalanced(root);
        }
    }

    private int traverseTreeAndCheckBalanced(Node node) throws NotBalancedTreeException {
        if (node == nil) {
            return 1;
        }
        int leftBlackHeight = traverseTreeAndCheckBalanced(node.left);
        int rightBlackHeight = traverseTreeAndCheckBalanced(node.right);
        if (leftBlackHeight != rightBlackHeight) {
            throw NotBalancedTreeException.create("Black height must be equal.", leftBlackHeight, rightBlackHeight, node.toString());
        }
        if (node.color == Color.RED) {
            checkRedNodeRule(node);
            return leftBlackHeight;
        }
        return leftBlackHeight + 1;
    }

    private void checkRedNodeRule(Node node) throws NotBalancedTreeException {
        if (node.left != null && node.left.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then left child must be black.\n" + node.toString());
        }
        if (node.right != null && node.right.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then right child must be black.\n" + node.toString());
        }
    }

    enum Color {
        RED, BLACK
    }

    class Node<E> {
        E value;
        Node<E> left;
        Node<E> right;
        Node<E> parent;
        Color color = Color.BLACK;


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
