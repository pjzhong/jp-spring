package jp.spring.orm.utils;

import jp.spring.orm.ActiveRecord;
import jp.spring.orm.Config;
import jp.spring.orm.Model;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2/7/2017.
 */
public class DbUtils {
    private static Map<Class<? extends Model>, Config> modelToConfig = new HashMap<Class<? extends Model>, Config>();
    private static Map<String, Config> configNameMap = new HashMap<String, Config>();

    public static void addConfig(Config config) {
        if( (config != null)
                && (!configNameMap.containsKey(config.getName())) ) {
            configNameMap.put(config.getName(), config);
        }
    }

    public static void addConfig(Class<? extends Model> modelClass, Config config) {
        if((modelClass != null) || (config != null)) {
            modelToConfig.put(modelClass, config);
        }
    }

    public static Config getConfig(String configName) {
        return configNameMap.get(configName);
    }

    public static Config getConfig(Class<? extends Model> modelClass) {
        return modelToConfig.get(modelClass);
    }

    public static Config removeConfig(String configName) {
        return configNameMap.remove(configName);
    }
}
