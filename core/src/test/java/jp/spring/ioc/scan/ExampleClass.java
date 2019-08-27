package jp.spring.ioc.scan;

import java.io.IOException;
import java.util.ArrayList;

@ExampleAnnotation
public class ExampleClass extends ArrayList implements ExampleInterface {

  @Override
  public void close() throws IOException {

  }

  @Override
  public void run() {

  }
}
