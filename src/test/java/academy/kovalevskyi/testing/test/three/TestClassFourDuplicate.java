package academy.kovalevskyi.testing.test.three;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;

@Container(course = TestProvider.class, week = 2, day = 2, id = 0)
public class TestClassFourDuplicate {

  public static final File TEST_FILE = new File(String.format(
      "%s%starget%2$sFileForTestOnlyAndCanBeSafelyDeleted",
      System.getProperty("user.dir"),
      File.separator));

  @Test
  public void name() throws IOException {
    if (TEST_FILE.exists()) {
      if (!TEST_FILE.delete()) {
        throw new IOException();
      }
    }
    if (!TEST_FILE.createNewFile()) {
      throw new IOException();
    }
  }

}
