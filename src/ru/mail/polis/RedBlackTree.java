package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private final Node nullNode = new Node(null);
    private Node root = nullNode;
    private int size;

    public RedBlackTree() {
        this(null);
    }
    public RedBlackTree(Comparator<E> comparator) {
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
        if (root == nullNode) {
            root = new Node(value);
            root.color = Color.BLACK;
            root.parent = nullNode;
        } else {
            Node temp = root;
            Node node = new Node(value);
            node.color = Color.RED;
            while (true) {
                int cmp = compare(node.value,temp.value);
                if (cmp < 0) {
                    if (temp.left == nullNode) {
                        temp.left = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.left;
                    }
                } else if (cmp > 0) {
                    if (temp.right == nullNode) {
                        temp.right = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.right;
                    }
                } else {
                    return false;
                }
            }
            balance(node);

        }
        size++;
        return true;
    }

    private void balance(Node node) {
        while (node.parent.color == Color.RED) {
            Node uncle = nullNode;
            Node grand = node.parent.parent;
            Node parent = node.parent;
            if (parent != grand.left) {
                uncle = grand.left;
                if (uncle != nullNode && uncle.color == Color.RED) {
                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grand.color = Color.RED;
                    node = grand;
                    continue;
                }
                if (node == parent.left) {
                    node = parent;
                    rotateright(node);
                }
                node.parent.color = Color.BLACK;
                node.parent.parent.color = Color.RED;
                rotateleft(node.parent.parent);
            } else {
                uncle = grand.right;
                if (uncle != nullNode && uncle.color == Color.RED) {
                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grand.color = Color.RED;
                    node = grand;
                    continue;
                }
                if (node == parent.right) {
                    node = parent;
                    rotateleft(node);
                }
                node.parent.color = Color.BLACK;
                node.parent.parent.color = Color.RED;
                rotateright(node.parent.parent);
            }
        }
        root.color = Color.BLACK;
    }

    private void rotateleft(Node node) {
        if (node.parent != nullNode) {
            if (node == node.parent.left) {
                node.parent.left = node.right;
            } else {
                node.parent.right = node.right;
            }
            node.right.parent = node.parent;
            node.parent = node.right;
            if (node.right.left != nullNode) {
                node.right.left.parent = node;
            }
            node.right = node.right.left;
            node.parent.left = node;
        } else {
            Node right = root.right;
            root.right = right.left;
            right.left.parent = root;
            root.parent = right;
            right.left = root;
            right.parent = nullNode;
            root = right;
        }
    }

    private void rotateright(Node node) {
        if (node.parent != nullNode) {
            if (node == node.parent.left) {
                node.parent.left = node.left;
            } else {
                node.parent.right = node.left;
            }
            node.left.parent = node.parent;
            node.parent = node.left;
            if (node.left.right != nullNode) {
                node.left.right.parent = node;
            }
            node.left = node.left.right;
            node.parent.right = node;
        } else {
            Node left = root.left;
            root.left = root.left.right;
            left.right.parent = root;
            root.parent = left;
            left.right = root;
            left.parent = nullNode;
            root = left;
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
    public boolean remove(Object object) {
        Node current, p;
        if((current = findNode(new Node((E)object), root))==null) return false;
        Node temp = current;
        Color color = temp.color;

        if (current.left == nullNode) {
            p = current.right;
            transplant(current, current.right);
        } else if (current.right == nullNode){
            p = current.left;
            transplant(current, current.left);
        } else {
            temp = minNode(current.right);
            color = temp.color;
            p = temp.right;
            if(temp.parent == current)
                p.parent = temp;
            else {
                transplant(temp, temp.right);
                temp.right = current.right;
                temp.right.parent = temp;
            }
            transplant(current, temp);
            temp.left = current.left;
            temp.left.parent = temp;
            temp.color = current.color;
        }
        if (color == Color.BLACK)
            rembalance(p);
        size--;
        return true;
    }

    private void transplant(Node target, Node with){
        if(target.parent == nullNode){
            root = with;
        }else if(target == target.parent.left){
            target.parent.left = with;
        }else
            target.parent.right = with;
        with.parent = target.parent;
    }

    private void rembalance(Node p){
        while (p!=root && p.color == Color.BLACK) {
            if (p == p.parent.left) {
                Node q = p.parent.right;
                if (q.color == Color.RED) {
                    q.color = Color.BLACK;
                    p.parent.color = Color.RED;
                    rotateleft(p.parent);
                    q = p.parent.right;
                }
                if (q.left.color == Color.BLACK && q.right.color == Color.BLACK) {
                    q.color = Color.RED;
                    p = p.parent;
                    continue;
                }
                else if (q.right.color == Color.BLACK) {
                    q.left.color = Color.BLACK;
                    q.color = Color.RED;
                    rotateright(q);
                    q = p.parent.right;
                }
                if (q.right.color == Color.RED) {
                    q.color = p.parent.color;
                    p.parent.color = Color.BLACK;
                    q.right.color = Color.BLACK;
                    rotateleft(p.parent);
                    p = root;
                }
            } else {
                Node q = p.parent.left;
                if (q.color == Color.RED) {
                    q.color = Color.BLACK;
                    p.parent.color = Color.RED;
                    rotateright(p.parent);
                    q = p.parent.left;
                }
                if(q.right.color == Color.BLACK && q.left.color == Color.BLACK){
                    q.color = Color.RED;
                    p = p.parent;
                    continue;
                }
                else if(q.left.color == Color.BLACK){
                    q.right.color = Color.BLACK;
                    q.color = Color.RED;
                    rotateleft(q);
                    q = p.parent.left;
                }
                if(q.left.color == Color.RED){
                    q.color = p.parent.color;
                    p.parent.color = Color.BLACK;
                    q.left.color = Color.BLACK;
                    rotateright(p.parent);
                    p = root;
                }
            }
        }
        p.color = Color.BLACK;
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
            while (curr.value != null) {
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
     * Ищет элемент с таким же значением в дереве.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param findNode элемент который необходимо поискать
     * @param node элемент с которым сравниваем, для продвижения по дереву
     * @return в отличие от contains возвращает целый Node, а не только значение
     */
    private Node findNode(Node findNode, Node node) {
        if (root == nullNode) {
            return null;
        }

        if (compare(findNode.value,node.value) < 0) {
            if (node.left != nullNode) {
                return findNode(findNode, node.left);
            }
        } else if (compare(findNode.value,node.value) > 0) {
            if (node.right != nullNode) {
                return findNode(findNode, node.right);
            }
        } else if (compare(findNode.value,node.value) == 0) {
            return node;
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
        if (isEmpty()) {
            throw new NoSuchElementException("first");
        }
        Node curr = root;
        while (curr.left != nullNode) {
            curr = curr.left;
        }
        return curr.value;
    }
    /**
     * Ищет наименьший элемент в поддереве
     * @param subTreeRoot начальный элемент поддерева
     * @return в отличие от contains возвращает целый Node, а не только значение
     * @throws NoSuchElementException если дерево пустое
     */
    private Node minNode(Node subTreeRoot){
        while(subTreeRoot.left!= nullNode){
            subTreeRoot = subTreeRoot.left;
        }
        return subTreeRoot;
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
        while (curr.right != nullNode) {
            curr = curr.right;
        }
        return curr.value;
    }

    private int compare(E v1, E v2) {
        return comparator == null ? v1.compareTo(v2) : comparator.compare(v1, v2);
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    /**
     * Returns the number of key-value pairs in this symbol table.
     * @return the number of key-value pairs in this symbol table
     */
    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        return "RBTree{" +
                "size=" + size + ", " +
                "tree=" + root +
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
        if (root != null) {
            if (root.color != Color.BLACK) {
                throw new NotBalancedTreeException("Root must be black");
            }
            traverseTreeAndCheckBalanced(root);
        }
    }

    private int traverseTreeAndCheckBalanced(Node node) throws NotBalancedTreeException {
        if (node == null) {
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

    private class Node {
        E value;
        Color color = Color.BLACK;
        Node left = nullNode, right = nullNode, parent = nullNode;

        Node(E value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", left=" + left +
                    ", right=" + right +
                    ", color=" + color +
                    '}';
        }
    }
}