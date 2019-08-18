package jp.spring.ioc;

/**
 * Created by Administrator on 1/3/2017.
 */
public class BeansException extends RuntimeException {

  public BeansException(String msg) {
    super(msg);
  }

  public BeansException(Throwable e) {
    super(e);
  }

  public BeansException(String msg, Throwable t) {
    super(msg, t);
  }

}
