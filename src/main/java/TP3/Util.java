package TP3;

import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Classes utilitaires
 */
public class Util {
    /**
     * Encodage d'un fichier
     * @param file fichier a encoder
     * @param progressBar barre de progres
     * @param status status de l'encodage
     * @throws IOException
     */
    public static void encodeFile(File file, ProgressBar progressBar, Text status) throws IOException {
        progressBar.setProgress(0.0);
        //Recensement
        status.setText("Recensement");

        long[] occurences = new long[256];
        Arrays.fill(occurences, 0);

        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        byte byteread;
        while ((byteread = (byte) bufferedInputStream.read()) != -1){
            if (byteread >= 0) {
                occurences[byteread]++;
            }else {
                occurences[(short)byteread + 256]++;
            }
        }
        //Écriture de la clé
        status.setText("Écriture de la clé");
        File keyFile = new File(file.getParent(), getFileNameWithoutExtension(file) + ".hk");
        keyFile.createNewFile();
        OutputStream writer1 = new FileOutputStream(keyFile);
        writer1.write("HK".getBytes());
        long max = occurences[0];
        for (short i = 1; i < occurences.length; i++) {
            if (occurences[i]>max) {
                max = occurences[i];
            }
        }
        if (max<Byte.MAX_VALUE){
            writer1.write(1);
            for (short i = 0; i < 256;i++) {
                writer1.write((byte) occurences[i]);
                incrementProgress(progressBar, 0.1/256);
            }
        } else if (max < Short.MAX_VALUE) {
            writer1.write(2);
            for (short i = 0; i < 256;i++) {
                ByteBuffer buffer = ByteBuffer.allocate(2);
                buffer.putShort((short) occurences[i]);
                writer1.write(buffer.array());
                incrementProgress(progressBar, 0.1/256);
            }
        } else if (max < Integer.MAX_VALUE) {
            writer1.write(4);
            for (short i = 0; i < 256;i++) {
                ByteBuffer buffer = ByteBuffer.allocate(4);
                buffer.putInt((int) occurences[i]);
                writer1.write(buffer.array());
                incrementProgress(progressBar, 0.1/256);
            }
        }else {
            writer1.write(8);
            for (int i = 0; i<256;i++) {
                ByteBuffer buffer = ByteBuffer.allocate(8);
                buffer.putLong(occurences[i]);
                writer1.write(buffer.array());
                incrementProgress(progressBar, 0.1 / 256);
            }
        }
        writer1.flush();
        writer1.close();
        //Construction de l'arbre
        status.setText("Construction de l'arbre");
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<HuffmanNode>();
        for (short i = 0; i < occurences.length;i++) {
            if (occurences[i] > 0){
                queue.push(new HuffmanNode((byte)i, null, null), occurences[i]);
            }
        }
        buildHuffmanTree(queue, progressBar);
        //Encodage
        status.setText("Encodage");
        String[] codes = new String[256];
        setCodes(codes, queue.front(),progressBar);
        //Chiffrement
        status.setText("Chiffrement");
        File dataFile = new File(file.getParent(),getFileNameWithoutExtension(file) + ".hd");
        dataFile.createNewFile();
        FileInputStream fileInputStream1 = new FileInputStream(file);
        BufferedInputStream bufferedInputStream1 = new BufferedInputStream(fileInputStream1);
        StringBuilder result = new StringBuilder();
        while ((byteread = (byte) bufferedInputStream1.read()) != -1) {
            if (byteread >= 0) {
                result.append(codes[byteread]);
            }else {
                result.append(codes[(short)byteread + 256]);
            }
            incrementProgress(progressBar,0.1/256);
        }
        byte[] bytes = stringToByteArray(String.valueOf(result));
        System.out.println(result);
        //Écriture du fichier chiffré
        FileOutputStream writer2 = new FileOutputStream(dataFile);
        status.setText("Écriture du fichier chiffré");
        writer2.write("HD".getBytes());
        for(byte b : bytes) {
            writer2.write(b);
            incrementProgress(progressBar, 0.2/bytes.length);
        }
        status.setText("Encodage completé");
        writer2.flush();
        writer2.close();
    }

