/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Irmis
 */
public class Huffman {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        HuffmanTrie ht = new HuffmanTrie((byte)16);
        ht.getWordFrequencies("test.txt");
        ht.head = ht.buildHuffmanTrie();
        
        HuffmanTrie.prepareToEncode(ht.head);
        
        
        /*HuffmanTrie.printMap(ht.values);
        System.out.println("\n\n\n");*/
        
        Collections.sort(HuffmanTrie.valuesToEncodeBy, new Comparator<Encoding>(){
            public int compare(Encoding e1, Encoding e2)
            {
                return e2.value - e1.value;
            }
        });
        ht.encode("test.txt", "encoded2.txt");
        
        ht.decode("encoded2.txt", "decoded.txt");
        /*for(Encoding e : HuffmanTrie.valuesToEncodeBy)
        {
            System.out.println("value: " + e.value + " encode value: " + e.encodeValue + " length: " + e.encodeValueLength);
        }*/
        //HuffmanTrie.printMap(ht.values);
    }
    
    
    static void printNode(Node node)
    {
        if(node != null)
        {
            if(node.left == null && node.right == null)
                System.out.println("value: " + node.bytes);
            if(node.left != null)
                printNode(node.left);
            if(node.right != null)
                printNode(node.right);
        }
    }
}
