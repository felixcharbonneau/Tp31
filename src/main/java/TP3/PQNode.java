package TP3;

/**
 * Noeud de file de prioritée
 * @param <T> type de données
 */
public class PQNode<T> {
    public T data;
    public long priority;
    public PQNode<T> right;

    /**
     * Constructeur
     * @param data Donnée
     * @param priority Prioritée
     * @param right Noeud a droite
     */
    public PQNode(T data, long priority, PQNode<T> right) {
        this.data = data;
        this.priority = priority;
        this.right = right;
    }
}
