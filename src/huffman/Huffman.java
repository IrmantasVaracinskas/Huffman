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
        HuffmanTrie ht = new HuffmanTrie((byte)8);
        ht.getWordFrequencies("test2.txt");
        
        HuffmanTrie.printMap(ht.values);
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
