package jp.spring.ioc.beans.support;


import java.util.ArrayList;
import java.util.List;

/**
 * 包装一个对象所有的PropertyValue。<br/>
 * 为什么封装而不是直接用List?因为可以封装一些操作。
 * @author yihua.huang@dianping.com
 *
 * 2016-12-25
 * @author pj_zhong@163.com
 * 为什么不用Map存储，而用list呢，目的何在？
 */
public class PropertyValues {

    private final List<PropertyValue> propertyValueList = new ArrayList<PropertyValue>();

    public PropertyValues() {
    }

    public void addPropertyValue(PropertyValue pv) {
        //TODO:这里可以对于重复propertyName进行判断，直接用list没法做到
        this.propertyValueList.add(pv);
    }

    public List<PropertyValue> getPropertyValues() {
        return this.propertyValueList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for(PropertyValue value : propertyValueList) {
            sb.append(value.getName());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\"");

        return  sb.toString();
    }
}
