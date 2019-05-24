package jp.spring.ioc.beans.io.loader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import jp.spring.ioc.util.FileUtils;

/**
 * Created by Administrator on 1/23/2017.
 */
public class PropertiesLoader {

  public static List<Properties> getResource(String strLocation) {
    String[] locations = strLocation.split(";");

    List<Properties> res = new ArrayList<>();
    for (String location : locations) {
      List<File> temp = FileUtils.findFiles(location, ".properties");
      for (File file : temp) {
        try {
          InputStream stream = new BufferedInputStream(new FileInputStream(file));
          Properties properties = new Properties();
          properties.load(stream);
          res.add(properties);
        } catch (Exception e) {
          //TODO LOG THIS ERROR
        }
      }
    }

    return res;
  }
}
