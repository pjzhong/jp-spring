package jp.spring.orm.utils;



import java.io.*;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 12/15/2016.
 */
public class FileUtils {
    private static final int BUFFER_SIZE = 1024 * 1024; //1M

    public static void copy(String filePath, OutputStream out) throws IOException {
        File file = new File(filePath);
        if(!file.exists()) {
            return;
        }

        FileChannel in = null;
        ByteBuffer buffer = null;
        try {
            int c;
            in = new FileInputStream(file).getChannel();
            buffer = ByteBuffer.allocate(BUFFER_SIZE); //new i/o
            while( (c = in.read(buffer)) != -1) {
                buffer.flip(); //Prepare for writing
                out.write(buffer.array(), 0, c);
                buffer.clear(); //Prepare for reading
                 }
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception  e) {

            }
        }
    }

}
