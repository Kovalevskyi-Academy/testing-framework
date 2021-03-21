package academy.kovalevskyi.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ConsoleCaptorTest {

  private static ByteArrayOutputStream buffer;
  private static ByteArrayOutputStream testedBuffer;
  private static PrintStream printStream;

  @BeforeAll
  public static void beforeAll() {
    buffer = new ByteArrayOutputStream();
    testedBuffer = new ByteArrayOutputStream();
    printStream = new PrintStream(testedBuffer);
  }

  @AfterAll
  public static void afterAll() {
    try {
      buffer.close();
      printStream.close();
    } catch (IOException ignored) {
    }
  }

  @AfterEach
  public void tearDown() {
    testedBuffer.reset();
  }

  @Test
  public void testCaptorCanPrint() throws InterruptedException {
    var captor = new ConsoleCaptor(buffer, printStream);
    captor.start();
    var text = "some text";
    var expected = colorText(text);
    buffer.writeBytes(text.getBytes());
    Thread.sleep(100);

    assertEquals(expected, testedBuffer.toString());

    captor.terminate();
    captor.join();
  }

  @Test
  public void testDaemonThread() throws InterruptedException {
    var captor = new ConsoleCaptor(buffer, printStream);
    captor.start();
    Thread.sleep(100);
    assertTrue(captor.isDaemon());
    captor.terminate();
    captor.join();
  }

  @Test
  public void testStartThread() throws InterruptedException {
    var captor = new ConsoleCaptor(buffer, printStream);
    captor.start();
    Thread.sleep(100);
    assertTrue(captor.isAlive());
    captor.terminate();
    captor.join();
  }

  @Test
  public void testStopThread() throws InterruptedException {
    var captor = new ConsoleCaptor(buffer, printStream);
    captor.start();
    Thread.sleep(100);
    captor.terminate();
    captor.join();

    assertFalse(captor.isAlive());
  }

  @Test
  public void testNewEntry() throws InterruptedException {
    var captor = new ConsoleCaptor(buffer, printStream);
    captor.start();
    captor.newEntry();
    var text = "some text";
    buffer.writeBytes(text.getBytes());
    Thread.sleep(100);

    assertEquals(String.format(":%n%s", colorText(text)), testedBuffer.toString());

    captor.terminate();
    captor.join();
  }

  @Test
  public void testCaptorResetsBuffer() throws InterruptedException {
    var captor = new ConsoleCaptor(buffer, printStream);
    captor.start();
    buffer.write(1);
    Thread.sleep(100);

    assertTrue(buffer.toString().isEmpty());

    captor.terminate();
    captor.join();
  }

  private String colorText(final String text) {
    return Ansi.ansi().fg(Color.MAGENTA).a(text).reset().toString();
  }
}