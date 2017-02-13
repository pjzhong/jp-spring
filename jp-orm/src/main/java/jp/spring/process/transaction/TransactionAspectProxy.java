package jp.spring.process.transaction;

import jp.spring.aop.BaseAspect;
import jp.spring.aop.Pointcut;
import jp.spring.aop.Proxy;
import jp.spring.aop.support.ProxyChain;
import jp.spring.ioc.stereotype.Component;
import jp.spring.orm.ActiveRecord;

/**
 * Created by Administrator on 2/12/2017.
 */
public class TransactionAspectProxy extends BaseAspect implements Proxy {

    private Pointcut transactionPointcut;

    private static final ThreadLocal<Boolean> localFlag = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public TransactionAspectProxy() {
        transactionPointcut = new TransactionPointcut();
    }

    @Override
    public void doProxy(ProxyChain proxyChain) {
        boolean flag = localFlag.get();
        boolean match = getPointcut().match(proxyChain.getTarget().getTargetMethod());
        if( !flag && match ) {
            try {
                //Begin transaction
                ActiveRecord.beginTransaction();

                //Run target Method
                proxyChain.doProxyChain();

                // Commit transaction
                ActiveRecord.commitTransaction();
            } catch (Exception e) {
                //Rollback transaction
                ActiveRecord.rollbackTransaction();
            } finally {
                localFlag.remove();
            }
        } else {
            proxyChain.doProxyChain();
        }
    }

    @Override
    public Pointcut getPointcut() {
        return this.transactionPointcut;
    }
}
