package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedSet;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;
    private Node root; //todo: Создайте новый класс если нужно. Добавьте новые поля, если нужно.
    private int size;
    //todo: добавьте дополнительные переменные и/или методы если нужно

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
        //todo: следует реализовать
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            root = new Node(value, null);
        } else {
            root = addNode(root, value);
            root.color = Color.BLACK;
        }
        size++;
        return true;

    }

    private Node addNode(Node node, E value) {
        if (node == null)
            return new Node(value, null);
        //Node curr = root;
        int cmp;
        cmp = compare(node.value, value);
        if (cmp > 0)
            node.left = addNode(node.left, value);
        else if (cmp < 0)
            node.right = addNode(node.right, value);
        else
            node.value = value;


        // fix-up any right-leaning links
        if (isRed(node.right) && !isRed(node.left))
            node = turnLeft(node);
        if (isRed(node.left)  &&  isRed(node.left.left))
            node = turnRight(node);
        if (isRed(node.left)  &&  isRed(node.right))
            flipColors(node);

        return node;
    }

    private void flipColors(Node node) {
        if (node.color==Color.BLACK)
            node.color=Color.RED;
        else
            node.color=Color.BLACK;

        if (node.left.color==Color.BLACK)
            node.left.color=Color.RED;
        else
            node.left.color=Color.BLACK;

        if (node.right.color==Color.BLACK)
            node.right.color=Color.RED;
        else
            node.right.color=Color.BLACK;
    }

    private boolean isRed(Node node) {
        if(node==null)
            return false;
        if(node.color==Color.RED)
            return true;
        return false;
    }

    private Node moveRedLeft(Node node) {
        flipColors(node);
        if (isRed(node.right.left)) {
            node.right = turnRight(node.right);
            node = turnLeft(node);
            flipColors(node);
        }
        return node;
    }

    private Node moveRedRight(Node node) {
        flipColors(node);
        if (isRed(node.left.left)) {
            node = turnRight(node);
            flipColors(node);
        }
        return node;
    }

    private Node balance(Node node) {
        if (isRed(node.right))
            node = turnLeft(node);
        if (isRed(node.left) && isRed(node.left.left))
            node = turnRight(node);
        if (isRed(node.left) && isRed(node.right))
            flipColors(node);
        return node;
    }



    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо вставить
     * @return true, если элемент содержался в дереве
     */
    @Override
    public boolean remove(Object value) {
        //todo: следует реализовать
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        @SuppressWarnings("unchecked")
        E key = (E) value;
        if (root == null) {
            return false;
        }
        Node parent = root;
        Node curr = root;
        int cmp;
        while ((cmp = compare(curr.value, key)) != 0) {
            parent = curr;
            if (cmp > 0) {
                curr = curr.left;
            } else {
                curr = curr.right;
            }
            if (curr == null) {
                return false; // ничего не нашли
            }
        }
        if (curr.left != null && curr.right != null) {
            Node next = curr.right;
            Node pNext = curr;
            while (next.left != null) {
                pNext = next;
                next = next.left;
            } //next = наименьший из больших
            curr.value = next.value;
            next.value = null;
            //у правого поддерева нет левых потомков
            if (pNext == curr) {
                curr.right = next.right;
            } else {
                pNext.left = next.right;
            }
            next.right = null;
        } else {
            if (curr.left != null) {
                reLink(parent, curr, curr.left);
            } else if (curr.right != null) {
                reLink(parent, curr, curr.right);
            } else {
                reLink(parent, curr, null);
            }
        }
        size--;

        //recolor;
        return true;
    }

    private void reLink(Node parent, Node curr, Node child) {
        if (parent == curr) {
            root = child;
            root.parent = null;
        } else if (parent.left == curr) {
            parent.left = child;
            parent.left.parent=parent;
        } else {
            parent.right = child;
            parent.right.parent=parent;
        }
        curr.value = null;
    }

    /**
     * Ищет элемент с таким же значением в дереве.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо поискать
     * @return true, если такой элемент содержится в дереве
     */
    @Override
    public boolean contains(Object value) {
        //todo: следует реализовать
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        @SuppressWarnings("unchecked")
        E key = (E) value;
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
        //todo: следует реализовать
        // тупо идти налево
        Node node = this.root;
        if(node==null)
            throw new NoSuchElementException("first");

        while (node.left!=null){
            node=node.left;
        }
        return node.value;
    }

    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
        //todo: следует реализовать
// тупо идти направо
        Node node = this.root;
        if(node==null)
            throw new NoSuchElementException("last");

        while (node.right!=null){
            node=node.right;
        }
        return node.value;
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
        return this.size;
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

    @Override
    public void checkBalanced() throws NotBalancedTreeException {
        if (root != null) {
            if (root.color != Color.BLACK) {
                throw new NotBalancedTreeException("Root must be black");
            }
//            reHeight(root);
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

//    private int reHeight(Node curr) {
//        if (curr == null) {
//            return 0;
//        }
//
//        curr.height = Math.max(reHeight(curr.left), reHeight(curr.right))+1;
//        return curr.height;
//    }



    private void checkRedNodeRule(Node node) throws NotBalancedTreeException {
        if(node==null)
            return;
        if (node.left != null && node.left.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then left child must be black.\n" + node.toString());
        }
        if (node.right != null && node.right.color != Color.BLACK) {
            throw new NotBalancedTreeException("If a node is red, then right child must be black.\n" + node.toString());
        }
        checkRedNodeRule(node.left);
        checkRedNodeRule(node.right);
    }

    //на вход подается верхняя нода
    private Node turnRight(Node node){
        Node temp = node.left;
        node.left = temp.right;
        temp.right = node;
        temp.color = temp.right.color;
        temp.right.color = Color.RED;
        temp.height = node.height;
        return temp;
    }

    private Node turnLeft(Node node){
        Node temp = node.right;
        node.right = temp.left;
        temp.left = node;
        temp.color = temp.left.color;
        temp.left.color = Color.RED;
        temp.height = node.height;
        return temp;
    }

//    private int height(Node node) {
//        if (node == null) return 0;
//        return node.height;
//    }

    enum Color {
        RED, BLACK
    }

    class Node {
        E value;
        Node left=null;
        Node right=null;
        Node parent;
        Color color = Color.RED;
        public int height=-1;

        public Node(E value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    ", color=" + color +
                    ", left=" + left +
                    ", right=" + right +
                    '}';
        }
    }


    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<Integer>();
//        tree.add(10);
//        tree.add(5);
//        tree.add(15);
//        System.out.println(tree);
//        tree.remove(10);
//        tree.remove(15);
//        System.out.println(tree);
//        tree.remove(5);
//        System.out.println(tree);
//        Random random = new Random(100);

//        tree.add(100);
//        tree.add(200);
//        tree.add(50);
//        tree.add(250);
//        tree.add(300);

        tree.add(50);
        tree.add(25);
        tree.add(75);
        tree.add(100);
        tree.add(60);
        tree.add(55);
        tree.add(70);
        tree.add(65);
        tree.add(71);
        tree.add(73);
        System.out.println(tree);
        //tree.turnLeftSmall();
        System.out.println(tree.root.toString());
    }
}
