package jp.spring.ioc.beans.scan.scanner;

/**
 * Created by Administrator on 10/28/2017.
 */
@FunctionalInterface
public interface FailureHandler {

    void onFailure(Throwable throwable);
}
