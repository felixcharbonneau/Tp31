package TP3;

/**
 * File de prioritée
 * @param <T> type de donnée
 */
public class PriorityQueue<T>{
    private PQNode<T> first;///<
    private long count;///<
    /**
     * Constructeur
     */
    public PriorityQueue(){
        first = null;
        count = 0;
    }
    /**
     * Empiler
     * @param data donnée à empiler
     * @param priority prioritée de la donnée
     */
    public void push(T data, long priority) {
        if (first == null || first.priority >= priority) {
            first = new PQNode<>(data, priority, first);
        } else {
            PQNode<T> tmpNode = first;
            while (tmpNode.right != null && tmpNode.right.priority <= priority) {
                tmpNode = tmpNode.right;
            }
            tmpNode.right = new PQNode<T>(data, priority, tmpNode.right);
        }
        count++;
    }
    /**
     * Defiler
     */
    public void pop() {
        if (count > 0) {
            count--;
            first = count > 0 ? first.right : null;
        }
    }
    /**
     * La donnée au devant de la pile
     * @return le devant de la pile
     */
    public T front() {
        if (count > 0) {
            return first.data;
        } else {
            return null;
        }
    }
    /**
     * Priorité du devant
     * @return la priorité du devant, -1 s'il n'a pas de devant
     */
    public long frontPriority() {
        return first != null ? first.priority : -1;
    }
    /**
     * Taille de la pile
     * @return le nombre de données dans la pile
     */
    public long size() {
        return count;
    }
}