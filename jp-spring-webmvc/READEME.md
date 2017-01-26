webmvc 模块使用说明
`======================`
依赖：jp-ioc(核心模块)
      fastJson
      com.google.guava
      commons-io
      commons-fileupload
<hr>
首先创建一个Controller吧
```java
@Controller
public class ControllerExample {

}
```
这样就可以了，一个非常最简单的控制器，虽然什么方法都没有。
但这样没什么用吧，那我们加个方法并告诉Controller需要处理哪些请求
```java
@Controller
@RequestMapping("/example") //Class级别只支持配置一个路径
public class ControllerExample {
      
     @RequestMapping(value = "/one", method = RequestMethod.GET) 
     public String exmapleOne() {
        return "one";
     }
     
     @RequestMapping(value = "/two", method = RequestMethod.POST) 
     public String exmapleTwo() {
         return "two";
     }
}
```
方法结果处理：
 情况1：如果方法标记了@ResponseBody或者没有标记@ResponseBody并且返回值不是String类型的，都会转换成json格式返回客户端
 情况2：没有标记@ResponseBody并且返回值是String类型，那么系统就会认为这个返回值是一个页面，并将此页面返回到客户端
**Class级别的@RequestMapping不是必须的。**


**下面这样写是不允许的， @RequestMapping的value 属性不能为空**
虽然编译的时候不会出错，但如果访问 /example 会得到404
```java
@Controller
@RequestMapping("/example")
public class ControllerExample {
      
     @RequestMapping(method = RequestMethod.GET) 
     public String exmapleOne() {
        return "one";
     } 
}
```
<hr>
**获取请求中的参数**
```java
@Controller
@RequestMapping("/example")
public class ControllerExample {
      
     @RequestMapping(value = "/one", method = RequestMethod.GET) 
     public String exmapleOne(@RequestParam("abc") Integer one) {
        return "one";
     }
     
     @RequestMapping(value = "/two", method = RequestMethod.POST) 
     public String exmapleTwo() {
         return "two";
     }
}
```
如果你想获取请求中的参数可以使用 @RequestParam 来获取， 目前必须在 @RequestParam里面明确说明参数的名字
不然获取不到。

还有 @CookieValue, @RequestHeader可以让从Cookie或者Header里面获取数据，使用方法和@RequestParam一样。
<hr>
**@PatVariable的用法**
```java
@Controller
@RequestMapping("/example")
public class ControllerExample {
      
     @RequestMapping(value = "/one/{abc}", method = RequestMethod.GET) 
     public String exmapleOne(@PatVariable("abc") Integer one) {
        return "one";
     }
}
```
目前@PathVariable只能作用于Method级别的@RequestMapping, 对类级别无效。
并且要明确声明变量的名字， 否则无法识别

