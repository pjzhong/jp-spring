package jp.spring;

import jp.spring.ioc.context.WebApplicationContext;
import jp.spring.web.context.DefaultWebApplicationContext;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    public static void main(String[] args) {
        String text = "redirect:/path";
        System.out.println("redirect:".length());
        System.out.println(text.substring("redirect:".length()));
    }

}
