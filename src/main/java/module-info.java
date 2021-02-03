module academy.kovalevskyi.testing {
  exports academy.kovalevskyi.testing;
  exports academy.kovalevskyi.testing.view;
  exports academy.kovalevskyi.testing.common;

  requires org.junit.jupiter.api;
  requires org.junit.jupiter.engine;
  requires org.junit.jupiter.params;
  requires org.junit.vintage.engine;
  requires org.junit.platform.launcher;
  requires org.mockito;
  requires com.google.common;
  requires truth;
  requires org.fusesource.jansi;
}