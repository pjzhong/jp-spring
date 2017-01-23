package jp.spring.ioc.util;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by Administrator on 1/10/2017.
 */
public class FileUtils {

    private static final MimetypesFileTypeMap MIMETYPE_MAP = new MimetypesFileTypeMap();

    private static final int BUFFER_SIZE = 1024 * 1024; // 1M

    private FileUtils(){}

    public static List<File> findFiles(String path , final String fileExtension) {
        List<File> files = null;

        try {
            //from the root directory
            URL url = FileUtils.class.getResource(path);
            if(url != null) {
                files = new ArrayList<File>();
                String protocol = url.getProtocol();
                if("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    File dir = new File(filePath);
                    if(!dir.exists() || !dir.isDirectory()) {
                        return files;
                    }

                    File[] targetFiles = dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.getName().endsWith(fileExtension);
                        }
                    });
                    if(!JpUtils.isEmpty(targetFiles)) {
                        files.addAll(Arrays.asList(targetFiles));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return files;
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
