package jp.spring.web.handler;

import java.util.function.Function;

/**
 * 根据 @code HandlerArgResolver 提供的数据进行参转换
 *
 * @author ZJP
 * @since 2019年04月23日 11:01:46
 **/
public interface Filler<T> extends Function<HandlerArgResolver, T> {

}
