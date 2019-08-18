package jp.spring.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 1/10/2017.
 */
public class FileUtils {

  private static final int BUFFER_SIZE = 1024 * 1024; // 1M

  private FileUtils() {
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
