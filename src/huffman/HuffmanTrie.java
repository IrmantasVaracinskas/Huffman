/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Irmis
 */
public class HuffmanTrie {
    static long totalBytesInFile = 0;
    HashMap<Integer, Integer> values;
    
    long readBitCount = 0;
    private BitWriter globalWriter;
    private BitReader globalReader;
    
    // first 6 bit positions are for length in which text will be devided into
    public byte byteLength;
    public Node head;
    public String filename;
    
    HuffmanTrie(String filename, byte byteLength)
    {
        this.filename = filename;
        this.byteLength = byteLength;
        //globalWriter = new BitWriter(filename);
        try {
            globalReader = new BitReader(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HuffmanTrie.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        totalBytesInFile = globalReader.length();
        
        values = new HashMap<Integer, Integer>();
    }
    
    void getWordFrequencies()
    {
        int temp;
        while(!eof())
        {
            temp = globalReader.readBits(byteLength);
            readBitCount += byteLength;
            if(values.containsKey(temp))
            {
                values.replace(temp, values.get(temp) + 1);
            }
            else
            {
                if(readBitCount != totalBytesInFile * 8 + byteLength)
                    values.put(temp, 1);
            }
        }
    }
    
    void writeTrieToFile()
    {
        globalWriter.writeBits(byteLength, 6);
        head.writeToFile(globalWriter, byteLength);
        
        globalWriter.flush();
    }
    
    void readTrieFromFile() throws FileNotFoundException
    {
        readBitCount += 6;
        byteLength = (byte)globalReader.readBits(6);
        head = readNode(globalReader);
    }
    
    private Node readNode(BitReader reader)
    {
        if(!eof())
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
    
    private boolean eof()
    {
        return readBitCount > totalBytesInFile * 8;
    }
    
    static public void printMap(Map map)
    {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }
}
