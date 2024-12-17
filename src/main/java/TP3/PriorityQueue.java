package TP3;

import TP3.PQNode.*;

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
        if (count > 0) {
            PQNode<T> tmpNode = first;
            while (tmpNode.right != null && tmpNode.right.priority < priority) {
                tmpNode = tmpNode.right;
            }
            tmpNode.right = new PQNode<T>(data, priority, tmpNode.right);
        }else {
            first = new PQNode<T>(data,priority,null);
        }
        count++;
    }
    /**
     * Defiler
     */
    public void pop() {
        if (count > 0) {
            first = first.right;
            count--;
        }
    }
    /**
     * La donnée au devant de la pile
     * @return le devant de la pile
     */
    public T front() {
        if (count != 0) {
            return first.data;
        } else {
            return null;
        }
    }
    /**
     * Priorité du devant
     * @return la priorité du devant
     */
    public long frontPriority() {
        return first.priority;
    }
    /**
     * Taille de la pile
     * @return le nombre de données dans la pile
     */
    public long size() {
        return count;
    }
}