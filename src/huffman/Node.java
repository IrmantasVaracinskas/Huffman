/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package huffman;

/**
 *
 * @author Irmis
 */
public class Node {
    public short bytes; //stores up to two bytes
    public int frequency;
    public Node left, right;
    
    Node(short bytes, int freq, Node left, Node right)
    {
        this.bytes = bytes;
        this.frequency = freq;
        this.left = left;
        this.right = right;
    }
    
    boolean isLeaf()
    {
        return left == null && right == null;
    }
    
    public void writeToFile(BitWriter writer, byte byteLength)
    {
        if (isLeaf())
        {
            writer.writeBit(1);
            writer.writeBits(bytes, byteLength);
            //writer.flush();
        }
        else
        {
            writer.writeBit(0);
            //writer.flush();
            if(left != null)
                left.writeToFile(writer, byteLength);
            if(right != null)
                right.writeToFile(writer, byteLength);
        }
    }
}
