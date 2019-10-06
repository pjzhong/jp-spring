package jp.spring.web.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @link https://github.com/cdapio/netty-http/blob/develop/src/main/java/io/cdap/http/internal/RequestRouter.java
 * @since 2019年04月17日 17:51:31
 **/
public class Router<T> {


  // non-greedy wild card match.
  public static final Pattern WILD_CARD_PATTERN = Pattern.compile("\\*\\*");
  // for remove duplicated '/'
  private static final Pattern CLEAN_PATH = Pattern.compile("/+");

  //GROUP_PATTERN is used for named wild card pattern in paths which is specified within braces.
  //Example: {id}
  static final Pattern GROUP_PATTERN = Pattern.compile("\\{(.*?)}");

  private final int maxPathParts;
  private List<Destination<T>> patternRouteList;

  static <T> Router<T> create(int maxParts) {
    return new Router<>(maxParts);
  }


  private Router(int maxPathParts) {
    this.maxPathParts = maxPathParts;
    this.patternRouteList = new ArrayList<>();
  }

  /**
   * Add a source and destination.
   *
   * @param source Source path to be routed. Routed path can have named wild-card pattern with
   * braces "{}".
   * @param destination Destination of the path.
   */
  public void add(final String source, final T destination) {
    String path = cleanPath(source);

    String[] parts = path.split("/", maxPathParts + 2);
    if (maxPathParts < parts.length - 1) {
      throw new IllegalArgumentException(
          String.format("Number of parts of path %s exceeds allowed limit %s",
              source, maxPathParts));
    }

    StringBuilder sb = new StringBuilder();
    List<String> groupNames = new ArrayList<>();

    for (String part : parts) {
      Matcher groupMatcher = GROUP_PATTERN.matcher(part);
      if (groupMatcher.matches()) {
        //{id} -> id
        groupNames.add(groupMatcher.group(1));
        sb.append("([^/]+?)");//catch any character except '/'
      } else if (WILD_CARD_PATTERN.matcher(part).matches()) {
        sb.append(".*?");
      } else {
        sb.append(part);
      }
      sb.append("/");
    }

    //Ignore the last "/"
    sb.setLength(sb.length() - 1);

    Pattern pattern = Pattern.compile(sb.toString());
    // if names is Empty, replace it with emptyList(for memory usage)
    groupNames = groupNames.isEmpty() ? Collections.emptyList() : groupNames;
    patternRouteList.add(new Destination<>(pattern, groupNames, destination));
  }

  /**
   * @param path URI path
   * @return [TARGET, GROUP_NAME<KEY, VALUE>]
   * @since 2019年04月17日 16:58:26
   */
  public List<Route<T>> getDestinations(String path) {
    String cleanPath = cleanPath(path);

    List<Route<T>> result = new ArrayList<>();
    patternRouteList.forEach(d -> {
      Matcher matcher = d.getPattern().matcher(cleanPath);
      if (matcher.matches()) {
        result.add(new Route<>(d, matcher));
      }
    });
    return result;
  }

  public static String cleanPath(String path) {
    path = CLEAN_PATH.matcher(path).replaceAll("/");
    path =
        (path.endsWith("/") && path.length() > 1) ? path.substring(0, path.length() - 1) : path;
    return path;
  }

  /**
   * 路由目的地
   *
   * @author ZJP
   * @since 2019年10月06日 15:29:37
   **/
  private static class Destination<T> {

    private Pattern pattern;
    /** 路径参数名 */
    private List<String> names;
    /** 目的地 */
    private T target;

    Destination(Pattern pattern, List<String> names, T target) {
      this.pattern = pattern;
      this.names = names;
      this.target = target;
    }

    public Pattern getPattern() {
      return pattern;
    }

    public List<String> getNames() {
      return names;
    }

    public T getTarget() {
      return target;
    }
  }


  /**
   * 路由结果
   *
   * @author ZJP
   * @since 2019年10月06日 15:29:29
   **/
  public static class Route<T> {

    private Destination<T> destination;
    private Matcher matcher;
    private Map<String, String> pathParams;

    private Route(Destination<T> d, Matcher matcher) {
      this.destination = d;
      this.matcher = matcher;
    }

    public Map<String, String> getPathParams() {
      if (pathParams == null) {
        resolverParams();
      }

      return pathParams;
    }

    public T getTarget() {
      return destination.getTarget();
    }

    private void resolverParams() {
      List<String> names = destination.getNames();
      Map<String, String> nameValues =
          names.isEmpty() ? Collections.emptyMap() : new HashMap<>();
      int matchIdx = 1;
      for (String name : names) {
        nameValues.put(name, matcher.group(matchIdx));
        matchIdx++;
      }
      this.pathParams = nameValues;
    }

  }
}
