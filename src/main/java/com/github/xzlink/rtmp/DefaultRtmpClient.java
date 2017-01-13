package com.github.xzlink.rtmp;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.github.xzlink.rtmp.io.RtmpConnection;
import com.github.xzlink.rtmp.io.packets.ContentData;
import com.github.xzlink.rtmp.io.packets.Data;
import com.github.xzlink.rtmp.output.FlvWriter;
import com.github.xzlink.rtmp.output.RawOutputStreamWriter;
import com.github.xzlink.rtmp.output.RtmpStreamWriter;

/**
 * Default implementation of an RTMP client
 * 
 * @author francois
 */
public class DefaultRtmpClient implements RtmpClient {

    private static final Pattern rtmpUrlPattern = Pattern.compile("^rtmp://([^/:]+)(:(\\d+))*/([^?]+)(\\?(.*))*$");
    
    private RtmpClient rtmpConnection;
    public String playPath;

    /** 
     * Constructor for specified host, port and application
     * 
     * @param host the hostname or IP address to connect to
     * @param port the port to connect to
     * @param application the application to connect to
     */
    public DefaultRtmpClient(String host, int port, String application) {
        rtmpConnection = new RtmpConnection(host, port, application);
    }

    /** 
     * Constructor for specified host and application, using the default RTMP port (1935)
     * 
     * @param host the hostname or IP address to connect to
     * @param application the application to connect to
     */
    public DefaultRtmpClient(String host, String application) {
        this(host, 1935, application);
    }

    /** 
     * Constructor for URLs in the format: rtmp://host[:port]/application[?streamName]
     * 
     * @param url a RTMP URL in the format: rtmp://host[:port]/application[?streamName]
     */
    public DefaultRtmpClient(String url) {
        Matcher matcher = rtmpUrlPattern.matcher(url);
        if (matcher.matches()) {
            String portStr = matcher.group(3);
            int port = portStr != null ? Integer.parseInt(portStr) : 1935;            
            playPath = matcher.group(6);            
            rtmpConnection = new RtmpConnection(matcher.group(1), port, matcher.group(4));
        } else {
            throw new RuntimeException("Invalid RTMP URL. Must be in format: rtmp://host[:port]/application[?streamName]");
        }
    }

   

    @Override
    public void connect() throws IOException {
        rtmpConnection.connect();
    }

    @Override
    public void pushMessage(byte[] data,boolean isPushDataFrame) {
        rtmpConnection.pushMessage(data,isPushDataFrame);
    }

    @Override
    public void shutdown() {
        rtmpConnection.shutdown();
    }

    public void play(RtmpStreamWriter rtmpStreamWriter) throws IllegalStateException, IOException {
        if (playPath == null) {
            throw new IllegalStateException("No stream name specified");
        }
        rtmpConnection.play(playPath, rtmpStreamWriter);
    }

    public void publish() throws IllegalStateException, IOException{
        rtmpConnection.publish();
    }
    
    public void playAsync(RtmpStreamWriter rtmpStreamWriter) throws IllegalStateException, IOException {
        if (playPath == null) {
            throw new IllegalStateException("No stream name specified");
        }
        rtmpConnection.playAsync(playPath, rtmpStreamWriter);
    }

    @Override
    public void play(String playPath, RtmpStreamWriter rtmpStreamWriter) throws IllegalStateException, IOException {
        rtmpConnection.play(playPath, rtmpStreamWriter);
    }

    @Override
    public void playAsync(String playPath, RtmpStreamWriter rtmpStreamWriter) throws IllegalStateException, IOException {
        rtmpConnection.playAsync(playPath, rtmpStreamWriter);
    }

    @Override
    public void closeStream() throws IllegalStateException {
        rtmpConnection.closeStream();
    }
    
    @Override
    public void pause() throws IllegalStateException {
        rtmpConnection.pause();
    }

//    public static void main(String args[]){
////        DefaultRtmpClient r = new DefaultRtmpClient("rtmp://4837.livepush.myqcloud" +
////                ".com/live/4837_df5b7746940511e69776e435c87f075e?bizid=4837");
//        DefaultRtmpClient r = new DefaultRtmpClient("4837.livepush.myqcloud.com",1935,"live");
//        r.playPath = "4837_df5b7746940511e69776e435c87f075e?bizid=4837";
//        try {
//            r.connect();
//
////            FlvWriter writer = new FlvWriter();
////            writer.open("/Users/symark/Downloads/ffmpeg3/output2.flv");
//            r.publish();
//            try {
//                Thread.sleep(100);
//            }catch(Exception e){
//
//            }
//            byte[] data = new byte[60000];
//            data[0] = 0x17;
//            r.pushMessage(data,false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
