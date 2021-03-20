package academy.kovalevskyi.testing.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.kovalevskyi.testing.service.ContainerHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

public class ContainerTest {

  @Test
  public void testTypeIsAnnotation() {
    assertTrue(Container.class.isAnnotation());
  }

  @Test
  public void testAnnotationConfiguration() {
    var retention = Container.class.getAnnotation(Retention.class);
    var target = Container.class.getAnnotation(Target.class);
    var extendWith = Container.class.getAnnotation(ExtendWith.class);

    assertNotNull(retention);
    assertNotNull(target);
    assertNotNull(extendWith);
    assertEquals(RetentionPolicy.RUNTIME, retention.value());
    assertEquals(1, target.value().length);
    assertEquals(ElementType.TYPE, target.value()[0]);
    assertEquals(1, extendWith.value().length);
    assertEquals(ContainerHandler.class, extendWith.value()[0]);
  }

}
