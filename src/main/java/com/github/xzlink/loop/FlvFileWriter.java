package com.github.xzlink.loop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by symark on 2017/3/9.
 */
public class FlvFileWriter {

    private FileOutputStream fo;

    private long previousTagSize = 0;
    private final static byte[] header = {'F','L','V',0x01,0x01,0x00,0x00,0x00,0x09};
    private static final byte[] videoTagType = {0x09};
    private static final byte[] streamId = {0, 0, 0};
    private int dataSize = 0;
    private long startTimestamp = 0;
    private boolean firstTag = true;

    private static byte FLV_TAG_HEADER_SIZE = 11;

    public void init(File flv) {
        setupFlvFile(flv);
    }

    public void writeDataToFile (ByteArrayOutputStream videoData) {

        int size = videoData.size();
        byte[] blockData = videoData.toByteArray();

        try {
            writePreviousTagSize();
            writeFlvTag(blockData, size);
        } catch(Exception e) {
            System.out.println("exception: "+e.getMessage());
        }
    }

    private void writeFlvTag(byte[] videoData, int size) throws IOException {
        writeTagType();
        writeDataSize(size);
        writeTimestamp();
        writeStreamId();
        writeVideoData(videoData);
    }

    private void setupFlvFile(File flv) {
        try {
            fo = new FileOutputStream(flv);
            fo.write(header);

//            byte[] demo = new byte[]{0x12,0x00,0x00,(byte)0xD4,0x00,0x00,0x00,0x00,0x00,0x00,0x00
//                    ,0x02,0x00,0x0A,0x6F,0x6E,0x4D,0x65,0x74,0x61,0x44,0x61,0x74,0x61,0x08
//                    ,0x00,0x00,0x00,0x09,0x00,0x08,0x64,0x75,0x72,0x61,0x74,0x69,0x6F,0x6E
//                    ,0x00,0x40,0x32,(byte)0xEB,(byte)0x85,0x1E,(byte)0xB8,0x51,(byte)0xEC,0x00,0x05,0x77,0x69,0x64
//                    ,0x74,0x68,0x00,0x40,(byte)0x9E,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x06,0x68
//                    ,0x65,0x69,0x67,0x68,0x74,0x00,0x40,(byte)0x90,(byte)0xE0,0x00,0x00,0x00,0x00,0x00
//                    ,0x00,0x0D,0x76,0x69,0x64,0x65,0x6F,0x64,0x61,0x74,0x61,0x72,0x61,0x74
//                    ,0x65,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x09,0x66,0x72
//                    ,0x61,0x6D,0x65,0x72,0x61,0x74,0x65,0x00,0x40,0x39,0x00,0x00,0x00,0x00
//                    ,0x00,0x00,0x00,0x0C,0x76,0x69,0x64,0x65,0x6F,0x63,0x6F,0x64,0x65,0x63
//                    ,0x69,0x64,0x00,0x40,0x1C,0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x05,0x74
//                    ,0x69,0x74,0x6C,0x65,0x02,0x00,0x12,0x4D,0x65,0x64,0x69,0x61,0x20,0x50
//                    ,0x72,0x65,0x73,0x65,0x6E,0x74,0x61,0x74,0x69,0x6F,0x6E,0x00,0x07,0x65
//                    ,0x6E,0x63,0x6F,0x64,0x65,0x72,0x02,0x00,0x0D,0x4C,0x61,0x76,0x66,0x35
//                    ,0x37,0x2E,0x35,0x31,0x2E,0x31,0x30,0x33,0x00,0x08,0x66,0x69,0x6C,0x65
//                    ,0x73,0x69,0x7A,0x65,0x00,0x41,0x44,0x50,(byte)0x80,0x00,0x00,0x00,0x00,0x00,0x00,0x09};
//
//            fo.write(demo);
//            previousTagSize = 223;
//            fo.write(0x00);
//            fo.write(0x00);
//            fo.write(0x00);
//            fo.write(0xdf);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void writePreviousTagSize() throws IOException {

        int byte1 = (int)previousTagSize >> 24;
        int byte2 = (int)previousTagSize >> 16;
        int byte3 = (int)previousTagSize >> 8;
        int byte4 = (int)previousTagSize & 0xff;

        //   	System.out.println("PrevTagSize: dec=" + previousTagSize + " hex=" + Long.toHexString(previousTagSize) + " bytes= "
        //   			+ Integer.toHexString(byte1) + " " + Integer.toHexString(byte2)
        //   			+ " " + Integer.toHexString(byte3) + " " + Integer.toHexString(byte4));

        fo.write(byte1);
        fo.write(byte2);
        fo.write(byte3);
        fo.write(byte4);
    }

    private void writeTagType() throws IOException {
        fo.write(videoTagType);
    }

    private void writeDataSize(int size) throws IOException {
        int byte1 = (size >> 16);
        int byte2 = (size >> 8);
        int byte3 = (size & 0x0ff);

//    	System.out.println("DataSize: dec=" + size + " hex=" + Integer.toHexString(size) + " bytes= "
////    			+ Integer.toHexString(byte1) + " " + Integer.toHexString(byte2)
        //   			+ " " + Integer.toHexString(byte3));

        fo.write(byte1);
        fo.write(byte2);
        fo.write(byte3);

        previousTagSize = FLV_TAG_HEADER_SIZE + size;
    }

    private void writeTimestamp() throws IOException {
        long now = System.currentTimeMillis();

        if (firstTag) {
            startTimestamp = now;
            firstTag = false;
        }

        long elapsed = now - startTimestamp;

        int fb = (int)(elapsed & 0xff0000) >> 16;
        int sb = (int)(elapsed & 0xff00) >> 8;
        int tb = (int)(elapsed & 0xff);
        int ub = ((int)elapsed & 0xff000000) >> 24;

//    	System.out.println("timestamp: dec=" + elapsed + " hex=" + Long.toHexString(elapsed) + " bytes=" +
//    			Integer.toHexString(fb) + " " + Integer.toHexString(sb) + " " + Integer.toHexString(tb) + " " + Integer.toHexString(ub));

        fo.write(fb);
        fo.write(sb);
        fo.write(tb);

        fo.write(ub );

    }

    private void writeStreamId() throws IOException {
        fo.write(streamId);
    }

    private void writeVideoData(byte[] videoData) throws IOException {
        fo.write(videoData);
    }

    public void stop() {
        try {
            System.out.println("Closing stream");
            fo.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
