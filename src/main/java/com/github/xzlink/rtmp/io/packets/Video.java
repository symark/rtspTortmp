package com.github.xzlink.rtmp.io.packets;

import com.github.xzlink.rtmp.amf.AmfMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Video data packet
 *  
 * @author francois
 */
public class Video extends ContentData {

    public Video(RtmpHeader header) {
        super(header);
    }

    @Override
    public void readBody(InputStream in) throws IOException {
        super.readBody(in);
    }

    @Override
    public void writeBody(OutputStream out) throws IOException {
        out.write(data);
    }
}
