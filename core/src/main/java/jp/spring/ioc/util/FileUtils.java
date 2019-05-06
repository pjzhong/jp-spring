package jp.spring.ioc.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 1/10/2017.
 */
public class FileUtils {

  private static final int BUFFER_SIZE = 1024 * 1024; // 1M

  private FileUtils() {
  }

  public static List<File> findFiles(String path, final String fileExtension) {
    List<File> files = Collections.emptyList();

    try {
      //from the root directory
      URL url = FileUtils.class.getResource(path);
      if (url != null) {
        files = new ArrayList<>();
        String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
          String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
          File dir = new File(filePath);
          if (!dir.exists() || !dir.isDirectory()) {
            return files;
          }

          File[] targetFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
              return file.getName().endsWith(fileExtension);
            }
          });
          if (!TypeUtil.isEmpty(targetFiles)) {
            files.addAll(Arrays.asList(targetFiles));
          }
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return files;
  }

  public static void copy(String filePath, OutputStream out) throws IOException {
    File file = new File(filePath);

    if (!file.exists()) {
      throw new IOException("Can't not find file:" + filePath);
    }

    FileChannel in = null;
    ByteBuffer buffer = null;
    try {
      int read = 0;
      in = new FileInputStream(file).getChannel();
      buffer = ByteBuffer.allocate(BUFFER_SIZE);
      while ((read = in.read(buffer)) != -1) {
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
