package TP3;

public class HuffmanNode {
    public Byte data;
    public HuffmanNode left;
    public HuffmanNode right;

    /**
     * Constructeur
     * @param data donnée
     * @param left Noeud à gauche
     * @param right Noeud à droite
     */
    public HuffmanNode(Byte data, HuffmanNode left, HuffmanNode right){
        this.data = data;
        this.left = left;
        this.right = right;
    }
}
