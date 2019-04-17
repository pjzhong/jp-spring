package jp.spring.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import jp.spring.mvc.handler.Pair;
import jp.spring.mvc.handler.PathRouter;
import org.junit.Assert;
import org.junit.Test;

public class RequestRouterTest {

  @Test
  public void testPathRouting() {
    PathRouter<String> pathRouter = PathRouter.create(25);
    pathRouter.add("/", "empty");

    pathRouter.add("/foo/{baz}/b", "foobard");

    pathRouter.add("/foo/bar/baz", "foobardbaz");

    pathRouter.add("/multi/{type}/{id}", "multi");

    pathRouter.add("/wildcard/**", "wildcard");

    pathRouter.add("/multi-wildcard/**/mid/**", "multi-wildcard");

    pathRouter.add("/group-wildcard/{abc}/**/split/**/{123}", "group-wildcard");

    pathRouter.add("**/multi-match/**/foo/{id}", "multi-match");
    pathRouter.add("/**/multi-match/**/foo/{id}", "multi-match-slash");

    List<Pair<String, Map<String, String>>> routes = Collections.emptyList();

    routes = pathRouter.getDestinations("/");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("empty", routes.get(0).getFirst());
    Assert.assertTrue(routes.get(0).getSecond().isEmpty());

    routes = pathRouter.getDestinations("/foo/bar/baz");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("foobardbaz", routes.get(0).getFirst());
    Assert.assertTrue(routes.get(0).getSecond().isEmpty());

    routes = pathRouter.getDestinations("/foo/bar/b");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("foobard", routes.get(0).getFirst());
    Assert.assertEquals(Collections.singletonMap("baz", "bar"), routes.get(0).getSecond());

    routes = pathRouter.getDestinations("/multi/1/123");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("multi", routes.get(0).getFirst());
    Assert.assertEquals(2, routes.get(0).getSecond().size());
    Assert.assertEquals("1", routes.get(0).getSecond().get("type"));
    Assert.assertEquals("123", routes.get(0).getSecond().get("id"));

    routes = pathRouter.getDestinations("/wildcard/1");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("wildcard", routes.get(0).getFirst());
    Assert.assertTrue(routes.get(0).getSecond().isEmpty());

    routes = pathRouter.getDestinations("/multi-wildcard/abc/mid/12341234");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("multi-wildcard", routes.get(0).getFirst());
    Assert.assertTrue(routes.get(0).getSecond().isEmpty());

    routes = pathRouter.getDestinations("/group-wildcard/test1/split/test2/test3/test4");
    Assert.assertEquals(0, routes.size());

    routes = pathRouter.getDestinations("/group-wildcard/test1/test2/split/test3/test4");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("group-wildcard", routes.get(0).getFirst());
    Assert.assertEquals(2, routes.get(0).getSecond().size());
    Assert.assertEquals("test1", routes.get(0).getSecond().get("abc"));
    Assert.assertEquals("test4", routes.get(0).getSecond().get("123"));

    routes = pathRouter.getDestinations("none-slash/multi-match/1/foo/2");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("multi-match", routes.get(0).getFirst());
    Assert.assertEquals(Collections.singletonMap("id", "2"), routes.get(0).getSecond());


    routes = pathRouter.getDestinations("/none-slash/multi-match/1/foo/2");
    Assert.assertEquals(2, routes.size());
    Assert.assertEquals(new HashSet<>(Arrays.asList("multi-match", "multi-match-slash")),
        new HashSet<>(Arrays.asList(routes.get(0).getFirst(), routes.get(1).getFirst())));
    Assert.assertEquals(Collections.singletonMap("id", "2"), routes.get(0).getSecond());
    Assert.assertEquals(Collections.singletonMap("id", "2"), routes.get(1).getSecond());

  }

}
