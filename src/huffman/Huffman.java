/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        ht.head = ht.buildHuffmanTrie();
        ht.writeTrieToFile("trie.txt");
        ht.readTrieFromFile("trie.txt");
        Node node = ht.head;
        printNode(node);
        /*HuffmanTrie.printMap(ht.values);
        
        System.out.println("\n\n\n");
        HuffmanTrie.printMap(sortByValues(ht.values));*/
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
    
    private static HashMap sortByValues(HashMap map) { 
       List list = new LinkedList(map.entrySet());
       // Defined Custom Comparator here
       Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
               return (int)((Map.Entry)o1).getValue() - (int)((Map.Entry)o2).getValue();
            }
       });

       // Here I am copying the sorted list in HashMap
       // using LinkedHashMap to preserve the insertion order
       HashMap sortedHashMap = new LinkedHashMap();
       for (Iterator it = list.iterator(); it.hasNext();) {
              Map.Entry entry = (Map.Entry) it.next();
              sortedHashMap.put(entry.getKey(), entry.getValue());
       } 
       return sortedHashMap;
  }
    
}
