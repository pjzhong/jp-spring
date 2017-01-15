启发项目:
  https://github.com/code4craft/tiny-spring
  https://github.com/menyouping/jw

这是一个模仿Spring的MVC框架。

1. 基本使用方法

 <context-param>目前必须配置，
      <param-name>:暂时固定为：contextConfigLocation
     <param-value>:你的配置目前在哪里。（“/”表示从根目录开始）
 <context-param>

目前已经实现了：
@Autowired, @PathVarable, @RequestMapping,@RequestParam,@CookieValue。
