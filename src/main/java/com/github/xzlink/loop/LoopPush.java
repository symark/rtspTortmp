package com.github.xzlink.loop;

import com.github.xzlink.rtmp.DefaultRtmpClient;
import com.github.xzlink.rtp.RtpHead;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by symark on 2016/11/29.
 */
public class LoopPush {

    int processDeviceNo;
    DefaultRtmpClient r;
//    FlvWriter flvWriter;
    FlvFileWriter flvFileWriter;
    private SendInvoke sendInvoke;

    public LoopPush(DefaultRtmpClient r,FlvFileWriter flvFileWriter,SendInvoke sendInvoke){
        this.r = r;
//        flvWriter = new FlvWriter(flvSavePath,flvName);
        this.flvFileWriter = flvFileWriter;
//        flvFileWriter.init(flv);
        this.sendInvoke = sendInvoke;
    }

    public void pushMessage(RtpHead rtpHead,byte[] data, boolean isPushDataFrame, int deviceNo){
        if(deviceNo==processDeviceNo) {
            if(r!=null) {
                r.pushMessage(data, isPushDataFrame);
            }
//            if(!isPushDataFrame) {
            ByteArrayOutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                out.write(data);
                flvFileWriter.writeDataToFile(rtpHead.getTimestamp(), out);
                if(sendInvoke!=null) {
                    sendInvoke.send(data,rtpHead.getTimestamp());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if(out!=null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

//                flvWriter.writeFrame(data);
//            }
        }
    }

    public void setProcessDeviceNo(int processDeviceNo) {
        this.processDeviceNo = processDeviceNo;
    }

}
