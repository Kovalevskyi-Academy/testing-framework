package academy.kovalevskyi.testing.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.test.one.TestClassOne;
import academy.kovalevskyi.testing.test.one.TestClassTwo;
import academy.kovalevskyi.testing.test.three.TestClassFour;
import academy.kovalevskyi.testing.test.three.TestClassFourDuplicate;
import academy.kovalevskyi.testing.test.two.TestClassThree;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BaseComparatorTest {

  private static BaseComparator comparator;

  @BeforeAll
  public static void beforeAll() {
    comparator = new BaseComparator();
  }

  @Test
  public void testComparator() {
    var original = new ArrayList<Class<?>>();
    original.add(TestClassOne.class);
    original.add(TestClassTwo.class);
    original.add(TestClassThree.class);
    original.add(TestClassFour.class);

    var sorted = new ArrayList<>(original);
    Collections.shuffle(sorted);
    sorted.sort(comparator);

    assertArrayEquals(original.toArray(), sorted.toArray());
  }

  @Test
  public void testEquals() {
    assertEquals(0, comparator.compare(TestClassFour.class, TestClassFourDuplicate.class));
  }

  @Test
  public void testBigger() {
    assertTrue(comparator.compare(TestClassTwo.class, TestClassOne.class) > 0);
  }

  @Test
  public void testLess() {
    assertTrue(comparator.compare(TestClassOne.class, TestClassTwo.class) < 0);
  }

  @Test
  public void testException() {
    try {
      comparator.compare(BaseComparatorTest.class, BaseComparatorTest.class);
      fail();
    } catch (NotAnnotatedContainerException ignored) {
    }
  }
}