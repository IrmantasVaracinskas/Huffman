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
public class Encoding {
    public int encodeValue;
    public int encodeValueLength;
    public int value;
    
    public Encoding(int encodeValue, int encodeValueLength, int value)
    {
        this.encodeValue = encodeValue;
        this.encodeValueLength = encodeValueLength;
        this.value = value;
    }
}
