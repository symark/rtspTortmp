package com.github.xzlink.rtmp.output;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import com.github.xzlink.rtmp.util.L;

/**
 * Simple writer class exposing ADTS-framed AAC audio stream data via an InputStream 
 * 
 * @author francois
 */
public class AacInputStreamWriter extends AacWriter implements InputStreamWrapper  {

    private PipedInputStream inputStream;

    public AacInputStreamWriter() throws IOException {
        inputStream = new PipedInputStream();
        out = new PipedOutputStream(inputStream);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void close() {
        try {
            inputStream.close();
        } catch (IOException ex) {
            L.e("Failed to close wrapped PipedInputStream", ex);
        }
        super.close();
    }
}
