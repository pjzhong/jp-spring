package jp.spring.ioc.beans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 1/9/2017.
 */
public class Autowireds {

    private final List<Autowired> autowiredList = new ArrayList<Autowired>();

    public Autowireds() {}

    public void addAutowired(Autowired autowired) {
        this.autowiredList.add(autowired);
    }

    public List<Autowired> getAutowiredList() {
        return Collections.unmodifiableList(autowiredList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for(Autowired value : autowiredList) {
            sb.append(value.getFieldName());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("\"");

        return  sb.toString();
    }
}
