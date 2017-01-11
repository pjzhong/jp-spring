package jp.spring.web.util;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 1/10/2017.
 */
public class FileUtils {

    private static final MimetypesFileTypeMap MIMETYPE_MAP = new MimetypesFileTypeMap();

    private static final int BUFFER_SIZE = 1024 * 1024; // 1M

    public final static Set<String> ALLOWED_EXTENSION = new HashSet<>();

    //只是为了方便而已，因为还没找(想)到更好的方法
    static {
        String[] exts = "js;css;jpg;ico;png;jpeg;gif;bmp;swf;eot;svg;ttf;woff;woff2;less;scss;".split(";");
        for(String ext : exts) {
            ALLOWED_EXTENSION.add(ext);
        }
    }

    /**
     * css和js进行了特殊处理，因为MimetypesFileTypeMap中的返回值不准确。
     * */
    public static String getMimeType(String extension) {
        if("css".equals(extension)) {
            return "text/css";
        }

        if("js".equals(extension)) {
            return "application/javascript";
        }

        return MIMETYPE_MAP.getContentType("x." + extension);
    }

    public static void copy(String filePath, OutputStream out) throws IOException {
        File file = new File(filePath);

        if(!file.exists()) {
            throw new IOException("Can't not find file:" + filePath);
        }

        FileChannel in = null;
        ByteBuffer buffer = null;
        try {
            int read = 0;
            in = new FileInputStream(file).getChannel();
            buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while((read = in.read(buffer)) != -1) {
                buffer.flip();
                out.write(buffer.array(), 0, read);
                buffer.clear();
            }
            out.flush();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
