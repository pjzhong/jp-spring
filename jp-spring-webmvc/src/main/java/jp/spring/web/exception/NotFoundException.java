package jp.spring.web.exception;

/**
 * Created by Administrator on 1/13/2017.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String msg) {
        super(msg);
    }
}