    /**
     * Decodage d'un fichier
     * @param file fichier de donnees
     * @param progressBar barre de progres
     * @param status status du décodage
     * @throws IOException
     */
    public static void decodeFile(File file, ProgressBar progressBar, Text status) throws IOException {
        progressBar.setProgress(0.0);

        File dataFile = new File(file.getParent(),getFileNameWithoutExtension(file) + ".hd");
        FileInputStream keyFileInputStream = new FileInputStream(file);
        DataInputStream keyDataInputStream = new DataInputStream(keyFileInputStream);
        String magicNumber = "";
        magicNumber+=(char)keyDataInputStream.read();
        magicNumber+=(char)keyDataInputStream.read();
        if (!magicNumber.equals("HK")) {
            System.out.println("Format de fichier invalide");
            return;
        }

        long[] occurences = new long[256];
        status.setText("Lecture de la clé");
        byte dataSize = (byte) keyDataInputStream.read();
        if (dataSize == 1) {
            for (short i = 0; i < 256; i++) {
                occurences[i] = keyDataInputStream.readByte();
                incrementProgress(progressBar, 0.1 / 256);
            }
        } else if (dataSize == 2) {
            for (short i = 0; i < 256; i++) {
                occurences[i] = keyDataInputStream.readShort();
                incrementProgress(progressBar, 0.1 / 256);
            }
        } else if (dataSize == 4) {
            for (short i = 0; i < 256; i++) {
                occurences[i] = keyDataInputStream.readInt();
                incrementProgress(progressBar, 0.1 / 256);
            }
        }else {
            for (short i = 0; i < 256; i++) {
                occurences[i] = keyDataInputStream.readLong();
                incrementProgress(progressBar, 0.1 / 256);
            }
        }
        //Construction de l'arbre
        status.setText("Construction de l'arbre");
        PriorityQueue<HuffmanNode> dataQueue = new PriorityQueue<HuffmanNode>();
        for(int i = 0; i<256; i++){
            if (occurences[i] != 0) {
                dataQueue.push(new HuffmanNode((byte)i,null,null),occurences[i]);
                incrementProgress(progressBar,0.1/256);
            }
        }
        buildHuffmanTree(dataQueue, progressBar);
        //Decodage
        status.setText("Décodage");
        String[] codes = new String[256];
        setCodes(codes, dataQueue.front(),progressBar);
        keyFileInputStream.close();
        keyDataInputStream.close();
        //Dechiffrement
        FileInputStream dataFileInputStream = new FileInputStream(dataFile);
        BufferedInputStream dataBufferedInputStream = new BufferedInputStream(dataFileInputStream);
        status.setText("Déchiffrement");
        magicNumber = "";
        magicNumber+=(char)dataBufferedInputStream.read();
        magicNumber+=(char)dataBufferedInputStream.read();
        if (!magicNumber.equals("HD")) {
            System.out.println("Fichier de données HD invalide: " + dataFile.getAbsolutePath());
            return;
        }
        StringBuilder data = new StringBuilder();
        byte byteRead;
        for (int i = 2; i < dataFile.length(); i++) {
            byteRead = (byte) dataBufferedInputStream.read();
            int unsignedByte = byteRead & 0xFF;
            String binaryString = String.format("%8s", Integer.toBinaryString(unsignedByte)).replace(' ', '0');
            data.append(binaryString);
        }
        dataBufferedInputStream.close();
        char[] directions = data.toString().toCharArray();
        HuffmanNode root = dataQueue.front();
        //Écriture du fichier
        File fileToWrite = new File(file.getParent(),getFileNameWithoutExtension(file) + ".dec");
        FileOutputStream fileOutputStream = new FileOutputStream(fileToWrite);
        BufferedOutputStream writer = new BufferedOutputStream(fileOutputStream);
        for (int i = 0; i < directions.length; i++) {
            if (directions[i] == '1') {
                root = root.right;
            }else if (directions[i] == '0') {
                root = root.left;
            }
            if((directions[i] == '0' && root.left == null) || (directions[i] == '1' && root.right == null)) {
                writer.write(root.data);
                root = dataQueue.front();
            }
            if (fileToWrite.length() >= dataQueue.frontPriority()) {
                i = directions.length;
            }
        }
        writer.flush();
        writer.close();

    }

