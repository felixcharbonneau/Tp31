package TP3;

/**
 * Noeud de Huffman
 */
public class HuffmanNode {
    public Byte data;
    public HuffmanNode left;
    public HuffmanNode right;

    /**
     * Constructeur
     * @param data donnée
     * @param left Nœud à gauche
     * @param right Nœud à droite
     */
    public HuffmanNode(Byte data, HuffmanNode left, HuffmanNode right){
        this.data = data;
        this.left = left;
        this.right = right;
    }
}
