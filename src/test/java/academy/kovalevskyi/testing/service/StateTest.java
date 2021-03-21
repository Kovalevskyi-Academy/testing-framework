package academy.kovalevskyi.testing.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class StateTest {

  private static State[] enums;

  @BeforeAll
  public static void beforeAll() {
    enums = State.values();
  }

  @Test
  public void testStatusNotNullAndNotEmpty() {
    for (var state : enums) {
      assertNotNull(state.status);
      assertFalse(state.status.isBlank());
    }
  }

  @Test
  public void testColorNotNull() {
    for (var state : enums) {
      assertNotNull(state.color);
    }
  }
}