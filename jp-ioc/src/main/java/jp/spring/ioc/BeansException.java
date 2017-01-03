package jp.spring.ioc;

/**
 * Created by Administrator on 1/3/2017.
 */
public class BeansException extends RuntimeException {

    public BeansException(String msg) {
        super(msg);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BeansException)) {
            return false;
        }
        BeansException otherBe = (BeansException) other;
        return (getMessage().equals(otherBe.getMessage()));
    }

    @Override
    public int hashCode() {
        return getMessage().hashCode();
    }
}
