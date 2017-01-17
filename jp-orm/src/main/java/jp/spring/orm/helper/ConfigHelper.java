package jp.spring.orm.helper;

import jp.spring.orm.utils.FileUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 1/17/2017.
 */
public class ConfigHelper {

    private static final Properties configProperties = init();

    private static Properties init() {
        List<File> files = FileUtils.findFiles(".properties");
        Properties properties = new Properties();
        for(File file : files) {
            try {
                properties.load(new FileInputStream(file));
            }  catch (IOException e) {
                //do nothing;
            }
        }

        return properties;
    }

    public static String getStringProperty(String key) {
        String value = "";
        if(configProperties.containsKey(key)) {
            value = configProperties.getProperty(key);
        } else {
            System.err.print("Can't find " + key + "\n");
        }
        return value;
    }

    public static int getNUmberProperty(String key) {
        int value = 0;
        String stringValue = getStringProperty(key);
        if(NumberUtils.isNumber(stringValue)) {
            value = Integer.parseInt(stringValue);
        }
        return  value;
    }
}
