package jp.spring.orm.utils;

import jp.spring.ioc.util.JpUtils;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * Created by Administrator on 12/15/2016.
 */
public class FileUtils {
    private static final int BUFFER_SIZE = 1024 * 1024; //1M

    public static List<File>  findFiles(final String fileExtension) {
        List<File> files = new LinkedList<File>();
        try {
            URL url = FileUtils.class.getResource("/"); // root directory
            if(url == null) {
                return files;
            }
            String protocol = url.getProtocol();
            if("file".equals(protocol)) { // 如果是以文件的形式保存在服务器上
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8"); //获取物理路径
                File dir = new File(filePath);

                if(!dir.exists() || !dir.isDirectory()) {
                    return  files;
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        return files;
    }

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