    /**
     * Set les codes pour les caracteres dans une liste
     * @param codes liste pour les codes
     * @param node Arbre de huffman
     * @param code code pour le caractere
     * @param progressBar bare de progres
     */
    private static void setCodes(String[] codes, HuffmanNode node , String code, ProgressBar progressBar) {
        if (node != null) {
            if (node.right == null && node.left == null) {
                codes[node.data >= 0 ? node.data : (short)node.data + 256] = code;
                incrementProgress(progressBar, 0.1/256);
            }
            if (node.right != null) {
                setCodes(codes, node.right, code + "1", progressBar);
            }
            if (node.left != null) {
                setCodes(codes, node.left, code + "0",progressBar);
            }
        }
    }
    /**
     * Set les codes pour les caracteres dans une liste
     * @param codes liste pour les codes
     * @param node Arbre de huffman
     * @param progressBar bare de progres
     */
    private static void setCodes(String[] codes, HuffmanNode node,ProgressBar progressBar){
        String code = "";
        if (node != null) {
            if (node.right == null && node.left == null) {
                codes[node.data >= 0 ? node.data : (short)node.data + 256] = code;
                incrementProgress(progressBar, 0.1/256);
            }
            if (node.right != null) {
                setCodes(codes, node.right, code + "1",progressBar);
            }
            if (node.left != null) {
                setCodes(codes, node.left, code + "0",progressBar);
            }
        }
    }
    /**
     * Transformer une chaine en tableau d'octets
     * @param string chaine a transformer
     * @return le tableau d'octets
     */
    private static byte[] stringToByteArray(String string) {
        int size;
        if (string.length() % 8 != 0) {
            string = string + "0".repeat(8 - (string.length() % 8));
        }
        size = string.length()/8;
        byte[] bytes = new byte[size];
        for (int i = 0; i < bytes.length; i++) {
            String byteStr = string.substring(i * 8, (i + 1) * 8);
            bytes[i] = (byte) Integer.parseInt(byteStr, 2);
        }
        return bytes;
    }
    /**
     * Trouver le nom d'un fichier sans son extension
     * @param file fichier
     * @return le nom du ficher sans extension
     */
    private static String getFileNameWithoutExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }
    /**
     * incrémenter le progres de la barre de chargement
     * @param bar barre de chargement
     * @param progress valeur de l'incrément
     */
    private static void incrementProgress(ProgressBar bar, double progress) {
        progress = progress >= 1.0 ? 1.0 : bar.getProgress()+progress;
        bar.setProgress(progress);
    }
    /**
     * Construction de l'arbre de huffman
     * @param queue File de nodes de huffman
     * @param progressBar bare de progres
     */
    private static void buildHuffmanTree(PriorityQueue<HuffmanNode> queue, ProgressBar progressBar) {
        long size = queue.size();
        while (queue.size() > 1) {
            HuffmanNode node1 = queue.front();
            long priority1 = queue.frontPriority();
            queue.pop();
            HuffmanNode node2 = queue.front();
            long priority2 = queue.frontPriority();
            queue.pop();
            queue.push(new HuffmanNode(null, node1, node2), priority1 + priority2);
            incrementProgress(progressBar,0.2/size);
        }
    }
}