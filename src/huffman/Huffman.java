/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import java.io.FileNotFoundException;

/**
 *
 * @author Irmis
 */
public class Huffman {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        HuffmanTrie ht = new HuffmanTrie(8);
        ht.encode("test.jpg", "encoded.txt");
        //ht.decode("encoded.txt", "decoded.jpg");
    }
}
