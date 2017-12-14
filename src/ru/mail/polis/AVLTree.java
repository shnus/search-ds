package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class AVLTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;

    private Node root; //todo: Создайте новый класс если нужно. Добавьте новые поля, если нужно.
    private int size;
    //todo: добавьте дополнительные переменные и/или методы если нужно

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
        //todo: следует реализовать
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null) {
            root = new Node(value);
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
                        curr.right = new Node(value);
                        break;
                    }
                } else /*if (cmp > 0)*/ {
                    if (curr.left != null) {
                        curr = curr.left;
                    } else {
                        curr.left = new Node(value);
                        break;
                    }
                }
            }
        }
        size++;
        checkBalanced();
        return true;

    }

    /**
     * Удаляет элемент с таким же значением из дерева.
     * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
     *
     * @param value элемент который необходимо удалить
     * @return true, если элемент удалился из дерева
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
        checkBalanced();
        return true;
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

    private void reLink(Node parent, Node curr, Node child) {
        if (parent == curr) {
            root = child;
        } else if (parent.left == curr) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        curr.value = null;
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
        return "AVLTree{" +
                "tree = " + root +
                " size = " + size + '}';
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
     * Возвращает 0, если дерево пустое либо всё хорошо, иначе - возвращает разницу:
     * если значение отрицательное - значит, перевес слева
     * иначе справа
     *
     * @throws NotBalancedTreeException если высоты отличаются более чем на один
     */
    @Override
    public void checkBalanced() {
        reHeight(root);
        traverseTreeAndCheckBalanced(null, root, 0);
    }

    private void traverseTreeAndCheckBalanced(Node parent, Node curr, int a){
        if (curr == null) {
            return;
        }

        if (curr.left!=null&&curr.right!=null)
        if ((curr.right.height - curr.left.height > 1) && (curr.right.leftHeight <= curr.right.rightHeight)){
            Node temp = turnRightSmall(curr);
            if(a==1)
                parent.right=temp;
            if(a==-1)
                parent.left=temp;
            if(a==0)
                root=temp;
            reHeight(root);
        } else if((curr.left.height - curr.right.height > 1) && (curr.left.rightHeight <= curr.left.leftHeight)){
            Node temp = turnLeftSmall(curr);
            if(a==1)
                parent.right=temp;
            if(a==-1)
                parent.left=temp;
            if(a==0)
                root=temp;
            reHeight(root);
        } else if((curr.right.height - curr.left.height > 1) && (curr.right.leftHeight > curr.right.rightHeight)) {
            Node temp = turnLeftBig(curr);
            if(a==1)
                parent.right=temp;
            if(a==-1)
                parent.left=temp;
            if(a==0)
                root=temp;
            reHeight(root);
        } else if((curr.left.height - curr.right.height > 1) && (curr.left.rightHeight <= curr.left.leftHeight)) {
            Node temp = turnRightBig(curr);
            if(a==1)
                parent.right=temp;
            if(a==-1)
                parent.left=temp;
            if(a==0)
                root=temp;
            reHeight(root);
        }

        traverseTreeAndCheckBalanced(curr, curr.left, -1);
        traverseTreeAndCheckBalanced(curr, curr.right, 1);
        return;
    }



    private int reHeight(Node curr) {
        if (curr == null) {
            return 0;
        }
        curr.leftHeight = reHeight(curr.left);
        curr.rightHeight = reHeight(curr.right);
        curr.height = Math.max(curr.leftHeight, curr.rightHeight)+1;
        return curr.height;
    }

    class Node {
        E value;
        Node left=null;
        Node right=null;
        int height=-1;
        int leftHeight=0;
        int rightHeight=0;

        Node(E value) {
            this.value = value;
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

    //на вход подается верхняя нода
    public Node turnRightSmall(Node node){
        Node b = node.left;
        Node c = node.left.right;
        Node l = node.left.left;

        node.left=l;
        node.right=node;
        node.value = b.value;
        node.right.left = c;
        return node;
    }

    public Node turnLeftSmall(Node node){
        Node b = node.right;
        Node r = node.right.right;
        Node c = node.right.left;

        node.left=node;
        node.left.right=c;
        node.right=r;
        node.value=b.value;
        return node;
    }

    public Node turnLeftBig(Node node){
        Node c = node.right.left;
        Node a = node;
        Node b = node.right;
        Node M = c.left;
        Node N = c.right;

        c.left=a;
        c.left.right=M;
        c.right=b;
        c.right.left=N;
        node=c;
        return node;
    }

    public Node turnRightBig(Node node){
        Node c = node.left.right;
        Node a = node;
        Node b = node.left;
        Node M = c.left;
        Node N = c.right;

        c.right=node;
        c.right.left=N;
        c.left=b;
        c.left.right=M;
        node=c;
        return node;
    }

    public static void main(String[] args) {
        AVLTree<Integer> tree = new AVLTree<>();
        tree.add(10);
        tree.add(5);
        tree.add(15);
        System.out.println(tree);
        tree.remove(10);
        tree.remove(15);
        System.out.println(tree);
        tree.remove(5);
        System.out.println(tree);
        tree.add(7);
        tree.add(2);
        tree.add(50);
        tree.add(48);
        tree.add(49);
        tree.add(46);
        tree.add(47);
        tree.add(45);
        tree.add(70);
        tree.add(68);
        tree.add(67);
        tree.add(69);
        tree.add(72);
        tree.add(71);
        tree.add(73);
        System.out.println(tree);
        //tree.turnLeftSmall();
        System.out.println(tree.root.toString());
    }

}
