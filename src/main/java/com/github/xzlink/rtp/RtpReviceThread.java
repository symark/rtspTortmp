package com.github.xzlink.rtp;

import com.github.xzlink.loop.LoopPush;
import com.github.xzlink.rtsp.RTSPClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 * Created by symark on 2016/11/29.
 */
public class RtpReviceThread extends Thread{

    private LoopPush loopPush;
    private int port;
    private byte[] pps;
    private byte[] sps;
    private byte[] sei;
    private boolean pushDataFrame = true;
    private int deviceNo;
    private String remoteIp;
    private String localeIp;
    private boolean stop = false;
    private RTSPClient client;

    public RtpReviceThread(String remoteIp,int port,LoopPush loopPush,int deviceNo,String localeIp){
        this.port = port;
        this.loopPush = loopPush;
        this.deviceNo = deviceNo;
        this.remoteIp = remoteIp;
        this.localeIp = localeIp;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        byte[] nalu = new byte[0];
        boolean isStart = true;
        try{
            client = new RTSPClient(
                    new InetSocketAddress(remoteIp, 554),
                    new InetSocketAddress(localeIp, 0),
                    "rtsp://admin:Yundong2015@"+remoteIp+":554/h264/ch1/main/av_stream/",port);
            client.start();//
        }catch(Exception e){
            e.printStackTrace();
        }

        DatagramSocket datagramSocket=null;
        try {
            //监视8081端口的内容
            datagramSocket=new DatagramSocket(port);
            byte[] buf=new byte[1482];
            while(true){
                if(stop){
                    client.shutdown();
                    break;
                }
                //定义接收数据的数据包
                DatagramPacket datagramPacket=new DatagramPacket(buf, buf.length);

                datagramSocket.receive(datagramPacket);
                byte[] data = new byte[datagramPacket.getLength()];
                System.arraycopy(datagramPacket.getData(),0,data,0,datagramPacket.getLength());
                RtpHead rtpHead = new RtpHead(data);
                RtpBody rtpBody = new RtpBody(rtpHead.getPayload());
//                    printHexString(rtpBody.getPayload());
                if(rtpBody.isSPS()){
                    sps=rtpBody.getPayload();
                    continue;
                }
                if(rtpBody.isPPS()){
                    pps = rtpBody.getPayload();
                    byte[] messages = firstFrame(sps,pps);
                    loopPush.pushMessage(messages,pushDataFrame,deviceNo);
                    pushDataFrame = false;
                    continue;
                }
                if(rtpBody.isSEI()){
                    sei = rtpBody.getPayload();
                    continue;
                }


                if(rtpBody.isStart() || isStart){
                    isStart = true;
                    int startIndex = nalu.length;
                    int length = rtpBody.getSteamPayLoad().length;
                    byte[] tmp = nalu;
                    nalu = new byte[startIndex+length];
                    System.arraycopy(tmp,0,nalu,0,tmp.length);
                    System.arraycopy(rtpBody.getSteamPayLoad(),0,nalu,startIndex,length);

                    if(rtpBody.isEnd()){
                        byte[] tmpData = new byte[nalu.length+1];
                        tmpData[0] = (byte)((((((byte)rtpBody.getF())<<7)|((byte)rtpBody.getNri()<<5))&0xe0)|
                                (((byte)rtpBody.getShardType())&0x1f));
                        System.arraycopy(nalu,0,tmpData,1,nalu.length);
                        nalu = new byte[0];
                        isStart = false;
                        if(rtpBody.isKeyFrame()){
                            byte[] messages = keyFrame(tmpData);
                            loopPush.pushMessage(messages,false,deviceNo);
                        }else{
                            byte[] messages = innerFrame(tmpData);
                            loopPush.pushMessage(messages,false,deviceNo);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            datagramSocket.close();
        }
    }

    private byte[] firstFrame(byte[] sps,byte[] pps){
        byte[] messages = new byte[13+sps.length+3+pps.length];
        messages[0] = 0x17;
        messages[1] = 0x00;
        messages[2] = 0x00;
        messages[3] = 0x00;
        messages[4] = 0x00;
        messages[5] = 0x01;
        messages[6] = 0x4d;
        messages[7] = 0x00;
        messages[8] = 0x1f;
        messages[9] = (byte)0xff;
        messages[10] = (byte)0xe1;
        messages[11] = 0x00;
        messages[12] = (byte)sps.length;
        System.arraycopy(sps,0,messages,13,sps.length);
        messages[13+sps.length] = 0x01;
        messages[13+sps.length+1] = 0x00;
        messages[13+sps.length+2] = (byte)pps.length;
        System.arraycopy(pps,0,messages,13+sps.length+3,pps.length);
        return messages;
    }

    private byte[] innerFrame(byte[] nalu){
        byte[] messages = new byte[9+nalu.length];
        messages[0] = 0x27;
        messages[1]  = 0x01;
        messages[2] = 0x00;
        messages[3] = 0x00;
        messages[4] = 0x00;
        messages[5] = (byte) (nalu.length>>24);
        messages[6] = (byte) (nalu.length>>16);
        messages[7] = (byte) (nalu.length>>8);
        messages[8] = (byte) (nalu.length&0xff);

        System.arraycopy(nalu,0,messages,9,nalu.length);
        return messages;
    }

    private byte[] keyFrame(byte[] nalu){
        byte[] messages = new byte[9+sps.length+4+pps.length+4+sei.length+4+nalu.length];
        messages[0] = 0x17;
        messages[1] = 0x01;
        messages[2] = 0x00;
        messages[3] = 0x00;
        messages[4] = 0x00;
        messages[5] = (byte) (sps.length>>24);
        messages[6] = (byte) (sps.length>>16);
        messages[7] = (byte) (sps.length>>8);
        messages[8] = (byte) (sps.length&0xff);
        System.arraycopy(sps,0,messages,9,sps.length);
        messages[9+sps.length] = (byte) (pps.length>>24);
        messages[9+sps.length+1] = (byte) (pps.length>>16);
        messages[9+sps.length+2] = (byte) (pps.length>>8);
        messages[9+sps.length+3] = (byte) (pps.length&0xff);

        System.arraycopy(pps,0,messages,9+sps.length+4,pps.length);

        messages[9+sps.length+4+pps.length] = (byte) (sei.length>>24);
        messages[9+sps.length+4+pps.length+1] = (byte) (sei.length>>16);
        messages[9+sps.length+4+pps.length+2] = (byte) (sei.length>>8);
        messages[9+sps.length+4+pps.length+3] = (byte)(sei.length&0xff);
        System.arraycopy(sei,0,messages,9+sps.length+4+pps.length+4,sei.length);

        messages[9+sps.length+4+pps.length+4+sei.length] = (byte) (nalu.length>>24);
        messages[9+sps.length+4+pps.length+4+sei.length+1] = (byte) (nalu.length>>16);
        messages[9+sps.length+4+pps.length+4+sei.length+2] = (byte) (nalu.length>>8);
        messages[9+sps.length+4+pps.length+4+sei.length+3] = (byte)(nalu.length&0xff);

        System.arraycopy(nalu,0,messages,9+sps.length+4+pps.length+4+sei.length+4,nalu.length);
        return messages;
    }

}
