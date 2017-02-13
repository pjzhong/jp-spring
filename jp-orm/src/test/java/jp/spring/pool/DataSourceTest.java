package jp.spring.pool;

import jp.spring.ioc.util.JpUtils;
import jp.spring.model.Blog;
import jp.spring.orm.ActiveRecord;
import jp.spring.orm.annotation.Entity;
import jp.spring.orm.pool.DataSourceProvider;
import jp.spring.orm.pool.impl.DefaultDataSourceProvider;
import org.junit.Test;

/**
 * Created by Administrator on 2/7/2017.
 */
public class DataSourceTest {

    @Test
    public void test() throws Exception {
        DataSourceProvider provider =
                new DefaultDataSourceProvider(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://localhost:3306/jfinal_demo?characterEncoding=utf8&allowMultiQueries=true",
                        "root",
                        "zjp19950321+"
                );

        ActiveRecord ar = new ActiveRecord(provider);
        ActiveRecord.init(
                "jdbc:mysql://localhost:3306/jfinal_demo?characterEncoding=utf8&allowMultiQueries=true",
                "com.mysql.jdbc.Driver",
                "root",
                "zjp19950321+"
        );

        ar.addMapping("blog", "id", Blog.class);
        ar.start();


        Blog test = new Blog();

        test.set("title", "zjp");
        test.set("content", "zjp");
        test.save();
        ActiveRecord.rollbackTransaction();
    }

    @Test
    public void test2() {
        System.out.println(JpUtils.isAnnotated(Blog.class, Entity.class));
    }
}
