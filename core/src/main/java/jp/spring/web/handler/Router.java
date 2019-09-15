package jp.spring.web.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @link https://github.com/cdapio/netty-http/blob/develop/src/main/java/io/cdap/http/internal/RequestRouter.java
 * @since 2019年04月17日 17:51:31
 **/
public class Router<T> {

  //GROUP_PATTERN is used for named wild card pattern in paths which is specified within braces.
  //Example: {id}
  public static final Pattern GROUP_PATTERN = Pattern.compile("\\{(.*?)\\}");

  // non-greedy wild card match.
  public static final Pattern WILD_CARD_PATTERN = Pattern.compile("\\*\\*");
  // for remove duplicated '/'
  public static final Pattern CLEAN_PATH = Pattern.compile("/+");

  private final int maxPathParts;
  private List<Destination> patternRouteList;

  public static <T> Router<T> create(int maxParts) {
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
    String path = CLEAN_PATH.matcher(source).replaceAll("/");

    path = (path.endsWith("/") && path.length() > 1) ? path.substring(0, path.length() - 1) : path;

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
    patternRouteList.add(new Destination(pattern, groupNames, destination));
  }

  /**
   * @param path URI path
   * @return [TARGET, GROUP_NAME<KEY, VALUE>]
   * @since 2019年04月17日 16:58:26
   */
  public List<Pair<T, Map<String, String>>> getDestinations(String path) {
    String cleanPath =
        (path.endsWith("/") && path.length() > 1) ? path.substring(0, path.length() - 1) : path;

    List<Pair<T, Map<String, String>>> result = new ArrayList<>();
    patternRouteList.forEach(d -> {
      Matcher matcher = d.getPattern().matcher(cleanPath);
      List<String> groupName = d.getNames();
      if (matcher.matches()) {
        Map<String, String> nameValues =
            groupName.isEmpty() ? Collections.emptyMap() : new HashMap<>();
        int matchIdx = 1;
        for (String name : groupName) {
          nameValues.put(name, matcher.group(matchIdx));
          matchIdx++;
        }
        result.add(Pair.of(d.getTarget(), nameValues));
      }
    });

    return result;
  }

  private class Destination {

    private Pattern pattern;
    private List<String> names;
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
}
