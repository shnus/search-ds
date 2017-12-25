package ru.mail.polis;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class AVLTree<E extends Comparable<E>> extends AbstractSet<E> implements BalancedSortedSet<E> {

    private final Comparator<E> comparator;

    private AVLNode root;
    private int size;
    private boolean isNotFinded;

    class AVLNode {

        E value;
        AVLNode left;
        AVLNode right;
        AVLNode(E value) {
            this.value = value;
            this.height = 1;
            this.right = null;
            this.left = null;
        }
        int height;

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

    int bfactor(AVLNode p)
    {
        return ((p.right != null)?p.right.height:0) - ((p.left != null)?p.left.height:0);
    }

    void fixheight(AVLNode p)
    {
        int hl = (p.left != null)?p.left.height:0;
        int hr = (p.right != null)?p.right.height:0;
        p.height = (hl>hr?hl:hr)+1;
    }

    AVLNode rotateright(AVLNode p) // правый поворот вокруг p
    {
        AVLNode q = p.left;
        p.left = q.right;
        q.right = p;
        fixheight(p);
        fixheight(q);
        return q;
    }

    AVLNode rotateleft(AVLNode q) // левый поворот вокруг q
    {
        AVLNode p = q.right;
        q.right = p.left;
        p.left = q;
        fixheight(q);
        fixheight(p);
        return p;
    }

    AVLNode balance(AVLNode p) // балансировка узла p
    {
        fixheight(p);
        if( bfactor(p)==2 )
        {
            if( bfactor(p.right) < 0 )
                p.right = rotateright(p.right);
            return rotateleft(p);
        }
        if( bfactor(p)==-2 )
        {
            if( bfactor(p.left) > 0  )
                p.left = rotateleft(p.left);
            return rotateright(p);
        }
        return p; // балансировка не нужна
    }

    public AVLTree() {
        this(null);
        isNotFinded = false;
    }

    public AVLTree(Comparator<E> comparator) {
        this.comparator = comparator;
        isNotFinded = false;
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
            root = new AVLNode(value);
            size++;
        } else {
            root = insert(root, value);
        }
        boolean ret = isNotFinded;
        isNotFinded = false;
        return !ret;
    }

    public AVLNode insert(AVLNode p, E value)
    {
        int cmp = compare(p.value, value);
        if (cmp == 0) {
            isNotFinded = true;
        } else if (cmp < 0) {
            if (p.right != null) {
                p.right = insert(p.right, value);
            } else {
                size++;
                p.right = new AVLNode(value);
            }
        } else /*if (cmp > 0)*/ {
            if (p.left != null) {
                p.left = insert(p.left, value);
            } else {
                size++;
                p.left = new AVLNode(value);
            }
        }
        return balance(p);
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
        //System.out.println(root.toString());
        @SuppressWarnings("unchecked")
        E value = (E) object;
        //System.out.println(value);
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (root == null)
            return false;
        root = remove(root, value);
        boolean ret = isNotFinded;
        isNotFinded = false;
        //System.out.println(root.toString());
        return !ret;
    }

    AVLNode findmin(AVLNode p) // поиск узла с минимальным ключом в дереве p
    {
        if (p.left == null)
            return p;
        return p.left;
    }

    AVLNode removemin(AVLNode p) // удаление узла с минимальным ключом из дерева p
    {
        if( p.left == null )
            return p.right;
        p.left = removemin(p.left);
        return balance(p);
    }

    AVLNode remove(AVLNode p, E value) // удаление ключа k из дерева p
    {
        int cmp = compare(p.value, value);
        if (cmp > 0) {
            if (p.left == null) {
                isNotFinded = true;
                return p;
            }
            p.left = remove(p.left, value);
        }
        else if (cmp < 0) {
            if (p.right == null) {
                isNotFinded = true;
                return p;
            }
            p.right = remove(p.right, value);
        }
        else //  k == p->key
        {
            size--;
            if (p.left == null && p.right == null)
                return null;
            AVLNode q = p.left;
            if (p.right == null)
                return q;
            AVLNode r = p.right;
            AVLNode min = findmin(r);
            min.right = removemin(r);
            min.left = q;
            //System.out.println(min.toString());
            return balance(min);
        }
        return balance(p);
    }

        /**
         * Ищет элемент с таким же значением в дереве.
         * Инвариант: на вход всегда приходит NotNull объект, который имеет корректный тип
         *
         * @param object элемент который необходимо поискать
         * @return true, если такой элемент содержится в дереве
         */
    @Override
    public boolean contains(Object value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        @SuppressWarnings("unchecked")
        E key = (E) value;
        if (root != null) {
            AVLNode curr = root;
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
    public E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("set is empty, no first element");
        }
        AVLNode curr = root;
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
            throw new NoSuchElementException("set is empty, no last element");
        }
        AVLNode curr = root;
        while (curr.right != null) {
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

    private int traverseTreeAndCheckBalanced(AVLNode curr) throws NotBalancedTreeException {
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
