package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class RedBlackTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private int size;
    private Node root;
    private Node nil;
    private final Comparator<E> comparator;

    public RedBlackTree() {
        this.comparator = null;
        nil = new Node(null, null);
        nil.color = Color.BLACK;
        root = nil;
    }

    public RedBlackTree(Comparator<E> comparator) {
        this.comparator = comparator;
        nil = new Node(null, null);
        nil.color = Color.BLACK;
        root = nil;
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }


    enum Color {
        RED, BLACK
    }

    public Color color(Node node){
    if (node==null)
        return Color.BLACK;
    return node.color;
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

    class Node {
        E value;
        Node left;
        Node right;
        Node parent;
        Color color = Color.RED;

        Node(E value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("N{");
            if (this.color==Color.BLACK)
                sb.append("black ");
            else
                sb.append("red ");
            sb.append("d=").append(this.value);
            if (this.left.value != null) {
                sb.append(", l=").append(this.left);
            }
            if (this.right.value != null) {
                sb.append(", r=").append(this.right);
            }
            sb.append('}');
            return sb.toString();
        }
    }

    /**
     * Ищет наименьший элемент в дереве
     * @return Возвращает наименьший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E first() {
        // тупо идти налево
        Node node = this.root;
        if(node.value==null)
            throw new NoSuchElementException("first");

        while (node.left!=null){
            if(node.left.value!=null)
                node=node.left;
            else break;
        }
        return node.value;
    }

    private int compare(E v1, E v2) {
        return this.comparator == null ? v1.compareTo(v2) : this.comparator.compare(v1, v2);
    }

    @Override
    public int size() {
        return this.size;
    }


    /**
     * Ищет наибольший элемент в дереве
     * @return Возвращает наибольший элемент в дереве
     * @throws NoSuchElementException если дерево пустое
     */
    @Override
    public E last() {
    // тупо идти направо
        Node node = this.root;
        if(node.value==null)
            throw new NoSuchElementException("last");


        while (node.right!=null){
            if(node.right.value!=null)
                node=node.right;
            else break;
        }
        return node.value;
    }

    private void inorderTraverse(Node curr, List<E> list) {
        if (curr.value == null) {
            return;
        }
        inorderTraverse(curr.left, list);
        list.add(curr.value);
        inorderTraverse(curr.right, list);
    }


    @Override
    public boolean isEmpty() {
        return root.value == null;
    }

   /**
   //     * Ищет элемент с таким же значением в дереве.
   //     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
   //     *
   //     * @param value элемент который необходимо поискать
   //     * @return true, если такой элемент содержится в дереве
   //     */
    @Override
    public boolean contains(Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root != null) {
            Node curr = root;
            while (curr.value != null) {
                int cmp = compare(curr.value, (E) value);
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
        if (isEmpty()) {
            root = newLeaf(value, nil);
            root.color = Color.BLACK;
        } else {
            Node curr = root;
            Node currParent = nil;
            while (curr.value != null) {
                currParent = curr;
                int cmp = compare(curr.value, value);
                if (cmp == 0) {
                    return false;
                } else {
                    if (cmp > 0) {
                        curr = curr.left;
                    } else {
                        curr = curr.right;
                    }
                }
            }
            Node node = newLeaf(value, currParent);
            int cmp = compare(currParent.value, value);
            if (cmp > 0) {
                currParent.left = node;
            } else {
                currParent.right = node;
            }
            fix(node);
        }
        size++;
        return true;
    }

    private void fix(Node node) {
        while (node.parent.color==Color.RED) {
            if (node.parent.parent.left == node.parent) {
                Node y = node.parent.parent.right;
                if (y.color==Color.RED) {
                    node.parent.color=Color.BLACK;
                    y.color=Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right){
                        node = node.parent;
                        turnLeft(node);
                    }
                    node.parent.color=Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    turnRight(node.parent.parent);
                }
            } else {
                Node y = node.parent.parent.left;
                if (y.color==Color.RED) {
                    node.parent.color = Color.BLACK;
                    y.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left){
                        node = node.parent;
                        turnRight(node);
                    }
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    turnLeft(node.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
    }

    //на вход подается верхняя нода
    private void turnLeft(Node node) {
        Node temp = node.right;
        node.right = temp.left;
        if (temp.left!=nil){
            temp.left.parent = node;
        }
        temp.parent = node.parent;
        if (node.parent == nil){
            this.root = temp;
        } else if (node == node.parent.left){
            node.parent.left = temp;
        } else {
            node.parent.right = temp;
        }
        temp.left = node;
        node.parent = temp;
    }

    //на вход подается верхняя нода
    private void turnRight(Node node) {
        Node temp = node.left;
        node.left = temp.right;
        if (temp.right!=nil){
            temp.right.parent = node;
        }
        temp.parent = node.parent;
        if (node.parent == nil){
            root = temp;
        } else if (node == node.parent.left){
            node.parent.left = temp;
        } else {
            node.parent.right = temp;
        }
        temp.right = node;
        node.parent = temp;
    }

    private Node newLeaf(E value, Node parent) {
        Node node = new Node(value, parent);
        node.left = nil;
        node.right = nil;
        return node;
    }


    private Node find(Object value) {
        Node curr = this.root;
        int cmp=0;
        while (curr.value!=null){
            cmp = compare(curr.value, (E) value);
            if (cmp > 0) {
                curr=curr.left;
            } else if (cmp < 0) {
                curr=curr.right;
            } else {
                return  curr;
            }
        }
        return null;
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
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (isEmpty()){
            return false;
        } else {
            Node curr = find(value);
            if(curr!=null) {
                Delete(curr);
                size--;
                return true;
            }
        }
        return false;
    }

    private void Delete(Node node){
        Node curr = node;
        Node temp;
        Color savedColor = curr.color;
        if (node.left == nil){
            temp = node.right;
            transplant(node, node.right);
        } else if (node.right == nil){
            temp = node.left;
            transplant(node, node.left);
        } else {
            curr = treeMinimum(node.right);
            savedColor = curr.color;
            temp = curr.right;
            if (curr.parent == node){
                temp.parent = curr;
            }
            else {
                transplant(curr, curr.right);
                curr.right = node.right;
                curr.right.parent = curr;
            }
            transplant(node, curr);
            curr.left = node.left;
            curr.left.parent = curr;
            curr.color = node.color;
        }
        if (savedColor == Color.BLACK){
            fixDel(temp);
        }
    }

    private void fixDel(Node node) {
        Node sibling;
        while (node != root && node.color==Color.BLACK){
            if (node == node.parent.left){
                sibling = node.parent.right;
                if (sibling.color==Color.RED){
                    sibling.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    turnLeft(node.parent);
                    sibling = node.parent.right;
                }
                if (sibling.left.color == Color.BLACK && sibling.right.color == Color.BLACK){
                    sibling.color = Color.RED;
                    node = node.parent;
                } else {
                    if (sibling.right.color == Color.BLACK){
                        sibling.left.color = Color.BLACK;
                        sibling.color = Color.RED;
                        turnRight(sibling);
                        sibling = node.parent.right;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    sibling.right.color = Color.BLACK;
                    turnLeft(node.parent);
                    node = root;
                }
            } else {
                sibling = node.parent.left;
                if (sibling.color == Color.RED){
                    sibling.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    turnRight(node.parent);
                    sibling = node.parent.left;
                }
                if (sibling.right.color == Color.BLACK && sibling.left.color == Color.BLACK){
                    sibling.color = Color.RED;
                    node = node.parent;
                } else {
                    if (sibling.left.color == Color.BLACK){
                        sibling.right.color = Color.BLACK;
                        sibling.color = Color.RED;
                        turnLeft(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = Color.BLACK;
                    sibling.left.color = Color.BLACK;
                    turnRight(node.parent);
                    node = root;
                }
            }
        }
        node.color = Color.BLACK;
    }

    private Node treeMinimum(Node node) {
        while (node.left!=nil){
            node = node.left;
        }
        return node;
    }

    private void transplant(Node thisIs, Node thatIs) {
        if (thisIs.parent == nil){
            this.root = thatIs;
        } else if (thisIs == thisIs.parent.left) {
            thisIs.parent.left = thatIs;
        } else {
            thisIs.parent.right = thatIs;
        }
        thatIs.parent = thisIs.parent;
    }



    public String toString() {
        return "RBTree{" + root + "}";
    }

    public static void main(String[] args) {
        RedBlackTree<Integer> tree = new RedBlackTree<Integer>();

        tree.add(10);
        tree.add(11);
        tree.add(13);
        tree.add(12);

        tree.remove(13);

        tree.add(50);
        tree.add(25);
        tree.add(75);
        tree.add(100);
        tree.add(55);
        tree.add(70);
        tree.add(65);
        tree.add(71);
        System.out.println(tree);
        tree.remove(50);
        System.out.println(tree);
        tree.remove(25);
        System.out.println(tree);
        tree.remove(100);
        System.out.println(tree);
        tree.add(73);
    }
}
