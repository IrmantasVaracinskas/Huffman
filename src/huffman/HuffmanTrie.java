/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
public class HuffmanTrie {
    HashMap<Integer, Integer> values;
    
    long readBitCount = 0;
    private BitWriter writer;
    private BitReader reader;
    
    // first 6 bit positions are for length in which text will be devided into
    public byte byteLength;
    public Node head;
    public String filename;
    
    HuffmanTrie(byte byteLength)
    {
        this.byteLength = byteLength;
        
        values = new HashMap<Integer, Integer>();
    }
    
    public Node buildHuffmanTrie()
    {
        ArrayList<Node> nodes = new ArrayList<Node>();
        values = sortByValues();
        Iterator it = values.entrySet().iterator();
        
        // put all values into ArrayList because
        // it is easier for me to work with
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            nodes.add(new Node((short)(int)pair.getKey(), (int)pair.getValue(), null, null));
        }
        /*nodes.sort(new Comparator()
        {
             public int compare(Object o1, Object o2) {
                return (int)((Node)o1).frequency - (int)((Node)o2).frequency;
             }
        });*/
        
        /*nodes.forEach((node) -> System.out.println("value = " + node.bytes
                + "  frequency = " + node.frequency));*/
        
        
        Node temp1, temp2;
        while(nodes.size() != 1)
        {
            nodes.sort(new Comparator()
                {
                     public int compare(Object o1, Object o2) {
                        return (int)((Node)o1).frequency - (int)((Node)o2).frequency;
                     }
                });
            
            temp1 = nodes.get(0);
            temp2 = nodes.get(1);
            nodes.set(0, new Node((short)-1,
                    temp1.frequency + temp2.frequency,
                    temp1, temp2));
            nodes.remove(1);
            
        }
        return nodes.get(0);
    }
    
    void getWordFrequencies(String filename) throws FileNotFoundException
    {
        reader = new BitReader(filename);
        int temp;
        while(!eof(reader))
        {
            temp = reader.readBits(byteLength);
            readBitCount += byteLength;
            if(values.containsKey(temp))
            {
                values.replace(temp, values.get(temp) + 1);
            }
            else
            {
                if(readBitCount != reader.length() * 8 + byteLength)
                    values.put(temp, 1);
            }
        }
    }
    
    void writeTrieToFile(String filename)
    {
        writer = new BitWriter(filename);
        writer.writeBits(byteLength, 6);
        head.writeToFile(writer, byteLength);
        
        writer.flush();
    }
    
    void readTrieFromFile(String filename) throws FileNotFoundException
    {
        reader = new BitReader(filename);
        byteLength = (byte)reader.readBits(6);
        head = readNode(reader);
    }
    
    private Node readNode(BitReader reader)
    {
        if(!eof(reader))
        {
            readBitCount++;
            if(reader.readBit() == 1)
            {
                readBitCount += byteLength;
                return new Node((short)reader.readBits(byteLength), 0, null, null);
            }
            else
            {
                Node left = readNode(reader);
                Node right = readNode(reader);
                return new Node((byte)0, 0, left, right);
            }
        }
        else
        {
            return head;
        }
    }
    
    private boolean eof(BitReader reader)
    {
        return reader.readBitsCount > reader.length() * 8;
    }
    
    static public void printMap(Map map)
    {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }
    
    
    private HashMap sortByValues() { 
        HashMap map = values;
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
