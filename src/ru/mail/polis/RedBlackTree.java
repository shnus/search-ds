package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private final Node nil = new Node(null);
    private Node root = nil;
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
        Node temp = root;
        Node node = new Node(value);
        if (root == nil) {
            root = node;
            node.color = Color.BLACK;
            node.parent = nil;
        } else {
            node.color = Color.RED;
            while (true) {
                if (compare(node.value,temp.value) < 0) {
                    if (temp.left == nil) {
                        temp.left = node;
                        node.parent = temp;
                        break;
                    } else {
                        temp = temp.left;
                    }
                } else if (compare(node.value,temp.value) > 0) {
                    if (temp.right == nil) {
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
            fixTree(node);

        }
        size++;
        return true;
    }

    /**
     * Делаем корекцию в соответствии с правилами красно-черного дерева
     * @param node в качестве аргумента принимает только что созданный лист
     */
    private void fixTree(Node node) {
        while (node.parent.color == Color.RED) {
            Node uncle = nil;
            if (node.parent == node.parent.parent.left) {
                uncle = node.parent.parent.right;

                if (uncle != nil && uncle.color == Color.RED) {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.right) {
                    //вращаем два раза
                    node = node.parent;
                    rotateLeft(node);
                }
                node.parent.color = Color.BLACK;
                node.parent.parent.color = Color.RED;
                rotateRight(node.parent.parent);
            } else {
                uncle = node.parent.parent.left;
                if (uncle != nil && uncle.color == Color.RED) {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                    continue;
                }
                if (node == node.parent.left) {
                    //вращаем два раза
                    node = node.parent;
                    rotateRight(node);
                }
                node.parent.color = Color.BLACK;
                node.parent.parent.color = Color.RED;
                rotateLeft(node.parent.parent);
            }
        }
        root.color = Color.BLACK;
    }

    /**
     * Малое левок вращение
     * @param node элемент относительно которого вращаем дерево/поддерево
     */
    private void rotateLeft(Node node) {
        if (node.parent != nil) {
            if (node == node.parent.left) {
                node.parent.left = node.right;
            } else {
                node.parent.right = node.right;
            }
            node.right.parent = node.parent;
            node.parent = node.right;
            if (node.right.left != nil) {
                node.right.left.parent = node;
            }
            node.right = node.right.left;
            node.parent.left = node;
        } else {//нужно вращать корень
            Node right = root.right;
            root.right = right.left;
            right.left.parent = root;
            root.parent = right;
            right.left = root;
            right.parent = nil;
            root = right;
        }
    }

    /**
     * Малое правое вращение
     * @param node элемент относительно которого вращаем дерево/поддерево
     */
    private void rotateRight(Node node) {
        if (node.parent != nil) {
            if (node == node.parent.left) {
                node.parent.left = node.left;
            } else {
                node.parent.right = node.left;
            }

            node.left.parent = node.parent;
            node.parent = node.left;
            if (node.left.right != nil) {
                node.left.right.parent = node;
            }
            node.left = node.left.right;
            node.parent.right = node;
        } else {//нужно вращать корень
            Node left = root.left;
            root.left = root.left.right;
            left.right.parent = root;
            root.parent = left;
            left.right = root;
            left.parent = nil;
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
        Node toRemove;
        if((toRemove = findNode(new Node((E)object), root))==null) return false;
        Node x;
        Node temp = toRemove;
        Color tempOriginalColor = temp.color;

        if(toRemove.left == nil){
            x = toRemove.right;
            transplant(toRemove, toRemove.right);
        }else if(toRemove.right == nil){
            x = toRemove.left;
            transplant(toRemove, toRemove.left);
        }else{
            temp = minimumNode(toRemove.right);
            tempOriginalColor = temp.color;
            x = temp.right;
            if(temp.parent == toRemove)
                x.parent = temp;
            else{
                transplant(temp, temp.right);
                temp.right = toRemove.right;
                temp.right.parent = temp;
            }
            transplant(toRemove, temp);
            temp.left = toRemove.left;
            temp.left.parent = temp;
            temp.color = toRemove.color;
        }
        if(tempOriginalColor == Color.BLACK)
            deleteFixup(x);
        size--;
        return true;
    }

    private void transplant(Node target, Node with){
        if(target.parent == nil){
            root = with;
        }else if(target == target.parent.left){
            target.parent.left = with;
        }else
            target.parent.right = with;
        with.parent = target.parent;
    }

    private void deleteFixup(Node x){
        while(x!=root && x.color == Color.BLACK){
            if(x == x.parent.left){
                Node w = x.parent.right;
                if(w.color == Color.RED){
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if(w.left.color == Color.BLACK && w.right.color == Color.BLACK){
                    w.color = Color.RED;
                    x = x.parent;
                    continue;
                }
                else if(w.right.color == Color.BLACK){
                    w.left.color = Color.BLACK;
                    w.color = Color.RED;
                    rotateRight(w);
                    w = x.parent.right;
                }
                if(w.right.color == Color.RED){
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.right.color = Color.BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            }else{
                Node w = x.parent.left;
                if(w.color == Color.RED){
                    w.color = Color.BLACK;
                    x.parent.color = Color.RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if(w.right.color == Color.BLACK && w.left.color == Color.BLACK){
                    w.color = Color.RED;
                    x = x.parent;
                    continue;
                }
                else if(w.left.color == Color.BLACK){
                    w.right.color = Color.BLACK;
                    w.color = Color.RED;
                    rotateLeft(w);
                    w = x.parent.left;
                }
                if(w.left.color == Color.RED){
                    w.color = x.parent.color;
                    x.parent.color = Color.BLACK;
                    w.left.color = Color.BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        x.color = Color.BLACK;
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
        if (root == nil) {
            return null;
        }

        if (compare(findNode.value,node.value) < 0) {
            if (node.left != nil) {
                return findNode(findNode, node.left);
            }
        } else if (compare(findNode.value,node.value) > 0) {
            if (node.right != nil) {
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
        while (curr.left != nil) {
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
    private Node minimumNode(Node subTreeRoot){
        while(subTreeRoot.left!=nil){
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
        while (curr.right != nil) {
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
        Node left = nil, right = nil, parent = nil;

        Node(E value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", left=" + left +
                    ", right=" + right +
                    ", parent=" + parent +
                    ", color=" + color +
                    '}';
        }
    }
}
