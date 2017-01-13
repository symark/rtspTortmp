package com.github.xzlink.rtmp.output;

import com.github.xzlink.rtmp.io.packets.ContentData;
import com.github.xzlink.rtmp.io.packets.Data;

import java.io.IOException;

/**
 * Interface for writing RTMP content streams (audio/video)
 * 
 * @author francois
 */
public abstract class RtmpStreamWriter {

    public abstract void write(Data dataPacket) throws IOException;

    public abstract void write(ContentData packet) throws IOException;

    public void close() {
        synchronized (this) {
            this.notifyAll();
        }
    }
}
