package jp.spring.web.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RouterTest {

  private Router<String> router;

  @BeforeEach
  void beforeEach() {
    router = Router.create(25);
  }

  @AfterEach
  void afterEach() {
    router = null;
  }

  @Test
  void routeTest() {
    router.add("/", "empty");
    router.add("/foo/bar/baz", "foobardbaz");

    List<Pair<String, Map<String, String>>> routes = Collections.emptyList();

    routes = router.getDestinations("/");
    assertEquals(1, routes.size());
    assertEquals("empty", routes.get(0).getLeft());
    assertTrue(routes.get(0).getRight().isEmpty());

    routes = router.getDestinations("/foo/bar/baz");
    assertEquals(1, routes.size());
    assertEquals("foobardbaz", routes.get(0).getLeft());
    assertTrue(routes.get(0).getRight().isEmpty());
  }

  @Test
  void resetRouteTest() {
    router.add("/foo/{baz}/b", "foobard");
    router.add("/multi/{type}/{id}", "multi");

    List<Pair<String, Map<String, String>>> routes = Collections.emptyList();

    routes = router.getDestinations("/foo/bar/b");
    assertEquals(1, routes.size());
    assertEquals("foobard", routes.get(0).getLeft());
    assertEquals(Collections.singletonMap("baz", "bar"), routes.get(0).getRight());

    routes = router.getDestinations("/multi/1/123");
    assertEquals(1, routes.size());
    assertEquals("multi", routes.get(0).getLeft());
    assertEquals(2, routes.get(0).getRight().size());
    assertEquals("1", routes.get(0).getRight().get("type"));
    assertEquals("123", routes.get(0).getRight().get("id"));
  }

  @Test
  void wildCardRouteTest() {
    router.add("/wildcard/**", "wildcard");
    router.add("/multi-wildcard/**/mid/**", "multi-wildcard");

    List<Pair<String, Map<String, String>>> routes = Collections.emptyList();

    routes = router.getDestinations("/wildcard/1");
    assertEquals(1, routes.size());
    assertEquals("wildcard", routes.get(0).getLeft());
    assertTrue(routes.get(0).getRight().isEmpty());

    routes = router.getDestinations("/multi-wildcard/abc/mid/12341234");
    assertEquals(1, routes.size());
    assertEquals("multi-wildcard", routes.get(0).getLeft());
    assertTrue(routes.get(0).getRight().isEmpty());
  }

  @Test
   void resetWildcardRouteTest() {
    router.add("/group-wildcard/{abc}/**/split/**/{123}", "group-wildcard");
    router.add("**/multi-match/**/foo/{id}", "multi-match");
    router.add("/**/multi-match/**/foo/{id}", "multi-match-slash");

    List<Pair<String, Map<String, String>>> routes = Collections.emptyList();

    routes = router.getDestinations("/group-wildcard/test1/split/test2/test3/test4");
    assertEquals(0, routes.size());

    routes = router.getDestinations("/group-wildcard/test1/test2/split/test3/test4");
    assertEquals(1, routes.size());
    assertEquals("group-wildcard", routes.get(0).getLeft());
    assertEquals(2, routes.get(0).getRight().size());
    assertEquals("test1", routes.get(0).getRight().get("abc"));
    assertEquals("test4", routes.get(0).getRight().get("123"));

    routes = router.getDestinations("none-slash/multi-match/1/foo/2");
    assertEquals(1, routes.size());
    assertEquals("multi-match", routes.get(0).getLeft());
    assertEquals(Collections.singletonMap("id", "2"), routes.get(0).getRight());

    routes = router.getDestinations("/none-slash/multi-match/1/foo/2");
    assertEquals(2, routes.size());
    assertEquals(new HashSet<>(Arrays.asList("multi-match", "multi-match-slash")),
        new HashSet<>(Arrays.asList(routes.get(0).getLeft(), routes.get(1).getLeft())));
    assertEquals(Collections.singletonMap("id", "2"), routes.get(0).getRight());
    assertEquals(Collections.singletonMap("id", "2"), routes.get(1).getRight());
  }

}
