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
    static final boolean DEBUG = true;
    static private int encodingValue = 0;
    static private int encodingValueLength = 0;
    static private int diff = 0;
    
    HashMap<Integer, Integer> values;
    
    long readBitCount = 0;
    private BitWriter writer;
    private BitReader reader;
    int lettersCount;
    
    // first 6 bit positions are for length in which text will be devided into
    public int byteLength;
    public Node head;
    public String filename;
    public static ArrayList<Encoding> valuesToEncodeBy = new ArrayList<Encoding>();
    
    public HuffmanTrie(int byteLength)
    {
        this.byteLength = byteLength;
        
        values = new HashMap<Integer, Integer>();
    }
    
    private Node buildHuffmanTrie()
    {
        ArrayList<Node> nodes = new ArrayList<Node>();
        values = sortByValues();
        Iterator it = values.entrySet().iterator();
        Node temp1, temp2;
        
        if(DEBUG)
            System.out.println("    Huffman trie building started");
        // put all values into ArrayList because
        // it is easier for me to work with
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            nodes.add(new Node((int)pair.getKey(), (int)pair.getValue(), null, null));
        }
        
        while(nodes.size() != 1)// do this until only one element is left tree
            // and that one element should be head
        {
            nodes.sort(new Comparator()
                {
                     public int compare(Object o1, Object o2) {
                        return (int)((Node)o1).frequency - (int)((Node)o2).frequency;
                     }
                });
            
            temp1 = nodes.get(0);
            temp2 = nodes.get(1);
            nodes.set(0, new Node(-1,
                    temp1.frequency + temp2.frequency,
                    temp1, temp2));
            nodes.remove(1);
            
        }
        if(DEBUG)
            System.out.println("    Huffman trie building finished");
        return nodes.get(0);
    }
    
    private void getWordFrequencies(String filename) throws FileNotFoundException
    {
        if(DEBUG)
            System.out.println("    Retreiving work frequency started");
        reader = new BitReader(filename);
        int temp;// used to read values into
        int t;// if file has 8 bits and you read in blocks of 7, then
        // result should be integer made of 7 bits and integer made of 1 bit
        // so t is used to implement that.
        while(!eof(reader))
        {
            t = (reader.length() * 8) - reader.readBitsCount;// get amount of 
            // unread bits
            if(t < byteLength && t >= 0)// if there are fewer unread bits than
                // size of blocks that usualy are read then read only all unread bits
            {
                temp = reader.readBits(t);
                reader.readBitsCount++;// increment so it would stop reading
            }
            else
                temp = reader.readBits(byteLength);
            readBitCount += byteLength;
            if(values.containsKey(temp))
            {
                values.replace(temp, values.get(temp) + 1);
            }
            else
            {
                //                                                      TODO:
                // this almost works but fails when file lenghth * 8 is not divisable by byteLength
                // (what I mean by "fails" is that values are incorrect, for example -1
                values.put(temp, 1);
            }
        }
        if(DEBUG)
            System.out.println("    Retreiving work frequency finished");
    }
    
    private void writeTrieToFile()
    {
        writer.writeBits(byteLength, 6);
        writer.writeBits(reader.length(), 30);
        head.writeToFile(writer, byteLength);
    }
    
    private void readTrieFromFile() throws FileNotFoundException
    {
        byteLength = (byte)reader.readBits(6);
        lettersCount = reader.readBits(30);
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
                return new Node(reader.readBits(byteLength), 0, null, null);
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
        return reader.readBitsCount >= reader.length() * 8;
    }
    
    
    public void encode(String encodeFrom, String encodeTo)
    {      
        long startTime = 0;
        Encoding tempEncoding;
        int temp;
        int t;// if file has 8 bits and you read in blocks of 7, then
        // result should be integer made of 7 bits and integer made of 1 bit
        // so t is used to implement that.
        
        try {
            if(DEBUG)
            {
                System.out.println("Preparing for encoding started");
                startTime = System.currentTimeMillis();
            }
            setupEncoding(encodeFrom);
            writer = new BitWriter(encodeTo);
            reader = new BitReader(encodeFrom);
            writeTrieToFile();
            if(DEBUG)
            {
                long endTime = System.currentTimeMillis();
                System.out.println("Preparing for encoding ended");
                System.out.println("Preparations finished in " + (endTime - startTime)
                 + "milliseconds");
                
            }
            if(DEBUG)
            {
                System.out.println("Encoding started");
                startTime = System.currentTimeMillis();
            }
            while(!eof(reader))
            {
                t = (reader.length() * 8) - reader.readBitsCount;// get amount of 
                // unread bits
                if(t < byteLength && t >= 0)// if there are fewer unread bits than
                    // size of blocks that usualy are read then read only all unread bits
                {
                    temp = reader.readBits(t);
                    tempEncoding = getValueToEncodeBy(temp);
                    writer.writeBits(prepareToPrint(tempEncoding.encodeValue, tempEncoding.encodeValueLength),
                            tempEncoding.encodeValueLength);
                    /*writer.writeBits(tempEncoding.encodeValue,
                            tempEncoding.encodeValueLength);*/
                    reader.readBitsCount++;// increment so it would stop reading
                }
                else
                {
                    temp = reader.readBits(byteLength);
                    tempEncoding = getValueToEncodeBy(temp);
                    writer.writeBits(prepareToPrint(tempEncoding.encodeValue, tempEncoding.encodeValueLength),
                            tempEncoding.encodeValueLength);
                    /*writer.writeBits(tempEncoding.encodeValue,
                            tempEncoding.encodeValueLength);*/
                }
                readBitCount += byteLength;
            }
            writer.flush();
        } catch (FileNotFoundException ex) {
            System.out.println("No file found to encode from, file name = " + encodeFrom);
        }
        if(DEBUG)
        {
            System.out.println("Encoding finished");
            System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime)
                    + " milliseconds\n");
        }
    }
    
    private void setupEncoding(String filename) throws FileNotFoundException
    {
        getWordFrequencies(filename);
        head = buildHuffmanTrie();
        prepareToEncode(head);
        sortValuesToEncodeBy();
    }
    
    private void sortValuesToEncodeBy()
    {
        Collections.sort(valuesToEncodeBy, new Comparator<Encoding>(){
            public int compare(Encoding e1, Encoding e2)
            {
                return e2.value - e1.value;
            }
        });
    }
    
    private Encoding getValueToEncodeBy(int bits)
    {
        int index = Collections.binarySearch(valuesToEncodeBy, new Encoding(0, 0, bits), new Comparator<Encoding>(){
                        public int compare(Encoding e1, Encoding e2){
                            return e2.value - e1.value;
                        }
                    });
        return valuesToEncodeBy.get(index);
    }
    
    static private int prepareToPrint(int value, int length)
    {
        int preparedValue = 0;
        
        for(int i = 0; i < length; i++)
        {
            if(getNthBit(value, i) > 0)
            {
                preparedValue += (int)Math.pow(2, length - 1 - i);
            }
        }
        
        return preparedValue;
    }
    
    static private int getNthBit(int value, int n)
    {
        return value & (int)Math.pow(2, n);
    }
    
    public void decode(String decodeFrom, String decodeTo)
    {
        long startTime = 0;
        if(DEBUG)
        {
            System.out.println("Decoding started");
            startTime = System.currentTimeMillis();
        }
        Node node = head;
        int writtenBits = 0;
        int temp;
        int t;
        try {
            writer = new BitWriter(decodeTo);
            reader = new BitReader(decodeFrom);
            readTrieFromFile();
            while(!eof(reader) && writtenBits <= lettersCount * 8)
            {
                t = lettersCount * 8 - writtenBits;
                temp = reader.readBit();
                if(temp == 0)
                {
                    if(node.left != null)
                    {
                        node = node.left;
                        if(node.left == null)//this means we found the node we need
                        {
                            if(t < byteLength && t > 0)
                                writer.writeBits(node.bytes, t);
                            else
                                writer.writeBits(node.bytes, byteLength);
                            node = head;
                            writtenBits += byteLength;
                        }
                    } /*else
                    {
                        if(t < byteLength && t > 0)
                                writer.writeBits(node.bytes, t);
                            else
                                writer.writeBits(node.bytes, byteLength);
                            node = head;
                            writtenBits += byteLength;
                    }*/
                }
                else
                {
                    if(node.right != null)
                    {
                        node = node.right;
                        if(node.right == null)
                        {
                            if(t < byteLength && t > 0)
                                writer.writeBits(node.bytes, t);
                            else
                                writer.writeBits(node.bytes, byteLength);
                            node = head;
                            writtenBits += byteLength;
                        }
                    }
                }
            }
            writer.flush();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found to decode from, file name is " + decodeFrom);
        }
        if(DEBUG)
        {
            System.out.println("Decoding finished");
            System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime)
                    + " milliseconds\n");
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
    
    private static void prepareToEncode(Node node)
    {
        if(node.left != null && node.right != null)
        {
            encodingValueLength++;
            if (diff == 0)
                diff = 1;
            else
                diff *= 2;
            prepareToEncode(node.left);
            
            encodingValue += diff;
            prepareToEncode(node.right);
            encodingValue -= diff;
            encodingValueLength--;
            if (diff == 1)
                diff = 0;
            else
                diff /= 2;
        } 
        else {
            //valuesToEncodeBy.add(new Encoding(prepareToPrint(encodingValue, encodingValueLength), encodingValueLength, node.bytes));
            valuesToEncodeBy.add(new Encoding(encodingValue, encodingValueLength, node.bytes));

        }
    }
}
