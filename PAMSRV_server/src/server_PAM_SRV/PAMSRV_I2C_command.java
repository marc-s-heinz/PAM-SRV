/*
*   delivers recurring commands and methods to convert bytes to shorts and vice versa
*/
package server_PAM_SRV;

public class PAMSRV_I2C_command {
    private static final byte[] startCmd = convertTwoShortsTo4ByteArray((short)1,(short) 0);
    private static final byte[] stopCmd = convertTwoShortsTo4ByteArray((short) 0, (short) 1);
    
    public static byte[] getStartCmd()  {return startCmd;}
    public static byte[] getStopCmd()   {return stopCmd;}
    
    public static byte[] convertTwoShortsTo4ByteArray(short num1, short num2) {
        byte[] temp = new byte[4];
        temp[0] = (byte) num1;
        temp[1] = (byte) (num1 >> 8);
        temp[2] = (byte) num2;
        temp[3] = (byte) (num2 >> 8);
        return temp;
    }//End: convert short -> byte
    
    public static short convertTwoBytesToShort(byte b1, byte b2) {
        short temp = (short) b1;
        temp = (short) ((temp << 8) | b2);
        return temp;
    }//End: convert bytes -> short
}//End: class PAMSRV_I2C_command
