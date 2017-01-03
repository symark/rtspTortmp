package com.github.xzlink.loop;

import com.github.xzlink.rtmp.DefaultRtmpClient;

/**
 * Created by symark on 2016/11/29.
 */
public class LoopPush {

    int processDeviceNo;
    DefaultRtmpClient r;
    FlvWriter flvWriter;

    public LoopPush(DefaultRtmpClient r){
        this.r = r;
        flvWriter = new FlvWriter("/Users/symark/Downloads/ffmpeg3","test");
    }

    public void pushMessage(byte[] data, boolean isPushDataFrame, int deviceNo){
//        if(deviceNo==processDeviceNo) {
            r.pushMessage(data, isPushDataFrame);
//            if(!isPushDataFrame) {
                flvWriter.writeFrame(data);
//            }
//        }
    }

    public void setProcessDeviceNo(int processDeviceNo) {
        this.processDeviceNo = processDeviceNo;
    }

}
