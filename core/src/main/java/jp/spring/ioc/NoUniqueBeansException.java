package jp.spring.ioc;

/**
 * Created by Administrator on 1/3/2017.
 */
public class NoUniqueBeansException extends RuntimeException {

  public NoUniqueBeansException(String msg) {
    super(msg);
  }

  public NoUniqueBeansException(Throwable e) {
    super(e);
  }

  public NoUniqueBeansException(String msg, Throwable t) {
    super(msg, t);
  }

}
