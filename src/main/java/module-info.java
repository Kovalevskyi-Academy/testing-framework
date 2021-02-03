module academy.kovalevskyi.testing {

  exports academy.kovalevskyi.testing;
  exports academy.kovalevskyi.testing.util;
  exports academy.kovalevskyi.testing.view;
  exports academy.kovalevskyi.testing.common;
  exports academy.kovalevskyi.testing.annotation;

  requires org.junit.jupiter.api;
  requires org.junit.jupiter.engine;
  requires org.junit.jupiter.params;
  requires org.junit.vintage.engine;
  requires org.junit.platform.launcher;
  requires org.mockito;
  requires org.fusesource.jansi;
  requires com.google.common;
  requires reflections;
}