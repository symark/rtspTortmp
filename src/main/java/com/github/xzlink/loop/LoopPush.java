package com.github.xzlink.loop;

import com.github.xzlink.rtmp.DefaultRtmpClient;

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

    public LoopPush(DefaultRtmpClient r,FlvFileWriter flvFileWriter){
        this.r = r;
//        flvWriter = new FlvWriter(flvSavePath,flvName);
        this.flvFileWriter = flvFileWriter;
//        flvFileWriter.init(flv);
    }

    public void pushMessage(byte[] data, boolean isPushDataFrame, int deviceNo){
        if(deviceNo==processDeviceNo) {
            r.pushMessage(data, isPushDataFrame);
//            if(!isPushDataFrame) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                out.write(data);
                flvFileWriter.writeDataToFile(out);
            } catch (IOException e) {
                e.printStackTrace();
            }

//                flvWriter.writeFrame(data);
//            }
        }
    }

    public void setProcessDeviceNo(int processDeviceNo) {
        this.processDeviceNo = processDeviceNo;
    }

}
