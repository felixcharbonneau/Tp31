package TP3;

import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;

import java.io.*;
import java.util.Arrays;
import java.util.Queue;

/**
 * Classes utilitaires
 */
public class Util {
    /**
     * Encrypter un ficher
     * @param file ficher a encrypter
     */
    public static void encryptFile(File file, ProgressBar progressBar, Text status) throws IOException {
        progressBar.setProgress(0.0);
        progressBar.setVisible(true);
        file.createNewFile();
        int[] occurences = new int[256];
        Arrays.fill(occurences, 0);
        try {
            status.setText("Recensement");
            long size = file.length();
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            byte byteread;
            while ((byteread = (byte) bufferedInputStream.read()) != -1){
                if (byteread > 0) {
                    occurences[byteread]++;
                }else {
                    occurences[(short)byteread + 256]++;
                }
                incrementProgress(progressBar,0.1/size);
            }
            //Écriture de la clé

            String keyName = getFileNameWithoutExtension(file) + ".hk";
            File key = new File(file.getParentFile(), keyName);
            FileWriter writer = new FileWriter(key);
            writer.write("HK");
            long max = occurences[0];
            for (short i = 0; i < occurences.length;i++) {
                    if (occurences[i]>max) {
                        max = occurences[i];
                    }
            }
            if (max<Byte.MAX_VALUE){
                writer.write(1);
            } else if (max < Short.MAX_VALUE) {
                writer.write(2);
            } else if (max < Integer.MAX_VALUE) {
                writer.write(4);
            }else {
                writer.write(8);
            }
            status.setText("Écriture de la clé");
            for (short i = 0; i< occurences.length;i++) {
                writer.write(occurences[i]);
                incrementProgress(progressBar,0.1/256);
            }
            writer.close();
            PriorityQueue<HuffmanNode> queue = new PriorityQueue<HuffmanNode>();
            for (short i = 0; i < occurences.length;i++) {
                if (occurences[i] > 0){
                    queue.push(new HuffmanNode((byte)i, null, null), occurences[i]);
                }
            }
            while (queue.size() > 1) {
                HuffmanNode node1 = queue.front();
                long priority1 = queue.frontPriority();
                queue.pop();
                HuffmanNode node2 = queue.front();
                long priority2 = queue.frontPriority();
                queue.pop();
                queue.push(new HuffmanNode(null, node1, node2), priority1 + priority2);
            }
            //Écriture du fichier encodé
            String[] codes = new String[256];
            System.out.println(queue.front());
            setCodes(codes, queue.front());
            String encryptedFileData = getFileNameWithoutExtension(file) + ".hd";
            File encryptedFile = new File(file.getParentFile(), encryptedFileData);
            writer.close();
            FileWriter writer1 = new FileWriter(encryptedFile);
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            StringBuilder result = new StringBuilder();
            while ((byteread = (byte) bufferedInputStream.read()) != -1) {
                if (byteread > 0) {
                    result.append(codes[byteread]);
                }else {
                    result.append(codes[(short)byteread + 256]);
                }
            }
            byte[] bytes = stringToByteArray(String.valueOf(result));

            writer1.write("HD");

            for (byte b : bytes){
                writer1.write(b);
            }

            writer1.close();
            fileInputStream.close();
            bufferedInputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Set les codes pour les caracteres dans une liste
     * @param codes liste pour les codes
     * @param node Arbre de huffman
     * @param code code pour le caractere
     */
    private static void setCodes(String[] codes, HuffmanNode node , String code) {
        if (node != null) {
            if (node.right == null && node.left == null) {
                codes[node.data > 0 ? node.data : node.data + 256] = code;
            }
            if (node.right != null) {
                setCodes(codes, node.right, code + "1");
            }
            if (node.left != null) {
                setCodes(codes, node.left, code + "0");
            }
        }
    }
    /**
     * Set les codes pour les caracteres dans une liste
     * @param codes liste pour les codes
     * @param node Arbre de huffman
     */
    private static void setCodes(String[] codes, HuffmanNode node){
        String code = "";
        if (node != null) {
            if (node.right == null && node.left == null) {
                codes[node.data > 0 ? node.data : node.data + 256] = code;
            }
            if (node.right != null) {
                setCodes(codes, node.right, code + "1");
            }
            if (node.left != null) {
                setCodes(codes, node.left, code + "0");
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
            bytes[i] = (byte) Integer.parseInt(byteStr, 2); // Convert binary string to byte
        }
        return bytes;
    }
    /**
     * Decodage d'un fichier
     *
     * @param file        fichier de donnees
     * @param progressBar
     * @param text
     * @throws IOException
     */
    public static void decryptFile(File file, ProgressBar progressBar, Text text) throws IOException {
        String keyName = getFileNameWithoutExtension(file) + ".hk";
        File key = new File(file.getParent(),keyName);
        FileInputStream fileInputStream = new FileInputStream(key);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        String magicNumber = "";
        magicNumber+=(char)bufferedInputStream.read();
        magicNumber+=(char)bufferedInputStream.read();
        if (!magicNumber.equals("HK")) {
            System.out.println("Format de fichier invalide");
        }else {
            Byte[] fichier = new Byte[256];
            for(short i = 0; i<256; i++){
                fichier[i] = (byte) bufferedInputStream.read();
            }
            PriorityQueue<HuffmanNode> data = new PriorityQueue<HuffmanNode>();
            for(int i = 0; i<256; i++){
                data.push(new HuffmanNode((byte) i,null,null), data[i]);
            }
        }
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
    public static void incrementProgress(ProgressBar bar, double progress) {
        progress = progress >= 1.0 ? 1.0 : bar.getProgress()+progress;
        bar.setProgress(progress);
    }



}

