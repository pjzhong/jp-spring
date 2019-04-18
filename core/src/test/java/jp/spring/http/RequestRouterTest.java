package jp.spring.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import jp.spring.mvc.handler.Pair;
import jp.spring.mvc.handler.Router;
import org.junit.Assert;
import org.junit.Test;

public class RequestRouterTest {

  @Test
  public void testPathRouting() {
    Router<String> router = Router.create(25);
    router.add("/", "empty");

    router.add("/foo/{baz}/b", "foobard");

    router.add("/foo/bar/baz", "foobardbaz");

    router.add("/multi/{type}/{id}", "multi");

    router.add("/wildcard/**", "wildcard");

    router.add("/multi-wildcard/**/mid/**", "multi-wildcard");

    router.add("/group-wildcard/{abc}/**/split/**/{123}", "group-wildcard");

    router.add("**/multi-match/**/foo/{id}", "multi-match");
    router.add("/**/multi-match/**/foo/{id}", "multi-match-slash");

    List<Pair<String, Map<String, String>>> routes = Collections.emptyList();

    routes = router.getDestinations("/");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("empty", routes.get(0).getFirst());
    Assert.assertTrue(routes.get(0).getSecond().isEmpty());

    routes = router.getDestinations("/foo/bar/baz");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("foobardbaz", routes.get(0).getFirst());
    Assert.assertTrue(routes.get(0).getSecond().isEmpty());

    routes = router.getDestinations("/foo/bar/b");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("foobard", routes.get(0).getFirst());
    Assert.assertEquals(Collections.singletonMap("baz", "bar"), routes.get(0).getSecond());

    routes = router.getDestinations("/multi/1/123");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("multi", routes.get(0).getFirst());
    Assert.assertEquals(2, routes.get(0).getSecond().size());
    Assert.assertEquals("1", routes.get(0).getSecond().get("type"));
    Assert.assertEquals("123", routes.get(0).getSecond().get("id"));

    routes = router.getDestinations("/wildcard/1");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("wildcard", routes.get(0).getFirst());
    Assert.assertTrue(routes.get(0).getSecond().isEmpty());

    routes = router.getDestinations("/multi-wildcard/abc/mid/12341234");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("multi-wildcard", routes.get(0).getFirst());
    Assert.assertTrue(routes.get(0).getSecond().isEmpty());

    routes = router.getDestinations("/group-wildcard/test1/split/test2/test3/test4");
    Assert.assertEquals(0, routes.size());

    routes = router.getDestinations("/group-wildcard/test1/test2/split/test3/test4");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("group-wildcard", routes.get(0).getFirst());
    Assert.assertEquals(2, routes.get(0).getSecond().size());
    Assert.assertEquals("test1", routes.get(0).getSecond().get("abc"));
    Assert.assertEquals("test4", routes.get(0).getSecond().get("123"));

    routes = router.getDestinations("none-slash/multi-match/1/foo/2");
    Assert.assertEquals(1, routes.size());
    Assert.assertEquals("multi-match", routes.get(0).getFirst());
    Assert.assertEquals(Collections.singletonMap("id", "2"), routes.get(0).getSecond());


    routes = router.getDestinations("/none-slash/multi-match/1/foo/2");
    Assert.assertEquals(2, routes.size());
    Assert.assertEquals(new HashSet<>(Arrays.asList("multi-match", "multi-match-slash")),
        new HashSet<>(Arrays.asList(routes.get(0).getFirst(), routes.get(1).getFirst())));
    Assert.assertEquals(Collections.singletonMap("id", "2"), routes.get(0).getSecond());
    Assert.assertEquals(Collections.singletonMap("id", "2"), routes.get(1).getSecond());

  }

}
