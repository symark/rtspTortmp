package com.github.xzlink.rtp;

/**
 * Created by symark on 2016/11/16.
 */
public class RtpBody {

    private int f;  //1 bit 当网络识别此单元存在比特错误时，可将其设为 1，以便接收方丢掉该单元。
    private int nri;    //2 bit 必须根据分片NAL单元的NRI域的值设置，用来指示该NALU的重要性等级。值越大，表示当前NALU越重要。
    /**
     * 0     没有定义
     * 1-23  NAL单元  单个 NAL 单元包.
     *      #define NALU_TYPE_SLICE 1
     *      #define NALU_TYPE_DPA 2
     *      #define NALU_TYPE_DPB 3
     *      #define NALU_TYPE_DPC 4
     *      #define NALU_TYPE_IDR 5
     *      #define NALU_TYPE_SEI 6
     *      #define NALU_TYPE_SPS 7
     *      #define NALU_TYPE_PPS 8
     *      #define NALU_TYPE_AUD 9
     *      #define NALU_TYPE_EOSEQ 10
     *      #define NALU_TYPE_EOSTREAM 11
     *      #define NALU_TYPE_FILL 12
     * 24    STAP-A   单一时间的组合包
     * 25    STAP-B   单一时间的组合包
     * 26    MTAP16   多个时间的组合包
     * 27    MTAP24   多个时间的组合包
     * 28    FU-A     分片的单元
     * 29    FU-B     分片的单元
     * 30-31 没有定义
     */
    private int type;   //5 bit

    /** shard parameter **/
    private int s; //1 bit  当设置成1,开始位指示分片NAL单元的开始。当跟随的FU荷载不是分片NAL单元荷载的开始，开始位设为0。
    private int e;  //1 bit  当设置成1,结束位指示分片NAL单元的结束。即荷载的最后字节是分片NAL单元的最后一个字节。当跟随的FU荷载不是分片NAL单元的最后分片,结束位设置为0。
    private int r;  //1 bit  保留位必须设置为0，接收者必须忽略该位。
    private int shardType;  //5 bit    值：5为关键帧
    /** shard parameter **/

    private byte[] payload;
    private byte[] steamPayLoad;

    public RtpBody(byte[] data){
        f = (data[0]&0x000000ff)>>>7;
        nri = (((data[0]&0x000000ff)<<1)&0x000000ff)>>>6;
        type = (((data[0]&0x000000ff)<<3)&0x000000ff)>>>3;
        if(type>=1 && type<=23){
            payload = new byte[data.length];
            System.arraycopy(data,0,payload,0,data.length);
            steamPayLoad = new byte[data.length-1];
            System.arraycopy(data,1,steamPayLoad,0,data.length-1);
        } else if (type == 24 || type == 25 || type == 26 || type == 27) {
            System.err.println("no process!!!");
        } else if (type == 28 || type == 29){
            s = (data[1]&0x000000ff)>>>7;
            e = (((data[1]&0x000000ff)<<1)&0x000000ff)>>>7;
            r = (((data[1]&0x000000ff)<<2)&0x000000ff)>>>7;
            shardType = (((data[1]&0x000000ff)<<3)&0x000000ff)>>>3;
            payload = new byte[data.length];
            System.arraycopy(data,0,payload,0,data.length);
            steamPayLoad = new byte[data.length-2];
            System.arraycopy(data,2,steamPayLoad,0,data.length-2);
        }else{
            throw new RuntimeException("nal error type");
        }
    }

    public boolean isStart(){
        return s==1;
    }

    public boolean isEnd(){
        return e==1;
    }

    public boolean isPPS(){
        return type==8;
    }

    public boolean isSPS(){
        return type==7;
    }

    public boolean isSEI(){
        return type==6;
    }

    public byte[] getPayload() {
        return payload;
    }

    public byte[] getSteamPayLoad() {
        return steamPayLoad;
    }

    public boolean isKeyFrame(){
        return shardType==5;
    }

    public int getF() {
        return f;
    }

    public int getNri() {
        return nri;
    }

    public int getShardType() {
        return shardType;
    }

    public static void main(String args[]){
        System.out.println( (((0x7c&0x000000ff)<<3)&0x000000ff)>>>3);
    }

}
