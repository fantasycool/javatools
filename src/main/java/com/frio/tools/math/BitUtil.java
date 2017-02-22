package com.frio.tools.math;

/**
 *
 * Created by frio on 17/2/22.
 */
public class BitUtil {
    /**
     * convert number to binary char
     * @return
     */
    public static String toBinaryChar(int value){
        StringBuilder sb = new StringBuilder();
        for(int i = 31; i >= 0; i -- ){
            if(i == 31 && value < 0){
                sb.append("1");
            }else {
                sb.append(((1 << i) & value) > 0 ? "1" : "0");
            }
        }
        return sb.toString();
    }

    /**
     * arg is BigEndian
     * convert bytes to int
     * 在java中bytes[0]做位运算前会自动转型integer,进行补位,导致数据不正确,所以要和0xFF位与消除补位的1
     * @param bytes
     * @return
     */
    public static int bytesToInt(byte[] bytes){
        if(bytes.length != 4){
            throw new IllegalArgumentException("illegal bytes array!");
        }
        return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) | ((bytes[3] & 0xFF));
    }

    /**
     * BigEndian
     * convert int to bytes
     * @param value
     * @return
     */
    public static byte[] intToBytes(int value){
        byte[] result = new byte[4];
        result[0] = (byte)((value >> 24) & 0xFF);
        result[1] = (byte)((value >> 16) & 0xFF);
        result[2] = (byte)((value >> 8) & 0xFF);
        result[3] = (byte)(value & 0xFF);
        return result;
    }
}
