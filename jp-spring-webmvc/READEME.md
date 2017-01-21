webmvc 模块使用说明
`======================`
依赖：jp-ioc(核心模块)

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
@RequestMapping("/example")
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

