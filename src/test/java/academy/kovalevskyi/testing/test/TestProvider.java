package academy.kovalevskyi.testing.test;

import academy.kovalevskyi.testing.annotation.CourseProvider;

public class TestProvider implements CourseProvider {

  public static String NAME = "some name";
  public static String KEY = "C3PO";

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public String key() {
    return KEY;
  }
}
