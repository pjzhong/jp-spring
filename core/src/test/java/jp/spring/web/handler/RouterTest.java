package jp.spring.web.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import jp.spring.web.handler.Router.Route;
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
  void noMapRouteTest() {
    List<Route<String>> routes = router.getDestinations("/");
    assertTrue(routes.isEmpty());
  }

  @Test
  void cleanPathTest() {
    router.add("////clean/////path/////", "cleanPath");

    List<Route<String>> routes = Collections.emptyList();

    routes = router.getDestinations("//////clean//////////path");
    assertEquals(1, routes.size());
    assertEquals("cleanPath", routes.get(0).getTarget());
    assertTrue(routes.get(0).getPathParams().isEmpty());

    routes = router.getDestinations("/clean/path");
    assertEquals(1, routes.size());
    assertEquals("cleanPath", routes.get(0).getTarget());
    assertTrue(routes.get(0).getPathParams().isEmpty());

  }


  @Test
  void routeTest() {
    router.add("/", "empty");
    router.add("/foo/bar/baz", "foobardbaz");

    List<Route<String>> routes = Collections.emptyList();

    routes = router.getDestinations("/");
    assertEquals(1, routes.size());
    assertEquals("empty", routes.get(0).getTarget());
    assertTrue(routes.get(0).getPathParams().isEmpty());

    routes = router.getDestinations("/foo/bar/baz");
    assertEquals(1, routes.size());
    assertEquals("foobardbaz", routes.get(0).getTarget());
    assertTrue(routes.get(0).getPathParams().isEmpty());
  }


  @Test
  void resetRouteTest() {
    router.add("/foo/{baz}/b", "foobard");
    router.add("/multi/{type}/{id}", "multi");

    List<Route<String>> routes = Collections.emptyList();

    routes = router.getDestinations("/foo/bar/b");
    assertEquals(1, routes.size());
    assertEquals("foobard", routes.get(0).getTarget());
    assertEquals(Collections.singletonMap("baz", "bar"), routes.get(0).getPathParams());

    routes = router.getDestinations("/multi/1/123");
    assertEquals(1, routes.size());
    assertEquals("multi", routes.get(0).getTarget());
    assertEquals(2, routes.get(0).getPathParams().size());
    assertEquals("1", routes.get(0).getPathParams().get("type"));
    assertEquals("123", routes.get(0).getPathParams().get("id"));
  }

  @Test
  void wildCardRouteTest() {
    router.add("/wildcard/**", "wildcard");
    router.add("/multi-wildcard/**/mid/**", "multi-wildcard");

    List<Route<String>> routes = Collections.emptyList();

    routes = router.getDestinations("/wildcard/1");
    assertEquals(1, routes.size());
    assertEquals("wildcard", routes.get(0).getTarget());
    assertTrue(routes.get(0).getPathParams().isEmpty());

    routes = router.getDestinations("/multi-wildcard/abc/mid/12341234");
    assertEquals(1, routes.size());
    assertEquals("multi-wildcard", routes.get(0).getTarget());
    assertTrue(routes.get(0).getPathParams().isEmpty());
  }

  @Test
  void resetWildcardRouteTest() {
    router.add("/group-wildcard/{abc}/**/split/**/{123}", "group-wildcard");
    router.add("**/multi-match/**/foo/{id}", "multi-match");
    router.add("/**/multi-match/**/foo/{id}", "multi-match-slash");

    List<Route<String>> routes = Collections.emptyList();

    routes = router.getDestinations("/group-wildcard/test1/split/test2/test3/test4");
    assertEquals(0, routes.size());

    routes = router.getDestinations("/group-wildcard/test1/test2/split/test3/test4");
    assertEquals(1, routes.size());
    assertEquals("group-wildcard", routes.get(0).getTarget());
    assertEquals(2, routes.get(0).getPathParams().size());
    assertEquals("test1", routes.get(0).getPathParams().get("abc"));
    assertEquals("test4", routes.get(0).getPathParams().get("123"));

    routes = router.getDestinations("none-slash/multi-match/1/foo/2");
    assertEquals(1, routes.size());
    assertEquals("multi-match", routes.get(0).getTarget());
    assertEquals(Collections.singletonMap("id", "2"), routes.get(0).getPathParams());

    routes = router.getDestinations("/none-slash/multi-match/1/foo/2");
    assertEquals(2, routes.size());
    assertEquals(new HashSet<>(Arrays.asList("multi-match", "multi-match-slash")),
        new HashSet<>(Arrays.asList(routes.get(0).getTarget(), routes.get(1).getTarget())));
    assertEquals(Collections.singletonMap("id", "2"), routes.get(0).getPathParams());
    assertEquals(Collections.singletonMap("id", "2"), routes.get(1).getPathParams());
  }

}
