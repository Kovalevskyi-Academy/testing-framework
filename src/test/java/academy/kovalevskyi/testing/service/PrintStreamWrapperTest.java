package academy.kovalevskyi.testing.service;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PrintStreamWrapperTest {

  private static final String template = ":\n";
  private static ByteArrayOutputStream buffer;
  private static PrintStream stream;
  private PrintStreamWrapper wrapper;

  @BeforeAll
  public static void beforeAll() {
    buffer = new ByteArrayOutputStream();
    stream = new PrintStream(buffer);
  }

  @AfterAll
  public static void afterAll() {
    stream.close();
  }

  @BeforeEach
  public void setUp() {
    wrapper = new PrintStreamWrapper(stream);
  }

  @AfterEach
  public void tearDown() {
    buffer.reset();
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
    streamWrapper.destroy();
  }

  @Test
  public void testClose() {
    var mock = mock(PrintStream.class);
    var streamWrapper = new PrintStreamWrapper(mock);
    assertThrows(UnsupportedOperationException.class, streamWrapper::close);
    verify(mock, never()).close();
    assertFalse(streamWrapper.hasContent());
    streamWrapper.destroy();
    assertThrows(NullPointerException.class, streamWrapper::close);
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
    assertThrows(NullPointerException.class, streamWrapper::flush);
  }

  @Test
  public void testWriteByte() {
    var arg = 10;
    var expected = String.format("%s\n", template);

    wrapper.write(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.write(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.write(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.write(arg));
  }

  @Test
  public void testWriteByteBuffer() {
    var bytes = new byte[]{33};

    wrapper.write(bytes, 0, bytes.length);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.write(bytes, 0, bytes.length);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", template, new String(bytes)), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", template, new String(bytes)), buffer.toString());

    buffer.reset();
    wrapper.write(bytes, 0, bytes.length);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.write(bytes, 0, bytes.length));
  }

  @Test
  public void testPrintBoolean() {
    wrapper.print(true);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(true);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%b", template, true), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%b\n", template, true), buffer.toString());

    buffer.reset();
    wrapper.print(true);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(true));
  }

  @Test
  public void testPrintChar() {
    var arg = '!';

    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%c", template, arg), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%c\n", template, arg), buffer.toString());

    buffer.reset();
    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(arg));
  }

  @Test
  public void testPrintInt() {
    wrapper.print(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(Integer.MAX_VALUE);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%d", template, Integer.MAX_VALUE), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%d\n", template, Integer.MAX_VALUE), buffer.toString());

    buffer.reset();
    wrapper.print(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(Integer.MAX_VALUE));
  }

  @Test
  public void testPrintLong() {
    wrapper.print(Long.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(Long.MAX_VALUE);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%d", template, Long.MAX_VALUE), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%d\n", template, Long.MAX_VALUE), buffer.toString());

    buffer.reset();
    wrapper.print(Long.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(Long.MAX_VALUE));
  }

  @Test
  public void testPrintFloat() {
    var arg = 1F;

    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%.1f", template, arg), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%.1f\n", template, arg), buffer.toString());

    buffer.reset();
    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(arg));
  }

  @Test
  public void testPrintDouble() {
    var arg = 1D;

    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%.1f", template, arg), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%.1f\n", template, arg), buffer.toString());

    buffer.reset();
    wrapper.print(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(arg));
  }

  @Test
  public void testPrintChars() {
    var chars = new char[]{'h', 'e', 'l', 'l', 'o'};

    wrapper.print(chars);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(chars);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", template, new String(chars)), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", template, new String(chars)), buffer.toString());

    buffer.reset();
    wrapper.print(chars);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(chars));
  }

  @Test
  public void testPrintString() {
    var text = "hello";

    wrapper.print(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(text);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", template, text), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", template, text), buffer.toString());

    buffer.reset();
    wrapper.print(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(text));
  }

  @Test
  public void testPrintObject() {
    var obj = new Object();

    wrapper.print(obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.print(obj);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", template, obj), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", template, obj), buffer.toString());

    buffer.reset();
    wrapper.print(obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.print(obj));
  }

  @Test
  public void testPrintln() {
    var expected = String.format("%s%n", template);

    wrapper.println();
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println();
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println();
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println());
  }

  @Test
  public void testPrintlnBoolean() {
    var expected = String.format("%s%b%n", template, true);

    wrapper.println(true);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(true);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(true);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(true));
  }

  @Test
  public void testPrintlnChar() {
    var arg = '!';
    var expected = String.format("%s%c%n", template, arg);

    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(arg));
  }

  @Test
  public void testPrintlnInt() {
    var expected = String.format("%s%d%n", template, Integer.MAX_VALUE);

    wrapper.println(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(Integer.MAX_VALUE);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(Integer.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(Integer.MAX_VALUE));
  }

  @Test
  public void testPrintlnLong() {
    var expected = String.format("%s%d%n", template, Long.MAX_VALUE);

    wrapper.println(Long.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(Long.MAX_VALUE);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(Long.MAX_VALUE);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(Long.MAX_VALUE));
  }

  @Test
  public void testPrintlnFloat() {
    var arg = 1F;
    var expected = String.format("%s%.1f%n", template, arg);

    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(arg));
  }

  @Test
  public void testPrintlnDouble() {
    var arg = 1D;
    var expected = String.format("%s%.1f%n", template, arg);

    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(arg));
  }

  @Test
  public void testPrintlnChars() {
    var chars = new char[]{'h', 'e', 'l', 'l', 'o'};
    var expected = String.format("%s%s%n", template, new String(chars));

    wrapper.println(chars);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(chars);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(chars);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(chars));
  }

  @Test
  public void testPrintlnString() {
    var text = "hello";
    var expected = String.format("%s%s%n", template, text);

    wrapper.println(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(text);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(text));
  }

  @Test
  public void testPrintlnObject() {
    var obj = new Object();
    var expected = String.format("%s%s%n", template, obj);

    wrapper.println(obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.println(obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    wrapper.println(obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.println(obj));
  }

  @Test
  public void testPrintf() {
    var obj = new Object();
    var expected = String.format("%s%s%n", template, obj);
    var format = "%s%n";

    wrapper.printf(format, obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.printf(format, obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    assertEquals(wrapper, wrapper.printf(format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.printf(format, obj));
  }

  @Test
  public void testPrintfWithLocal() {
    var obj = new Object();
    var expected = String.format(Locale.US, "%s%s%n", template, obj);
    var format = "%s%n";

    wrapper.printf(Locale.US, format, obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.printf(Locale.US, format, obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    assertEquals(wrapper, wrapper.printf(Locale.US, format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.printf(Locale.US, format, obj));
  }

  @Test
  public void testFormat() {
    var obj = new Object();
    var expected = String.format("%s%s%n", template, obj);
    var format = "%s%n";

    wrapper.format(format, obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.format(format, obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    assertEquals(wrapper, wrapper.format(format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.format(format, obj));
  }

  @Test
  public void testFormatWithLocal() {
    var obj = new Object();
    var expected = String.format(Locale.US, "%s%s%n", template, obj);
    var format = "%s%n";

    wrapper.format(Locale.US, format, obj);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.format(Locale.US, format, obj);
    assertTrue(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(expected, buffer.toString());

    buffer.reset();
    assertEquals(wrapper, wrapper.format(Locale.US, format, obj));
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.format(Locale.US, format, obj));
  }

  @Test
  public void testAppend() {
    var text = "hello";

    wrapper.append(text);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.append(text);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", template, text), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", template, text), buffer.toString());

    buffer.reset();
    assertEquals(wrapper, wrapper.append(text));
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.append(text));
  }

  @Test
  public void testAppendRange() {
    var text = "hello\n";
    var end = text.length() - 2;

    wrapper.append(text, 0, end);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.append(text, 0, end);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%s", template, text.substring(0, end)), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%s\n", template, text.substring(0, end)), buffer.toString());

    buffer.reset();
    assertEquals(wrapper, wrapper.append(text, 0, end));
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.append(text, 0, end));
  }

  @Test
  public void testAppendChar() {
    var arg = '!';

    wrapper.append(arg);
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.enable();
    wrapper.append(arg);
    assertTrue(wrapper.hasContent());
    assertEquals(String.format("%s%c", template, arg), buffer.toString());

    wrapper.disable();
    assertFalse(wrapper.hasContent());
    assertEquals(String.format("%s%c\n", template, arg), buffer.toString());

    buffer.reset();
    assertEquals(wrapper, wrapper.append(arg));
    assertFalse(wrapper.hasContent());
    assertEquals(0, buffer.size());

    wrapper.destroy();
    assertThrows(NullPointerException.class, () -> wrapper.append(arg));
  }
}