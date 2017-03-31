package com.github.xzlink.rtp;

/**
 * Created by symark on 2016/11/15.
 */
public class RtpHead {

    private int v;   //2 bit   version   2:RFC 1889 Version
    private int p;   //1 bit   padding   1 is true 0 is false
    private int x;   //1 bit   extension   1 is true 0 is false
    private int cc;  //4 bit   Contributing source identifiers count

    private int m;   //1 bit   mark    1 is true 0 is false
    private int pt;  //7 bit   payload type

    private int sequenceNumber;  //16 bit

    private int timestamp;   //32 bit

    private int ssrc;    //32 bit synchronization source (SSRC) identifier
//    private int csrc;    //32 bit contributing source (CSRC) identifiers

    private byte[] payload;

    private int paddingCount;   //1 bit
    private int headLength = 12;
//    private byte[] paddingData; //

    public RtpHead(byte[] data){
        if(data.length>headLength){
            v = ((data[0]&0x000000ff)>>>6);
            p = (((data[0]&0x000000ff)<<2)&0x000000ff)>>>7;
            x = (((data[0]&0x000000ff)<<3)&0x000000ff)>>>7;
            cc = (((data[0]&0x000000ff)<<4)&0x000000ff)>>>4;
            m = (data[1]>>7);
            pt = data[1]&0x7f;
            sequenceNumber = data[2]<<8+data[3];

            timestamp = byte2Int(new byte[]{data[4],data[5],data[6],data[7]});
            ssrc = (data[8]<<24)+(data[9]<<16)+(data[10]<<8)+data[11];
//            csrc = (data[12]<<24)+(data[13]<<16)+(data[14]<<8)+data[15];

            if(isPadding()){
                paddingCount = data[data.length-1];
                payload = new byte[data.length-paddingCount-headLength];
                System.arraycopy(data,headLength,payload,0,data.length-paddingCount-headLength);
            }else{
                payload = new byte[data.length-headLength];
                System.arraycopy(data,headLength,payload,0,data.length-headLength);
            }
        }
    }

    public static int byte2Int(byte[] b) {
        int intValue = 0;
        for (int i = 0; i < b.length; i++) {
            intValue += (b[i] & 0xFF) << (8 * (3 - i));
        }
        return intValue;
    }


    public static void main(String args[]){
//        int a = (0x71<<24)+(0x93<<16)+(0x21<<8)+0x82;
//        int b = ((0xf1<<24)+(0x93<<16)+(0x21<<8)+0x82)&0xffffff;
//        System.out.println(Integer.toBinaryString(0xf1<<24));
//        System.out.println("00000000"+Integer.toBinaryString(0x93<<16));
//
//        System.out.println(Integer.toHexString(a));
//        System.out.println(a);
//        System.out.println(Integer.toBinaryString(b));
//        System.out.println(b);
//
//        System.out.println(getUnsignedIntt(a));
//        System.out.println(a);
        int a = (int)-96&0xff>>>6;
        System.out.println(Integer.toBinaryString(0xe0));
        System.out.println(Integer.toBinaryString(0xe0&0x7f));
        System.out.println(Integer.toBinaryString((0xe0&0x7f)));
        System.out.println(a);
        int b = 0xa0>>>6;
        System.out.println(b);
    }
    public static long getUnsignedIntt (int data){     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data&0x0FFFFFFFFl;
    }

    public long getTimestamp() {
        return (long)(timestamp/90);
//        return &0x0FFFFFFFFl;
    }

//    public int getTimestamp() {
//        return timestamp;
//    }

    public int getPayloadType() {
        return pt;
    }

    public boolean getMark(){
        return m==1;
    }
    public boolean isPadding(){
        return p==1;
    }
    public byte[] getPayload(){
        return payload;
    }
}
