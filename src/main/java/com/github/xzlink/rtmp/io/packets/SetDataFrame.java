package com.github.xzlink.rtmp.io.packets;

import com.github.xzlink.rtmp.amf.AmfMap;
import com.github.xzlink.rtmp.amf.AmfObject;
import com.github.xzlink.rtmp.amf.AmfString;
import com.github.xzlink.rtmp.io.ChunkStreamInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by symark on 16/10/24.
 */
public class SetDataFrame extends RtmpPacket  {

    private AmfMap amfMap;

    public SetDataFrame(RtmpHeader header, AmfMap amfMap) {
        super(header);
        this.amfMap = amfMap;
    }

    public SetDataFrame(AmfMap amfMap){
        super(new RtmpHeader(RtmpHeader.ChunkType.TYPE_0_FULL, ChunkStreamInfo.RTMP_STREAM_CHANNEL, RtmpHeader
                .MessageType.DATA_AMF0));
        this.amfMap = amfMap;
    }

    @Override
    public void readBody(InputStream in) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void writeBody(OutputStream out) throws IOException {
        AmfString.writeStringTo(out,"@setDataFrame",false);
        AmfString.writeStringTo(out,"onMetaData",false);
//        writeVariableData(out);
        amfMap.writeTo(out);
//        out.flush();
    }

    @Override
    public String toString() {
        return "RTMP SetDataFrame (@setDataFrame" + ", ChunkStream ID: " + header.getChunkStreamId() + ")";
    }
}
