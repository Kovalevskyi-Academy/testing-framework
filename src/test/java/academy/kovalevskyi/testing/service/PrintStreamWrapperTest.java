package academy.kovalevskyi.testing.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrintStreamWrapperTest {

  private static final String TEMPLATE = ":\n";
  private static final ByteArrayOutputStream BUFFER = new ByteArrayOutputStream();
  private static final PrintStream STREAM = new PrintStream(BUFFER);
  private PrintStreamWrapper wrapper;

  @AfterAll
  public static void afterAll() {
    STREAM.close();
  }

  @BeforeEach
  public void setUp() {
    wrapper = new PrintStreamWrapper(STREAM);
  }

  @AfterEach
  public void tearDown() {
    BUFFER.reset();
    wrapper.destroy();
  }

  @Test
  public void testHasContent() {
    wrapper.print(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());

    wrapper.enable();
    assertFalse(wrapper.hasContent());
    wrapper.print(Integer.MAX_VALUE);
    assertTrue(wrapper.hasContent());

    wrapper.destroy();
    assertFalse(wrapper.hasContent());
  }

  @Test
  public void testCheckError() {
    var mock = mock(PrintStream.class);
    var streamWrapper = new PrintStreamWrapper(mock);

    streamWrapper.checkError();
    verify(mock).checkError();
    reset(mock);

    streamWrapper.destroy();
    assertFalse(streamWrapper.checkError());
    verify(mock, never()).checkError();
  }

  @Test
  public void testClose() {
    var mock = mock(PrintStream.class);
    var streamWrapper = new PrintStreamWrapper(mock);

    assertThrows(UnsupportedOperationException.class, streamWrapper::close);
    verify(mock, never()).close();

    streamWrapper.destroy();
    assertDoesNotThrow(streamWrapper::close);
  }

  @Test
  public void testFlush() {
    var mock = mock(PrintStream.class);
    var streamWrapper = new PrintStreamWrapper(mock);

    streamWrapper.flush();
    verify(mock).flush();
    assertFalse(streamWrapper.hasContent());
    reset(mock);

    streamWrapper.enable();
    streamWrapper.flush();
    verify(mock).flush();
    assertFalse(streamWrapper.hasContent());
    reset(mock);

    streamWrapper.disable();
    streamWrapper.flush();
    verify(mock).flush();
    assertFalse(streamWrapper.hasContent());

    streamWrapper.destroy();
    reset(mock);
    assertDoesNotThrow(streamWrapper::flush);
    verify(mock, never()).flush();
  }

  @Test
  public void testWriteByte() {
    var arg = 10;
    var expected = String.format("%s\n", TEMPLATE);

    wrapper.write(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.write(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.write(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.write(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testWriteByteBuffer() {
    var bytes = new byte[]{33};

    wrapper.write(bytes, 0, bytes.length);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.write(bytes, 0, bytes.length);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", TEMPLATE, new String(bytes)), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", TEMPLATE, new String(bytes)), BUFFER.toString());

    BUFFER.reset();
    wrapper.write(bytes, 0, bytes.length);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.write(bytes, 0, bytes.length));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintBoolean() {
    wrapper.print(true);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(true);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%b", TEMPLATE, true), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%b\n", TEMPLATE, true), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(true);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(true));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintChar() {
    var arg = '!';

    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%c", TEMPLATE, arg), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%c\n", TEMPLATE, arg), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintInt() {
    wrapper.print(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(Integer.MAX_VALUE);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%d", TEMPLATE, Integer.MAX_VALUE), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%d\n", TEMPLATE, Integer.MAX_VALUE), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(Integer.MAX_VALUE));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintLong() {
    wrapper.print(Long.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(Long.MAX_VALUE);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%d", TEMPLATE, Long.MAX_VALUE), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%d\n", TEMPLATE, Long.MAX_VALUE), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(Long.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(Long.MAX_VALUE));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintFloat() {
    var arg = 1F;

    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%.1f", TEMPLATE, arg), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%.1f\n", TEMPLATE, arg), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintDouble() {
    var arg = 1D;

    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%.1f", TEMPLATE, arg), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%.1f\n", TEMPLATE, arg), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintChars() {
    var chars = new char[]{'h', 'e', 'l', 'l', 'o'};

    wrapper.print(chars);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(chars);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", TEMPLATE, new String(chars)), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", TEMPLATE, new String(chars)), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(chars);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(chars));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintString() {
    var text = "hello";

    wrapper.print(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(text);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", TEMPLATE, text), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", TEMPLATE, text), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(text));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintObject() {
    var obj = new Object();

    wrapper.print(obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.print(obj);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", TEMPLATE, obj), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", TEMPLATE, obj), BUFFER.toString());

    BUFFER.reset();
    wrapper.print(obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.print(obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintln() {
    var expected = String.format("%s%n", TEMPLATE);

    wrapper.println();
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println();
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println();
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println());
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnBoolean() {
    var expected = String.format("%s%b%n", TEMPLATE, true);

    wrapper.println(true);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(true);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(true);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(true));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnChar() {
    var arg = '!';
    var expected = String.format("%s%c%n", TEMPLATE, arg);

    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnInt() {
    var expected = String.format("%s%d%n", TEMPLATE, Integer.MAX_VALUE);

    wrapper.println(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(Integer.MAX_VALUE);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(Integer.MAX_VALUE));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnLong() {
    var expected = String.format("%s%d%n", TEMPLATE, Long.MAX_VALUE);

    wrapper.println(Long.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(Long.MAX_VALUE);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(Long.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(Long.MAX_VALUE));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnFloat() {
    var arg = 1F;
    var expected = String.format("%s%.1f%n", TEMPLATE, arg);

    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnDouble() {
    var arg = 1D;
    var expected = String.format("%s%.1f%n", TEMPLATE, arg);

    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnChars() {
    var chars = new char[]{'h', 'e', 'l', 'l', 'o'};
    var expected = String.format("%s%s%n", TEMPLATE, new String(chars));

    wrapper.println(chars);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(chars);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(chars);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(chars));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnString() {
    var text = "hello";
    var expected = String.format("%s%s%n", TEMPLATE, text);

    wrapper.println(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(text);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(text));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintlnObject() {
    var obj = new Object();
    var expected = String.format("%s%s%n", TEMPLATE, obj);

    wrapper.println(obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.println(obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    wrapper.println(obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.println(obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintf() {
    var obj = new Object();
    var expected = String.format("%s%s%n", TEMPLATE, obj);
    var format = "%s%n";

    wrapper.printf(format, obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.printf(format, obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    assertEquals(wrapper, wrapper.printf(format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.printf(format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testPrintfWithLocal() {
    var obj = new Object();
    var expected = String.format(Locale.US, "%s%s%n", TEMPLATE, obj);
    var format = "%s%n";

    wrapper.printf(Locale.US, format, obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.printf(Locale.US, format, obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    assertEquals(wrapper, wrapper.printf(Locale.US, format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.printf(Locale.US, format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testFormat() {
    var obj = new Object();
    var expected = String.format("%s%s%n", TEMPLATE, obj);
    var format = "%s%n";

    wrapper.format(format, obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.format(format, obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    assertEquals(wrapper, wrapper.format(format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.format(format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testFormatWithLocal() {
    var obj = new Object();
    var expected = String.format(Locale.US, "%s%s%n", TEMPLATE, obj);
    var format = "%s%n";

    wrapper.format(Locale.US, format, obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.format(Locale.US, format, obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, BUFFER.toString());

    BUFFER.reset();
    assertEquals(wrapper, wrapper.format(Locale.US, format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.format(Locale.US, format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testAppend() {
    var text = "hello";

    wrapper.append(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.append(text);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", TEMPLATE, text), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", TEMPLATE, text), BUFFER.toString());

    BUFFER.reset();
    assertEquals(wrapper, wrapper.append(text));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.append(text));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testAppendRange() {
    var text = "hello\n";
    var end = text.length() - 2;

    wrapper.append(text, 0, end);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.append(text, 0, end);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", TEMPLATE, text.substring(0, end)), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", TEMPLATE, text.substring(0, end)), BUFFER.toString());

    BUFFER.reset();
    assertEquals(wrapper, wrapper.append(text, 0, end));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.append(text, 0, end));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }

  @Test
  public void testAppendChar() {
    var arg = '!';

    wrapper.append(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.enable();
    wrapper.append(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%c", TEMPLATE, arg), BUFFER.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%c\n", TEMPLATE, arg), BUFFER.toString());

    BUFFER.reset();
    assertEquals(wrapper, wrapper.append(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());

    wrapper.destroy();
    assertDoesNotThrow(() -> wrapper.append(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, BUFFER.size());
  }
}