package jp.spring.web.handler;

import static jp.spring.util.TypeUtil.resolveName;
import static jp.spring.web.annotation.RequestMethod.GET;
import static jp.spring.web.annotation.RequestMethod.POST;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jp.spring.web.annotation.Controller;
import jp.spring.web.annotation.RequestMapping;
import jp.spring.web.annotation.RequestMethod;
import jp.spring.web.annotation.ResponseBody;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;

public class HandlerBuilderTest {

  private Map<String, Handler> toMap(List<Handler> hs) {
    return hs.stream().collect(Collectors.toMap(Handler::getUrl, h -> h));
  }

  @Test
  void notAController() {
    Class<?> arrayList = ArrayList.class;
    assertTrue(HandlerBuilder.buildHandler(resolveName(arrayList), arrayList).isEmpty());
  }

  @Controller
  public static class EmptyController {

  }

  @Test
  void emptyController() {
    Class<?> empty = EmptyController.class;
    assertTrue(HandlerBuilder.buildHandler(resolveName(empty), empty).isEmpty());
  }

  @Controller
  public static class SimpleController {

    @RequestMapping
    public void hello() {
    }

    public void noHandler() {
    }

    @RequestMapping
    private void privateHandler() {
    }
  }

  @Test
  void simpleControllerTest() {
    Class<?> type = SimpleController.class;
    Map<String, Handler> handlers = toMap(HandlerBuilder.buildHandler(resolveName(type), type));

    assertEquals(1, handlers.size());
    Handler h = handlers.get("/hello");
    assertNotNull(h);

    RequestMethod[] methods = new RequestMethod[]{GET};
    assertThat(methods, Is.is(h.getHttpMethods()));
  }

  @Controller
  @RequestMapping("index")
  public static class IndexController {

    @RequestMapping(method = {GET, POST})
    public void hello() {

    }

    @RequestMapping(value = "//world")
    public void handler() {

    }
  }

  @Test
  void indexControllerTest() {
    Class<?> type = IndexController.class;
    Map<String, Handler> handlers = toMap(HandlerBuilder.buildHandler(resolveName(type), type));

    assertEquals(2, handlers.size());

    Handler world = handlers.get("/index/world");
    assertNotNull(world);
    assertThat(new RequestMethod[]{GET}, Is.is(world.getHttpMethods()));

    Handler hello = handlers.get("/index/hello");
    assertNotNull(hello);
    assertThat(new RequestMethod[]{GET, POST}, Is.is(hello.getHttpMethods()));
  }

  public static class ParentController {

    @RequestMapping
    @ResponseBody
    public void hello() {

    }
  }

  @Controller
  public static class ChildController extends ParentController {

    @RequestMapping
    public void world() {

    }
  }

  @Test
  void inheritedControllerTest() {
    Class<?> type = ChildController.class;
    Map<String, Handler> handlers = toMap(HandlerBuilder.buildHandler(resolveName(type), type));

    assertEquals(2, handlers.size());

    Handler world = handlers.get("/world");
    assertNotNull(world);
    assertThat(new RequestMethod[]{GET}, Is.is(world.getHttpMethods()));

    Handler hello = handlers.get("/hello");
    assertNotNull(hello);
    assertThat(new RequestMethod[]{GET}, Is.is(hello.getHttpMethods()));
  }


}
