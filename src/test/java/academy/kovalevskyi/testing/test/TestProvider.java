package academy.kovalevskyi.testing.test;

import academy.kovalevskyi.testing.annotation.CourseProvider;

public class TestProvider implements CourseProvider {

  public static final String KEY = "C3PO";

  @Override
  public String name() {
    return "some name";
  }

  @Override
  public String key() {
    return KEY;
  }
}
