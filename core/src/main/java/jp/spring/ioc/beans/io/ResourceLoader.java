package jp.spring.ioc.beans.io;

/**
 * Created by Administrator on 12/25/2016.
 */
public interface ResourceLoader {

    Resource[] getResource(String location);
}
