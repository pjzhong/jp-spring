package jp.spring.model;

import jp.spring.orm.base.BaseBean;

/**
 * Created by Administrator on 1/17/2017.
 */
public class BaseEntity extends BaseBean {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
